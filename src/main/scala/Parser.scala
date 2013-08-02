import java.net.URL
import scala.xml.XML
import scala.util.matching.Regex

trait Parser {
  def download() : Seq[ImageItem]
}

class MultipleRssParser(downloader: Downloader, urls: Seq[String]) extends Parser {
  private val parsers = urls.map(url => new RssParser(downloader, url))

  override def download() = parsers.foldLeft(Seq[ImageItem]()) {(result, parser) => 
    result ++ parser.download()
  }
}

class RssParser(downloader: Downloader, url: String) extends Parser {
  val rss = XML.load(new URL(url))
  val imageUrlPattern = ".* src=\"(.+?)\".*".r

  def parse() : Seq[ImageItem] = {
    (rss \\ "item").take(Configuration.maxItemsPerRss) map {item =>
      val title = (item \ "title").text
      val description = (item \ "description").text.replaceAll("\n", "")
      val imageUrlPattern(imgSrc) = description
      ImageItem(title, imgSrc)
    }
  }

  override def download() = {
    parse().map {item => 
      val file = downloader.download(new URL(item.imagePath))
      item.copy(imagePath = file.toString())
    }
  }
}