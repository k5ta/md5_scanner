package scanner.database

import scanner._
import slick.jdbc.PostgresProfile.api._
import ProcessedFilesTable._

class ProcessedFilesTable(tag: Tag) extends Table[ProcessedFileData](tag, FILES_TABLE_NAME) {
  def path = column[String](FILE_PATH_COLUMN, O.PrimaryKey)

  def hash = column[String](FILE_HASH_COLUMN)

  def timestamp = column[Long](TIMESTAMP_COLUMN)

  def * = (path, hash, timestamp) <> (ProcessedFileData.tupled, ProcessedFileData.unapply)
}

object ProcessedFilesTable {
  private val FILES_TABLE_NAME = "processed_files"

  private val FILE_PATH_COLUMN = "path"
  private val FILE_HASH_COLUMN = "hash"
  private val TIMESTAMP_COLUMN = "timestamp"
}
