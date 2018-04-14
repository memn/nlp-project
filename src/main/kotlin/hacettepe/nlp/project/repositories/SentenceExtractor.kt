package hacettepe.nlp.project.repositories

import com.google.gson.Gson
import zemberek.tokenization.TurkishSentenceExtractor
import zemberek.tokenization.TurkishTokenizer
import zemberek.tokenization.antlr.TurkishLexer
import java.io.FileWriter


/**
 *
 * @author Memn
 * @date 13.04.2018
 *
 */

fun main(args: Array<String>) {
    Repository.instance.read(false)
    val tokenizer = TurkishTokenizer.builder()
            .acceptAll()
            .ignoreTypes(TurkishLexer.Punctuation, TurkishLexer.NewLine, TurkishLexer.SpaceTab)
            .build()
    val movies = Repository.instance.movies.values
    val list: List<MutableList<String>> = TurkishSentenceExtractor.DEFAULT.fromParagraphs(movies.flatMap { it.posts.map { it.description } }).map {
        //        TurkishTokenizer.DEFAULT.tokenizeToStrings(it)
        tokenizer.tokenizeToStrings(it)
    }
    FileWriter("data/word2vec/sentences-wo-puncs.json").use {
        it.write(Gson().toJson(list))
    }
//
//    val word2VecModel = Repository.instance.readWord2VecModel()
//    val similarity = word2VecModel.forSearch().cosineDistance("güzel", "iyi")
//    val similarity2 = word2VecModel.forSearch().cosineDistance("güzel", "kötü")
//    println(similarity)
//    println(similarity2)
}
