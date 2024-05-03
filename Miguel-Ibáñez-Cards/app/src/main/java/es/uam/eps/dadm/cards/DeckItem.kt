package es.uam.eps.dadm.cards

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun DeckItem(
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable {
                onItemClick(deck) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = deck.name,
                style = TextStyle(fontWeight = FontWeight.Bold), fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 4.dp)

            )
            Text(
                text = deck.description,
            )
        }
        Column {
            val numberOfCardsInEnglishDeck = cards.filter { it.deckId == deck.deckId }.size
            Text(
                text = numberOfCardsInEnglishDeck.toString() + " " + context.getString(R.string.app_name),
            )
        }
    }


}

@Composable
fun DeckListScreen(
    viewModel: CardViewModel,
    navController: NavController
) {
    val cards by viewModel.cards.observeAsState(listOf())
    val decks by viewModel.decks.observeAsState(listOf())
    DeckList(cards = cards, decks = decks)
}

@Composable
fun DeckList(cards: List<Card>, decks: List<Deck>) {
    val context = LocalContext.current
    val onDeckClick = { deck: Deck ->
        val message = deck.name + " " + context.getString(R.string.selected)
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text( context.getString(R.string.list_decks),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(decks) {deck ->
            DeckItem(deck = deck, cards = cards, onItemClick = onDeckClick)
        }
    }
}

@Composable
fun DeckEditor(viewModel: CardViewModel, deck: Deck) {
    var name by remember { mutableStateOf(deck.name) }
    var description by remember { mutableStateOf(deck.description) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.deck_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(context.getString(R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val updatedDeck = Deck(deck.deckId, name, description)
                    //viewModel.updateDeck(updatedDeck)
                    viewModel.addDeck(updatedDeck)
                    val message = "${deck.name} " + context.getString(R.string.updated)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(Color.Black),
            ) {
                Text(context.getString(R.string.add_card_button_title))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val message = "${deck.name} " + context.getString(R.string.edit_cancel)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
            ) {
                Text(context.getString(R.string.cancel))
            }
        }
    }
}



