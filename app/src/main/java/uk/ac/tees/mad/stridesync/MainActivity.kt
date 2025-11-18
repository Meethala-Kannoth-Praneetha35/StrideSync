package uk.ac.tees.mad.stridesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import uk.ac.tees.mad.stridesync.ui.AuthViewModel
import uk.ac.tees.mad.stridesync.ui.AuthenticationScreen
import uk.ac.tees.mad.stridesync.ui.SplashScreen
import uk.ac.tees.mad.stridesync.ui.theme.AppColors
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
                NavHost(navController = navController, startDestination = "Auth"){
                    composable("splash"){
                        SplashScreen(navController)
                    }
                    composable("Auth"){
                        AuthenticationScreen(
                            authViewModel, navController
                        )
                    }
                }
            }
        }
    }
}
