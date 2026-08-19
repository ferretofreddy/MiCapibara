package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.model.CapybaraState
import com.example.ui.screens.CapybaraCustomizerScreen
import com.example.ui.screens.CapybaraShowcaseScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
  HOME,
  CUSTOMIZER,
  SHOWCASE
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CapybaraApp()
      }
    }
  }
}

@Composable
fun CapybaraApp() {
  var capyState by remember { mutableStateOf(CapybaraState()) }
  var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
      fadeIn() togetherWith fadeOut()
    },
    label = "screen_transition",
    modifier = Modifier.fillMaxSize()
  ) { screen ->
    when (screen) {
      AppScreen.HOME -> {
        HomeScreen(
          state = capyState,
          onStateChange = { capyState = it },
          onOpenCustomizer = { currentScreen = AppScreen.CUSTOMIZER }
        )
      }

      AppScreen.CUSTOMIZER -> {
        CapybaraCustomizerScreen(
          state = capyState,
          onStateChange = { capyState = it },
          onDone = { currentScreen = AppScreen.HOME }
        )
      }

      AppScreen.SHOWCASE -> {
        CapybaraShowcaseScreen(
          state = capyState,
          onEditAgain = { currentScreen = AppScreen.CUSTOMIZER },
          onResetNew = {
            capyState = CapybaraState()
            currentScreen = AppScreen.HOME
          }
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun CapybaraAppPreview() {
  MyApplicationTheme {
    CapybaraApp()
  }
}
