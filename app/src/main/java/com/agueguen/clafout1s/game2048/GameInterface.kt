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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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


class GameInterface(gameBoardInput: GameBoard,goalValuePower:Int) {

    var gameBoard = gameBoardInput
    val goalValuePower = goalValuePower // The power of 2 of the result considered a win
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
        val resetGridPressed = remember { mutableStateOf(false) }
        val score = remember { mutableIntStateOf(getGridScore()) }

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
                    modifier = Modifier
                        .height(tileSize * tileNbY)
                        .width(tileSize * tileNbX)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                ) {
                    items(tileNbX*tileNbY){ i->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                                .width(tileSize)
                                .height(tileSize)
                                .pointerInput(Unit) {
                                    var dragEnded = true
                                    detectDragGestures(
                                        onDragStart = {
                                            dragEnded = false
                                        },
                                        onDragEnd = {
                                            dragEnded = true
                                        }
                                    ) { _, dragAmount ->
                                        if (!dragEnded) {
                                            val dragDetectionMin = 5
                                            dragEnded = true

                                            var horizontalMove = false
                                            if (abs(dragAmount.x) > abs(dragAmount.y)) horizontalMove =
                                                true

                                            if (horizontalMove && dragAmount.x > dragDetectionMin) {
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeLeft()
                                                score.intValue = getGridScore()
                                            } else if (horizontalMove && dragAmount.x < -dragDetectionMin) {
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeRight()
                                                score.intValue = getGridScore()
                                            } else if (!horizontalMove && dragAmount.y > dragDetectionMin) {
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeDown()
                                                score.intValue = getGridScore()
                                            } else if (!horizontalMove && dragAmount.y < -dragDetectionMin) {
                                                movesTaken++
                                                gameContinues.value = gameBoard.swipeUp()
                                                score.intValue = getGridScore()
                                            }
                                        }
                                    }
                                }
                        ){
                            // i/tileNb is y, i%tileNb is x
                            val iVal = gameGrid[i/tileNbX, i%tileNbX]
                            val iValPower = powerToBase(iVal)
                            var textValue: String = iValPower.toString()
                            if(textValue == "1") textValue = "" // 2^0 is 1, so no number on the tile
                            if(iVal >= goalValuePower){
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
            Button(
                onClick = {
                    resetGridPressed.value = true
                }
            ){
                Text("Resets the game")
            }
            Text("Score: ${score.intValue}")
        }
        val endGameDialogStarted = remember { mutableStateOf(false) }
        if(!gameContinues.value){
            // The game cannot continue because no move is possible -> loose
            val context = LocalContext.current
            endGameDialogStarted.value = true
            EndGameDialog(gameContinues,false)
        }
        else if(endGameDialogStarted.value){
            // Called after EndGameDialog is closed
            endGameDialogStarted.value = false
            resetBoard(score)
        }
        if(goalAchieved.value){
            // The goal value has been reached -> win
            val context = LocalContext.current
            EndGameDialog(goalAchieved,true)
            LaunchedEffect(gameContinues){
                // Calls this part of the code only once
                saveActualScore(context)
                resetBoard(score)
            }
        }
        if(resetGridPressed.value){
            // The reset button has been pressed
            ResetGridDialog(resetGridPressed,score)
        }
    }

    /**
     * Calculates the score of the actual grid.
     *
     * @return the total score of the grid.
     */
    fun getGridScore(): Int {
        var score = 0
        for (i in 0..<boardWidth*boardHeight){
            val iVal = gameBoard.getGameGrid()[i/boardWidth, i%boardWidth]
            if(iVal.toInt() != 0)
                score += powerToBase(iVal).toInt()
        }
        return score
    }

    /**
     * Shows a dialog at the end of the game.
     *
     * @param mutableBoolean: The function must be called on an if statement on the value of mutableBoolean, for the AlertDialog to be able to close
     */
    @Composable
    fun EndGameDialog(mutableBoolean: MutableState<Boolean>,win: Boolean){
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                mutableBoolean.value = !mutableBoolean.value // Closes the dialog
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
                    Text("Do you want to add your score to the scoreboard ?")
            },
            confirmButton = {
                if(win) {
                    Button(
                        onClick = {
                            context.startActivity(Intent(context, ScoreboardActivity::class.java))
                            mutableBoolean.value = !mutableBoolean.value
                        }
                    ) {
                        Text("To scoreboard")
                    }
                }
                else{
                    Button(
                        onClick = {
                            saveActualScore(context)
                            mutableBoolean.value = !mutableBoolean.value
                        }
                    ) {
                        Text("Add to scoreboard")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mutableBoolean.value = !mutableBoolean.value
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    /**
     * Dialog when pressing the reset button.
     *
     * @param mutableBoolean: The function must be called on an if statement on the value of mutableBoolean, for the AlertDialog to be able to close.
     * @param scoreMutable: Passed down to resetBoard to be able to reset the score of the board.
     */
    @Composable
    fun ResetGridDialog(mutableBoolean: MutableState<Boolean>,scoreMutable: MutableIntState){
        val resetConfirmed = remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                mutableBoolean.value = !mutableBoolean.value
            },
            title = { Text("Reseting grid") },
            text = { Text("Do you really want to reset the grid ?") },
            confirmButton = {
                Button(
                    onClick = {
                        resetConfirmed.value = true // Launches SaveScoreDialog
                        // Closes this dialog after showing SaveScoreDialog, later in the function
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mutableBoolean.value = !mutableBoolean.value // closes this dialog
                    }
                ) { Text("No") }
            }
        )
        val saveScoreDialogStarted = remember { mutableStateOf(false) }
        if(resetConfirmed.value){
            saveScoreDialogStarted.value = true
            SaveScoreDialog(resetConfirmed)
        }
        if(saveScoreDialogStarted.value && !resetConfirmed.value){
            // After the SaveScoreDialog is closed
            resetBoard(scoreMutable)
            mutableBoolean.value = !mutableBoolean.value // Closes this dialog
        }
    }

    /**
     * Dialog choice to save the score, after pressing the reset button.
     *
     * @param mutableBoolean: The function must be called on an if statement on the value of mutableBoolean, for the AlertDialog to be able to close.
     */
    @Composable
    fun SaveScoreDialog(mutableBoolean: MutableState<Boolean>){
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                mutableBoolean.value = !mutableBoolean.value
            },
            title = { Text("Saving score") },
            text = { Text("Do you want to save your actual score in the scoreboard ?") },
            confirmButton = {
                Button(
                    onClick = {
                        saveActualScore(context)
                        mutableBoolean.value = !mutableBoolean.value // closes this dialog
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mutableBoolean.value = !mutableBoolean.value // closes this dialog
                    }
                ) { Text("No") }
            }
        )
    }

    /**
     * Saves the current score and other game values onto the database.
     * @param context: The context of the app, used to get the database.
     */
    fun saveActualScore(context: Context){
        val db = AppDatabase.getDatabase(context)
        val scoreDao = db.scoreDao()
        val gameGrid = gameBoard.getGameGrid()
        var highestTile:Long=0
        var score:Long = 0
        for (i in 0..<boardWidth*boardHeight){
            val tileValue:Byte = gameGrid[i/boardWidth, i%boardWidth]
            if(tileValue.toInt() != 0){
                val tilePowerValue = powerToBase(tileValue)
                if(tileValue>highestTile){
                    highestTile = tileValue.toLong()
                }
                score += tilePowerValue.toInt()
            }
        }
        // TODO: Recording time in a global variable
        scoreDao.save(score,highestTile,0,movesTaken.toLong(),boardHeight,boardWidth)
    }

    /**
     * Resets the board to restart the game
     *
     * @param scoreMutable: The mutable of the score, passed down to be able to reset the score of the board.
     */
    fun resetBoard(scoreMutable: MutableIntState){
        // TODO: Making sure the function is only called once (and not in continuation because of mutable boolean)
        gameBoard = GameBoard(boardWidth,boardHeight)
        movesTaken = 0
        scoreMutable.intValue = getGridScore()
    }
}
