package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
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

class ScoreboardActivity : Activity2048(
	modifier = Modifier.fillMaxSize().padding(top = 50.dp, bottom = 20.dp)
) {
	private lateinit var scoreDao: ScoreDao

	@Composable
	override fun ScreenContent() {
		val db = AppDatabase.getDatabase(this)
		scoreDao = db.scoreDao()
		var allScores = remember { mutableStateOf(scoreDao.getAll()) }
		var showDialog = remember { mutableStateOf(false) }

		Box(modifier = Modifier.fillMaxSize()) {
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
				items(allScores.value) { score ->
					ScoreCard(score)
				}
			}

			IconButton(
				onClick = { showDialog.value = true },
				modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
				.background(
					color = MaterialTheme.colorScheme.errorContainer,
					shape = CircleShape
				)
			) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Reset scores"
				)
			}

			if (showDialog.value) {
				AlertDialog(
					onDismissRequest = { showDialog.value = false },
					title = { Text("Confirm reset") },
					text = { Text("Are you sure you want to delete all scores?") },
					confirmButton = {
						TextButton(onClick = {
							scoreDao.reinitializeAll()
							allScores.value = scoreDao.getAll()
							showDialog.value = false
						}) {
							Text("Yes")
						}
					},
					dismissButton = {
						TextButton(onClick = { showDialog.value = false }) {
							Text("Cancel")
						}
					}
				)
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
				Text("Highest tile reached: ${powerToBase(score.highestTile.toByte())}")
				Text("Moves used: ${score.movesTaken}")
				Text("Board Format: ${score.boardLength}x${score.boardHeight}")
				Text("Date: ${formatUnixTime(score.date)}")
			}
		}
	}
}

