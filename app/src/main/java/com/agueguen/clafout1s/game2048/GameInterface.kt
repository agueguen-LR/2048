package com.agueguen.clafout1s.game2048

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.IntegerRes
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import kotlin.math.abs
import kotlin.math.max


class GameInterface(gameBoardInput: GameBoard) {

    val gameBoard = gameBoardInput
    val goalValue = 4 // The power of 2 of the result considered a win
    var movesTaken = 0
    val boardWidth = gameBoard.getGameGrid().width
    val boardHeight = gameBoard.getGameGrid().height

    @Composable
    fun GameInterfaceComposable(){
        val gameGrid = gameBoard.getGameGrid()
        val tileNbX = boardWidth
        val tileNbY = boardHeight
        val screenHeight = LocalWindowInfo.current.containerSize.height
        val screenWidth = LocalWindowInfo.current.containerSize.width
        var tileSize:Dp = 80.dp
        val gameContinues = remember { mutableStateOf(true) }
        val goalAchieved = remember { mutableStateOf(false) }

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
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeLeft()
                                                Log.d("2048GAME",gameGrid.toString())
                                            }
                                            else if(horizontalMove && dragAmount.x < -dragDetectionMin){
                                                Log.d("2048GAME","Left")
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeRight()
                                                Log.d("2048GAME",gameGrid.toString())
                                            }
                                            else if(!horizontalMove && dragAmount.y > dragDetectionMin){
                                                Log.d("2048GAME","Down")
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeDown()
                                                Log.d("2048GAME",gameGrid.toString())
                                            }
                                            else if(!horizontalMove && dragAmount.y < -dragDetectionMin){
                                                Log.d("2048GAME","Up")
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeUp()
                                                Log.d("2048GAME",gameGrid.toString())
                                            }
                                        }
                                    }
                                }
                        ){
                            // i/tileNb is y, i%tileNb is x
                            val iVal = gameGrid[i/tileNbX, i%tileNbX]
                            var textValue: String = powerToBase(iVal).toString()
                            if(textValue == "1") textValue = "" // 2^0 is 1, so no number on the tile
                            if(iVal >= goalValue){
                                goalAchieved.value = true
                            }
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
        if(!gameContinues.value){
            EndGameDialog(gameContinues,false)
            //endGameScoreAndReset(LocalContext.current)
        }
        if(goalAchieved.value){
            EndGameDialog(goalAchieved,true)
            //endGameScoreAndReset(LocalContext.current)
        }
    }

    /**
     * Shows a dialog at the end of the game.
     *
     * @param showing: The function must be called on an if statement on the value of showing, for the AlertDialog to be able to close
     */
    @Composable
    fun EndGameDialog(showing: MutableState<Boolean>,win: Boolean){
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                showing.value = !showing.value // Closes the dialog
            },
            title = {
                if(win)
                    Text("You won !")
                else
                    Text("You lost...")
            },
            text = {
                if(win)
                    Text("You can check your score in the scoreboard.")
                else
                    Text("But you can still check your score in the scoreboard.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Replace SwapTestActivity by the ScoreBoardActivity
                        context.startActivity(Intent(context, SwapTestActivity::class.java))
                    }
                ) {
                    Text("To scoreboard")
                }
            },
            dismissButton = {}
        )
    }

    fun endGameScoreAndReset(context: Context){
        val db = AppDatabase.getDatabase(context)
        val scoreDao = db.scoreDao()
        val gameGrid = gameBoard.getGameGrid()
        var highestTile:Long=0
        var score:Long = 0
        for(i in 0..<boardWidth*boardHeight){
            val tileValue:Byte = gameGrid[i/boardWidth, i%boardWidth]
            if(tileValue>highestTile){
                highestTile = tileValue.toLong()
            }
            score+=tileValue
        }
        // TODO: Recording time in a global variable
        scoreDao.save(score,highestTile,0,movesTaken.toLong(),boardHeight,boardWidth)
        resetBoard()
    }

    fun resetBoard(){
        // TODO: Clearing game board, potentially making the user choose a new grid size
    }
}
