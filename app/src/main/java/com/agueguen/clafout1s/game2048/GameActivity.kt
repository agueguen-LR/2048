package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults.flingBehavior
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.pow

class GameActivity : ComponentActivity() {
	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val gameBoard = GameBoard()
		Log.d("2048GAME",gameBoard.toString())
		gameBoard.swipeLeft()
		// Log.d("2048GAME",gameBoard.toString())
		// gameBoard.swipeRight()
		// Log.d("2048GAME",gameBoard.toString())
		// gameBoard.swipeUp()
		// Log.d("2048GAME",gameBoard.toString())
		// gameBoard.swipeDown()
		// Log.d("2048GAME",gameBoard.toString())
		setContent{
			AppTheme() {
				Scaffold(modifier = Modifier
				.fillMaxSize()
				.focusable()
				.padding(top = 50.dp, bottom = 20.dp)) {
					GameBoardAlt(gameBoard)
				}
			}
		}

	}
}

@Composable
fun GameBoardLayout(gameBoard: GameBoard){
	val tile_width = 50.dp
	val tile_height = 50.dp
	Column(
		modifier = Modifier
		.fillMaxHeight()
		.padding(50.dp),
		verticalArrangement = Arrangement.Center
	){
		for (y in 0 until gameBoard.getGameGrid().height) {
			Row(
				modifier = Modifier.height(tile_height).
				border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary)),
				horizontalArrangement = Arrangement.Center
			) {
				for (x in 0 until gameBoard.getGameGrid().width){
					Text(
						modifier = Modifier
						.fillMaxHeight()
						.width(tile_width)
						.border(BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)),
						text = x.toString() + " " + y.toString(),
						textAlign = TextAlign.Center
					)
				}
			}
		}
	}
}


enum class DragValue { Start, End }


@Composable
fun AnchoredDraggableBox() {
	val anchors = DraggableAnchors<DragValue> {
		DragValue.Start at 100.dp.value
		DragValue.End at 500.dp.value
	}
	val state = remember {
		AnchoredDraggableState(
			initialValue = DragValue.Start,
			anchors = anchors,
		)
		.apply {
			updateAnchors(anchors)
		}
	}
	Box(
		modifier = Modifier.anchoredDraggable(
			state = state,
			orientation = Orientation.Horizontal,
			flingBehavior = flingBehavior(
				state = state,
				positionalThreshold = { distance: Float -> 0f },
				animationSpec = tween()
			)
		),
	) {
		Text(
			text = "Truc",
			modifier = Modifier
			.size(80.dp)
			.offset {
				IntOffset(
					x = state.requireOffset().roundToInt(),
					y = 0
				)
			}
			.anchoredDraggable(state, Orientation.Horizontal),
		)
	}
}

fun printTileList(tileList: SnapshotStateList<Array<Int>>){
	var message = ""
	for (rows in tileList){
		for(ele in rows){
			message+= " $ele"
		}
		message += "\n"
	}
	Log.d("2048GAME",message)
}

@Composable
fun GameBoardAlt(gameBoard: GameBoard){
	val tileWidth:Dp = 80.dp
	val tileHeight:Dp = 80.dp
	var gameGrid = gameBoard.getGameGrid()

	Column(
		modifier = Modifier.fillMaxHeight(),
		verticalArrangement = Arrangement.Center
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center,
		){
			LazyHorizontalGrid(
				rows = GridCells.Fixed(4),
				horizontalArrangement = Arrangement.Center,
				verticalArrangement = Arrangement.Center,
				modifier = Modifier.height(tileHeight*4)
				.width(tileWidth*4)
				.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
			) {
				items(4*4){ i->
					Box(
						contentAlignment = Alignment.Center,
						modifier = Modifier.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
						.width(tileWidth)
						.height(tileHeight)
						.pointerInput(Unit) {
							var dragEnded = true
							detectDragGestures(
								onDragStart = {
									Log.d("2048GAME","Drag start")
									dragEnded = false
								},
								onDragEnd = {
									dragEnded = true
								}
							) { _, dragAmount ->
								Log.d("2048GAME", "Drag detected, amount $dragAmount")
								if (!dragEnded){
									val dragDetectionMin = 20
									var horizontalMove = false
									if(abs(dragAmount.x)>abs(dragAmount.y)) horizontalMove = true

									if(horizontalMove && dragAmount.x > dragDetectionMin){
										Log.d("2048GAME","Right")
										gameBoard.swipeLeft()
										Log.d("2048GAME",gameGrid.toString())
									}
									else if(horizontalMove && dragAmount.x < -dragDetectionMin){
										Log.d("2048GAME","Left")
										gameBoard.swipeRight()
										Log.d("2048GAME",gameGrid.toString())
									}
									else if(!horizontalMove && dragAmount.y > dragDetectionMin){
										Log.d("2048GAME","Down")
										gameBoard.swipeDown()
										Log.d("2048GAME",gameGrid.toString())
									}
									else if(!horizontalMove && dragAmount.y < -dragDetectionMin){
										Log.d("2048GAME","Up")
										gameBoard.swipeUp()
										Log.d("2048GAME",gameGrid.toString())
									}
								}
							}
						}
					){
						// i/4 is y, i%4 is x
						var textValue: String = powerToBase(gameGrid[i/4, i%4]).toString()
						if(textValue == "1") textValue = "" // 2^0 is 1, so no number on the tile
						Text(
							text = textValue,
							fontSize = 30.sp,
							fontFamily = blockyFont,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.primary
						)
					}
				}
			}
		}

	}

}
