package hacettepe.nlp.project.model


/**
 *
 * @author Memn
 * @date 28.03.2018
 *
 */

class Post(val userId: String,
           var description: String,
           val ratingValue: Double,
           val upVote: Int,
           val downVote: Int) {

}