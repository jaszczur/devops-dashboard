import java.nio.channels._
import java.io._
import java.net.URL
import scala.xml.XML

object Main extends App {
  val downloader = new Downloader("/tmp/devops")
  val parser = new DevopsParser(downloader)
  for (item <- parser.parse()) println(item)
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

case class DevopsItem(title: String, imagePath: String)

class DevopsParser(downloader: Downloader) {
  val rss = XML.load(new URL(DevopsParser.RssUrl))

  def parse() : Seq[DevopsItem] = {
    rss \\ "item" map {item =>
      val title = (item \ "title").text
      DevopsItem(title, title)
    }
  }
}

object DevopsParser {
  private val RssUrl = "http://devopsreactions.tumblr.com/rss"
}

