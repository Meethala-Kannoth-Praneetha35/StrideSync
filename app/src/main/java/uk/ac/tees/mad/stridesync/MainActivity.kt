package uk.ac.tees.mad.stridesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.stridesync.ui.AuthViewModel
import uk.ac.tees.mad.stridesync.ui.AuthenticationScreen
import uk.ac.tees.mad.stridesync.ui.HomeScreen
import uk.ac.tees.mad.stridesync.ui.ProfileScreen
import uk.ac.tees.mad.stridesync.ui.SplashScreen
import uk.ac.tees.mad.stridesync.ui.StepViewModel
import uk.ac.tees.mad.stridesync.ui.history.StepHistoryScreen
import uk.ac.tees.mad.stridesync.ui.theme.StrideSyncTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StrideSyncTheme {

                val authViewModel = hiltViewModel<AuthViewModel>()
                val navController = rememberNavController()
                val stepViewModel = hiltViewModel<StepViewModel>()
                NavHost(navController = navController, startDestination = "splash"){
                    composable("splash"){
                        SplashScreen(authViewModel,navController)
                    }
                    composable("auth"){
                        AuthenticationScreen(
                            authViewModel, navController
                        )
                    }
                    composable("home"){
                        HomeScreen(
                            viewModel = stepViewModel,
                            onHistoryClick = {
                                navController.navigate("history")
                            },
                        onProfileClick = {
                            navController.navigate("profile/$it")

                        },
                        onNotificationsClick = {})
                    }
                    composable("history") {
                        StepHistoryScreen(stepViewModel) {
                            navController.popBackStack()
                        }
                    }
                    composable("profile/{userName}") { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName")
                        ProfileScreen(userName, onSaveName = {}, onLogout = {})
                    }
                }
            }
        }
    }
}
