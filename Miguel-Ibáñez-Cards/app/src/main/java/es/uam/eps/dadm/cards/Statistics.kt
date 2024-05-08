package es.uam.eps.dadm.cards

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

fun fromReviewsToMap(reviews: List<Review>): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    reviews.forEach { review ->
        val date = extractDate(review.reviewDate)
        map[date] = map.getOrDefault(date, 0) + 1
    }
    return map
}

fun extractDate(dateTimeString: String): String {
    return dateTimeString.take(10)
}
@Composable
fun Statistics(viewModel: CardViewModel, navController: NavController) {
    val reviews by viewModel.review.observeAsState()
    val context = LocalContext.current

    reviews?.let { reviewList ->
        if (reviewList.isNotEmpty()) {
            val reviewMap = fromReviewsToMap(reviewList)
            BarchartWithSolidBars(reviewMap)
        } else {
            val message = context.getString(R.string.noStatistics)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            navController.navigate(NavRoutes.Home.route)
        }
    }
}
