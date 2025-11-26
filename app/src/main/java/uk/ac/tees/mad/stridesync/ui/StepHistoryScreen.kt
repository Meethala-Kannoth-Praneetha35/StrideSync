package uk.ac.tees.mad.stridesync.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.stridesync.data.local.StepEntity
import uk.ac.tees.mad.stridesync.ui.StepViewModel
import uk.ac.tees.mad.stridesync.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepHistoryScreen(
    stepViewModel: StepViewModel,
    onBackClick: () -> Unit,
) {
    val steps = stepViewModel.steps.collectAsState()

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Step History",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = AppColors.TextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (steps.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No history available",
                        fontSize = 18.sp,
                        color = AppColors.TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(steps.value) { step ->
                        StepHistoryCard(step)
                    }
                }
            }
        }
    }
}

@Composable
fun StepHistoryCard(step: StepEntity) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = try {
        outputFormat.format(inputFormat.parse(step.date)!!)
    } catch (e: Exception) {
        step.date
    }

    val distanceMeters = (step.steps * 0.762f)
    val distanceKm = distanceMeters / 1000f
    val kcal = step.steps * 0.05f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Primary,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "${step.steps}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = AppColors.TextPrimary
                        )
                    )
                    Text("steps", color = AppColors.TextSecondary, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        String.format("%.2f km", distanceKm),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppColors.TextPrimary
                        )
                    )
                    Text(
                        "${distanceMeters.toInt()} m",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                    Text(
                        String.format("%.1f kcal", kcal),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val progress = step.steps.toFloat() / 12000f
                    drawRoundRect(
                        color = Color.LightGray,
                        size = Size(width = size.width, height = size.height),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                    drawRoundRect(
                        color = AppColors.Primary,
                        size = Size(width = size.width * progress.coerceIn(0f, 1f), height = size.height),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                }

                Text(
                    text = "${step.steps} / 12000 steps 🚶‍➡️`",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
        }
    }
}
