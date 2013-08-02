
import java.util.Random
import scala.math.{abs, min}

object Main {
  val random = new Random()

  def main(args: Array[String]) : Unit = {
    val directory = if (args.length >= 1) args(0) else Configuration.defaultDownloadFolder
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
