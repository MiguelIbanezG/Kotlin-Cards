package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "decks_table")
data class Deck(
    @PrimaryKey
    var deckId: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String,
    val userId: String = UUID.randomUUID().toString(),
)