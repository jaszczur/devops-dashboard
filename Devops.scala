import java.nio.channels._
import java.io._
import java.net.URL

object Main extends App {
  val downloader = new Downloader("/tmp/devops")
}


class Downloader(dirPath: String) {
  private val directory = new File(dirPath)
  if (!directory.isDirectory()) {
    directory.mkdir();
  }

  def download(url: URL) : File = {
    val file = new File(directory, url.getFile())
    val inputChannel = Channels.newChannel(url.openStream())
    val output = new FileOutputStream(file)
    output.getChannel().transferFrom(inputChannel, 0, Long.MaxValue)
    file
  }
}
