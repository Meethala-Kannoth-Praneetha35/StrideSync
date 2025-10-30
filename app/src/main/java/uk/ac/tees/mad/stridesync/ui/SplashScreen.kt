package uk.ac.tees.mad.stridesync.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.stridesync.R
import uk.ac.tees.mad.stridesync.ui.theme.AppColors

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isTrue by remember {
        mutableStateOf(false)
    }
    // Navigate after 2 seconds
    LaunchedEffect(Unit) {
        delay(400)
        isTrue = true
        delay(2000)
        // Replace with login/home check later
//        navController.navigate("auth") {
//            popUpTo("splash") { inclusive = true }
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(isTrue,
                enter = scaleIn(tween(2000)) + expandVertically( animationSpec = tween(2000, easing = FastOutSlowInEasing))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "StrideSync Logo",
                    modifier = Modifier.size(120.dp)
                        .clip(CircleShape).shadow(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "StrideSync",
                color = AppColors.Surface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track. Compete. Achieve.",
                color = AppColors.Surface.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
