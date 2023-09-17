import java.nio.file.Paths
import java.time.LocalDateTime


fun main() {

    val startDateTime = LocalDateTime.of(2020, 1, 1, 2, 0, 0)
    val endDateTime = LocalDateTime.of(2020, 2, 10, 5, 6, 0)

    val averageDistances = AverageDistancesImpl()
    averageDistances.init(Paths.get("/Users/agavrikov/dataDir"))
    println(averageDistances.getAverageDistances(startDateTime, startDateTime.plusHours(10)))
    println(averageDistances.getAverageDistances(startDateTime, startDateTime.plusDays(1)))
    println(averageDistances.getAverageDistances(startDateTime, startDateTime.plusDays(2)))
    println(averageDistances.getAverageDistances(startDateTime, endDateTime))
    averageDistances.close()
}
