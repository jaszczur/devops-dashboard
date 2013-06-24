import java.nio.channels._
import java.io._
import java.net.URL
import scala.xml.XML
import scala.util.matching.Regex
import scala.util.Random

object Main {

  def main(args: Array[String]) : Unit = {
    val downloader = new Downloader("/tmp/devops")
    val parser = new DevopsParser(downloader)
    for (item <- parser.download()) println(item)
  }

  def randomItem[T](seq : Seq[T]) : T = {
    val index = (Random.nextDouble * seq.length).ceil.toInt
    seq(index)
  }
}


class Downloader(dirPath: String) {
  private val directory = new File(dirPath)
  if (!directory.isDirectory()) {
    directory.mkdir();
  }

  def download(url: URL) : File = {
    val file = new File(directory, fileName(url.getFile()))
    if (!file.exists()) {
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

case class DevopsItem(title: String, imagePath: String)

class DevopsParser(downloader: Downloader) {
  val rss = XML.load(new URL(DevopsParser.RssUrl))
  val imageUrlPattern = ".* src=\"(.+gif)\".*".r

  def parse() : Seq[DevopsItem] = {
    rss \\ "item" map {item =>
      val title = (item \ "title").text
      val description = (item \ "description").text.replaceAll("\n", "")
      val imageUrlPattern(imgSrc) = description
      DevopsItem(title, imgSrc)
    }
  }

  def download() : Seq[DevopsItem] = {
    parse().map {item => 
      val file = downloader.download(new URL(item.imagePath))
      item.copy(imagePath = file.toString())
    }
  }
}

object DevopsParser {
  private val RssUrl = "http://devopsreactions.tumblr.com/rss"
}

