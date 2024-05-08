package es.uam.eps.dadm.cards

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.database.CardDao
import es.uam.eps.dadm.cards.database.CardDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class CardViewModel(application: Application) : ViewModel() {
    val cards: LiveData<List<Card>>
    val decks: LiveData<List<Deck>>
    val review: LiveData<List<Review>>

    val dueCard: LiveData<Card?>
    val nDueCards: LiveData<Int>


    private val cardDao: CardDao
    var userId = Firebase.auth.currentUser?.uid
        ?: "unknown user"


    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao

        deleteCards()
        deleteDecks()

        val english = Deck(name = "English", description = "Description of the English deck")
        addDeck(english)
        val french = Deck(name = "French", description = "Description of the French deck")
        addDeck(french)


        addCard(Card("To wake up", "Despertarse", deckId = english.deckId))
        addCard(Card("To slow down", "Ralentizar", deckId = english.deckId))
        addCard(Card("To give up", "Rendirse", deckId = english.deckId))
        addCard(Card("To come up", "Acercarse", deckId = english.deckId))
        addCard(Card("La voiture", "El coche", deckId = french.deckId))
        addCard(Card("Le chien", "El perro", deckId = french.deckId))

        cards = cardDao.getCards()
        decks = cardDao.getDecks()
        review = cardDao.getReviews()

        dueCard = cards.map {
            it.filter { card -> card.isDue(LocalDateTime.now()) }.run {
                if (any()) random() else null
            }
        }

        nDueCards = cards.map { cards -> cards.count { card -> card.isDue(LocalDateTime.now()) } }
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardDao.addCard(card)
    }

    fun addReview(review: Review) = viewModelScope.launch {
        cardDao.addReview(review)
    }

    private fun deleteCards() = viewModelScope.launch {
        cardDao.deleteCards()
    }

    private fun deleteDecks() = viewModelScope.launch {
        cardDao.deleteDecks()
    }

    fun getCard(cardId: String) = cardDao.getCard(cardId)

    fun getDeck(deckId: String) = cardDao.getDeck(deckId)

    fun deleteDeckById(deckId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cardDao.deleteDeckById(deckId)
            }
        }
    }

    fun deleteCardById(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cardDao.deleteCardById(id)
            }
        }
    }


    fun updateCard(card: Card) = viewModelScope.launch {
        cardDao.updateCard(card)
    }

    fun update(card: Card, quality: Int) {
        card.quality = quality
        card.update(LocalDateTime.now())
        updateCard(card)
        addReview(Review(deckId = card.deckId, cardId = card.id, userId = card.userId, reviewDate = card.date, nextReviewDate = card.nextPresentationDate, repetitions = card.repetitions))
    }

    fun updateDeck(deck: Deck) {
        viewModelScope.launch {
            cardDao.updateDeck(deck)
        }
    }

    fun uploadToFirebase(cards: List<Card>) {
        val reference = FirebaseDatabase.getInstance().getReference("cards")
        reference.setValue(null)
        cards.forEach { reference.child(it.id).setValue(it) }
    }
    fun downloadFromFirebase() {
        val reference = FirebaseDatabase.getInstance().getReference("cards")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = mutableListOf<Card>()
                snapshot.children.forEach { it ->
                    it.getValue(Card::class.java)?.let {
                        cards.add(it)
                    }
                }
                viewModelScope.launch {
                    cardDao.insertCards(cards)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun addDeck(deck: Deck) = viewModelScope.launch {
        cardDao.addDeck(deck)
    }

    fun getCardsByDeckName(deckName: String) = cardDao.getCardsByDeckName(deckName)

    fun getCardsOfDeck(deckId: String) = cardDao.getCardsOfDeck(deckId)

    fun getCardsAndDecks() = cardDao.getCardsAndDecks()

    fun getReviews() = cardDao.getReviews()

}

class CardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}