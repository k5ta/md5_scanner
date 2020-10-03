package scanner.files

import java.io.{File, FileInputStream}
import java.security.{DigestInputStream, MessageDigest}

class FileHashCalculator {

  import FileHashCalculator._

  def computeHash(file: File): String = {
    val hashAlgorithm = MessageDigest.getInstance(HASH_ALGORITHM)
    val fileBuffer = new Array[Byte](BYTE_BUFFER_SIZE)

    val hashingInputStream = new DigestInputStream(new FileInputStream(file), hashAlgorithm)
    try {
      while (hashingInputStream.read(fileBuffer) != -1) {}
    } finally {
      hashingInputStream.close()
    }

    hashAlgorithm.digest.map("%02x".format(_)).mkString
  }
}

object FileHashCalculator {

  private val HASH_ALGORITHM = "MD5"

  private val BYTE_BUFFER_SIZE = 16 * 1024 // 16 Kb
}
