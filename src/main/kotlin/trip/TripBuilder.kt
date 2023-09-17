import trip.Trip
import org.apache.parquet.example.data.simple.SimpleGroup
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

fun buildFromParquetSimpleGroup(simpleGroup: SimpleGroup): Trip {
    val start = getLocalDateTime(simpleGroup.getLong("tpep_pickup_datetime", 0))
    val end = getLocalDateTime(simpleGroup.getLong("tpep_dropoff_datetime", 0))
    val passengerCount = simpleGroup.getDoubleOfZero("passenger_count").toInt()
    val tripDistance = simpleGroup.getDouble("trip_distance", 0)
    return Trip(start, end, passengerCount, tripDistance)
}

fun buildFromLine(line: String): Trip {
    val strings = line.split(" ")
    check(strings.size == 4) { strings }
    val zoneId = ZoneId.systemDefault()
    val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(strings[0].toLong()), zoneId)
    val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(strings[1].toLong()), zoneId)
    val passengerCount = strings[2].toInt()
    val tripDistance = strings[3].toDouble()
    return Trip(startDateTime, endDateTime, passengerCount, tripDistance)
}

private fun getLocalDateTime(micros: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUnit.MICROSECONDS.toMillis(micros)), ZoneOffset.UTC)

private fun SimpleGroup.getDoubleOfZero(field: String): Double {
    return try {
        getDouble(field, 0)
    } catch (e: RuntimeException) {
        0.0
    }
}
