package distance

import trip.Trip

data class PassengerAggregateDistance(
    private val distanceMap: MutableMap<Int, AggregateDistance> = mutableMapOf()
) {

    data class AggregateDistance(
        var tripDistanceSum: Double = 0.0,
        var count: Int = 0
    ) {
        fun add(tripDistance: Double) {
            this.tripDistanceSum += tripDistance
            count++
        }

        fun add(aggregateDistance: AggregateDistance) {
            this.tripDistanceSum += aggregateDistance.tripDistanceSum
            this.count += aggregateDistance.count
        }

        fun average() = if (count == 0) 0.0 else tripDistanceSum / count
    }

    fun add(trip: Trip) {
        val passengerCount = trip.passengerCount
        distanceMap.putIfAbsent(passengerCount, AggregateDistance())
        distanceMap.getValue(passengerCount).add(trip.tripDistance)
    }

    fun contains(passengerCount: Int) = distanceMap.containsKey(passengerCount)

    fun get(passengerCount: Int): AggregateDistance {
        return distanceMap.getOrDefault(passengerCount, AggregateDistance())
    }

    fun add(other: PassengerAggregateDistance) {
        val passengerCounts = distanceMap.keys + other.distanceMap.keys
        passengerCounts.forEach { passengerCount ->
            if (!contains(passengerCount)) distanceMap[passengerCount] = AggregateDistance()
            get(passengerCount).add(other.get(passengerCount))
        }
    }

    fun toMap() = distanceMap
        .entries
        .sortedBy { it.key }
        .associate { (passengerCount, aggregateDistance) -> passengerCount to aggregateDistance.average() }
}
