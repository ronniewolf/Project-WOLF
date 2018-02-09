#/#
 #
 # Project Name:     Neural Network based Motorcycle Ecosystem
 # Author List:      Kartikeya Kawadkar
 # Filename:         centroid_extraction.py
 # Function:         save_model(model, path), load_model(path), remove_time(arr), compile_dataset(time, dataset, window, overlap)
 # Global Variables: 
 #
 #/


import numpy as np
from sklearn.cluster import MiniBatchKMeans as KMeans

#to save model using sklearn:
def save_model(model, path):
    from sklearn.externals import joblib
    joblib.dump(model, path)
    return

#load model for computations:
def load_model(path):
    import os.path
    if os.path.isfile(path):
        from sklearn.externals import joblib
        return joblib.load(path)
    return None

#removing time from data set file:
def remove_time(arr):
    compiled, time = [], []
    for row in arr:
        time.append(row[0:1][0])
        compiled.append(row[1:4])
    return time, compiled

#compiling output from dataset:
def compile_dataset(time, dataset, window, overlap):
    if overlap >= 1:
        raise ValueError('overlap must be below 1')
    import math
    length = len(dataset)
    offset = int(math.floor(window * overlap))
    n_w = int(math.floor((length - offset) / (window - offset)))

    t, compiled = [], []
    import math
    for i in range(n_w):
        first = dataset[i * offset]
        data_slice = dataset[i * offset + 1: i * offset + window]
        compiled.append(np.append(first, data_slice))
        t.append(time[i*offset + int(math.floor(window / 2))])
    return t, compiled
