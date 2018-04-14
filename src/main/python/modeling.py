import json
import logging
import os.path
from os.path import dirname
from pathlib import Path

import gensim
from gensim.models import KeyedVectors

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


class Modeling:
    project_folder_path = dirname(dirname(dirname(dirname(__file__))))
    data_folder_path = os.path.join(project_folder_path, 'data')
    vectors_no_puncs = '%s/word2vec/word2vec-no-puncs.txt' % data_folder_path
    sentences_no_puncs = '%s/word2vec/sentences-no-puncs.json' % data_folder_path

    def generate_model(self):
        vectors_file = Path(self.vectors_no_puncs)
        if vectors_file.exists():
            return KeyedVectors.load_word2vec_format(self.vectors_no_puncs)
        else:
            logging.info("reading file {0}...this may take a while".format(self.sentences_no_puncs))
            documents = json.load(open(self.sentences_no_puncs, 'r', encoding='utf-8'))
            w2v = gensim.models.Word2Vec(documents, size=150, window=10, min_count=2, workers=10)
            w2v.train(documents, total_examples=len(documents), epochs=10)
            w2v.wv.save_word2vec_format(self.vectors_no_puncs)
            return w2v.wv


vectors = Modeling().generate_model()
logging.info(vectors.similarity('güzel', 'iyi'))
logging.info(vectors.similarity('güzel', 'kötü'))
logging.info(vectors.most_similar(positive=['guzel', 'güzel']))
logging.info(vectors.most_similar(positive=['oyuncu', 'senaryo']))
