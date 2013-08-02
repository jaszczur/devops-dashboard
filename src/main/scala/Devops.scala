
case class ImageItem(title: String, imagePath: String)

object Configuration {
  val rssUrls = Seq(
    "http://devopsreactions.tumblr.com/rss",
    "http://thecodinglove.com/rss")
	
  val maxItemsPerRss = 5
  
  val defaultDownloadFolder = "C:/Temp"
}



