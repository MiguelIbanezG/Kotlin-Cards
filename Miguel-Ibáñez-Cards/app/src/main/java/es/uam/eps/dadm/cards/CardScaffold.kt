package es.uam.eps.dadm.cards

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScaffold(viewModel: CardViewModel, navController: NavController, deckId: String="", cardId: String="", currentRoute: String) {
    val cards by viewModel.cards.observeAsState()
    val decks by viewModel.decks.observeAsState()

    val context = LocalContext.current
    Scaffold(

        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                when (currentRoute) {
                    NavRoutes.Cards.route -> CardList(navController = navController, viewModel = viewModel, deckId = deckId)
                    NavRoutes.CardEditor.route -> CardEditor(navController, viewModel = viewModel, cardId = cardId, deckId = deckId)
                    NavRoutes.Decks.route -> DeckListScreen(viewModel = viewModel, navController)
                    NavRoutes.DeckEditor.route -> DeckEditor(navController, viewModel = viewModel, deckId = deckId)
                    NavRoutes.Statistics.route -> Statistics(navController = navController, viewModel = viewModel)
                    NavRoutes.Study.route -> Study(viewModel = viewModel, navController = navController)
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Cards",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .clickable {
                            context?.startActivity(Intent(context, SettingsActivity::class.java))
                        }
                        .padding(8.dp)
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black
            ),
            actions = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        modifier = Modifier
                            .clickable {
                                cards?.let { viewModel.uploadToFirebase(it) }
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Download from Firebase",
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        modifier = Modifier
                            .clickable {
                                viewModel.downloadFromFirebase()
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Download from Firebase",
                    )
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(NavRoutes.Login.route)
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Exit",
                    )
                }
            })
        },
        floatingActionButton = {
            if (currentRoute == NavRoutes.Decks.route || currentRoute == NavRoutes.Cards.route) {
                FloatingActionButton(
                    onClick = {
                        if (currentRoute == NavRoutes.Decks.route) {
                            val idDeck = "adding deck"
                            val routeDeck = NavRoutes.CardScaffold.route + "/${NavRoutes.DeckEditor.route}" + "/${idDeck}"
                            navController.navigate(routeDeck)
                        } else {
                            val id = "adding card"
                            val route = NavRoutes.CardScaffold.route + "/${NavRoutes.CardEditor.route}" + "/${id}" + "/${deckId}"
                            navController.navigate(route)
                        }
                    },
                    containerColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = context.getString(R.string.addCard),
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { CardBottomNavigationBar(navController = navController)
        }
    )
}
@Composable
fun CardBottomNavigationBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title,
                        tint = Color.Black
                    )
                },
                label = { Text(text = navItem.title, color = Color.Black) }
            )
        }
    }
}
