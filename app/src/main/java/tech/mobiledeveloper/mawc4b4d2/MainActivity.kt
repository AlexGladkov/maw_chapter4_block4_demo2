package tech.mobiledeveloper.mawc4b4d2

import android.os.Bundle
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
            MAWC4B4D2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val items = listOf(
                        ChartItem(0.00010f, 0.00010f),
                        ChartItem(0.00020f, 0.00070f),
                        ChartItem(0.00050f, 0.00050f),
                        ChartItem(0.00070f, 0.00100f),
                        ChartItem(0.00100f, 0.00020f),
                        ChartItem(0.00110f, 0.00110f)
                    )

                    val chartColors = listOf(
                        Color(0xFFBB86FC), Color(0xFF3700B3)
                    )

                    var currentChartType: ChartType by remember { mutableStateOf(ChartType.Pie) }

                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                    ) {
                        // Кнопки переключения типа графика
                        Row(modifier = Modifier.padding(top = 64.dp).align(Alignment.TopCenter)) {
                            Button(onClick = { currentChartType = ChartType.Pie }) {
                                Text(text = "Pie")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { currentChartType = ChartType.Bar }) {
                                Text(text = "Bar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { currentChartType = ChartType.Line }) {
                                Text(text = "Line")
                            }
                        }

                        // Сам график
                        ChartView(
                            chartType = currentChartType,
                            items = items,
                            canvasColors = ChartDefaults.canvasColors(),
                            axisColors = ChartDefaults.axisColors(),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(450.dp)
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MAWC4B4D2Theme {
        Greeting("Android")
    }
}