import java.nio.channels._
import java.io._
import java.net.URL
import scala.xml.XML
import scala.util.matching.Regex
import scala.util.Random

object Main {

  def main(args: Array[String]) : Unit = {
    val directory = if (args.length >= 1) args(0) else "/tmp/devops"
    val downloader = new Downloader(directory)
    val parser = new DevopsParser(downloader)
    val item = randomItem(parser.download())
    val html = <html>
      <head>
        <title>Devops Reactions</title>
        <style type="text/css">
          body {{
            text-align: center;
            font-size: 25px;
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
    val index = (Random.nextDouble * seq.length).ceil.toInt - 1
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



