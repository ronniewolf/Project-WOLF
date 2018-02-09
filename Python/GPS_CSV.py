#/#
 #
 # Project Name:     Neural Network based Motorcycle Ecosystem
 # Author List:      Heramb Khanvilkar, Kartikeya Kawadkar
 # Filename:         GPS_CSV.py
 # Function:         parse_file(path), under_sample(arr), clustering()
 # Global Variables: filename_gps
 #
 #/


import csv
import datetime
import folium
from scipy.interpolate import interp1d
from numpy import arange
import webbrowser
import clustering

filename_gps = ''

# Class to store GPS_Data: time(HH:MM:SS), longitude, latitude
class GPS_Data:
    def __init__(self, row):
        self.time = datetime.datetime.strptime(row[2], "%H:%M:%S").time()
        self.longitude = float(row[3])
        self.latitude = float(row[4])

#Parsing GPS data
def parse_GPS():
    # Open GPS_CSV File:
    file = open(filename_gps, newline='')
    reader = csv.reader(file)
    # remove Header:
    # header = next(reader)
    # Store Data in list
    data = [row for row in reader]

    # Array of objects GPS_Data
    GPS_Data_array = []
    for i in range(0, len(data)):
        GPS_Data_array.append(GPS_Data(data[i]))

    # Separate time, longitude, latitude
    GPS_Data_time = []
    GPS_Data_longitude = []
    GPS_Data_latitude = []
    for i in range(0, len(data)):
        GPS_Data_time.append(GPS_Data_array[i].time)
        GPS_Data_longitude.append(GPS_Data_array[i].longitude)
        GPS_Data_latitude.append(GPS_Data_array[i].latitude)

    # Actual time/x dimension for interpolation
    time_Offset = arange(0, len(data) * 5, 5)
    # Extended/Interpolated time/x dimension for interpolation (0.5 sec)
    time_Offset_new = arange(0, (len(data) * 5 - 5), 0.25)

    # Interpolate Latitude:
    f_interpolate_latitude = interp1d(time_Offset, GPS_Data_latitude, kind='cubic')
    latitude_new = f_interpolate_latitude(time_Offset_new)
    # Interpolate Longitude:
    f_interpolate_longitude = interp1d(time_Offset, GPS_Data_longitude, kind='cubic')
    longitude_new = f_interpolate_longitude(time_Offset_new)

    # Average Latitude and Longitude
    avg_latitude, avg_longitude = 0.0, 0.0
    for i in range(0, len(latitude_new)):
        avg_latitude = avg_latitude + latitude_new[i]
        avg_longitude = avg_longitude + longitude_new[i]

    avg_latitude = avg_latitude / len(latitude_new)
    avg_longitude = avg_longitude / len(longitude_new)
	
    #Plotting Potholes and Speed breakers on MAP 
    my_map = folium.Map(location=[avg_longitude, avg_latitude], zoom_start=14)

    j=0
    for i in range(0, 10, len(time_Offset_new)):
        if clustering.labels[j] == 1:
            folium.Marker(location=(longitude_new[i], latitude_new[i]), icon=folium.Icon(icon_color='green')).add_to(my_map)

        if clustering.labels[j] == 2:
            folium.Marker(location=(longitude_new[i], latitude_new[i]), icon=folium.Icon(icon_color='red')).add_to(my_map)

        j = j+1


    my_map.save("./marker.html")

    webbrowser.get("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe %s").open("./marker.html")
