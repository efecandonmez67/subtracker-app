package com.efecandonmez.subtracker.app.ui.subscriptions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.efecandonmez.subtracker.app.data.model.CategorySummary

private val DonutColors = listOf(
    Color(0xFF7C3AED),
    Color(0xFFFF8A5B),
    Color(0xFFC4A6FF),
    Color(0xFF34D399),
    Color(0xFFF87171),
    Color(0xFF60A5FA),
    Color(0xFFFBBF24),
    Color(0xFFA78BFA)
)

@Composable
fun DonutChart(
    data: List<CategorySummary>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val total = data.sumOf { it.monthlyTotal }
    if (total <= 0.0) return

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 28.dp.toPx()
            var startAngle = -90f

            data.forEachIndexed { index, item ->
                val sweep = (item.monthlyTotal / total * 360.0).toFloat()
                drawArc(
                    color = DonutColors[index % DonutColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
                )
                startAngle += sweep
            }
        }

        Spacer(Modifier.width(16.dp))

        Column {
            data.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(DonutColors[index % DonutColors.size], androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${item.category} · %.0f".format(item.monthlyTotal),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}