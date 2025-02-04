package tech.mobiledeveloper.mawc4b4d2.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

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
    canvasColors: CanvasColors = ChartDefaults.canvasColors(),
    axisColors: AxisColors = ChartDefaults.axisColors(),
    axisSizes: AxisSizes = ChartDefaults.axisSizes(),
    paddings: ChartPaddings = ChartDefaults.paddings()
) {
    when (chartType) {
        is ChartType.Pie -> drawPieChart(items, canvasColors)
        is ChartType.Bar -> drawBarChart(items, canvasColors)
        is ChartType.Line -> drawGradientLineChart(
            modifier,
            items,
            canvasColors,
            axisColors,
            axisSizes,
            paddings
        )
    }
}

@Composable
private fun drawPieChart(
    items: List<ChartItem>,
    canvasColors: CanvasColors = ChartDefaults.canvasColors(),
) {
    Canvas(modifier = Modifier.background(canvasColors.backgroundColor)) {
        val totalValue = items.sumOf { it.valueY.toDouble() }.toFloat()
        var startAngle = 0f
        val arcSize = Size(size.width, size.height)

        items.forEachIndexed { index, item ->
            val color = canvasColors.gradientColors.first()
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
}

@Composable
private fun drawBarChart(items: List<ChartItem>, canvasColors: CanvasColors) {
    Canvas(modifier = Modifier.background(canvasColors.backgroundColor)) {
        val maxValue = items.maxOf { it.valueY }
        val barWidth = size.width / (items.size * 2)

        items.forEachIndexed { index, item ->
            val color = canvasColors.gradientColors.first()
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
}

@Composable
private fun drawGradientLineChart(
    modifier: Modifier = Modifier,
    items: List<ChartItem>,
    canvasColors: CanvasColors,
    axisColors: AxisColors,
    axisSizes: AxisSizes,
    paddings: ChartPaddings
) {
    Canvas(
        modifier = modifier
            .background(canvasColors.backgroundColor)
    ) {
        // Минимальные и максимальные значения
        val maxX = items.maxOf { it.valueX }
        val minX = items.minOf { it.valueX }
        val maxY = items.maxOf { it.valueY }
        val minY = items.minOf { it.valueY }

        // Доступные размеры графика
        val graphWidth = size.width - paddings.paddingStart.value
        val graphHeight = size.height - paddings.paddingBottom.value - paddings.paddingTop.value

        // Масштабирование данных
        val points = items.map { item ->
            val xRatio = (item.valueX - minX) / (maxX - minX)
            val x = paddings.paddingStart.value + xRatio * graphWidth

            val yRatio = (item.valueY - minY) / (maxY - minY)
            val y = size.height - paddings.paddingBottom.value - yRatio * graphHeight

            Offset(x, y)
        }

        // --- Построение сглаженного пути ---
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val prevPoint = points[i - 1]
                val currentPoint = points[i]
                val controlPoint1X = (prevPoint.x + currentPoint.x) / 2
                val controlPoint1Y = prevPoint.y
                val controlPoint2X = (prevPoint.x + currentPoint.x) / 2
                val controlPoint2Y = currentPoint.y

                cubicTo(
                    controlPoint1X,
                    controlPoint1Y,
                    controlPoint2X,
                    controlPoint2Y,
                    currentPoint.x,
                    currentPoint.y
                )
            }
        }

        // --- Построение заливки ---
        val gradientPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, size.height - paddings.paddingBottom.value) // Замыкаем до нижней границы
            lineTo(points.first().x, size.height - paddings.paddingBottom.value)
            close()
        }

        // Градиент
        drawPath(
            path = gradientPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    canvasColors.gradientColors.first().copy(alpha = 0.8f), // Тёмный сверху
                    canvasColors.gradientColors.last().copy(alpha = 0.0f)   // Прозрачный снизу
                ),
                startY = paddings.paddingTop.value,
                endY = size.height - paddings.paddingBottom.value
            ),
            style = Fill
        )

        // Рисуем линию графика
        drawPath(
            path = linePath,
            color = canvasColors.lineColor,
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        // Рисуем точки графика
        points.forEach { point ->
            drawCircle(
                color = canvasColors.lineColor,
                radius = 8f,
                center = point
            )
        }

        // --- Оси координат ---
        drawLine(
            color = axisColors.axisXColor,
            start = Offset(paddings.paddingStart.value, paddings.paddingTop.value),
            end = Offset(paddings.paddingStart.value, size.height - paddings.paddingBottom.value),
            strokeWidth = 2f
        )
        drawLine(
            color = axisColors.axisYColor,
            start = Offset(paddings.paddingStart.value, size.height - paddings.paddingBottom.value),
            end = Offset(size.width, size.height - paddings.paddingBottom.value),
            strokeWidth = 2f
        )

        // --- Метки осей ---
        val textXPaint = android.graphics.Paint().apply {
            color = axisColors.labelXColor.toArgb()
            textSize = axisSizes.labelXSize
            textAlign = android.graphics.Paint.Align.CENTER
        }

        val textYPaint = android.graphics.Paint().apply {
            color = axisColors.labelYColor.toArgb()
            textSize = axisSizes.labelYSize
            textAlign = android.graphics.Paint.Align.CENTER
        }

        drawIntoCanvas { canvas ->
            // Метки оси Y
            val stepY = graphHeight / axisSizes.axisYSteps
            for (i in 0..axisSizes.axisYSteps) {
                val valueY = minY + (maxY - minY) / axisSizes.axisYSteps * i
                val y = size.height - paddings.paddingBottom.value - i * stepY
                canvas.nativeCanvas.drawText(
                    String.format("%.2f", valueY),
                    paddings.paddingStart.value,
                    y,
                    textYPaint
                )
            }

            val stepX = graphHeight / axisSizes.axisXSteps
            for (i in 0..axisSizes.axisXSteps) {
                val valueX = minX + (maxX - minX) / axisSizes.axisXSteps * i
                val x = size.width - paddings.paddingStart.value - i * stepX
                canvas.nativeCanvas.drawText(
                    String.format("%.2f", valueX),
                    x,
                    size.height,
                    textXPaint
                )
            }
        }
    }
}