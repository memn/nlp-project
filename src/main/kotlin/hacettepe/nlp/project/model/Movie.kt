package hacettepe.nlp.project.model


/**
 *
 * @author Memn
 * @date 28.03.2018
 *
 */

class Movie {
    var id = ""
    var title = ""
    var director: String? = ""
    var genres = HashSet<String>()
    var country = ""
    var description = ""
    var ratingValue = 0.0
    var ratingCount = 0
    var reviewCount = 0
    var duration = ""
    var cast = Cast()
    val posts = HashSet<Post>()


}