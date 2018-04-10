package hacettepe.nlp.project.model


/**
 *
 * @author Memn
 * @date 7.04.2018
 *
 */

class Cast(val movieId: String) {

    private val cast = HashMap<String, HashSet<String>>()

    fun add2Cast(castType: String, name: String) {
        cast.putIfAbsent(castType, HashSet())
        cast[castType]!!.add(name)
    }
}