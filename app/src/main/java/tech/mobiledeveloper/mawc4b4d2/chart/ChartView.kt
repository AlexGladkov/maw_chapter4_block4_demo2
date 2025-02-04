package tech.mobiledeveloper.mawc4b4d2.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

sealed class ChartType {
    data object Pie : ChartType()
    data object Bar : ChartType()
    data object Line : ChartType()
}

data class ChartItem(
    val valueX: Float,
    val valueY: Float
)

@Composable
fun ChartView(
    modifier: Modifier = Modifier,
    items: List<ChartItem>,
    chartType: ChartType,
    colors: List<Color> = listOf(Color(0xFFBB86FC), Color(0xFF3700B3)),
    lineColor: Color = Color(0xFFBB86FC),
    backgroundColor: Color = Color.White,
) {
    Canvas(modifier = modifier.background(backgroundColor)) {
        if (items.isEmpty() || colors.isEmpty()) return@Canvas

        when (chartType) {
            is ChartType.Pie -> drawPieChart(items, colors)
            is ChartType.Bar -> drawBarChart(items, colors)
            is ChartType.Line -> drawGradientLineChart(items, colors, lineColor)
        }
    }
}

private fun DrawScope.drawPieChart(items: List<ChartItem>, colors: List<Color>) {
    val totalValue = items.sumOf { it.valueY.toDouble() }.toFloat()
    var startAngle = 0f
    val arcSize = Size(size.width, size.height)

    items.forEachIndexed { index, item ->
        val color = colors.getOrElse(index) { colors.last() } // Защита от выхода за границы списка цветов
        val sweepAngle = if (totalValue == 0f) 0f else (item.valueY / totalValue) * 360f
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
    val maxValue = items.maxOf { it.valueY }
    val barWidth = size.width / (items.size * 2)

    items.forEachIndexed { index, item ->
        val color = colors.getOrElse(index) { colors.last() }
        val barHeight = if (maxValue == 0f) 0f else (item.valueY / maxValue) * size.height
        val left = index * (barWidth + barWidth)
        val top = size.height - barHeight
        drawRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(barWidth, barHeight)
        )
    }
}

private fun DrawScope.drawGradientLineChart(
    axisXPadding: Float = 40f,
    axisYPadding: Float = 40f,
    items: List<ChartItem>,
    gradientColors: List<Color>,
    lineColor: Color
) {

    // Максимальное и минимальное значение в данных по X
    val maxX = items.maxOf { it.valueX }
    val minX = items.minOf { it.valueX }

    // Максимальное и минимальное значение в данных по Y
    val maxY = items.maxOf { it.valueY }
    val minY = items.minOf { it.valueY }

    // Доступные размеры для графика
    val availableWidth = size.width - axisXPadding
    val availableHeight = size.height - axisYPadding

    // Позиции точек графика
    val points = items.map { item ->
        val xRatio = (item.valueX - minX) / (maxX - minX) // Нормализация X
        val x = axisXPadding + (xRatio * availableWidth)

        val yRatio = (item.valueY - minY) / (maxY - minY) // Нормализация Y
        val y = size.height - axisYPadding - (yRatio * availableHeight) // Инвертируем Y

        Offset(x, y)
    }

    // --- Построение сглаженного пути для линии ---
    val linePath = Path().apply {
        smoothCurveThrough(points) // Используем улучшенное сглаживание
    }

    // --- Построение заливки графика ---
    val fillPath = Path().apply {
        moveTo(points.first().x, size.height - paddingBottom) // Начало от нижней левой точки
        addPath(linePath) // Добавляем сглаженную линию
        lineTo(points.last().x, size.height - paddingBottom) // Завершаем нижней правой точкой
        close()
    }

    // Рисуем градиентную заливку (сверху тёмный, снизу прозрачный)
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                gradientColors.first().copy(alpha = 0.8f), // Тёмный цвет сверху
                gradientColors.last().copy(alpha = 0.0f)   // Прозрачный цвет снизу
            )
        ),
        style = Fill
    )

    // Рисуем сглаженную линию поверх заливки
    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(width = 4f)
    )

    // Рисуем точки графика
    points.forEach { point ->
        drawCircle(
            color = lineColor,
            radius = 8f,
            center = point
        )
    }

    // --- Рисуем оси координат ---
    drawLine(
        color = Color.Gray,
        start = Offset(paddingLeft, 0f),
        end = Offset(paddingLeft, size.height - paddingBottom),
        strokeWidth = 3f
    )
    drawLine(
        color = Color.Gray,
        start = Offset(paddingLeft, size.height - paddingBottom),
        end = Offset(size.width, size.height - paddingBottom),
        strokeWidth = 3f
    )

    // --- Подписи осей координат ---
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK // Подходит для светлого фона
        textSize = 30f
    }

    drawIntoCanvas { canvas ->
        // Подписи значений по оси Y
        val stepY = availableHeight / 5
        repeat(6) { i ->
            val value = minY + ((maxY - minY) / 5) * i
            val y = size.height - paddingBottom - (i * stepY)
            canvas.nativeCanvas.drawText(
                String.format("%.5f", value),
                10f,
                y,
                textPaint
            )
        }

        // Подписи значений по оси X
        items.forEach { item ->
            val xRatio = (item.valueX - minX) / (maxX - minX)
            val x = paddingLeft + (xRatio * availableWidth)
            canvas.nativeCanvas.drawText(
                String.format("%.5f", item.valueX),
                x - 20,
                size.height - 10,
                textPaint
            )
        }
    }
}

fun Path.smoothCurveThrough(points: List<Offset>) {
    if (points.size < 2) return

    moveTo(points.first().x, points.first().y)

    for (i in 1 until points.size) {
        val prev = points[i - 1]
        val curr = points[i]

        // Средняя точка между предыдущей и текущей
        val midPointX = (prev.x + curr.x) / 2
        val controlPoint1 = Offset(midPointX, prev.y)
        val controlPoint2 = Offset(midPointX, curr.y)

        // Добавляем сглаженную кривую между точками
        cubicTo(
            controlPoint1.x, controlPoint1.y,
            controlPoint2.x, controlPoint2.y,
            curr.x, curr.y
        )
    }
}