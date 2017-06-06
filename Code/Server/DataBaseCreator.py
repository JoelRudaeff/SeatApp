import os
import sqlite3
import traceback

# Configuration files
VEHICLE_INSTRUCTOR_FILE = 'Config.txt'

# Path files
ACCOUNTS_FOLDER = 'Accounts'
VEHICLES_FOLDER = 'Vehicles'


def prepare_vehicles_table(c):
    #VEHICLES - TABLE

    try:
        c.execute('''CREATE TABLE IF NOT EXISTS vehicles(id TEXT NOT NULL,next_stop TEXT NOT NULL);''')
    except sqlite3.Error as e:
        print "Error in function 'prepare_vehicles_table'"
        print e        

def prepare_active_table(c,start_and_end):
    #ACTIVE - TABLE

    try:
        c.execute('''CREATE TABLE IF NOT EXISTS active(start_time text NOT NULL,end_time text NOT NULL);''')

        start_and_end = start_and_end.split("_")
        for temp in start_and_end:
            start,end = temp.split(":")
            c.execute('''SELECT start_time FROM active WHERE start_time = ?;''',(start,))
            data = c.fetchall()
            if len(data) is 0:
                c.execute('''INSERT INTO active(start_time,end_time) VALUES(?,?);''',(start,end))
    except sqlite3.Error as e:
        print "Error in function 'prepare_active_table'"
        print e

def prepare_information_table(c,path_and_delay):
    #INFORMATION - TABLE
    try:
        c.execute('''CREATE TABLE IF NOT EXISTS information(path text NOT NULL,delay text NOT NULL);''')

        path_and_delay = path_and_delay.split("_")
        for temp in path_and_delay:
            path, delay = temp.split(":")
            c.execute('''SELECT path FROM information WHERE path = ?;''', (path,))
            data = c.fetchall()
            if len(data) is 0:
                c.execute('''INSERT INTO information(path,delay) VALUES(?,?);''',(path, delay))

    except sqlite3.Error as e:
        print "Error in function 'prepare_information_table'"
        print e

        
                
# checks if the account.db and its foldier exists, otherwise creates it
def create_account_db():
    # checks if the accounts folder for the db exist
    if not os.path.isdir(ACCOUNTS_FOLDER):
        os.makedirs(ACCOUNTS_FOLDER)
    else:
        print "The folder: 'Accounts' already exists!"

    try:
        print "Connecting to the: 'Accounts' DataBase!"
        conn = sqlite3.connect(ACCOUNTS_FOLDER + '/Accounts.db')
        c = conn.cursor()
        
        print "Preparing the 'users' table!"
        print "Creating 'users' table inside the 'Accounts' DataBase!"
        c.execute('''CREATE TABLE IF NOT EXISTS users(username text NOT NULL,email text NOT NULL,password text NOT NULL,PRIMARY KEY(username,email));''')
        conn.commit()
        conn.close()

        print "Accounts DataBase is all set!"

    except sqlite3.Error as e:
        print "Failed to create 'Accounts' DataBase!"
        print e


def create_vehicle_db():
    # checks if the configuration file exist
    print "Checking if the configurationg file exists!"
    if not os.path.isfile(VEHICLE_INSTRUCTOR_FILE):
        print "Creating a new basic configurationg file!"
        temp = open(VEHICLE_INSTRUCTOR_FILE, 'a')
        # path:delay - RABIN:0000_BIG:0025. START:END - 930:1000_1000:1030
        temp.write("VEHICLE_TYPE ; VEHICLE_COMPANY ; STARTING_CITY ; VEHICLE_NUMBER ; AMOUNT_OF_LINES ; PATH:DELAY_PATH1:DELAY1 ; START:END_START1:END1\n")
        temp.write("===================================================================================================================\n")
        temp.close()
        print "Please fill the Configuration file if you wish to proceed with the process!"
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

        try:
            index = 0  # will be used to skip the format tutorials - the first two lines
            for line in lines:
                if index >= 2:
                    # remove whitespaces,\n,lower case, upper case the first letter  and split into the required data
                    vehicle_type, vehicle_company, city,vehicle_number, amount_of_lines,path_and_delay,start_and_end = line.replace(" ", "").replace('\n',"").lower().title().split(';')
                    dest_path =  ""
                    
                    if 'Bus' in vehicle_type or 'bus' in vehicle_type:
                        dest_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number
                        if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type):
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type)
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company)
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city)
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number)
                        else:
                            if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company):
                                os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company)
                                os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city)
                                os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number)
                            else:
                                if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city):
                                    os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city)
                                    os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number)
                                else:
                                    if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number):
                                        os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city + '/' + vehicle_number)
                    else: #train
                        dest_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + city
                        if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type):
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type)
                            os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + city)
                        else:
                            if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type + '/' + city):
                                os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + city)

                    conn = sqlite3.connect(dest_path + '/Transport.db')
                    c = conn.cursor()
                    print "Preparing the 'active' table!"
                    prepare_active_table(c,start_and_end)
                    print "Preparing the 'information' table!"
                    prepare_information_table(c,path_and_delay)
                    print "Preparing the 'vehicles' table!"
                    prepare_vehicles_table(c)

                    print "Vehicle: ",vehicle_type," Company: ",vehicle_company," City: ",city," Number: ",vehicle_number," Was added to DB"
                    conn.commit()
                    conn.close()
                else:    
                    index +=1
            print "Vehicles DataBase is all set!"
        except:
            print "Error while creating the vehicles part!"
            traceback.print_exc()

    
def main():
    try:
        print "Checking accounts"
        create_account_db()
        print "\nChecking vehicles"
        create_vehicle_db()

    except:
        print "ran into global error"
        traceback.print_exc()

    raw_input("Press any key to continue")


if __name__ == "__main__":
    main()
