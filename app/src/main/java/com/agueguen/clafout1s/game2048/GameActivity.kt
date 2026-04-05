package com.agueguen.clafout1s.game2048

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agueguen.clafout1s.game2048.ui.theme.AppTheme

class GameActivity : ComponentActivity() {
	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val gameBoard = GameBoard(2,2)
        val gameInterface = GameInterface(gameBoard)
		setContent{
			AppTheme() {
				Scaffold(modifier = Modifier
				.fillMaxSize()
				.focusable()
				.padding(top = 50.dp, bottom = 20.dp)) {
					gameInterface.GameInterfaceComposable()
				}
			}
		}
	}
}

