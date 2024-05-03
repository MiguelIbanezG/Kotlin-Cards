package es.uam.eps.dadm.cards

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScaffold(viewModel: CardViewModel, navController: NavController) {
    val cards by viewModel.cards.observeAsState()
    Scaffold(

        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                CardList(viewModel = viewModel, navController)
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
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Black
            ), actions = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        modifier = Modifier
                            .clickable {
                                cards?.let { viewModel.uploadToFirebase(it) }
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Settings",
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Agregamos un espacio entre los iconos
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
                    Spacer(modifier = Modifier.width(8.dp)) // Agregamos un espacio entre los iconos
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(NavRoutes.Login.route)
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Download from Firebase",
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavRoutes.CardEditor.route)
                },
                containerColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add card",
                    tint = Color.White
                )
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
