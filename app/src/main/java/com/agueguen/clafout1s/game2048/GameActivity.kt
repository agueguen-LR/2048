package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agueguen.clafout1s.game2048.database.AppDatabase
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme

class GameActivity : Activity2048(
	modifier = Modifier.fillMaxSize().focusable().padding(top = 50.dp, bottom = 20.dp)
) {
	val gameBoard = GameBoard(4, 4)

	@Composable
	override fun ScreenContent(){
		GameInterface(gameBoard, 11).GameInterfaceComposable()
	}
}

