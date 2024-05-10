package es.uam.eps.dadm.cards

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.ui.theme.CardsTheme

class MainActivity : ComponentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference = database.getReference("message")
        reference.setValue("Hello from Cards")

        PreferenceManager.setDefaultValues(
            this,
            R.xml.root_preferences,
            false
        )

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {}
        })

        setContent {
            CardsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: CardViewModel = viewModel(
                            it,
                            "CardViewModel",
                            CardViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            )
                        )
                        MainScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen(viewModel: CardViewModel) {

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route
        ) {
            composable(NavRoutes.Home.route) {
                Home(navController, viewModel)
            }
            composable(NavRoutes.Login.route) {
                EmailPassword(navController, viewModel)
            }
            composable(NavRoutes.Statistics.route) {
                CardScaffold(viewModel, navController, currentRoute =  NavRoutes.Statistics.route)
            }
            composable(NavRoutes.Decks.route) {
                CardScaffold(viewModel, navController, currentRoute =  NavRoutes.Decks.route)
            }
            composable(NavRoutes.Study.route) {
                CardScaffold(viewModel, navController, currentRoute =  NavRoutes.Study.route)
            }
            composable(NavRoutes.Cards.route + "/{deckId}") { backEntry ->
                val deckId = backEntry.arguments?.getString("deckId")
                deckId?.let { deckId ->
                    CardScaffold(viewModel, navController, deckId, currentRoute =  NavRoutes.Cards.route)
                }
            }
            composable(NavRoutes.DeckEditor.route + "/{deckId}") { backEntry ->
                val deckId = backEntry.arguments?.getString("deckId")
                deckId?.let { deckId ->
                    CardScaffold(viewModel, navController, deckId, currentRoute =  NavRoutes.DeckEditor.route)
                }
            }
            composable(NavRoutes.CardEditor.route + "/{cardId}" + "/{deckId}") { backEntry ->
                val deckId = backEntry.arguments?.getString("deckId")
                val cardId = backEntry.arguments?.getString("cardId")

                deckId?.let { deckId ->
                    cardId?.let { cardId ->
                        CardScaffold(viewModel, navController, deckId = deckId, currentRoute =  NavRoutes.CardEditor.route, cardId = cardId)
                    }
                }
            }
            composable(NavRoutes.CardScaffold.route + "/{currentRoute}") { backEntry ->
                val currentRoute = backEntry.arguments?.getString("currentRoute")
                currentRoute?.let { currentRoute ->
                    CardScaffold(viewModel, navController, currentRoute =  currentRoute)
                }
            }

        }
    }


}


