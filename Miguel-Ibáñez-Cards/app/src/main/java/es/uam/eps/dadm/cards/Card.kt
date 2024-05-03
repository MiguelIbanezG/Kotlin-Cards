package es.uam.eps.dadm.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    var answer: String,
    var date: String = LocalDateTime.now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString(),
    var userId: String = UUID.randomUUID().toString()

) {
    var easiness: Double = 2.5
    var repetitions: Int = 0
    var interval: Long = 1
    var nextPresentationDate: String = date
    var quality: Int = 0
    var answered = false

    constructor() : this("","", "")

    open val type: String
        get() = "card"

    fun update(currentDate: LocalDateTime) {

        easiness = kotlin.math.max(1.3, easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))

        if (quality == 0) {
            repetitions = 0
        } else {
            repetitions++
        }

        interval = if (repetitions <= 1) {
            1
        }else if(repetitions == 2) {
            6
        }else{
            ceil(interval * easiness).toLong()
        }

        nextPresentationDate = currentDate.plusDays(interval).toString()
        
    }

    fun isDue(date: LocalDateTime) =
        LocalDateTime.parse(nextPresentationDate) <= date

    fun simulate(period: Long) {
        println("Simulación de la tarjeta $question:")
        var now = LocalDateTime.now()
        repeat(period.toInt()) {
            println("Fecha: ${now.toLocalDate()}")
            if(now.toString() == nextPresentationDate){
                print("  $question (INTRO para ver respuesta)")
                readlnOrNull()
                print("  $answer (Teclea 0, 3 o 5): ")
                quality = readlnOrNull()?.toIntOrNull() ?: 0
                update(now)
                println(details())
            }
            now = now.plusDays(1)
        }
    }

    open fun details(): String {
        return "  eas = %.2f rep = %d int = %d next = %s".format(easiness, repetitions, interval, nextPresentationDate)
    }

    override fun toString() = "$type | $question | $answer | $date | $id | $easiness | $repetitions | $interval | $nextPresentationDate"

    companion object {
        fun fromString(question: String, answer: String): Card {
            return Card(question, answer)
        }
    }
}
fun main() {
    //Ejemplo de Card ejecicio
    val question = "To wake up"
    val answer = "Despertarse"
    val card = Card(question, answer)
    card.simulate(12)
    println("Simulación completada.")
}
