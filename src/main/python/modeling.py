import logging

import classifier
import utilities

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)

train_data = utilities.read_train()
test_data = utilities.read_test()

train = utilities.get_tokens(train_data)
test = utilities.get_tokens(test_data)
score = classifier.train_test_svm_doc2vec(train, test)
logging.info(score)
