**Data Collection:**

You can use the `data` folder from the previous tagged commit. (tagged as with_data) 
Otherwise you need to generate yourself whole dataset and vector caches.

It is suggested to use Intellij IDEA to run Kotlin runnable.

From command line: (needs kotlin)

**_Takes Time_**

`$ kotlinc BeyazPerdeCrawlController.kt`

This command crawls beyazperde.com and scraps movies. After it gathers almost 100k reviews.

Then,

`$ kotlinc SentenceExtractor.kt`

It generates tokenized data folder and it should be ready for vectorization and training.

**Training and Testing:**

**_Takes Time_**

`$ python modeling.py`

it generates document(review) vectors and trains svm and caches them into `data` folder.


