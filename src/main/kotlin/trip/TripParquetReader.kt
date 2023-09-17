package trip

import distance.AggregateDistanceStorage
import buildFromParquetSimpleGroup
import org.apache.hadoop.conf.Configuration
import org.apache.parquet.example.data.simple.SimpleGroup
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.io.ColumnIOFactory
import java.io.BufferedWriter
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import kotlin.io.path.absolutePathString

class TripParquetReader(private val tripDataDir: Path): Closeable {

    private val bufferedWriters = mutableMapOf<LocalDate, BufferedWriter>()

    fun parseParquetFileAndSaveTripData(parquetPath: Path) {
        val hadoopInputFile =
            HadoopInputFile.fromPath(org.apache.hadoop.fs.Path(parquetPath.absolutePathString()), Configuration())
        val reader = ParquetFileReader.open(hadoopInputFile)
        val schema = reader.footer.fileMetaData.schema
        val pages = reader.readNextRowGroup()
        val recordReader = ColumnIOFactory().getColumnIO(schema).getRecordReader(pages, GroupRecordConverter(schema))

        (0 until pages.rowCount).forEach { _ ->
            val record = buildFromParquetSimpleGroup(recordReader.read() as SimpleGroup)
            AggregateDistanceStorage.add(record)

            val startDate = record.startDate
            val tripDataFile = Paths.get(tripDataDir.toString(), startDate.toString())
            if (!Files.exists(tripDataFile)) Files.createFile(tripDataFile)

            val writer = bufferedWriters.getOrPut(startDate) {
                Files.newBufferedWriter(tripDataFile, StandardOpenOption.APPEND)
            }

            writer.write(record.toString())
            writer.newLine()
        }
        println("parse file: $parquetPath")
        reader.close()
    }

    override fun close() {
        bufferedWriters.values.forEach { writer -> writer.close() }
    }
}