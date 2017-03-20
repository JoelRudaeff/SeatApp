import os 
import sqlite3

#Configuration files
VEHICLE_INSTRUCTOR_FILE = 'Config.txt'

#Path files
ACCOUNTS_FOLDER = 'Accounts'
VEHICLES_FOLDER = 'Vehicles'


#checks if the account.db and its foldier exists, otherwise creates it
def create_account_db():
	#checks if the accounts folder for the db exist
	if not os.path.isdir(ACCOUNTS_FOLDER):
		os.makedirs(ACCOUNTS_FOLDER)
	else:
		print "The folder: 'Accounts' already exists!"

	try:
		print "Connecting to the: 'Accounts' DataBase!"
		conn = sqlite3.connect(ACCOUNTS_FOLDER+'/Accounts.db')
		c = conn.cursor()
			
		print "Creating 'users' table inside the 'Accounts' DataBase!"
		c.execute('''CREATE TABLE IF NOT EXISTS users(username text NOT NULL,email text NOT NULL,password text NOT NULL,PRIMARY KEY(username,email))''')
		conn.commit()
		conn.close()
			
		print "Accounts DataBase is all set!"
		
	except sqlite3.Error as e:
		print e
		print "Failed to create 'Accounts' DataBase!"
	
def create_vehicle_db():
	#checks if the configuration file exist
	print "Checking if the configurationg file exists!"
	if not os.path.isfile(VEHICLE_INSTRUCTOR_FILE):
		print "Creating a new basic configurationg file!"
		temp = open(VEHICLE_INSTRUCTOR_FILE,'a')
		temp.write("VEHICLE_TYPE ; VEHICLE_COMPANY ; VEHICLE_NUMBER \n")
		temp.write("================================================\n")
		temp.close()
		return
	else:
		print "Checking if the Vehicles folder exists!"
		if not os.path.isdir(VEHICLES_FOLDER):
			print "Creating a new Vehicles folder!"
			os.makedirs(VEHICLES_FOLDER)
			
		print "Reading from the configuration file!"
		data_file = open(VEHICLE_INSTRUCTOR_FILE)
		lines = data_file.readlines()
		data_file.close()
		
		
		index = 0 # will be used to skip the format tutorials - the first two lines
		for line in lines:
			if index >= 2:			
				#remove whitespaces,\n,lower case, upper case the first letter  and split into the required data
				vehicle_type,vehicle_company,vehicle_number = line.replace(" ","").replace('\n',"").lower().title().split(';')
				dest_path = VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company+'/'+vehicle_number
				if not os.path.isdir(VEHICLES_FOLDER+'/'+vehicle_type):	
					os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type)
					os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company)
					os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company+'/'+vehicle_number)
				else:
					if not os.path.isdir(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company):	
						os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company)
						os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company+'/'+vehicle_number)
					else:
						if not os.path.isdir(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company+'/'+vehicle_number):
							os.makedirs(VEHICLES_FOLDER+'/'+vehicle_type+'/'+vehicle_company+'/'+vehicle_number)
				
				#TODO: add the required db format for each one
				conn = sqlite3.connect(dest_path+'/Seats.db')
				conn.close()
				conn1 = sqlite3.connect(dest_path+'/Information.db')
				conn1.close()
				
			index+=1
				
	print "Vehicles DataBase is all set!"


def main():
	print "Checking accounts"
	create_account_db()
	print "\nChecking vehicles"
	create_vehicle_db()
	
	






if __name__ == "__main__":
	main()