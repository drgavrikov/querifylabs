package distance

import trip.Trip
import java.time.LocalDate

object AggregateDistanceStorage {

    private val aggregateDistanceByDate = mutableMapOf<LocalDate, PassengerAggregateDistance>()

    fun add(row: Trip) {
        val startDate = row.startDate
        aggregateDistanceByDate.getOrPut(startDate) { PassengerAggregateDistance() }.add(row)
    }

    fun get(date: LocalDate) = aggregateDistanceByDate.getOrDefault(date, PassengerAggregateDistance())
}
