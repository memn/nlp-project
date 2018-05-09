import json
import logging
import os
from os.path import dirname

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')


def project_path():
    return dirname(dirname(dirname(dirname(__file__))))


def data_path():
    return os.path.join(project_path(), 'data')


def doc2vec_path():
    return os.path.join(data_path(), 'data_huge')


def read_train():
    raw_posts_train = '%s/tokenized-data/tokenized-posts-train.json' % data_path()
    return json.load(open(raw_posts_train, 'r', encoding='utf-8'))


def read_test():
    raw_posts_test = '%s/tokenized-data/tokenized-posts-test.json' % data_path()
    return json.load(open(raw_posts_test, 'r', encoding='utf-8'))


def get_tokens(my_data):
    # contents = [" ".join(data.split("\n")) for data in my_data.data]
    contents = [(data['description']) for data in my_data]
    labels = [(data['ratingValue'] > 2.5) for data in my_data]
    return {'Tokens': contents, 'Labels': labels}
