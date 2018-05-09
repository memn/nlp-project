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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (userId != other.userId) return false
        if (description != other.description) return false
        if (ratingValue != other.ratingValue) return false
        if (upVote != other.upVote) return false
        if (downVote != other.downVote) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + ratingValue.hashCode()
        result = 31 * result + upVote
        result = 31 * result + downVote
        return result
    }
    fun ToTokenized(desc: MutableList<String>): TokenizedPost{
        return TokenizedPost(userId, desc, ratingValue, upVote, downVote)
    }
}