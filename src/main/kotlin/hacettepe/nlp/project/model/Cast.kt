package hacettepe.nlp.project.model


/**
 *
 * @author Memn
 * @date 7.04.2018
 *
 */

data class Cast(val castMembersByType: HashMap<String, HashSet<String>> = HashMap()) {

    fun add2Cast(castType: String, name: String) {
        castMembersByType.getOrPut(castType, { HashSet() }).add(name)
    }
}