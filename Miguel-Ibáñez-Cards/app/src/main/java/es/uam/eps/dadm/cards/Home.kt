package es.uam.eps.dadm.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Home(navController: NavController, viewModel: CardViewModel) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (viewModel.userId == "unknown user") {
                        navController.navigate(NavRoutes.Login.route)
                    } else
                        navController.navigate(NavRoutes.Cards.route)
                },
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text("CARDS")
            }
        }
    }
}
