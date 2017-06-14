import socket
import sqlite3
import sys
import time
import os
from threading import Thread, Lock

CLIENT_HOST = '192.168.1.42'  # TODO:
CLIENT_PORT = 8888

# Path files
ACCOUNTS_FOLDER = 'Accounts'
VEHICLES_FOLDER = 'Vehicles'

# Server's log for user who use the app currently 
SERVER_LOG = {}

# kind of a mutex lock, will be used for DataBase access
lock = Lock()


def print_server_information():
    while True:
        try:
            time.sleep(10) #update every 10 seconds
            print "Client handler running!"
            print "Running on ip: ",CLIENT_HOST," : ",CLIENT_PORT
            print "Connected users:"
            for username, address in SERVER_LOG.iteritems():
                print username, " : ",address
        except:
            print "Error in print_server_information()"

########################################################################################################################
#                                                   RPI_SIDE

# function that will update the seats data inside the data-base. will be used during communication with the RPI
def update_transport_database(vehicle_type, vehicle_company, city ,vehicle_number, id, line_number, data):
    #u/c;t;C;city;n;id;L1;A;L2;B
    old_line_status = ""
    with lock:
        if 'Bus' in vehicle_type or 'bus' in vehicle_type:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/' + id + '.db'
        else:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/' + id + '.db'
        conn = sqlite3.connect(db_path)  # connection to the database
        c = conn.cursor()

        try:
            seat_results = c.execute('''SELECT status FROM seats WHERE line=?''',(line_number,))
            old_line_status = seat_results


            # after we stored the backup data, we can change to the new data
            c.execute('''UPDATE seats SET status = ? WHERE line = ?;''',(data,line_number))  # Updating the data for each seat's data

            

        except:  # restoring the old data
            if old_line_status is not "":
                data = old_line_status
                c.execute('''UPDATE seats SET status = ? WHERE line = ?;''',(data, line_number))  # Updating the data for each seat's data
                # save changes and close db
                
        # save changes
        conn.commit()
        conn.close()

        
# when a vehicle starts to run, it sends a message to the server in order to get an id - lets users get seats from his db and not from the other vehicles     
def init_vehicle(socket,vehicle_type,vehicle_company,city,vehicle_number,amount_of_lines):
    #i;vehicle_type;vehicle_company;city;vehicle_number
    new_id = 0

    with lock:
        if 'Bus' in vehicle_type or 'bus' in vehicle_type:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db'
        else:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
        conn = sqlite3.connect(db_path)  # connection to the database
        c = conn.cursor()

        try:
            # first of all, if an error has been occured in the process, get the data before changing it so if something happened we can restore the OLD_DATA
            c.execute('''SELECT * FROM vehicles ORDER BY id DESC LIMIT 1;''')
            latest_vehicle_id = c.fetchone()[0]
            if latest_vehicle_id is None:
                new_id = 0
            else:
                new_id = int(latest_vehicle_id)+1
                
            c.execute('''INSERT INTO vehicles (id,next_stop) VALUES( ?, ?);''',(str(new_id),str(0)))  # inserting vehicle into the active vehicle list
            conn.commit()
            conn.close()
            
            print "passed"
            
            if 'Bus' in vehicle_type or 'bus' in vehicle_type:
                db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/' + str(new_id) +'.db'
            else:
                db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/' + str(new_id) +'.db'
            
            conn = sqlite3.connect(db_path)  # connection to the database OF THE VEHICLE ITSELF
            c = conn.cursor()
            
            c.execute('''CREATE TABLE IF NOT EXISTS seats(line int,status text NOT NULL);''')
            for x in xrange(1, int(amount_of_lines) + 1):
                c.execute('''INSERT INTO seats(line,status) VALUES(?,?);''',(x,"0000"))


            
        except sqlite3.Error as e:
            print e
            new_id = -1      

        # save changes
        conn.commit()            
        conn.close()

        socket.send("i;" + str(new_id))
    
    
#when a vehicle stops his service - completes his destination, the vehicle notifies the server
def destroy_vehicle(socket,vehicle_type,vehicle_company,city,vehicle_number,vehicle_id):    
      #d;vehicle_type;vehicle_company;city;vehicle_number;vehicle_id
    to_remove_path = ""
    with lock:
        if 'Bus' in vehicle_type or 'bus' in vehicle_type:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db'
            to_remove_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/' + str(vehicle_id) +'.db'
        else:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
            to_remove_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/' + str(vehicle_id) +'.db'

            
        conn = sqlite3.connect(db_path)  # connection to the database
        c = conn.cursor()        
        try:
            c.execute('''DELETE FROM vehicles WHERE id=?;''',(vehicle_id,))
            os.remove(to_remove_path)
            
        except:        
            new_id = 0            
            
        # save changes
        conn.commit()
        conn.close()
        socket.send("d;" + str(id))
        
        
        
#everytime a vehicle reaches its next stop, it notifies the server
def update_stop(socket,vehicle_type,vehicle_company,city,vehicle_number,vehicle_id):
    with lock:
        if 'Bus' in vehicle_type or 'bus' in vehicle_type:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db'
        else:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
        
        conn = sqlite3.connect(db_path)  # connection to the database
        c = conn.cursor()  
        
        try:
            c.execute("UPDATE vehicles SET next_stop=next_stop+1 WHERE id=?",(vehicle_id,))
        except:
            print "error in update_stop"
            
        # save changes
        conn.commit()    
        conn.close()
########################################################################################################################
#                                                   CLIENT SIDE

def delete_user_from_logs(address,username):
    try:
        if username in SERVER_LOG and SERVER_LOG[username]==address:
            del SERVER_LOG[username]
        else:
            print "ERROR! Unregistered user logged out!" #security failure
    except:
        print "couldn't delete: ",address," ",username

def insert_user_to_logs(address,username):
    if (username not in SERVER_LOG) and (address not in SERVER_LOG.values()): #username and ip must be unique
        SERVER_LOG[username] = address
        return True
    elif (username in SERVER_LOG) and (SERVER_LOG[username] is not address): #user is logged in from another device
        return False
    else:
        SERVER_LOG[username] = address
        return True
    '''
    if username not in SERVER_LOG and address in SERVER_LOG.values(): #same computer uses different users
        return False
    '''

#TODO: Add "current stop" to protocol in order to send the proper vehicle's seats to the user      
def send_get_seats(client_socket, client_msg):
    # g;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company;len(city); city;len(vehicle_number); vehicle_number ; len(curr_stop) ; curr_stop
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    city = client_msg[6]
    vehicle_number = client_msg[8]
    current_stop = client_msg[10] #number - starts from 1 to the number of the latest stop
    
    seats = ""      
    db_path = ""
    id_path = ""
    id = ""
    
    if 'Bus' in vehicle_type or 'bus' in vehicle_type:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db' 
        id_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/'
       
    else:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
        id_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/'
          
    
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            conn = sqlite3.connect(db_path)  # connection to the database
            c = conn.cursor()


            #FINDs THE CLOSEST VEHICLE TO USER AND THEN USES HIS ID TO GET THE SEATS' DATA
            c.execute('''SELECT MAX(id) FROM vehicles WHERE next_stop <= ?;''',(current_stop,))
            id = c.fetchone()[0] #gets the value itself

            conn.close()
            if id is None:
                print "No vehicles found under: " + vehicle_type + "" + vehicle_company + "" + vehicle_number
                raise sqlite3.Error #no vehicles are alive
                
            id_path = id_path + id + '.db'
            conn = sqlite3.connect(id_path)  # connection to the database
            c = conn.cursor()

            #if the seats table doesn't exist - the vehicle wasn't init at all
            if not c.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='seats'").fetchone():
                c.execute('''CREATE TABLE seats(line INTEGER NOT NULL,status TEXT NOT NULL);''')
                c.execute('''INSERT INTO seats(line,status) VALUES(?,?);''',(1,0))
                seats = str(1) + "_" + str(0)
            else: 
                index = 0
                data = c.execute('''SELECT * FROM seats''')
                for row in data:
                    if index is not 0:
                        seats += "|" #indicates space between the previous line and the current
                    else:
                        index +=1
                    seats += str(row[0])  # append the seat's line number, after converting from int to string
                    seats += "_" #indicates the line number before the sign, and the seats' status after the sign
                    seats += str(row[1])  # append the seat's status, after converting from int to string

            conn.close()
        except sqlite3.Error as e:
            print e
            print "Error in send_get_seats: " + vehicle_type + "" + vehicle_company + "" + vehicle_number
            seats = "-1"  # an error has been occured
            conn.close()
    client_socket.send('g;'+str(len(seats))+";" + str(seats)) # by the protocol


def send_register_client(address,client_socket, client_msg):
    # r;length(username);username;length(password);password;length(email);email
    username = client_msg[2]
    password = client_msg[4]
    email = client_msg[6]

    db_path = ACCOUNTS_FOLDER + '/Accounts.db'

    confirmation = "r;0"
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            conn = sqlite3.connect(db_path)  # connection to the database
            c = conn.cursor()
            c.execute('''SELECT username FROM users WHERE username = ? AND password = ? AND email = ?''',(username, password, email))
            data = c.fetchall()
            if len(data) is 0:  # if no user was found under these conditions - a success of registrations
                c.execute('''INSERT INTO users(username,password,email) VALUES(?,?,?)''', (username, password, email))
                confirmation = "r;1"
            conn.commit(); #save changes
            conn.close()
        except:
            print "Error in send_register_client: " + username + "" + password + "" + email
            conn.close()
            
    if ('1' in confirmation) and (insert_user_to_logs(address,username)): #all good, user achives the standards
        client_socket.sendall(confirmation)
    else:
        client_socket.sendall("r;0") #failure


def send_login_client(address,client_socket, client_msg):
    # l;length(username);username;length(password);password
    username = client_msg[2]
    password = client_msg[4]

    db_path = ACCOUNTS_FOLDER + '/Accounts.db'
    conn = sqlite3.connect(db_path)  # connection to the database
    c = conn.cursor()

    confirmation = "l;0"
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            c.execute('''SELECT username FROM users WHERE username = ? AND password = ? ''',(username, password))
            data = c.fetchall()
            if len(data) is not 0:  # the user was found
                confirmation = "l;1"  # success
            conn.close()
        except:
            print "Error in send_login_client: " + username + "" + password
            conn.close()
            
    if ('1' in confirmation) and (insert_user_to_logs(address,username)): #all good, user achives the standards
        client_socket.send(confirmation)
    else:
        client_socket.send("l;0") #failure


def send_view_vehicle(client_socket, client_msg):
    # v;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company;len(city); city;len(vehicle_number); vehicle_number
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    city = client_msg[6]
    vehicle_number = client_msg[8]

    view_vehicle = ""
    start_and_end = ""
    location_and_delay = ""
    db_path = ""
    index = 0
    if 'Bus' in vehicle_type or 'bus' in vehicle_type:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db'
    else:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
                
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            conn = sqlite3.connect(db_path)  # connection to the database
            c = conn.cursor()

            # start_time -> end_time
            data = c.execute('''SELECT * FROM active''')
            for row in data:
                if index is not 0:
                    start_and_end += '|'
                else:
                    index +=1
                start_and_end += str(row[0])  # append the start time number
                start_and_end += "_"
                start_and_end += str(row[1])  # append the end time number

            index = 0
            # location -> delay
            data = c.execute('''SELECT * FROM information''')
            for row in data:
                if index is not 0:
                    location_and_delay += '|'
                else:
                    index +=1
                location_and_delay += str(row[0])  # append the location number
                location_and_delay += "_"
                location_and_delay += str(row[1])  # append the delay number

            view_vehicle = 'v;' + str(len(start_and_end)) + ';' + str(start_and_end) + ';' + str(len(location_and_delay)) + ';' + str(location_and_delay)
            conn.close()
        except:
            view_vehicle = ""
            print "Error in send_view_vehicle: " + vehicle_type + "" + vehicle_company + "" + vehicle_number
    client_socket.send(view_vehicle)
    
    
def send_supported_lines(client_socket, client_msg):
    # N;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company; len(city);city
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    city = client_msg[6]
  
    msg_to_send = ""
    first_flag = True # will help organize the msg by the protocols
    vehicle_list_in_string = "" # will be by the protocol
    directory = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city
    try:
        for line in os.listdir(directory): #return a tupel where the x[0] is the name of the sub-directory itself
            if first_flag is not True:
                vehicle_list_in_string+='|'
            vehicle_list_in_string+=line
            first_flag = False
            continue
        msg_to_send = "N;"+str(len(vehicle_list_in_string))+";"+vehicle_list_in_string
    except:
        msg_to_send = "N;1;-1"
    client_socket.send(msg_to_send)  


def send_stops(client_socket,client_msg):
    #S;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company; len(city);city;len(vehicle_number);vehicle_number
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    city = client_msg[6]
    vehicle_number = client_msg[8]
    
    stops = ""
    if 'Bus' in vehicle_type or 'bus' in vehicle_type:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + city +'/' +vehicle_number + '/Transport.db'
    else:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' +  city + '/Transport.db'
        
        
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            conn = sqlite3.connect(db_path)  # connection to the database
            c = conn.cursor()
            index = 0
            data = c.execute('''SELECT path FROM information''')
            for row in data:
                if index is not 0:
                    stops += '|'
                else:
                    index +=1
                stops += str(row[0])  # append the start time number


            stops = 'S;' + str(len(stops)) + ';' + str(stops)
            conn.close()
        except:
            stops = ""
            print "Error in send_stops: " + vehicle_type + "" + vehicle_company + "" + vehicle_number
    client_socket.send(stops)    
    
def handle_client(address,client_socket,client_msg):
    option = client_msg[0]
    option = option[-1]
    if option is "g":  # If the client sent a seats request-message to the server ("g" - protocol message for request for the seats' status)
        send_get_seats(client_socket, client_msg)
    elif option is "r":  # register       
        send_register_client(address,client_socket, client_msg)
    elif option is "l":  # login       
        send_login_client(address,client_socket, client_msg)
    elif option is "v":  # view vehicle
        send_view_vehicle(client_socket, client_msg)
    elif option is "N":  # supported lines in city - ONLY BUS
        send_supported_lines(client_socket, client_msg)
    elif option is "S":  # get stops of a specific vehicle - ONLY BUS CURRENTLY
        send_stops(client_socket,client_msg)
    elif option is "E": # Exit
        delete_user_from_logs(address,client_msg[2]) #ip and username
    client_socket.close()

########################################################################################################################

class ThreadedServer:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)        
        self.sock.bind((self.host, self.port))
        print "Server's running at: " , host,":",port

    def client_listener(self):
        self.sock.listen(5)
        print "Client handler running!"
        while True:
            client, address = self.sock.accept()
            address = address[0] # (ip,port)
            print "Client connected!"
            client.settimeout(60)
            Thread(target=self.handle_client, args=(client, address)).start()

    def handle_client(self, client, address):
        size = 1024
        while True:
            try:
                if client is None:
                    break
                data = client.recv(size)
                print data
                if data:
                    if data.startswith("c"):  # c - close
                        client.close()
                        break
                    elif data.startswith('u'):  #not close but update seats -RPI
                        seat_data = data.split(';')
                        #u/c;t;C;city;n;id;L1;A;L2;B
                        update_transport_database(seat_data[1],seat_data[2],seat_data[3],seat_data[4], seat_data[5], seat_data[7],seat_data[9]) #send the information of the vehicle + actual status of a single line
                        client.send('r') #ack - recieved:
                    elif data.startswith('i'): #init vehicle
                        data = data.split(';')
                        #i;vehicle_type;vehicle_company;city;vehicle_number;amount_of_lines
                        init_vehicle(client,data[1],data[2],data[3],data[4],data[5])
                    elif data.startswith('d'):
                        data = data.split(';')
                        #d;vehicle_type;vehicle_company;city;vehicle_number;vehicle_id
                        destroy_vehicle(client,data[1],data[2],data[3],data[4],data[5])
                    else: #Client
                        data = data.split(';')
                        data.pop(0) # clears all the junk at the start, basically separates only once ; in order to get the message it
                        handle_client(address,client,data)
                else:
                    raise Exception('Client disconnected')
            except:
                client.close()
                break
        return

    def shutdown(self):
        self.socket.close()

########################################################################################################################

def main():
    client_handler = ThreadedServer(CLIENT_HOST, CLIENT_PORT)
    try:
        information = Thread(target=print_server_information)
        information.daemon = True
        information.start()
    
        client_handler.client_listener()
    except:
        client_handler.shutdown()
        sys.exit()


if __name__ == "__main__":
    main()
