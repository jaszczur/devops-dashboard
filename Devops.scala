import java.nio.channels._
import java.io._
import java.net.URL
import scala.xml.XML
import scala.util.matching.Regex
import scala.math.{abs, min}
import java.util.Random


object Main {
  val random = new Random()

  def main(args: Array[String]) : Unit = {
    val directory = if (args.length >= 1) args(0) else "C:/Temp"
    val downloader = new Downloader(directory)
    val parser = new MultipleRssParser(downloader, Configuration.rssUrls)
    val item = randomItem(parser.download())
    val html = <html>
      <head>
        <title>Devops Reactions</title>
        <style type="text/css">
          body {{
            text-align: center;
            font-size: 20px;
            font-family: sans-serif;
            font-weight: bold;
          }}
        </style>
      </head>
      <body>
        <h1>{item.title}</h1>
        <p><img src={"file://" + item.imagePath}/></p>
      </body>
    </html>
    println(html.toString)
  }



  def randomItem[T](seq : Seq[T]) : T = {
    val index = (random.nextDouble * seq.length).floor.toInt
    //val r = random.nextGaussian()
    //val sd = 1
    //val mean = 1
    //val index = sd * r + mean

    seq(index)
  }
}



case class DevopsItem(title: String, imagePath: String)



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

trait Parser {
  def download() : Seq[DevopsItem]
}

class MultipleRssParser(downloader: Downloader, urls: Seq[String]) extends Parser {
  private val parsers = urls.map(url => new RssParser(downloader, url))

  override def download() = parsers.foldLeft(Seq[DevopsItem]()) {(result, parser) => 
    result ++ parser.download()
  }
}

class RssParser(downloader: Downloader, url: String) extends Parser {
  val rss = XML.load(new URL(url))
  val imageUrlPattern = ".* src=\"(.+?)\".*".r

  def parse() : Seq[DevopsItem] = {
    (rss \\ "item").take(Configuration.maxItemsPerRss) map {item =>
      val title = (item \ "title").text
      val description = (item \ "description").text.replaceAll("\n", "")
      val imageUrlPattern(imgSrc) = description
      DevopsItem(title, imgSrc)
    }
  }

  override def download() = {
    parse().map {item => 
      val file = downloader.download(new URL(item.imagePath))
      item.copy(imagePath = file.toString())
    }
  }
}

object Configuration {
  val rssUrls = Seq(
    "http://devopsreactions.tumblr.com/rss",
    "http://thecodinglove.com/rss")
  val maxItemsPerRss = 5
}



