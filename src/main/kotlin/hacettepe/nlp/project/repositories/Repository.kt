package hacettepe.nlp.project.repositories

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import hacettepe.nlp.project.model.Cast
import hacettepe.nlp.project.model.Movie
import hacettepe.nlp.project.model.Post
import hacettepe.nlp.project.model.User
import org.apache.logging.log4j.LogManager
import java.io.File
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

        private val ORIGINAL_FILE = "data/original/100-k"
        private val USERS_FILE_SUFFIX = "-users.json"
        private val MOVIES_FILE_SUFFIX = "-movies.json"

        fun fileName(isMovies: Boolean): String {
            var s = ""
            s += ORIGINAL_FILE
            s += if (isMovies) MOVIES_FILE_SUFFIX else USERS_FILE_SUFFIX
            return s
        }
    }

    private val logger = LogManager.getLogger(Repository::class.java.name)

    internal val movies = HashMap<String, Movie>()
    internal val users = HashMap<String, User>()
    private val posts = HashMap<String, ArrayList<Post>>()
    private val casts = HashMap<String, Cast>()

    fun read() {
        read(Repository.fileName(true), Repository.fileName(false))
    }

    private fun read(moviesFileName: String, usersFileName: String) {
        read(File(moviesFileName), File(usersFileName))
    }

    private fun read(moviesFile: File, usersFile: File) {
        movies.clear()
        users.clear()
        logger.info("Start reading files ${moviesFile.path}  &  ${usersFile.path}")
        val mapper = jacksonObjectMapper()
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        mapper.readValue<List<User>>(usersFile).associateByTo(users) { it.id }
        logger.info("End reading file ${usersFile.path} with size ${users.size}")
        mapper.readValue<List<Movie>>(moviesFile).associateByTo(movies) { it.id }
        logger.info("End reading file ${moviesFile.path}")
    }

    private fun save(filePath: String) {
        logger.info("save called for: $filePath")
        try {
            FileWriter("$filePath-movies.json").use {
                it.write(Gson().toJson(initPostsAndCast()))
            }
            FileWriter("$filePath-users.json").use {
                it.write(Gson().toJson(users.values))
            }
        } finally {
            if (i == 3) {
                // stop after 100k posts
                System.exit(0)
            }

        }
    }

    private fun initPostsAndCast(): List<Movie> {
        // casts to movies
        casts.forEach {
            movies[it.key]?.cast = it.value
        }
        // posts to movies
        posts.forEach {
            movies[it.key]?.posts?.addAll(it.value)
        }
        return movies.values.filter({ it.posts.isNotEmpty() })
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
            // save when 50k
            save("data/original/${i++ * 50}-k")
        }
    }

}