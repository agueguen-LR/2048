package com.agueguen.clafout1s.game2048

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.SaveState
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.ui.theme.blockyFont
import kotlin.random.Random
import kotlin.math.pow
import kotlin.random.nextInt
import kotlin.random.nextLong

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Menu()
                }
            }
        }
        startDatabase()
    }

    fun startDatabase(){
        Log.i("MainActivity", "Starting")
        val db = AppDatabase.getDatabase(this)

        val saveStateDao = db.saveStateDao()
        val saveState = SaveState(1, ByteArray(16), 4, 4)
        saveStateDao.create(saveState)
        Log.i("MainActivity", saveStateDao.get(1).toString())
        saveStateDao.delete(saveState)

        val scoreDao = db.scoreDao()
        scoreDao.reinitializeAll()
        scoreDao.save(4, 4, 12, 1, 4, 4)
        scoreDao.save(8, 8, 15, 2, 4, 4)
        scoreDao.save(6, 8, 18, 3, 4, 4)
        val two = 2.0
        for (i in 0..<50){
            scoreDao.save(
                score = Random.nextLong(1, 10),
                highestTile = two.pow(Random.nextDouble(1.0,6.0)).toLong(),
                timeTaken = Random.nextLong(5,25),
                movesTaken = Random.nextLong(1,10),
                boardHeight = Random.nextInt(3,10),
                boardLength = Random.nextInt(3,10)
            )
        }

        //Log.i("MainActivity", scoreDao.getAll().toString())
    }
}

// TODO: Change the connexions of the buttons to their actual activities
@Composable
fun Menu() {
    val commonModifierBases = Modifier.padding(15.dp).fillMaxWidth()
    val buttonModifiers = commonModifierBases.height(80.dp)
    val context = LocalContext.current
    val buttonColors = ButtonColors(
        MaterialTheme.colorScheme.surfaceBright,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.error)
    Column (
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
        ) {
        Text(
            text = "2048",
            fontSize = 60.sp,
            fontFamily = blockyFont,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary ,MaterialTheme.colorScheme.inversePrimary))
            ),
            modifier = commonModifierBases.weight(1F).wrapContentHeight(align= Alignment.CenterVertically),
            textAlign = TextAlign.Center,
        )
        Column(verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(2F)
        ) {
            Button(
                onClick = { context.startActivity(Intent(context, SwapTestActivity::class.java)) },
                modifier = buttonModifiers,
                colors = buttonColors
            ) {
                Text("START", fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
            }
            Button(
                onClick = { context.startActivity(Intent(context, ScoreboardActivity::class.java))},
                modifier = buttonModifiers,
                colors = buttonColors
            ){
                Text("SCOREBOARD",fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
            }
            Button(
                onClick = { context.startActivity(Intent(context, SwapTestActivity::class.java))},
                modifier = buttonModifiers,
                colors = buttonColors
            ){
                Text("SETTINGS",fontFamily = blockyFont, fontWeight = FontWeight.Light, fontSize = 40.sp)
            }
        }
        Column(modifier = Modifier.weight(0.5F)) { }

    }
}

@Preview
@Composable
fun PreviewMenu(){
    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Menu()
        }
    }

}
