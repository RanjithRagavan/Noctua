package com.noctua.example.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noctua.example.ui.theme.AlertRed
import com.noctua.example.ui.theme.MintGreen
import com.noctua.example.ui.theme.WarmAmber

@Composable
fun DashboardScreen(state: NoctuaUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.demoMode) {
            Text(
                "Demo mode — connect your Oura account in the Connect tab",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        ScoreRingRow(state)

        state.report?.forecastedReadiness?.let { forecast ->
            ForecastCard(forecast, state.snapshot.latestReadiness?.score, state.forecasterLabel)
        }

        ReadinessTrendCard(state)

        MetricDetailsCard(state)
    }
}

@Composable
private fun ScoreRingRow(state: NoctuaUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ScoreRing("Readiness", state.snapshot.latestReadiness?.score, MintGreen)
        ScoreRing("Sleep", state.snapshot.latestSleep?.score, MaterialTheme.colorScheme.primary)
        ScoreRing("Activity", state.snapshot.latestActivity?.score, WarmAmber)
    }
}

@Composable
fun ScoreRing(label: String, score: Int?, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(96.dp)) {
                val stroke = 10.dp.toPx()
                val inset = stroke / 2
                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                if (score != null) {
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * (score / 100f),
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
            }
            Text(
                text = score?.toString() ?: "–",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ForecastCard(forecast: Int, today: Int?, engineLabel: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Tomorrow's readiness forecast",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$forecast", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.size(8.dp))
                val delta = today?.let { forecast - it }
                if (delta != null) {
                    val arrow = if (delta >= 0) "▲" else "▼"
                    val color = if (delta >= 0) MintGreen else AlertRed
                    Text(
                        "$arrow ${kotlin.math.abs(delta)} vs today",
                        color = color,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
            Text(
                "Predicted on-device by Noctua AI ($engineLabel) — your data never left this phone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun ReadinessTrendCard(state: NoctuaUiState) {
    val scores = state.snapshot.readiness
        .sortedBy { it.day }
        .mapNotNull { it.score }
        .takeLast(14)
    if (scores.size < 2) return

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Readiness — last ${scores.size} days", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            val lineColor = MaterialTheme.colorScheme.primary
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
            ) {
                val min = (scores.min() - 5).coerceAtLeast(0)
                val max = (scores.max() + 5).coerceAtMost(100)
                val range = (max - min).coerceAtLeast(1)
                val stepX = size.width / (scores.size - 1)

                val path = Path()
                scores.forEachIndexed { i, score ->
                    val x = i * stepX
                    val y = size.height * (1f - (score - min) / range.toFloat())
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                scores.forEachIndexed { i, score ->
                    val x = i * stepX
                    val y = size.height * (1f - (score - min) / range.toFloat())
                    drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(x, y))
                }
            }
        }
    }
}

@Composable
private fun MetricDetailsCard(state: NoctuaUiState) {
    val period = state.snapshot.sleepPeriods.maxByOrNull { it.day.orEmpty() }
    val activity = state.snapshot.latestActivity
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Last night", style = MaterialTheme.typography.titleSmall)
            MetricRow("Total sleep", period?.totalSleepDuration?.let { "%.1f h".format(it / 3600f) })
            MetricRow("Efficiency", period?.efficiency?.let { "$it %" })
            MetricRow("Average HRV", period?.averageHrv?.let { "$it ms" })
            MetricRow("Lowest HR", period?.lowestHeartRate?.let { "$it bpm" })
            MetricRow("Steps", activity?.steps?.toString())
            MetricRow("Active calories", activity?.activeCalories?.let { "$it kcal" })
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value ?: "–", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
