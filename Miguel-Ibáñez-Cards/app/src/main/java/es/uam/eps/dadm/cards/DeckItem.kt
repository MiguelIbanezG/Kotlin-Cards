package es.uam.eps.dadm.cards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.UUID


@Composable
fun DeckItem(
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
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
        Column(verticalArrangement = Arrangement.Center) {
            val numberOfCardsInEnglishDeck = cards.filter { it.deckId == deck.deckId }.size
            ClickableText(
                text = buildAnnotatedString {
                    append("$numberOfCardsInEnglishDeck ")
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append(context.getString(R.string.cards))
                    }
                },
                onClick = {
                    navController.navigate(NavRoutes.Cards.route +"/${deck.deckId}")
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
            Icon(
                imageVector = Icons.Filled.Menu,
                tint = Color.Black,
                contentDescription = "List",
                modifier = Modifier
                    .clickable {
                        navController.navigate(NavRoutes.Cards.route +"/${deck.deckId}")
                    }
                    .padding(start = 20.dp).size(34.dp).fillMaxWidth()

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
    DeckList(cards = cards, decks = decks, navController, viewModel)
}

@Composable
fun DeckList(
    cards: List<Card>,
    decks: List<Deck>,
    navController: NavController,
    viewModel: CardViewModel
) {
    val context = LocalContext.current
    val onDeckClick = { deck: Deck ->
        val message = deck.name + " " + context.getString(R.string.selected)
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
        navController.navigate(NavRoutes.DeckEditor.route + "/${deck.deckId}")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = context.getString(R.string.list_decks),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black
            )
             Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.padding(vertical = 20.dp))
        }
        items(decks) { deck ->
            DeleteOrOpenDeck(navController, viewModel, deck, cards, onDeckClick)
            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteOrOpenDeck(navController: NavController, viewModel: CardViewModel, decks: Deck, cards:List<Card>, onItemClick: (Deck) -> Unit) {

    val state = rememberSwipeToDismissBoxState()

    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                viewModel.deleteCardsByIds(decks.deckId)
                viewModel.deleteDeckById(decks.deckId)
                navController.navigate(NavRoutes.Decks.route)
            }
            SwipeToDismissBoxValue.EndToStart -> {
                viewModel.deleteDeckById(decks.deckId)
                navController.navigate(NavRoutes.Decks.route)
            }
            else -> Unit
        }
    }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            val color = when (state.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> Color.Red
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }
            Box(Modifier.fillMaxSize().background(color)){
                if (state.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(end = 16.dp)
                            .size(36.dp)
                    )
                }
                if (state.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    ) {
        DeckItem(deck = decks, cards = cards, navController = navController, onItemClick = onItemClick)
    }
}

@Composable
fun DeckEditor(navController: NavController ,viewModel: CardViewModel, deckId: String) {
    if (deckId == "adding deck")
        InnerDeckEditor(
            navController = navController,
            viewModel = viewModel,
            deck = Deck( deckId = "adding deck", name = "", description = "", userId = viewModel.userId))
    else {
        val deck by viewModel.getDeck(deckId).observeAsState(null)
        deck?.let {
            InnerDeckEditor(
                navController = navController,
                viewModel = viewModel,
                deck = it)
        }
    }
}


@Composable
fun InnerDeckEditor(navController: NavController ,viewModel: CardViewModel, deck: Deck) {
    var name by remember { mutableStateOf(deck.name) }
    var description by remember { mutableStateOf(deck.description) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(110.dp))

        if(deck.deckId == "adding deck"){
            Text(stringResource(id = R.string.addDeck), modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black)
        }else{
            Text(stringResource(id = R.string.updateDeck),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black)
        }

        Spacer(modifier = Modifier.height(60.dp))
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
            val onAcceptClicked: () -> Unit = {
                deck.name = name
                deck.description = description
                if (deck.deckId == "adding deck") {
                    deck.deckId = UUID.randomUUID().toString()
                    viewModel.addDeck(deck)
                } else
                    viewModel.updateDeck(deck = deck)

                navController.navigate(NavRoutes.Decks.route){
                    popUpTo(NavRoutes.Home.route)
                }
            }
            Button(
                onClick = onAcceptClicked,
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
                    navController.navigate(NavRoutes.Decks.route)
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
            ) {
                Text(context.getString(R.string.cancel))
            }
        }
    }
}



