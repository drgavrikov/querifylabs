package trip

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class Trip(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val passengerCount: Int,
    val tripDistance: Double
) {
    val startDate: LocalDate = startDateTime.toLocalDate()

    override fun toString(): String {
        val zoneId = ZoneId.systemDefault()
        val startMilli = startDateTime.atZone(zoneId).toInstant().toEpochMilli()
        val endMilli = endDateTime.atZone(zoneId).toInstant().toEpochMilli()
        return "$startMilli $endMilli $passengerCount $tripDistance"
    }
}
