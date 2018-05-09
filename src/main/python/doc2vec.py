import os

import gensim

import utilities

# logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)

min_count = 1
context_window = 20
vector_size = 300
down_sample = 1e-5
negative_sampling = 5
num_threads = 4
num_epochs = 10


def train_doc2vec(tokens, save_path):
    docs = [gensim.models.doc2vec.TaggedDocument(words=token, tags=['DOC_' + str(idx)])
            for idx, token in enumerate(tokens)]

    path = os.path.join(utilities.data_path(), save_path)
    if os.path.exists(path):
        model = gensim.models.Doc2Vec.load(path)
    else:
        model = gensim.models.Doc2Vec(docs, min_count=min_count, window=context_window, size=vector_size,
                                      sample=down_sample, negative=negative_sampling, workers=num_threads,
                                      iter=num_epochs)
        model.save(path)

    return model
