package es.uam.eps.dadm.cards

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Decks : NavRoutes("decks")
    data object Cards : NavRoutes("cards")
    data object Study : NavRoutes("study")
    data object CardEditor : NavRoutes("editor")
    data object Login : NavRoutes("login")
    data object Statistics : NavRoutes("statistics")
    data object DeckEditor : NavRoutes("deckEditor")
    data object CardScaffold : NavRoutes("scaffold")

}