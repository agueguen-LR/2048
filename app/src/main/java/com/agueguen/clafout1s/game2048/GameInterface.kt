package com.agueguen.clafout1s.game2048

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.SaveState
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import com.agueguen.clafout1s.game2048.utilities.ByteGrid
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class GameInterface(
	val boardWidth: Int = 4,
	val boardHeight: Int = 4
) {

	var gameBoard by mutableStateOf(GameBoard(boardWidth, boardHeight))
	val score = mutableStateOf(0L)
	val movesTaken = mutableStateOf(0L)
	val playerHasLost = mutableStateOf(false)
	val highestTile = mutableStateOf(1.toByte())
	val timer = mutableStateOf(0L)
	private var timerJob: Job? = null

	constructor(saveState: SaveState): this(saveState.boardLength, saveState.boardHeight) {
		gameBoard = GameBoard(saveState)
		score.value = getGridScore()
		timer.value = saveState.timeTaken
		movesTaken.value = saveState.movesTaken
	}

 	@Composable
	fun GameInterfaceComposable(tileSize: Dp, marginSize: Dp){

		Box(modifier = Modifier.padding(marginSize)){
			LazyHorizontalGrid(
				rows = GridCells.Fixed(boardWidth),
				horizontalArrangement = Arrangement.spacedBy(marginSize),
				verticalArrangement = Arrangement.spacedBy(marginSize),
				modifier = Modifier
				.height(tileSize * boardHeight + marginSize * (boardHeight-1))
			) {
				items(boardWidth*boardHeight){ i -> Tile(i, tileSize) }
			}
		}

		score.value = getGridScore()
		LaunchedEffect(Unit) {
			startTimer(this)
		}
	}

	/**
	 * Displays a single tile of the game grid. It detects drag gestures to perform the swipe actions.
	 *
	 * @param i The index of the tile in the grid (0 to boardWidth*boardHeight-1).
	 * @param tileSize The size of the tile in Dp.
	 */
	@Composable
	private fun Tile(
		i: Int,
		tileSize: Dp,
	) {
		var dragEnded = remember { mutableStateOf(true) }
		val context = LocalContext.current
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
			//.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
			.background(MaterialTheme.colorScheme.secondaryContainer)
			.width(tileSize)
			.height(tileSize)
			.pointerInput(Unit) {
				//TODO this should be on the grid rather than the tiles
				// but there are detection issues, cause the tiles cover the grid
				detectDragGestures(
					onDragStart = { dragEnded.value = false },
					onDragEnd = { dragEnded.value = true }
				) { _, dragAmount ->
					handleDragGesture(context, dragAmount, dragEnded)
				}
			}
		) {
			val iVal = gameBoard.getGameGrid()[i / boardWidth, i % boardWidth]
			val iValPower = powerToBase(iVal)

			var textValue = iValPower.toString()
			if (textValue == "1") textValue = ""

			if (iVal >= highestTile.value) {
				highestTile.value = iVal
			}

			Text(
				text = textValue,
				fontSize = with(LocalDensity.current) { (tileSize/max(textValue.length,2)).toSp() },
				fontFamily = blockyFont,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.secondary
			)
		}
	}

	/**
	 * Handles the drag gesture to determine the swipe direction and perform the corresponding action on the game board.
	 *
	 * @param dragAmount The amount of drag in both x and y directions.
	 * @param dragEnded A mutable state that indicates whether the drag has ended to prevent multiple detections during a single drag.
	 */
	private fun handleDragGesture(
		context: Context,
		dragAmount: Offset,
		dragEnded: MutableState<Boolean>
	) {
		if (!dragEnded.value) {
			val dragDetectionMin = 5f  // Minimum distance for detecting swipe
			dragEnded.value = true  // Mark the drag as ended

			val horizontalMove = abs(dragAmount.x) > abs(dragAmount.y)

			// Perform the swipe actions based on the drag direction
			if (horizontalMove && dragAmount.x > dragDetectionMin) {
				movesTaken.value++
				AudioManager.playSound(context, R.raw.swipe)
				playerHasLost.value = !gameBoard.swipeLeft()
				score.value = getGridScore()
			} else if (horizontalMove && dragAmount.x < -dragDetectionMin) {
				AudioManager.playSound(context, R.raw.swipe)
				movesTaken.value++
				playerHasLost.value = !gameBoard.swipeRight()
				score.value = getGridScore()
			} else if (!horizontalMove && dragAmount.y > dragDetectionMin) {
				AudioManager.playSound(context, R.raw.swipe)
				movesTaken.value++
				playerHasLost.value = !gameBoard.swipeDown()
				score.value = getGridScore()
			} else if (!horizontalMove && dragAmount.y < -dragDetectionMin) {
				AudioManager.playSound(context, R.raw.swipe)
				movesTaken.value++
				playerHasLost.value = !gameBoard.swipeUp()
				score.value = getGridScore()
			}
		}
	}

	private fun getGridScore(): Long {
		var score: Long = 0
		for (i in 0..<boardWidth*boardHeight){
			val iVal = gameBoard.getGameGrid().data[i]
			if(iVal.toInt() != 0) score += powerToBase(iVal)
		}
		return score
	}

	fun resetBoard(){
		gameBoard = GameBoard(boardWidth, boardHeight)
		movesTaken.value = 0
		timer.value = 0
		score.value = getGridScore()
	}

	private fun startTimer(scope: CoroutineScope) {
		timerJob?.cancel()
		timerJob = scope.launch {
			while (isActive) {
				timer.value += 1
				delay(1000)
			}
		}
	}
}
