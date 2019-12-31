import sqlite3
import csv

create_android_table = 'CREATE TABLE android_metadata (locale TEXT DEFAULT en_US)'
create_routes_table = 'CREATE TABLE "routes" ( route_id TEXT NOT NULL, route_short_name TEXT NOT NULL, route_long_name TEXT NOT NULL, route_color TEXT NOT NULL, route_text_color TEXT NOT NULL, _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT )'
create_stops_table = 'CREATE TABLE "stops" ( stop_id TEXT NOT NULL, stop_code TEXT NOT NULL, stop_name TEXT NOT NULL, corner_stop_name TEXT NOT NULL, stop_lat TEXT NOT NULL, stop_lon TEXT NOT NULL, _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT )'

file_stops_csv = "stops.txt"
file_routes_csv = "routes.txt"

conn = sqlite3.connect('stops.db')

cursor = conn.cursor()

#Create table
cursor.execute(create_android_table)
cursor.execute(create_routes_table)
cursor.execute(create_stops_table)

#Populate stops table
stops_csv = csv.reader(open(file_stops_csv, "r"), delimiter=',')
firstRow = True
for row in stops_csv:
	print(row)
	if (not firstRow):
		stop_id = row[0]
		stop_code = row[1]
		stop_name = ""
		corner_stop_name = row[2].replace("&", "and")
		stop_lat = row[4]
		stop_lon = row[5]

		if (corner_stop_name.find(' (') == -1):
			stop_name = corner_stop_name
		else:
			endIndex = corner_stop_name.index(' (')
			stop_name = corner_stop_name[:endIndex]

		cursor.execute("INSERT INTO stops (stop_id, stop_code, stop_name, corner_stop_name, stop_lat, stop_lon) VALUES (?,?,?,?,?,?)", (stop_id, stop_code, stop_name, corner_stop_name, stop_lat, stop_lon))
	firstRow = False

#Populate routes table
stops_csv = csv.reader(open(file_routes_csv, "r"), delimiter=',')
firstRow = True
for row in stops_csv:
	print(row)
	if (not firstRow):
		route_id = row[0]
		route_short_name = row[2]
		route_long_name = row[3]
		route_color = row[7]
		route_text_color = row[8]

		cursor.execute("INSERT INTO routes (route_id, route_short_name, route_long_name, route_color, route_text_color) VALUES (?,?,?,?,?)", (route_id, route_short_name, route_long_name, route_color, route_text_color))
	firstRow = False

conn.commit()
conn.close()