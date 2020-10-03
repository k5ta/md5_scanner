package scanner.files

import java.io.File

class DirectoryScanner(expectedExtensions: Set[String]) {
  private def isExpectedExtension(file: File): Boolean = {
    file.getName.split('.').lastOption.exists(expectedExtensions.contains)
  }

  def recursiveListFiles(filePath: String): Array[File] = {
    val file = new File(filePath)
    recursiveListFiles(file)
  }

  // TODO probably also add execution in multiple threads
  def recursiveListFiles(initialDirectory: File): Array[File] = {
    val allFiles = initialDirectory.listFiles

    allFiles.filter { file =>
      file.isFile && isExpectedExtension(file)
    } ++ allFiles.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}
