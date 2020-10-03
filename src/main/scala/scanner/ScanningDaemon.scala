package scanner

import com.typesafe.config.Config
import org.apache.logging.log4j.scala.Logging
import scanner.database.DatabaseConnector
import scanner.files.FilesHandler

import scala.jdk.CollectionConverters.CollectionHasAsScala

class ScanningDaemon(config: Config) extends Logging {

  private val dbConnector = new DatabaseConnector(config.getString("db.connection_url"))

  private val scannerConfig = config.getConfig("scanner")

  private val scanningDirectory = scannerConfig.getString("directory")
  private val scanningTimeoutSeconds = scannerConfig.getInt("timeout_seconds")

  private val filesHandler = new FilesHandler(
    extensions = scannerConfig.getStringList("extensions").asScala.toSet,
    maxThreads = scannerConfig.getInt("max_threads")
  )

  def run(): Unit = {
    while (true) {
      logger.info("Started directory scanning...")
      val existedFiles = dbConnector.getExistedFiles
      val newFiles = filesHandler.processDirectory(scanningDirectory, existedFiles)
      dbConnector.dumpData(newFiles)

      Thread.sleep(scanningTimeoutSeconds * 1000)
    }
  }
}
