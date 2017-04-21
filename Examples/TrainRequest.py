#!/usr/bin/env python
# -*- coding: utf-8 -*-
import urllib
import re

codes = {
"Nharyia": "1600",
"Akko": "1500",
"Kiryat Motzkin": "800",
"Kiryat Haim": "700",
"Lev Hamifratz": "1220",
"Hutzot Hamifratz": "1300",
"Haifa Center Hashmona": "2100",
"Haifa Bat Galim": "2200",
"Haifa Hof Hacarmel": "2300",
"Atlit": "2500",
"Benyamina": "2800",
"Tel Aviv Savidor Center": "3700",
"Tel Aviv Hashalom": "4600",
"Tel Aviv Hahagana": "4900",
"Tel Aviv University": "3600"
}



def request_page(date,time,source,destination):
	exit_time = []
	arrival_time = []
	source_code = codes[source]
	destination_code = codes[destination]
	url = r"http://www.rail.co.il"
	request = r"/pages/trainsearchres.aspx?FSID="+source_code+"&TSID="+destination_code+"&Date="+date+"&Hour="+time+"&IOT=true&IBA=false"
	page = url + request
	exit_time_string_to_find = '''<divclass ="col-md-2 col-sm-2 col-xs-5 hours ng-binding" > < span class ="sr-only ng-binding" > שעת יציאה, < / span > [0-9]{1,2}:[0-9]{2} </div>'''
	arrival_time_string_to_find = '''<divclass ="col-md-2 col-sm-2 col-xs-5 hours ng-binding" > < span class ="sr-only ng-binding" > שעת הגעה, < / span > [0-9]{1,2}:[0-9]{2} </div>'''

	r = urllib.urlopen(page)
	#print html

	while True:
		line1 = r.readline()
		line2 = r.readline()
		if ( re.findall(exit_time_string_to_find,line1) is not None):
			exit_time.append(line2)
		if ( re.findall(arrival_time_string_to_find,line2) is not None):
			arrival_time.append(line2)

	print arrival_time
	print exit_time
	r.close()

def main():
	date = "20172104"
	time = "1600" #16:00
	source = "Nharyia"
	destination = "Tel Aviv Hashalom"
	request_page(date,time,source,destination)
	temp = raw_input("Press any key to continue...")


if __name__ == "__main__":
	main()