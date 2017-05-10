import socket
import sqlite3
import sys
import time
from Crypto.Cipher import AES
import base64
from threading import Thread, Lock

CLIENT_HOST = '192.168.1.42'  # TODO:
CLIENT_PORT = 8888

# Path files
ACCOUNTS_FOLDER = 'Accounts'
VEHICLES_FOLDER = 'Vehicles'

# Server's log for user who use the app currently 
SERVER_LOG = {}
ENCRYPTION_LOG = {}

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
def update_transport_database(vehicle_type, vehicle_company, vehicle_number, line_number, data):
    old_line_status = ""
    with lock:
        db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number + '/Transport.db'
        conn = sqlite3.connect(db_path)  # connection to the database
        c = conn.cursor()

        try:
            # first of all, if an error has been occured in the process, get the data before changing it so if something happened we can restore the OLD_DATA
            try:
                seat_results = c.execute('''SELECT status FROM seats WHERE line=?''',(line_number,))
                old_line_status = seat_results
            except:
                conn.close() #if there's no access to db, backup won't work
                lock.release()
                return

            # after we stored the backup data, we can change to the new data
            c.execute('''UPDATE seats SET status = ? WHERE line = ?;''',(data,line_number))  # Updating the data for each seat's data

            # save changes
            conn.commit()

        except:  # restoring the old data
            data = old_line_status
            c.execute('''UPDATE seats SET status = ? WHERE line = ?;''',(data, line_number))  # Updating the data for each seat's data
            # save changes and close db
            conn.commit()
        conn.close()

########################################################################################################################
#                                                   CLIENT SIDE

def encrtpyt_msg(msg,user):
    return "x;"+base64.b64encode(ENCRYPTION_LOG[user].encrypt(msg))

def decrypt_msg(data):
    # junk;X;len(user);user;encrypted_data
    if not ';' in data:
        return "ERROR"

    data.split(';',1)[1]
    if not data.startwith('X'):
        return "ERROR"

    data = data.split(';')
    user = data[2]
    decrypted = ENCRYPTION_LOG[user].decrypt(base64.b64decode(data[3]))
    return decrypted,user

def register_client_encryption(msg):
    # x;len(username);username;len(key);key
    user = msg[2]
    key = msg[4]
    cipher = AES.new(key, AES.MODE_CBC)
    ENCRYPTION_LOG[user] = cipher

def delete_user_from_logs(address,username):
    try:
        if username in SERVER_LOG and SERVER_LOG[username]==address:
            del SERVER_LOG[username]
            del ENCRYPTION_LOG[username]
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
    
def send_get_seats(username,client_socket, client_msg):
    # g;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company;len(vehicle_number); vehicle_number
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    vehicle_number = client_msg[6]

    db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number + '/Transport.db'
    conn = sqlite3.connect(db_path)  # connection to the database
    c = conn.cursor()

    seats = ""
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
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
        except:
            print "Error in send_get_seats: " + vehicle_type + "" + vehicle_company + "" + vehicle_number
            seats = ""  # an error has been occured
            conn.close()
    to_send = 'g;'+str(len(seats))+";" + str(seats)
    client_socket.sendall(encrtpyt_msg(to_send,username)) # by the protocol


def send_register_client(address,username,client_socket, client_msg):
    # r;length(username);username;length(password);password;length(email);email
    username = client_msg[2]
    password = client_msg[4]
    email = client_msg[6]

    db_path = ACCOUNTS_FOLDER + '/Accounts.db'
    conn = sqlite3.connect(db_path)  # connection to the database
    c = conn.cursor()

    confirmation = "r;0"
    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
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
        client_socket.sendall(encrtpyt_msg(confirmation,username))
    else:
        client_socket.sendall(encrtpyt_msg("r;0",username)) #failure


def send_login_client(address,username,client_socket, client_msg):
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
        client_socket.sendall(encrtpyt_msg(confirmation,username))
    else:
        client_socket.sendall(encrtpyt_msg("l;0",username)) #failure


def send_view_vehicle(username,client_socket, client_msg):
    # v;len(vehicle_type);vehicle_type;len(vehicle_company);vehicle_company;len(vehicle_number); vehicle_number
    vehicle_type = client_msg[2]
    vehicle_company = client_msg[4]
    vehicle_number = client_msg[6]

    view_vehicle = ""
    start_and_end = ""
    location_and_delay = ""

    with lock:  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            db_path = VEHICLES_FOLDER + '/' + vehicle_type + '/' + vehicle_company + '/' + vehicle_number + '/Transport.db'
            conn = sqlite3.connect(db_path)  # connection to the database
            c = conn.cursor()

            index = 0
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
    client_socket.sendall(encrtpyt_msg(view_vehicle,username))


def handle_client(address,username,client_socket,client_msg):
    option = client_msg[0]
    option = option[-1]
    if option is "g":  # If the client sent a seats request-message to the server ("g" - protocol message for request for the seats' status)
        send_get_seats(username,client_socket, client_msg)
    elif option is "r":  # register       
        send_register_client(address,username,client_socket, client_msg)
    elif option is "l":  # login       
        send_login_client(address,username,client_socket, client_msg)
    elif option is "v":  # view vehicle
        send_view_vehicle(username,client_socket, client_msg)
    elif option is "E": # Exit
        delete_user_from_logs(address,username) #ip and username
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
                    if data.startswith('c'):  # c - close
                        client.close()
                        break
                    elif data.startswith('u'):  #not close but update seats -RPI
                        seat_data = data.split(';')
                        #u/c;t;C;n;L1;A
                        update_transport_database(seat_data[1],seat_data[2],seat_data[3],seat_data[5], seat_data[7]) #send the information of the vehicle + actual status of every line
                        client.sendall('r') #ack - recieved:
                    else: #Client
                        if ';' in data: #checking for encryption request
                            if (data.split(';',1)[1]).startswith('x'):
                                register_client_encryption(data.split(';',1)[1].split(';'))
                        else:
                            data,username = decrypt_msg(data)
                            if data is "ERROR":
                                raise Exception('Client Error')

                            data.split(';')
                            data.pop(0) # clears all the junk at the start, basically separates only once ; in order to get the message it
                            handle_client(address,username,client,data)
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
