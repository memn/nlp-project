package hacettepe.nlp.project.crawler

import com.google.gson.JsonParser
import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import hacettepe.nlp.project.model.Cast
import hacettepe.nlp.project.model.Movie
import hacettepe.nlp.project.model.Post
import hacettepe.nlp.project.model.User
import hacettepe.nlp.project.repositories.Repository
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.regex.Pattern


/**
 *
 * @author Memn
 * @date 17.03.2018
 *
 */

class BeyazPerdeCrawler : WebCrawler() {

    companion object {
        private val logger = LogManager.getLogger(BeyazPerdeCrawler::class)
    }

    var FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$")
    var PAGE_FILTERS = Pattern.compile(".*/(fragman|box-office|fotolar|haberler|kullanici-puani/tur-|tum-filmleri|enteresan-bilgiler|seanslar|vizyondakiler|en-merak-edilenler|benzer|basin-elestrileri|elestiriler-beyazperde).*$")
    val BASE_URL = "http://www.beyazperde.com/filmler/"


    //we will want to print in standard "System.out" and in "file"
//    init {
//        System.setOut(PrintStream(TeeOutputStream(System.out, FileOutputStream(File("/data/log.txt"))), true))
//    }

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    override fun shouldVisit(referringPage: Page, url: WebURL): Boolean {
        val href = url.url.toLowerCase()
        return !FILTERS.matcher(href).matches() &&
                !PAGE_FILTERS.matcher(href).matches() &&
                href.startsWith(BASE_URL)
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    override fun visit(page: Page) {
        val url = page.webURL.url
        logger.debug("URL: $url")

        val parseData = page.parseData
        if (parseData is HtmlParseData) {

            val parameters = url.substring(BASE_URL.length).trim().trim('/').split('/')
            assert(parameters.isNotEmpty())
            val id = parameters[0].split('-').getOrNull(1)!!
            val document = Jsoup.parse(parseData.html)
            if (parameters.size == 1) {
                val title = parseData.metaTags["og:title"]
                val director = parseData.metaTags["video:director"]
                val description = parseData.metaTags["og:description"]
                val country = parseData.metaTags["geo.country"]
                scrapMovie(id, document, title, director, description, country)
            } else {
                if (parameters[1] == "oyuncular") {
                    scrapCast(id, document)
                } else if (parameters[1] == "kullanici-elestirileri") {
                    // allows pagination if crawler traverses
                    scrapPosts(id, document)
                }
            }

        }

    }

    private fun scrapPosts(id: String, document: Document?) {
        val reviewElements = document!!.getElementsByAttributeValueContaining("itemprop", "review")
        val posts = HashSet<Post>()
        reviewElements.forEach {
            try {
                val userId = scrapUser(it)
                val post = scrapReview(it, userId)
                posts.add(post)
            } catch (e: Exception) {
                // ignored
            }
        }
        Repository.instance.addAll(id, posts)
    }

    private fun scrapReview(it: Element, userId: String): Post {
        val reviewRating = it.getElementsByAttributeValueContaining("itemprop", "reviewRating").first()
        val ratingValue = it.getElementsByAttributeValueContaining("itemprop", "ratingValue").text().replace(',', '.').toDouble()
        val description = reviewRating.getElementsByAttributeValueContaining("itemprop", "description").text()
        val usefulnessJson = JsonParser().parse(reviewRating.getElementsByAttributeValueContaining("class",
                "reviews-users-comment-useful").attr("data-statistics")).asJsonObject
        val upvote = usefulnessJson["helpfulCount"].asInt
        val downvote = usefulnessJson["unhelpfulCount"].asInt
        return Post(userId, description, ratingValue, upvote, downvote)
//        Repository.instance.add(id, post)
    }

    private fun scrapUser(it: Element): String {
        val userElement = it.getElementsByAttributeValueContaining("class", "card reviews-user-infos cf").first()
        val name = userElement.getElementsByAttributeValueContaining("itemprop", "author").text()
        val userId = userElement.getElementsByAttributeValueContaining("class", "item-profil").first().attr("data-targetuserid")
        val user = User(userId, name)
        Repository.instance.add(userId, user)
        return userId
    }

    private fun scrapCast(id: String, document: Document?) {
        val cast = Cast(id)
        val actorsElement = document!!.getElementById("actors")
        val castMembers = actorsElement.getElementsByAttributeValue("itemtype", "http://schema.org/Person")
        var castingType = actorsElement.getElementsByAttributeValueContaining("class", "titlebar").first().text()
        castMembers.forEach {
            val actorName = it.getElementsByAttributeValueContaining("itemprop", "name").text()
            cast.add2Cast(castingType, actorName)
        }

        val castingLists = document.getElementsByAttributeValueContaining("class", "casting-list")
        castingLists.forEach {
            castingType = it.getElementsByAttributeValueContaining("class", "titlebar").first().text()
            val members = it.getElementsByAttributeValue("itemtype", "http://schema.org/Person")
            members.forEach {
                val memberName = it.getElementsByAttributeValueContaining("itemprop", "name").text()
                cast.add2Cast(castingType, memberName.orEmpty())
            }
        }
        Repository.instance.add(id, cast)
    }


    private fun scrapMovie(id: String,
                           document: Document?,
                           title: String?,
                           director: String?,
                           description: String?,
                           country: String?) {
        val movie = Movie()
        document!!
        movie.id = id
        movie.title = title.orEmpty()
        movie.director = director.orEmpty()
        movie.genres.addAll(document.getElementsByAttributeValueContaining("itemprop", "genre").map { it.text() }.toSet())
        movie.country = country.orEmpty()
        movie.description = description.orEmpty()
        try {
            movie.ratingValue = document.getElementsByAttributeValueContaining("itemprop", "ratingValue").text().replace(',', '.').toDouble()
        } catch (_: Exception) {
        }
        try {
            movie.ratingCount = document.getElementsByAttributeValueContaining("itemprop", "ratingCount").text().toInt()
        } catch (_: Exception) {
        }
        try {
            movie.reviewCount = document.getElementsByAttributeValueContaining("itemprop", "reviewCount").text().toInt()
        } catch (_: Exception) {
        }
        movie.duration = document.getElementsByAttributeValueContaining("class", "meta-body-item").first().text()

        Repository.instance.add(id, movie)
    }


}
