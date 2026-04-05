package com.agueguen.clafout1s.game2048

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import kotlin.math.abs
import kotlin.math.max


class GameInterface(gameBoardInput: GameBoard) {

    val gameBoard = gameBoardInput

    @Composable
    fun GameInterfaceComposable(){
        val gameGrid = gameBoard.getGameGrid()
        val tileNbX = gameGrid.width
        val tileNbY = gameGrid.height
        val screenHeight = LocalWindowInfo.current.containerSize.height
        val screenWidth = LocalWindowInfo.current.containerSize.width
        var tileSize:Dp = 80.dp

        val totalTileWidth = with(LocalDensity.current) { tileNbX * tileSize.toPx() }
        val totalTileHeight = with(LocalDensity.current) { tileNbY * tileSize.toPx() }
        if(totalTileWidth > screenWidth && totalTileHeight > screenHeight){
            if(totalTileWidth>=totalTileHeight){
                // +1 to tileNbX to have some margins (of half the tileSize)
                tileSize = with(LocalDensity.current){(screenWidth / (tileNbX+1)).toDp()}
            }
            else{
                tileSize = with(LocalDensity.current){(screenHeight / (tileNbY+1)).toDp()}
            }
        }
        else if(totalTileWidth > screenWidth){
            tileSize = with(LocalDensity.current){(screenWidth / (tileNbX+1)).toDp()}
        }
        else if(totalTileHeight > screenHeight){
            tileSize = with(LocalDensity.current){(screenHeight / (tileNbY+1)).toDp()}
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ){
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(tileNbX),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.height(tileSize*tileNbY)
                        .width(tileSize*tileNbX)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                ) {
                    items(tileNbX*tileNbY){ i->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                                .width(tileSize)
                                .height(tileSize)
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
                                        if (!dragEnded){
                                            val dragDetectionMin = 5
                                            dragEnded = true
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
                            // i/tileNb is y, i%tileNb is x
                            var textValue: String = powerToBase(gameGrid[i/tileNbX, i%tileNbX]).toString()
                            if(textValue == "1") textValue = "" // 2^0 is 1, so no number on the tile
                            Text(
                                text = textValue,
                                fontSize = with(LocalDensity.current){tileSize.toSp() / 2},
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
}