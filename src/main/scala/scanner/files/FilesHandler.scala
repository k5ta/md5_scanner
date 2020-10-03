package scanner.files

import scanner.ProcessedFileData

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport

class FilesHandler(extensions: Set[String], maxThreads: Int) {

  import FilesHandler._

  private lazy val directoryScanner = new DirectoryScanner(extensions)


  def processDirectory(path: String, existedFiles: Set[String]): Seq[ProcessedFileData] = {
    val allFiles = directoryScanner.recursiveListFiles(path)

    val newFiles = allFiles.filterNot(file => existedFiles.contains(file.getAbsolutePath)).par

    newFiles.tasksupport = new ForkJoinTaskSupport(new java.util.concurrent.ForkJoinPool(maxThreads))
    val processedFiles = newFiles.map { file =>
      ProcessedFileData(
        file.getAbsolutePath,
        hashCalculator.computeHash(file),
        System.currentTimeMillis()
      )
    }

    processedFiles.toList
  }
}

object FilesHandler {

  private val hashCalculator = new FileHashCalculator()
}
