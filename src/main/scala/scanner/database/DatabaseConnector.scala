package scanner.database

import org.apache.logging.log4j.scala.Logging
import scanner.ProcessedFileData
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class DatabaseConnector(url: String) extends Logging {

  import DatabaseConnector._

  private val databaseInstance = Database.forURL(url, driver = POSTGRES_DRIVER)

  private lazy val files = TableQuery[ProcessedFilesTable]

  def getExistedFiles: Set[String] = {
    val query = files.map(p => p.path)
    val fut = databaseInstance.run(query.result)

    logger.info("Getting existed files from database")
    val alreadyExistedFiles = Await.result(fut, EXISTED_FILES_QUERY_TIME).toSet

    logger.info(s"DB already contains ${alreadyExistedFiles.size} entries")
    alreadyExistedFiles
  }

  def dumpData(newFiles: Seq[ProcessedFileData]): Unit = {
    val query = files ++= newFiles

    logger.info(s"Adding ${newFiles.length} entries to db")
    val fut = databaseInstance.run(query)

    fut.onComplete({
      case Success(numberOfEntries) =>
        numberOfEntries.foreach(num => logger.info(s"Successfully added $num entries to db"))

      case Failure(exception) =>
        logger.error(s"An error occured while added entries to db: ${exception.getMessage}")
    })
  }
}

object DatabaseConnector {

  private final val EXISTED_FILES_QUERY_TIME = 5.second

  private final val POSTGRES_DRIVER = "org.postgresql.Driver"
}
