#/#
 #
 # Project Name:     Neural Network based Motorcycle Ecosystem
 # Author List:      Kartikeya Kawadkar
 # Filename:         clustering.py
 # Function:         parse_file(path), under_sample(arr), clustering()
 # Global Variables: labels
 #
 #/


import numpy as np
from sklearn.cluster import MiniBatchKMeans as KMeans
from centroid_extraction import compile_dataset, remove_time


labels = []

#parsing dataset file:
def parse_file(path):
    raw = open(path, 'r').read().split('\n')
    parsed_data = []
    for row in raw:
        tmp = np.array(row.split('\t')).astype(np.float)
        parsed_data.append(tmp)
    return parsed_data

#Under sampling dataset if needed
def under_sample(arr):
    last_epoch = arr[0][0]
    new_data = [arr[0]]
    for row in arr:
        delta = row[0] - last_epoch
        if delta >= 0.1:
            last_epoch = row[0]
            new_data.append(row)
    return new_data

#clustering model
def clustering():
    time, dataset = remove_time(parse_file('test.txt'))
    time, dataset = compile_dataset(time, dataset, window=50, overlap=0.5)

    cluster_size = 3

    kmeans = KMeans(n_clusters=cluster_size)
    kmeans.fit(dataset)

    centroid = kmeans.cluster_centers_
    labels = kmeans.labels_
