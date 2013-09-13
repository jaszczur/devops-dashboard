case class ImageItem(title: Option[String], imagePath: String)

object Configuration {
  val rssUrls = Seq(
    "http://devopsreactions.tumblr.com/rss",
    "http://thecodinglove.com/rss",
    "http://itmemes.tumblr.com/rss")
	
  val maxItemsPerRss = 5
  
  val defaultDownloadFolder = "C:/Temp"
}

