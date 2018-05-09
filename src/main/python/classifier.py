import os
import pickle

from sklearn import svm

import doc2vec
import utilities


def train_test_svm(train_data, train_labels, test_data, test_labels):
    save_file = os.path.join(utilities.data_path(), 'svm.pk')
    if os.path.exists(save_file):
        clf = pickle.load(open(save_file, 'rb'))
    else:
        clf = svm.SVC(decision_function_shape='ovo', C=100, gamma=0.9, kernel='rbf')
        clf.fit(train_data, train_labels)
        pickle.dump(clf, open(save_file, 'wb'))

    return clf.score(test_data, test_labels)


def train_test_svm_doc2vec(train, test):
    tokens = train['Tokens'] + test['Tokens']
    vector_model = doc2vec.train_doc2vec(tokens, 'doc2vec__' + str(1))

    train_tokens = train['Tokens']
    train_labels = train['Labels']
    # train_arrays = numpy.zeros_like(len(train_tokens), doc2vec.vector_size)
    train_arrays = []
    for i in range(len(train_tokens)):
        train_arrays.append(vector_model.docvecs['DOC_' + str(i)])

    test_tokens = test['Tokens']
    test_labels = test['Labels']
    # test_arrays = numpy.zeros_like(len(test_tokens), doc2vec.vector_size)
    test_arrays = []
    for i in range(len(test_tokens)):
        test_arrays.append(vector_model.docvecs['DOC_' + str(i + len(train_tokens))])

    return train_test_svm(train_arrays, train_labels, test_arrays, test_labels, )
