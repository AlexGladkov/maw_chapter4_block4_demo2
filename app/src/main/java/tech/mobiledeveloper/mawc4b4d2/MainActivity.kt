package tech.mobiledeveloper.mawc4b4d2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.mobiledeveloper.mawc4b4d2.chart.ChartDefaults
import tech.mobiledeveloper.mawc4b4d2.chart.ChartItem
import tech.mobiledeveloper.mawc4b4d2.chart.ChartType
import tech.mobiledeveloper.mawc4b4d2.chart.ChartView
import tech.mobiledeveloper.mawc4b4d2.ui.theme.MAWC4B4D2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var chartType: ChartType by remember { mutableStateOf(ChartType.Line) }

            MAWC4B4D2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))

                        Row {
                            ClickButton(text = "Pie") {
                                chartType = ChartType.Pie
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            ClickButton(text = "Bar") {
                                chartType = ChartType.Bar
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            ClickButton(text = "Line") {
                                chartType = ChartType.Line
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val items = listOf(
                            ChartItem(0.10f, 0.010f),
                            ChartItem(0.20f, 0.020f),
                            ChartItem(0.50f, 0.070f),
                            ChartItem(0.70f, 0.050f),
                            ChartItem(1.00f, 0.070f),
                            ChartItem(1.10f, 0.050f),
                        )

                        ChartView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .padding(innerPadding),
                            chartType = chartType,
                            items = items,
                            canvasColors = ChartDefaults.canvasColors(
                            ),
                            axisSizes = ChartDefaults.axisSizes(
                                axisStokeWidth = 8f
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClickButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}