package es.uam.eps.dadm.cards

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow

object NavBarItems {
    lateinit var BarItems: List<BarItem>

    fun initialize(context: Context) {
        BarItems = listOf(
            BarItem(
                title = context.getString(R.string.decks),
                image = Icons.Filled.List,
                route = "decks"
            ),
            BarItem(
                title = context.getString(R.string.study2),
                image = Icons.Filled.PlayArrow,
                route = "study"
            ),
            BarItem(
                title = context.getString(R.string.Statistics),
                image = Icons.Filled.DateRange,
                route = "statistics"
            )
        )
    }
}