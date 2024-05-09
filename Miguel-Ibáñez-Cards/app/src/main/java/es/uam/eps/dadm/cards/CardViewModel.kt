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

class CardViewModel(application: Application) : ViewModel() {
    var cards: LiveData<List<Card>>
    var decks: LiveData<List<Deck>>
    val review: LiveData<List<Review>>

    var dueCard: LiveData<Card?>
    var nDueCards: LiveData<Int>


    private val cardDao: CardDao
    var userId = Firebase.auth.currentUser?.uid
        ?: "unknown user"

    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao

        cards = cardDao.getCardsFromUser(userId)
        decks = cardDao.getDecksFromUser(userId)
        review = cardDao.getReviews()

        dueCard = cards.map {
            it.filter { card -> card.isDue(LocalDateTime.now()) }.run {
                if (any()) random() else null
            }
        }
        nDueCards = cards.map { cards -> cards.count { card -> card.isDue(LocalDateTime.now()) } }

    }

    fun refreshData() {
        userId = Firebase.auth.currentUser?.uid
            ?: "unknown user"
        cards = cardDao.getCardsFromUser(userId)
        decks = cardDao.getDecksFromUser(userId)
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

    private fun addReview(review: Review) = viewModelScope.launch {
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

    fun getCardsOfDeck(deckId: String) = cardDao.getCardsOfDeck(deckId)

    fun getDeckNameByDeckId(deckId: String) = cardDao.getDeckNameByDeckId(deckId)


}

class CardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}