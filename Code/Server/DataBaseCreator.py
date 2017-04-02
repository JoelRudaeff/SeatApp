import os
import sqlite3

# Configuration files
VEHICLE_INSTRUCTOR_FILE = 'Config.txt'

# Path files
ACCOUNTS_FOLDER = 'Accounts'
VEHICLES_FOLDER = 'Vehicles'


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

        print "Creating 'users' table inside the 'Accounts' DataBase!"
        c.execute(
            '''CREATE TABLE IF NOT EXISTS users(username text NOT NULL,email text NOT NULL,password text NOT NULL,PRIMARY KEY(username,email))''')
        conn.commit()
        conn.close()

        print "Accounts DataBase is all set!"

    except sqlite3.Error as e:
        print e
        print "Failed to create 'Accounts' DataBase!"


def create_vehicle_db():
    # checks if the configuration file exist
    print "Checking if the configurationg file exists!"
    if not os.path.isfile(VEHICLE_INSTRUCTOR_FILE):
        print "Creating a new basic configurationg file!"
        temp = open(VEHICLE_INSTRUCTOR_FILE, 'a')
        # path:delay - RABIN:0000_BIG:0025. START:END - 930:1000_1000:1030
        temp.write("VEHICLE_TYPE ; VEHICLE_COMPANY ; VEHICLE_NUMBER ; AMOUNT_OF_LINES ; PATH:DELAY_PATH1:DELAY1 ; START:END_START1:END1\n")
        temp.write("===================================================================================================================\n")
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

        index = 0  # will be used to skip the format tutorials - the first two lines
        for line in lines:
            if index >= 2:
                # remove whitespaces,\n,lower case, upper case the first letter  and split into the required data
                vehicle_type, vehicle_company, vehicle_number, amount_of_lines,path_and_delay,start_and_end = line.replace(" ", "").replace('\n',"").lower().title().split(';')
                dest_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number
                if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type):
                    os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type)
                    os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company)
                    os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number)
                else:
                    if not os.path.isdir(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company):
                        os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company)
                        os.makedirs(VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number)
                    else:
                        if not os.path.isdir(
                                                                                VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number):
                            os.makedirs(
                                VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number)

                # TODO: add the required db format for each one
                conn = sqlite3.connect(dest_path + '/Transport.db')
                c = conn.cursor()
                c.execute('''CREATE TABLE IF NOT EXISTS information(path text NOT NULL,delay text NOT NULL);''')
                c.execute('''CREATE TABLE IF NOT EXISTS seats(line int,status text NOT NULL,parts int NOT NULL,chairs_per_part int NOT NULL);''')
                c.execute('''CREATE TABLE IF NOT EXISTS active(start_time text NOT NULL,end_time text NOT NULL);''')


                #SEATS - TABLE
                result = c.execute('''SELECT line FROM seats WHERE line = ?''',(amount_of_lines,))
                data = c.fetchall()
                if  len(data) is 0:
                    x = 1
                    for x in xrange(1, int(amount_of_lines) + 1):
                        command = '''INSERT INTO seats(line,status,parts,chairs_per_part) VALUES('''
                        command += str(x)
                        command += ''',"0101",1,1);'''
                        try:
                            c.execute(command)
                        except sqlite3.Error as e:
                            print e

                #ACTIVE - TABLE
                start_and_end = start_and_end.split("_")
                for temp in start_and_end:
                    start,end = temp.split(":")
                    result = c.execute('''SELECT start_time FROM active WHERE start_time = ?''',(start,))
                    data = c.fetchall()
                    if len(data) is 0:
                        command = '''INSERT INTO active(start_time,end_time) VALUES("''' + str(start) + '''","''' + str(end) + '''");'''
                        try:
                            c.execute(command)
                        except sqlite3.Error as e:
                            print e

                #INFORMATION - TABLE
                path_and_delay = path_and_delay.split("_")
                for temp in path_and_delay:
                    path,delay = temp.split(":")
                    result = c.execute('''SELECT path FROM information WHERE path = ?''',(path,))
                    data = c.fetchall()
                    if len(data) is 0:
                        command = '''INSERT INTO information(path,delay) VALUES("''' + str(path) + '''","''' + str(delay) + '''");'''
                        try:
                            c.execute(command)
                        except sqlite3.Error as e:
                            print e

                conn.commit()
                conn.close()
            index += 1


print "Vehicles DataBase is all set!"


def main():
    try:
        print "Checking accounts"
        create_account_db()
        print "\nChecking vehicles"
        create_vehicle_db()
    except:
        print "ran into global error"

if __name__ == "__main__":
    main()
