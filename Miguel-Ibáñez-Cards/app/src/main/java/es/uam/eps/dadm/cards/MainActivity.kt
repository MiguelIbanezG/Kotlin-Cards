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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.ui.theme.CardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference = database.getReference("message")
        reference.setValue("Hello from Cards")

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(
                    applicationContext,
                    snapshot.value.toString(),
                    Toast.LENGTH_SHORT).show()
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
                        // CardScaffold(viewModel = viewModel)
                        // Study(viewModel = viewModel)
                        // CardList(viewModel = viewModel)
                        // DeckList(viewModel = viewModel)

                        // DeckEditor(viewModel = viewModel, deck = Deck(name = "Espgañol", description = "aa"))

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
            composable(NavRoutes.Cards.route) {
                CardScaffold(viewModel = viewModel , navController)
            }
            composable(NavRoutes.Decks.route) {
                DeckListScreen(viewModel = viewModel, navController = navController)
            }
            composable(NavRoutes.Study.route) {
                Study(viewModel = viewModel, navController = navController)
            }
            composable(NavRoutes.CardEditor.route + "/{cardId}") { backEntry ->
                val id = backEntry.arguments?.getString("cardId")
                id?.let {
                    CardEditor(viewModel = viewModel, navController = navController, cardId = id)
                }
            }
            composable(NavRoutes.CardEditor.route + "/{cardId}" + "/{deckId}") { backEntry ->
                val cardId = backEntry.arguments?.getString("cardId")
                val deckId = backEntry.arguments?.getString("deckId")
                cardId?.let { cardId ->
                    deckId?.let {
                        CardEditor(navController, viewModel = viewModel, cardId = cardId, deckId = it)
                    }
                }
            }

        }
    }


}


