package com.agueguen.clafout1s.game2048

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                Menu()
            }
        }
    }
}

@Composable
fun Menu() {
    Column {
        Text(
            text = "2048",
            modifier = Modifier.padding(2.dp)
        )
        Button(
            onClick = { Log.d("Button", "Switch to game") },
        ) {
            Text("START")
        }
        Button(
            onClick = { Log.d("Button","Switch to scoreboard")}
        ){
            Text("SCOREBOARD")
        }
        Button(
            onClick = { Log.d("Button","Switch to settings")}
        ){
            Text("SETTINGS")
        }
    }
}
