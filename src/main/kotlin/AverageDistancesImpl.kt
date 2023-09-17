import AverageDistancesImpl.Companion.TRIP_DATA_DIRECTORY
import distance.AggregateDistanceStorage
import distance.PassengerAggregateDistance
import trip.TripParquetReader
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.Comparator
import kotlin.io.path.exists

class AverageDistancesImpl : AverageDistances {
    override fun init(dataDir: Path) {
        Files.createDirectories(TRIP_DATA_DIRECTORY)
        val tripParquetReader = TripParquetReader(TRIP_DATA_DIRECTORY)
        Files.walk(dataDir)
            .filter(Path::isParquetFile)
            .forEach { path -> tripParquetReader.parseParquetFileAndSaveTripData(path) }
        tripParquetReader.close()
    }

    override fun getAverageDistances(start: LocalDateTime, end: LocalDateTime): Map<Int, Double> {
        val startDate = start.toLocalDate()
        val endDate = end.toLocalDate()

        val passengerDistances = calcAverageDistances(getDatePath(startDate), start, end)
        var currentDate = startDate.plusDays(1)
        while (currentDate.isBefore(endDate)) {
            passengerDistances.add(AggregateDistanceStorage.get(currentDate))
            println("currentDate=$currentDate")
            currentDate = currentDate.plusDays(1)
        }

        if (endDate != startDate) passengerDistances.add(calcAverageDistances(getDatePath(endDate), start, end))
        return passengerDistances.toMap()
    }


    override fun close() {
        try {
            Files.walk(TRIP_DATA_DIRECTORY)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
        } catch (e: Exception) {
            System.err.println("Exception occurred while deleting the directory: ${e.message}")
        }
    }

    companion object {
        val TRIP_DATA_DIRECTORY = Paths.get("tripdata-${UUID.randomUUID()}")
    }
}

private fun calcAverageDistances(
    datePath: Path,
    start: LocalDateTime,
    end: LocalDateTime
): PassengerAggregateDistance {
    val passengerDistances = PassengerAggregateDistance()
    if (!datePath.exists()) return passengerDistances
    println("datePath=${datePath.fileName}")
    try {
        BufferedReader(FileReader(datePath.toFile())).use { reader ->
            var currentLine: String?
            while (reader.readLine().also { currentLine = it } != null) {
                val trip = buildFromLine(currentLine!!)
                if (start <= trip.startDateTime && trip.endDateTime <= end) passengerDistances.add(trip)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return passengerDistances
}

private fun getDatePath(date: LocalDate) = Paths.get(TRIP_DATA_DIRECTORY.toString(), date.toString())

private fun Path.isParquetFile(): Boolean = fileName.toString().endsWith(".parquet")
