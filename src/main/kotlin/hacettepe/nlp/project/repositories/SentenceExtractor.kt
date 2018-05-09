package hacettepe.nlp.project.repositories

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import zemberek.tokenization.TurkishTokenizer
import zemberek.tokenization.antlr.TurkishLexer
import java.io.FileWriter


/**
 *
 * @author Memn
 * @date 13.04.2018
 *
 */
val logger: Logger = LogManager.getLogger(SentenceExtractor::class.java.name)

class SentenceExtractor{}

fun main(args: Array<String>) {

    Repository.instance.read()
    val tokenizer = TurkishTokenizer.builder().acceptAll()
            .ignoreTypes(TurkishLexer.Punctuation, TurkishLexer.NewLine, TurkishLexer.SpaceTab)
            .build()
    val movies = Repository.instance.movies.values
    val flatMap = movies.flatMap { it.posts.map { it.ToTokenized(tokenizer.tokenizeToStrings(it.description)) } }
    splitTrainAndTest(flatMap, "posts")
}

fun splitTrainAndTest(rawMovies: List<Any>, name: String) {
    val trainSize = (rawMovies.size * 0.9).toInt()
    logger.info("Total size: ${rawMovies.size}")
    logger.info("Train size: $trainSize")
    logger.info("Test size: ${rawMovies.size - trainSize}")

    FileWriter("data/tokenized-data/tokenized-$name-train.json").use {
        it.write(Gson().toJson(rawMovies.subList(0, trainSize)))
    }
    FileWriter("data/tokenized-data/tokenized-$name-test.json").use {
        it.write(Gson().toJson(rawMovies.subList(trainSize, rawMovies.size)))
    }
}
