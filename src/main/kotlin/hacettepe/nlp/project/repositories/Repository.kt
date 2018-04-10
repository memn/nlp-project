package hacettepe.nlp.project.repositories

import com.google.gson.Gson
import hacettepe.nlp.project.model.Cast
import hacettepe.nlp.project.model.Movie
import hacettepe.nlp.project.model.Post
import hacettepe.nlp.project.model.User
import java.io.FileWriter


/**
 *
 * @author Memn
 * @date 7.04.2018
 *
 */

class Repository {
    companion object {
        val instance = Repository()
    }

    private val movies = HashMap<String, Movie>()
    private val users = HashMap<String, User>()
    private val posts = HashMap<String, ArrayList<Post>>()
    private val casts = HashMap<String, Cast>()

    fun save() {
        // casts to movies
        casts.forEach {
            movies[it.key]?.cast = it.value
        }
        // posts to movies
        posts.forEach {
            movies[it.key]?.posts?.addAll(it.value)
        }
        println("save called for: ${i}0-k")

        val filter = movies.values.filter { it.posts.isNotEmpty() }
        try {

            FileWriter("${i}0-k-movies.json").use {
                it.write(Gson().toJson(filter))
            }
            FileWriter("${i}0-k-users.json").use {
                it.write(Gson().toJson(users.values))
            }

        } finally {
            if (i == 3) {
                System.exit(0)
            }

        }
    }

    private var postCount = 0
    private var i = 1
    fun add(id: String, obj: Any) {
        when (obj) {
            is User -> users.put(id, obj)
            is Cast -> casts.put(id, obj)
            is Movie -> movies.put(id, obj)
        }
    }

    fun addAll(id: String, postSet: HashSet<Post>) {
        posts.getOrPut(id, { ArrayList() }).addAll(postSet)
        postCount += postSet.size
        if (postCount > 50000 * i) {
            i++
            save()

        }
    }

}