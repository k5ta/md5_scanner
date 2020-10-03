package scanner

import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {
    val appConfig = ConfigFactory.load().resolve()

    val daemon = new ScanningDaemon(appConfig)
    daemon.run()
  }
}
