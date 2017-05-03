#!/usr/bin/env python
# -*- coding: utf-8 -*-
import urllib2
import re
from bs4 import BeautifulSoup # https://www.crummy.com/software/BeautifulSoup/bs4/download/4.5/beautifulsoup4-4.5.3.tar.gz


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


def request_page_new(year,month,day,hour,minute,source,destination):
	exit_time = []
	arrival_time = []
	source_code = codes[source]
	destination_code = codes[destination]
	date = year+"-"+ month+ "-"+ day
	page = "http://www1.rail.co.il/EN/DrivePlan/Pages/DrivePlan.aspx?DrivePlanPage=true&OriginStationId=" + source_code + "&DestStationId=" + destination_code + "&HoursDeparture=" + hour + "&MinutesDeparture=" + minute + "&GoingHourDeparture=true&ArrivalHourDeparture=false&GoingHourReturn=true&ArrivalHourReturn=false&IsReturn=false&GoingTrainCln=" +date+"&ReturnningTrainCln=" + date+"+&IsFullURL=true"

	r = urllib2.urlopen(page)  # a socket
	html = r.read()
	# html = html.encode("UTF-8")
	# html = html.encode('ascii', 'ignore').decode('ascii')
	r.close()

	file = open("text.html", "w")
	file.write(html)
	soup = BeautifulSoup(html, "html.parser")
	file.close()

	#div = soup.find_all('div',id = "s4-mainarea")
	#saved_class = soup.find('div', class_="s4-ba")
	#table = saved_class.find('td', id="tdHPMain")
	for times in soup.find_all('td', class_ = "GridSortDateItemStyle",onclick="SelectRow(this);"):
		print times.text

	trs1,trs2 = soup.find_all('tr',class_ = "blueBG"),soup.find_all('tr',class_ = "normalBG")
	for times1,times2 in trs1,trs2:
		tds = times.find_all('td',onclick="SelectRow(this);")
		for td in tds:
			if  (td.text[0] >= '0' and td.text[0] <= '9' )and td.text[1] is not ':':
				print td.text




'''
def request_page(date,time,source,destination):
	exit_time = []
	arrival_time = []
	source_code = codes[source]
	destination_code = codes[destination]
	url = r"http://www.rail.co.il"
	request1 = "http://www1.rail.co.il/EN/DrivePlan/Pages/DrivePlan.aspx?DrivePlanPage=true&OriginStationId=" + source_code + "&DestStationId=" + destination_code +"&HoursDeparture="+hour+"&MinutesDeparture="+minute+"&GoingHourDeparture=true&ArrivalHourDeparture=false&GoingHourReturn=true&ArrivalHourReturn=false&IsReturn=false&GoingTrainCln="+date+2017-04-24"&ReturnningTrainCln="+date"+&IsFullURL=true
	request = r"/pages/trainsearchres.aspx?FSID="+source_code+"&TSID="+destination_code+"&Date="+date+"&Hour="+time+"&IOT=true&IBA=false"
	page = url + request
	exit_time_string_to_find = ''שעת יציאה, [0-9]{1,2}:[0-9]{2}''
	arrival_time_string_to_find = ''שעת הגעה, [0-9]{1,2}:[0-9]{2}''

	r = urllib2.urlopen(page) # a socket
	html = r.read()
	print html
	#html = html.encode("UTF-8")
	#html = html.encode('ascii', 'ignore').decode('ascii')
	r.close()

	file = open("text.html","w")
	file.write(html)
	soup = BeautifulSoup(html, "html.parser")
	file.close()

	print soup.find_all("html_element", ["sr-only ng-binding","sr-only.ng-binding", "col-md-2 col-sm-2 col-xs-5 hours ng-binding,col-md-2.col-sm-2.col-xs-5.hours.ng-binding"])
	#print soup.findAll("div", class_ = "sr-only.ng-binding" )
	#print soup.findAll(re.compile("div", { "class" : "stylelistrow" }))

	try:
		while True:
			line1 = text.readline()
			line2 = r.readline()
			if line1 is not "" and line2 is not "":
				if  re.findall(exit_time_string_to_find,line1) is not None:
					exit_time.append(line2)
					print line2
				if  re.findall(arrival_time_string_to_find,line2) is not None:
					arrival_time.append(line2)
					print line2
				if line2 is None or line1 is None:
					break
	except:
		print "Error has been occurred"
	print arrival_time
	print exit_time


'''
def main():
	year = "2017"
	month = "04"
	day = "24"
	hour = "16"
	minute = "00"
	source = "Nharyia"
	destination = "Tel Aviv Hashalom"
	request_page_new(year,month,day,hour,minute,source,destination)
	temp = raw_input("Press any key to continue...")


if __name__ == "__main__":
	main()