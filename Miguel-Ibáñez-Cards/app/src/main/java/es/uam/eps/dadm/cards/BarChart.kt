package es.uam.eps.dadm.cards

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.models.BarData
import java.util.Locale


@Composable
fun BarchartWithSolidBars(reviewMap: Map<String, Int>) {
    // Extracting data from the reviewMap
    val labels = reviewMap.keys.toList()
    val values = reviewMap.values.map { it.toFloat() }

    val barData = labels.mapIndexed { index, label ->
        BarData(point = Point(x = index.toFloat(), y = values[index]), label = label)
    }

    val yStepSize = 10

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barData.size - 1)
        .bottomPadding(40.dp)
        .axisLabelAngle(20f)
        .startDrawPadding(48.dp)
        .labelData { index -> barData[index].label }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index ->
            String.format("%.1f", index * (values.maxOrNull()!! / yStepSize))
        }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp,
            barWidth = 25.dp
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 10.dp,
    )
    BarChart(modifier = Modifier.fillMaxWidth(), barChartData = barChartData)
}