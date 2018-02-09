 #/#
 #
 # Project Name:     Neural Network based Motorcycle Ecosystem
 # Author List:      Rohan Mahajan, Kartikeya Kawadkar
 # Filename:         Accelerometer_Parse.py
 # Function:         parse_ACC()
 # Global Variables: filename_acc
 #
 #/


import csv
import datetime

filename_acc = ''

#Parsing logged Accelerometer Data
def parse_ACC():
    file = open(filename_acc)
    data = [line for line in file]

    timestamp = []
    acc_data = []
    acc_x, acc_y, acc_z = [], [], []

    prev_line = False

    # Fetching timestamp = HH:MM:SS.ms(str) & Accelerometer values(str)
    for i in range(len(data)):

        if (data[i][0] == '>') and (not prev_line):
            timestamp.append(data[i][1:16])
            prev_line = True

        elif (data[i][len(data[i]) - 2] == 'r') and prev_line:
            acc_data.append(data[i][0:len(data[i]) - 3])
            prev_line = False

        else:
            raise SystemExit('Error in the File at line : ' + str(i + 1))

    reader = csv.reader(acc_data)

    data = [row for row in reader]

    # Actual Readings(int):
    for i in range(len(data)):
        acc_x.append(int(data[i][0]))
        acc_y.append(int(data[i][1]))
        acc_z.append(int(data[i][2]))

    # Normalization of Accelerometer Readings:
    N_acc_x, N_acc_y, N_acc_z = [], [], []
    max_x = max(acc_x)
    max_y = max(acc_y)
    max_z = max(acc_z)
    min_x = min(acc_x)
    min_y = min(acc_y)
    min_z = min(acc_z)

    # Normalized Readings:
    for i in range(len(acc_x)):
        N_acc_x.append(format(float(acc_x[i] - min_x) / float(max_x - min_x), '.8f'))
        N_acc_y.append(format(float(acc_y[i] - min_y) / float(max_y - min_y), '.8f'))
        N_acc_z.append(format(float(acc_z[i] - min_z) / float(max_z - min_z), '.8f'))


    # Converting timestamp string:
    time = []
    for i in range(len(timestamp)):
        time.append(datetime.datetime.strptime(timestamp[i], "%H:%M:%S.%f").time())

    # Converting time in seconds and then referencing it to zero
    N_time_sec = []
    for i in range(len(time)):
        hour = time[i].hour - time[0].hour
        minute = time[i].minute - time[0].minute
        second = time[i].second - time[0].second
        microsecond = time[i].microsecond - time[0].microsecond
        final_microsecond = (hour * 3600) + (minute * 60) + second + (microsecond * 0.000001)
        N_time_sec.append(format(final_microsecond, '.6f'))

    # Finally storing data as CSV:
    Final_csv = []
    for i in range(len(N_time_sec)):
        Final_csv.append([N_time_sec[i], N_acc_x[i], N_acc_y[i], N_acc_z[i]])

    name = 'test.txt'
    fp = open(name, 'w', newline='')
    writer_csv = csv.writer(fp, delimiter='\t')
    writer_csv.writerows(Final_csv)