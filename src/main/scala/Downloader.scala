import java.nio.channels._
import java.io._
import java.net.URL

class Downloader(dirPath: String) {
  private val directory = new File(dirPath)
  if (!directory.isDirectory()) {
    directory.mkdir();
  }

  def download(url: URL) : File = {
    val file = new File(directory, fileName(url.getFile()))
    if (!file.exists()) {
      //println("Downloading " + url)
      val inputChannel = Channels.newChannel(url.openStream())
      val output = new FileOutputStream(file)
      output.getChannel().transferFrom(inputChannel, 0, Long.MaxValue)
    }
    file
  }

  private def fileName(path: String) : String  = {
    val lastSlash = path.lastIndexOf("/")
    path.substring(lastSlash)
  }
}