package tech.mobiledeveloper.mawc4b4d2.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ChartDefaults {

    data class AxisColors(
        val axisXColor: Color,
        val labelXColor: Color,
        val axisYColor: Color,
        val labelYColor: Color
    )

    data class CanvasColors(
        val backgroundColor: Color,
        val lineColor: Color,
        val gradientColors: List<Color>
    )

    data class AxisSizes(
        val labelXSize: Float,
        val labelYSize: Float,
        val axisXSteps: Int,
        val axisYSteps: Int,
        val axisStokeWidth: Float
    )

    data class ChartPaddings(
        val paddingStart: Dp,
        val paddingEnd: Dp,
        val paddingTop: Dp,
        val paddingBottom: Dp
    )

    @Composable
    fun axisColors(
        axisXColor: Color = Color.LightGray,
        labelXColor: Color = Color.Black,
        axisYColor: Color = Color.LightGray,
        labelYColor: Color = Color.Black
    ): AxisColors = AxisColors(
        axisXColor = axisXColor,
        labelXColor = labelXColor,
        axisYColor = axisYColor,
        labelYColor = labelYColor
    )

    @Composable
    fun canvasColors(
        backgroundColor: Color = Color.White,
        lineColor: Color = Color(0xFFBB86FC),
        gradientColors: List<Color> = listOf(Color(0xFFBB86FC), Color(0xFF3700B3))
    ): CanvasColors = CanvasColors(
        backgroundColor = backgroundColor,
        lineColor = lineColor,
        gradientColors = gradientColors
    )

    @Composable
    fun axisSizes(
        labelXSize: Float = 30f,
        labelYSize: Float = 30f,
        axisXSteps: Int = 5,
        axisYSteps: Int = 5,
        axisStokeWidth: Float = 2f
    ): AxisSizes = AxisSizes(
        labelXSize = labelXSize,
        labelYSize = labelYSize,
        axisXSteps = axisXSteps,
        axisYSteps = axisYSteps,
        axisStokeWidth = axisStokeWidth
    )

    @Composable
    fun paddings(
        paddingStart: Dp = 16.dp,
        paddingEnd: Dp = 16.dp,
        paddingTop: Dp = 16.dp,
        paddingBottom: Dp = 16.dp
    ): ChartPaddings = ChartPaddings(
        paddingStart = paddingStart,
        paddingEnd = paddingEnd,
        paddingTop = paddingTop,
        paddingBottom = paddingBottom
    )
}