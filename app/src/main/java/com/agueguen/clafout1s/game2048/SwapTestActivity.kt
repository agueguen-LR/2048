package com.agueguen.clafout1s.game2048

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.core.content.ContextCompat.startActivity

// TODO: replacing the whole activity by a real one (changes in MainMenuActivity and the AndroidManifest)
// This class also serves as an example of the minimum amount of code needed to show things on the screen
class SwapTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize().focusable()) {
                MenuTest()
            }
        }
    }
}

@Composable
fun MenuTest() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
        // If no arrangement, the text doesn't appear (hidden behind the page header ?)
    ) {
        Text(
            text = "Test"
        )
    }
}