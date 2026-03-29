package com.devlearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devlearn.ui.ExerciseScreen
import com.devlearn.ui.HomeScreen
import com.devlearn.ui.SettingsScreen
import com.devlearn.ui.theme.Background
import com.devlearn.ui.theme.DevLearnTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ExerciseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevLearnTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                ) {
                    composable("home") {
                        HomeScreen(navController = navController, viewModel = viewModel)
                    }
                    composable("exercise/{language}/{type}") { backStackEntry ->
                        val language = backStackEntry.arguments?.getString("language") ?: return@composable
                        val type = backStackEntry.arguments?.getString("type") ?: return@composable
                        ExerciseScreen(
                            navController = navController,
                            viewModel = viewModel,
                            languageName = language,
                            typeName = type
                        )
                    }
                    composable("settings") {
                        SettingsScreen(navController = navController, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
