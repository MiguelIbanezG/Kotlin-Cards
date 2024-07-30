@file:Suppress("KotlinDeprecation", "KotlinDeprecation", "KotlinDeprecation", "KotlinDeprecation")

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.UUID

//one try
@Composable
fun CardItem(
    card: Card,
    onItemClick : (Card) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp).
            clickable { onItemClick(card) },

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    )  {

        var switchState by remember { mutableStateOf(false) }
        val onSwitchChange = { it: Boolean -> switchState = it }

        CardData(
            card = card,
            switchState = switchState,
            onSwitchChange = onSwitchChange,
            modifier = modifier
        )
    }
}

@Composable
fun CardData(
    card: Card,
    switchState: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    Row {
        SwitchICon(switchState = switchState, onSwitchChange = onSwitchChange)
        Column {
            Text(card.question, modifier, style = TextStyle(fontWeight = FontWeight.Bold), fontSize = 20.sp)
            Text(card.answer, modifier)
            if (switchState) {

                Text(stringResource(id = R.string.quality) + ": " + card.quality.toString())
                Text(stringResource(id = R.string.easiness) + ": " + String.format("%.2f", card.easiness))
                Text(stringResource(id = R.string.repetitions) + ": " + card.repetitions.toString())
            }
        }
    }
    Column {
        Text(card.date.substring(0..9))

    }
}

@Composable
fun SwitchICon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val drawableResource = if (switchState) R.drawable.baseline_keyboard_double_arrow_up_24
    else R.drawable.baseline_keyboard_double_arrow_down_24

    Icon(
        painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier
            .clickable { onSwitchChange(!switchState) }
    )
}

@Composable
fun CardView(viewModel: CardViewModel, card: Card) {
    var answered by remember { mutableStateOf(false) }

    val onAnswered = { value: Boolean ->
        answered = value
    }

    LaunchedEffect(card) {
        answered = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (answered) {

            val question = card.question
            if (question != null) {
                Text(text = question,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            val answer = card.answer
            if (answer != null) {
                Text(text = answer,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            DifficultyButtons(onDifficultyChecked = { difficulty ->
                viewModel.update(card, difficulty)
            })

            }else{
                val question = card.question
                if (question != null) {
                    Text(text = question,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp)
                }
                ViewAnswerButton(onClick = { answered = true} ,onValueChange = onAnswered)
            }
        } 
}


@Composable
fun Study(viewModel: CardViewModel, navController: NavController) {
    val card by viewModel.dueCard.observeAsState()
    println(card)
    val ncard by viewModel.nDueCards.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Text(fontSize = 24.sp, text = stringResource(id = R.string.study), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.padding(horizontal = 60.dp, vertical = 5.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.cards_left) + " = $ncard",
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(90.dp))
        card?.let {
            CardView(viewModel = viewModel, it)
        } ?: run {
            val context = LocalContext.current
        }
    }
}

@Composable
fun ViewAnswerButton( onClick: () -> Unit, onValueChange: (Boolean) -> Unit) {
    Button(
        onClick = { onValueChange(true)
                    onClick() },
        modifier = Modifier
            .padding(vertical = 20.dp)
            .width(300.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(Color.Black),

    ) {
        Text(stringResource(id = R.string.view_answer))
    }
}

@Composable
fun CardList(viewModel: CardViewModel, navController: NavController, deckId: String) {
    val cards by viewModel.getCardsOfDeck(deckId).observeAsState(listOf())
    val deckName by viewModel.getDeckNameByDeckId(deckId).observeAsState("")
    val context = LocalContext.current

    val onItemClick = { card: Card ->
        val id = "adding card"
        navController.navigate(NavRoutes.CardEditor.route + "/${card.id}" + "/${card.deckId}")
    }

    val all by viewModel.getCardsOfDeck(deckId).observeAsState()
    all?.let {
        it.forEach { card ->
            println(card.question) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(stringResource(id = R.string.list_cards) + " " + deckName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black
            )
            Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.padding(vertical = 20.dp))
        }
        items(cards) { card ->
            DeleteOrOpenCards(navController, viewModel, card, deckId, onItemClick)
            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteOrOpenCards(navController: NavController, viewModel: CardViewModel, card: Card, deckId: String, onItemClick: (Card) -> Unit) {

    val state = rememberSwipeToDismissBoxState()

    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                viewModel.deleteCardById(card.id)
                navController.navigate(NavRoutes.Cards.route + "/${deckId}")
            }
            SwipeToDismissBoxValue.EndToStart -> {
                viewModel.deleteCardById(card.id)
                navController.navigate(NavRoutes.Cards.route + "/${deckId}")
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
        },

    ) {
        CardItem(card = card, onItemClick)
    }

}

@Composable
fun DifficultyButtons(
    onDifficultyChecked: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onDifficultyChecked(5) }, // Easy
            modifier = Modifier.padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(Color.Green),
        ) {
            Text(stringResource(id = R.string.easy))
        }
        Button(
            onClick = { onDifficultyChecked(3) }, // Medium
            modifier = Modifier.padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue),
        ) {
            Text(stringResource(id = R.string.medium))
        }
        Button(
            onClick = { onDifficultyChecked(0) }, // Hard
            modifier = Modifier.padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(Color.Red),
        ) {
            Text(stringResource(id = R.string.hard))
        }
    }
}

@Composable
fun CardEditor(
    navController: NavController,
    viewModel: CardViewModel,
    cardId: String = "",
    deckId: String = ""
) {
    if (cardId == "adding card")

        InnerCardEditor(
            navController = navController,
            viewModel = viewModel,
            card = Card("", "", id = "adding card", deckId = deckId, userId = viewModel.userId))
    else {
        val card by viewModel.getCard(cardId).observeAsState(null)
        card?.let {
            InnerCardEditor(
                navController = navController,
                viewModel = viewModel,
                card = it)
        }
    }
}

@Composable
fun InnerCardEditor(
    viewModel: CardViewModel,
    navController: NavController,
    card: Card
) {
    Column( modifier = Modifier.padding(16.dp)
    ) {
        var question by remember { mutableStateOf(card.question) }
        var answer by remember { mutableStateOf(card.answer) }
        val onQuestionChanged = { value: String -> question = value }
        val onAnswerChanged = { value: String -> answer = value }
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(110.dp))

        if(card.id == "adding card"){
            Text(stringResource(id = R.string.addCard),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black)
        }else{
            Text(stringResource(id = R.string.updateCard),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = Color.Black)
        }

        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChanged,
            label = { Text(stringResource(id = R.string.question)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChanged,
            label = { Text(stringResource(id = R.string.answer)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val onAcceptClicked: () -> Unit = {
                card.question = question
                card.answer = answer
                if (card.id == "adding card") {
                    card.id = UUID.randomUUID().toString()
                    viewModel.addCard(card)
                } else {
                    viewModel.updateCard(card)
                }
                navController.navigate(NavRoutes.Cards.route + "/${card.deckId}"){
                    popUpTo(NavRoutes.Home.route)
                }
            }
            Button(onClick = onAcceptClicked,
                   colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text(context.getString(R.string.add_card_button_title))
            }
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val message = "$question " + context.getString(R.string.cancel)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoutes.Cards.route + "/${card.deckId}") {
                        popUpTo(NavRoutes.Home.route)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
            ) {
                Text(context.getString(R.string.cancel))
            }
        }
    }
}


