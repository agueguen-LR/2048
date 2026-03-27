package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.Score
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme

class ScoreboardActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)) {
                    Scoreboard()
                }
            }
        }
    }

    @Composable
    fun Scoreboard(){
        val context = LocalContext.current
        val db = AppDatabase.getDatabase(context)
        val scoreDao = db.scoreDao()
        val allScores = scoreDao.getAll()
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            item(){
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(20.dp)){
                    Text("Scoreboard", color = MaterialTheme.colorScheme.secondary, fontSize = 30.sp)
                }
            }
            item(){
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(6) {
                        index->Column(modifier = Modifier.padding(horizontal = 10.dp)){
                            Text(getScoreValue(index, null), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            HorizontalDivider(thickness = 20.dp)
                            allScores.forEach { score ->
                                Text(getScoreValue(index,score), fontSize = 18.sp)
                                HorizontalDivider(thickness = 10.dp)
                            }
                    }
                        VerticalDivider(thickness = 10.dp, color = DividerDefaults.color)
                    }
                }
            }
        }
    }

    fun getScoreValue(i: Int,scoreData: Score?): String {
        if(i == 0) {
            if(scoreData==null) return "Date"
            return scoreData.date.toString()
        }
        else if(i==1) {
            if(scoreData==null) return "Score"
            return scoreData.score.toString()
        }
        else if(i==2) {
            if(scoreData==null) return "Highest tile"
            return scoreData.highestTile.toString()
        }
        else if(i==3) {
            if(scoreData==null) return "Time taken"
            return scoreData.timeTaken.toString()
        }
        else if(i==4) {
            if(scoreData==null) return "Number of moves"
            return scoreData.movesTaken.toString()
        }
        else if(i==5) {
            if(scoreData==null) return "Board size"
            return scoreData.boardLength.toString()+"x"+scoreData.boardHeight.toString()
        }
        return "?"
    }
}


