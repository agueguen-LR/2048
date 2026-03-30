package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.database.Score
import com.agueguen.clafout1s.game2048.database.ScoreDao
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme
import com.agueguen.clafout1s.game2048.utilities.formatUnixTime
import com.agueguen.clafout1s.game2048.utilities.powerToBase
import com.agueguen.clafout1s.game2048.utilities.powerToBase

class ScoreboardActivity : ComponentActivity() {
	private lateinit var scoreDao: ScoreDao

	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	override fun onCreate(savedInstanceState: Bundle?) {
		val db = AppDatabase.getDatabase(this)
		scoreDao = db.scoreDao()

		super.onCreate(savedInstanceState)
		setContent {
			AppTheme {
				Scaffold(modifier = Modifier
				.fillMaxSize()
				.padding(top = 50.dp, bottom = 20.dp)) {
					Scoreboard()
				}
			}
		}
	}

	@Composable
	private fun Scoreboard(){
		val allScores = scoreDao.getAll()
		LazyColumn(
			modifier = Modifier.fillMaxHeight()
		) {
			item(){
				Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp)){
					Text("Scoreboard", color = MaterialTheme.colorScheme.secondary, fontSize = 30.sp)
				}
			}
			items(allScores) { score ->
				ScoreCard(score)
			}
		}
	}

	@Composable
	private fun ScoreCard(score: Score) {
		Card(
			modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp)
		) {
			Column(
				modifier = Modifier.padding(12.dp)
			) {
				Text(
					text = "Score: ${score.score}",
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(6.dp))

				Text("Time Taken: ${score.timeTaken}")
				Text("Highest tile reached: ${powerToBase(score.highestTile)}")
				Text("Moves used: ${score.movesTaken}")
				Text("Board Format: ${score.boardLength}x${score.boardHeight}")
				Text("Date: ${formatUnixTime(score.date)}")
			}
		}
	}
}

