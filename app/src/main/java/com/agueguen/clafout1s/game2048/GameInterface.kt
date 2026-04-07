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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.abs

import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import com.agueguen.clafout1s.game2048.utilities.ByteGrid

class GameInterface(
	val boardWidth: Int = 4,
	val boardHeight: Int = 4
) {

	private var gameBoard by mutableStateOf(GameBoard(boardWidth, boardHeight))
	private lateinit var movesTaken: MutableState<Long>
	private lateinit var score: MutableState<Long>
	private lateinit var playerHasLost: MutableState<Boolean>
	private lateinit var highestTile: MutableState<Byte>

	@Composable
	fun GameInterfaceComposable(){

		score = remember { mutableStateOf(0) }
		playerHasLost = remember { mutableStateOf(false) }
		movesTaken = remember { mutableStateOf(0) }
		highestTile = remember { mutableStateOf(1) }
		val screenHeight = LocalWindowInfo.current.containerSize.height
		val screenWidth = LocalWindowInfo.current.containerSize.width
		var tileSize:Dp = 80.dp
        val marginSize:Dp = 10.dp // Used for space between tiles, padding, and the borders of the grid
        val marginNbX = boardWidth+3 // nbTiles -1 + 4 padding
        val marginNbY = boardHeight+3

		val totalWidthPx = with(LocalDensity.current) { boardWidth*tileSize.toPx() + marginNbX*marginSize.toPx()}
		val totalHeightPx = with(LocalDensity.current) { boardHeight*tileSize.toPx() + marginNbY*marginSize.toPx()}
        if(totalWidthPx > screenWidth && totalHeightPx > screenHeight){
			if(totalWidthPx>=totalHeightPx){
                tileSize = with(LocalDensity.current){((screenWidth-marginNbX*marginSize.toPx())/boardWidth).toDp()}
			}
			else{
				tileSize = with(LocalDensity.current){((screenHeight-marginNbY*marginSize.toPx())/boardHeight).toDp()}
			}
		}
		else if(totalWidthPx > screenWidth){
            tileSize = with(LocalDensity.current){((screenWidth-marginNbX*marginSize.toPx())/boardWidth).toDp()}
		}
		else if(totalHeightPx > screenHeight){
            tileSize = with(LocalDensity.current){((screenHeight-marginNbY*marginSize.toPx())/boardHeight).toDp()}
		}
        Log.d("Test","${tileSize * boardHeight + marginSize * marginNbY}, tile_size: $tileSize, margin_size: $marginSize")
		Box(modifier = Modifier.padding(marginSize)){
            LazyHorizontalGrid(
                rows = GridCells.Fixed(boardWidth),
                horizontalArrangement = Arrangement.spacedBy(marginSize),
                verticalArrangement = Arrangement.spacedBy(marginSize),
                modifier = Modifier
                    .height(tileSize * boardHeight + marginSize * (marginNbY-4))
                //.background(MaterialTheme.colorScheme.primaryContainer)
                //.border(BorderStroke(marginSize, MaterialTheme.colorScheme.primaryContainer))
            ) {
                items(boardWidth*boardHeight){ i -> Tile(i, tileSize) }
            }
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
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
                //.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .width(tileSize)
                .height(tileSize)
                .pointerInput(Unit) {
                    //TODO this should be on the grid rather than the tiles
                    // but there are detection issues, cause the tiles cover the grid
                    detectDragGestures(
                        onDragStart = { dragEnded.value = false },
                        onDragEnd = { dragEnded.value = true }
                    ) { _, dragAmount ->
                        handleDragGesture(dragAmount, dragEnded)
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
				fontSize = with(LocalDensity.current) { tileSize.toSp() / 2 },
				fontFamily = blockyFont,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.primary
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
				playerHasLost.value = !gameBoard.swipeLeft()
				score.value = getGridScore()
			} else if (horizontalMove && dragAmount.x < -dragDetectionMin) {
				movesTaken.value++
				playerHasLost.value = !gameBoard.swipeRight()
				score.value = getGridScore()
			} else if (!horizontalMove && dragAmount.y > dragDetectionMin) {
				movesTaken.value++
				playerHasLost.value = !gameBoard.swipeDown()
				score.value = getGridScore()
			} else if (!horizontalMove && dragAmount.y < -dragDetectionMin) {
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

	fun getScore(): MutableState<Long> {
		return this.score
	}

	fun hasPlayerLost(): MutableState<Boolean> {
		return this.playerHasLost
	}

	fun getHighestTile(): MutableState<Byte> {
		return this.highestTile
	}

	fun getMovesTaken(): MutableState<Long> {
		return this.movesTaken
	}

	fun resetBoard(){
		gameBoard = GameBoard(boardWidth, boardHeight)
		movesTaken.value = 0
		score.value = getGridScore()
	}
}
