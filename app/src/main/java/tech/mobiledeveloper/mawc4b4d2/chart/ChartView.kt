package tech.mobiledeveloper.mawc4b4d2.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

sealed class ChartType {
    data object Pie : ChartType()
    data object Bar : ChartType()
    data object Line : ChartType()
}

data class ChartItem(
    val label: String,
    val value: Float
)

/**
 * Composable, рисующий три типа графиков (Pie, Bar и Line).
 *
 * @param items Список данных для графика
 * @param chartType Тип графика (Pie, Bar, Line)
 * @param colors Цвета для графиков
 * @param modifier Modifier для вью
 */
@Composable
fun ChartView(
    items: List<ChartItem>,
    chartType: ChartType,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (items.isEmpty() || colors.isEmpty()) return@Canvas

        when (chartType) {
            is ChartType.Pie -> drawPieChart(items, colors)
            is ChartType.Bar -> drawBarChart(items, colors)
            is ChartType.Line -> drawLineChart(items, colors)
        }
    }
}

private fun DrawScope.drawPieChart(items: List<ChartItem>, colors: List<Color>) {
    val totalValue = items.sumOf { it.value.toDouble() }.toFloat()
    var startAngle = 0f
    val arcSize = Size(size.width, size.height)

    items.forEachIndexed { index, item ->
        val color = colors.getOrElse(index) { colors.last() } // Защита от выхода за границы списка цветов
        val sweepAngle = if (totalValue == 0f) 0f else (item.value / totalValue) * 360f
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset.Zero,
            size = arcSize
        )
        startAngle += sweepAngle
    }
}

fun DrawScope.drawBarChart(items: List<ChartItem>, colors: List<Color>) {
    val maxValue = items.maxOf { it.value }
    val barWidth = size.width / (items.size * 2)

    items.forEachIndexed { index, item ->
        val color = colors.getOrElse(index) { colors.last() }
        val barHeight = if (maxValue == 0f) 0f else (item.value / maxValue) * size.height
        val left = index * (barWidth + barWidth)
        val top = size.height - barHeight
        drawRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(barWidth, barHeight)
        )
    }
}

fun DrawScope.drawLineChart(items: List<ChartItem>, colors: List<Color>) {
    val maxValue = items.maxOf { it.value }
    val horizontalPadding = 40f
    val verticalPadding = 20f
    val availableWidth = size.width - horizontalPadding * 2
    val availableHeight = size.height - verticalPadding * 2

    val points = items.mapIndexed { index, item ->
        val x = horizontalPadding + index * (availableWidth / (items.size - 1))
        val yRatio = if (maxValue == 0f) 0f else (item.value / maxValue)
        val y = size.height - verticalPadding - (availableHeight * yRatio)
        Offset(x, y)
    }

    val path = Path().apply {
        points.forEachIndexed { i, point ->
            if (i == 0) moveTo(point.x, point.y)
            else lineTo(point.x, point.y)
        }
    }

    drawPath(
        path = path,
        color = colors.firstOrNull() ?: Color.Gray,
        style = Stroke(width = 4f)
    )

    points.forEachIndexed { index, point ->
        drawCircle(
            color = colors.getOrElse(index) { colors.last() },
            radius = 8f,
            center = point
        )
    }
}