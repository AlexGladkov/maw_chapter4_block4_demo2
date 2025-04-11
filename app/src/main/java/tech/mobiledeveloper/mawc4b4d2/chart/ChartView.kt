package tech.mobiledeveloper.mawc4b4d2.chart

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlin.math.min

sealed class ChartType {
    data object Pie : ChartType()
    data object Bar : ChartType()
    data object Line : ChartType()
}

data class ChartItem(
    val pointX: Float,
    val pointY: Float
)

@Composable
fun ChartView(
    modifier: Modifier = Modifier,
    chartType: ChartType,
    items: List<ChartItem>,
    canvasColors: ChartDefaults.CanvasColors = ChartDefaults.canvasColors(),
    axisColors: ChartDefaults.AxisColors = ChartDefaults.axisColors(),
    axisSizes: ChartDefaults.AxisSizes = ChartDefaults.axisSizes(),
    paddings: ChartDefaults.ChartPaddings = ChartDefaults.paddings()
) {
    when (chartType) {
        ChartType.Bar -> drawBarChart(items, canvasColors.gradientColors)
        ChartType.Pie -> drawPieChart(items, canvasColors.gradientColors)
        ChartType.Line -> drawLineChart(
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
    colors: List<Color>
) {
    Canvas(modifier = Modifier) {
        val totalValue = items.sumOf { it.pointY.toDouble() }.toFloat()
        var startAngle = 0f
        val minSize = min(size.width, size.height)
        val arcSize = Size(minSize, minSize)

        items.forEachIndexed { index, item ->
            val color = colors[index % colors.size]
            val sweepAngle = if (totalValue == 0f) 0f else (item.pointY / totalValue) * 360
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(x = (size.width - minSize) / 2, y = 0f),
                size = arcSize
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
private fun drawBarChart(
    items: List<ChartItem>,
    colors: List<Color>
) {
    Canvas(modifier = Modifier) {
        val maxValue = items.maxOf { it.pointY }
        val barWidth = size.width / (items.size * 2)

        items.forEachIndexed { index, item ->
            val color = colors[index % colors.size]
            val barHeight = if (maxValue == 0f) 0f else (item.pointY / maxValue) * size.height
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
private fun drawLineChart(
    modifier: Modifier = Modifier,
    items: List<ChartItem>,
    canvasColors: ChartDefaults.CanvasColors,
    axisColors: ChartDefaults.AxisColors,
    axisSizes: ChartDefaults.AxisSizes,
    paddings: ChartDefaults.ChartPaddings
) {
    Canvas(
        modifier = modifier
            .background(canvasColors.backgroundColor)
    ) {
        val maxX = items.maxOf { it.pointX }
        val minX = items.minOf { it.pointX }
        val maxY = items.maxOf { it.pointY }
        val minY = items.minOf { it.pointY }

        val graphWidth = size.width - paddings.paddingStart.value - paddings.paddingEnd.value
        val graphHeight = size.height - paddings.paddingBottom.value - paddings.paddingTop.value

        val points = items.map { item ->
            val xRatio = item.pointX / maxX
            val x = paddings.paddingStart.value + xRatio * graphWidth

            val yRatio = item.pointY / maxY
            val y = size.height - paddings.paddingBottom.value - yRatio * graphHeight

            Offset(x, y)
        }

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

        val gradientPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, size.height - paddings.paddingBottom.value)
            lineTo(points.first().x, size.height - paddings.paddingBottom.value)
            close()
        }

        drawPath(
            path = gradientPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    canvasColors.gradientColors.first().copy(alpha = 0.8f),
                    canvasColors.gradientColors.last().copy(alpha = 0.0f)
                ),
                startY = paddings.paddingTop.value,
                endY = size.height - paddings.paddingBottom.value
            ),
            style = Fill
        )

        drawPath(
            path = linePath,
            color = canvasColors.lineColor,
            style = Stroke(width = 8.dp.value, cap = StrokeCap.Round)
        )

        drawLine(
            color = axisColors.axisYColor,
            start = Offset(paddings.paddingStart.value, paddings.paddingTop.value),
            end = Offset(paddings.paddingStart.value, size.height - paddings.paddingBottom.value),
            strokeWidth = axisSizes.axisStokeWidth
        )

        drawLine(
            color = axisColors.axisXColor,
            start = Offset(paddings.paddingStart.value, size.height - paddings.paddingBottom.value),
            end = Offset(size.width, size.height - paddings.paddingBottom.value),
            strokeWidth = axisSizes.axisStokeWidth
        )

        val textXPaint = Paint().apply {
            color = axisColors.labelXColor.toArgb()
            textSize = axisSizes.labelXSize
            textAlign = Paint.Align.CENTER
        }

        val textYPaint = Paint().apply {
            color = axisColors.labelYColor.toArgb()
            textSize = axisSizes.labelXSize
            textAlign = Paint.Align.CENTER
        }

        drawIntoCanvas { canvas ->
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

            val stepX = graphWidth / axisSizes.axisXSteps
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