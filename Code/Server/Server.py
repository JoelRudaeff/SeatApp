import socket
import sqlite3
import sys
from threading import Thread, Lock

RPI_HOST = '10.10.0.14'  # Symbolic name, meaning all available interfaces. TODO: need to change it
RPI_PORT = 8886  # TODO: Need to change it

CLIENT_HOST = '10.10.0.14'  # TODO: need to change it
CLIENT_PORT = 8888  # TODO: need to change it

# kind of a mutex lock, will be used for DataBase access
lock = Lock()


########################################################################################################################
#                                                   RPI_SIDE

# function that will update the seats data inside the data-base. will be used during communication with the RPI
def update_database(data):
    old_data = ""
    """
	d = []
	i = 0
	j = 0

	An another method to arrange the data to more understandable way
	while i < length: # For each seat (seat number and status), do:
		dict = {'seat_num' : data[i], 'status' : data[i+1]}
		d[j].append(dict) # Append the data of each seat (number & status) to the dictionary
		j+=1 # The next cell in the list
		i+=2 # The next seat's number-status element to append the dictionaries list
	"""

    with lock:
        # TODO: conn = sqlite3.connect(vehicle_type+'\'+vehicle_company+'\'vehicle_number+'\'+'Seats.db')
        conn = sqlite3.connect('C:\\Users\\User\\Desktop\\Liran\\Studing - Liran\\Magshimim\\12th Grade\\Joel_and_Liran_Magshimim_Porject\\Code\\Server\\DataBase.db')  # connection to the database (just for trying! TODO: change this path)
        db = conn.cursor()
        i = 0  # Reset the value of 'i'
        try:

            # first of all, if an error has been occured in the process, get the data before changing it so if something happened we can restore the OLD_DATA
            seat_results = db.execute("SELECT * FROM seats")
            for row in seat_results:
                old_data += str(row[1])  # append the seat's status, after converting from int to string, because the data includes only the seats' status

            # after we stored the backup data, we can change to the new data
            while i < len(data):  # For each seat (seat number and status), do:
                to_executre = "UPDATE seats SET status = " + str(data[i]) + " WHERE seat_num = " + str(i+1)
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 1  # The next seat's number-status element in the list

            # save changes and close db
            conn.commit()
            conn.close()

        except:  # restoring the old data
            data = old_data
            while i < len(old_data):  # For each seat (seat number and status), do:
                to_execute = "UPDATE seats SET status = " + str(data[i]) + " WHERE seat_num = " + str(i+1)
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 1  # The next seat's number-status element in the list

            # save changes and close db
            conn.commit()
            conn.close()

########################################################################################################################
#                                                   CLIENT SIDE


def get_seats_from_database_by_vehicle_type(msg):  # return the data about the seats, from the database, as a string
    seats = ""

    with lock.acquire():  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            #TODO: conn = sqlite3.connect(vehicle_type+'\'+vehicle_company+'\'vehicle_number+'\'+'Seats.db')
            conn = sqlite3.connect('DataBase.db')  # connection to the database
            db = conn.cursor()

            data = db.execute("SELECT * FROM seats")

            for row in data:
                seats += str(row[0])  # append the seat's number, after converting from int to string
                seats += str(row[1])  # append the seat's status, after converting from int to string

            conn.close()
        except:
            seats = ""  # an error has been occured
    return seats

def send_client_seats(client_socket,client_msg ):
    client_msg = client_msg.split(";") #dividing the msg into a list to include the parts separately
    client_socket.sendall(get_seats_from_database_by_vehicle_type(client_msg))


def register_client(client_socket,client_msg):
    #Username, Password , Email
    client_msg = client_msg.split(";")  # dividing the msg into a list to include the parts separately
    print client_msg
    #TODO: check if username is available, if yes - create a new entry with username,password,email and return ack to the client

def login_client(client_socket,client_msg):
    # Username, Password
    client_msg = client_msg.split(";")  # dividing the msg into a list to include the parts separately
    #TODO: check if username + password appears on the database, if exists send ack to the client

def handle_client(client_msg, client_socket):
    option = client_msg[0]
    if option is "1":  # If the client sent a seats request-message to the server ("1" - protocol message for request for the seats' status)
        send_client_seats(client_socket,client_msg[1:])
    if option is "2":  # register
        register_client(client_socket, client_msg[1:])
    if option is "3":  # login
        login_client(client_socket, client_msg[1:])


########################################################################################################################

class ThreadedServer:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print host, port
        self.sock.bind((self.host, self.port))

    def rpi_listener(self):
        self.sock.listen(5)
        while True:
            rpi, address = self.sock.accept()
            print "RPI connected"
            rpi.settimeout(60)
            Thread(target=self.handle_rpi, args=(rpi, address)).start()

    def handle_rpi(self, rpi, address):
        size = 1024
        while True:
            try:
                seat_data = rpi.recv(size)
                if seat_data:
                    if not seat_data.startswith('c') and seat_data.startswith('u'):  # TODO: define protocol - not close but update
                        update_database(seat_data[1:])
                        rpi.sendall('r')
                    else:  # close
                        raise Exception('Rpi disconnected')
            except:
                rpi.close()
                break
        return

    def client_listener(self):
        self.sock.listen(5)
        while True:
            client, address = self.sock.accept()
            client.settimeout(60)
            Thread(target=self.handle_client, args=(client, address)).start()

    def handle_client(self, client, address):
        size = 1024
        while True:
            try:
                data = client.recv(size)
                if data:
                    if data.startswith("c"):  # c - close
                        client.close()
                        break
                    elif data.startswith("g"): # g - get
                        handle_client(data[1:], client)
                else:
                    raise Exception('Client disconnected')
            except:
                client.close()
                break
        return

########################################################################################################################

def main():
    try:
        rpi_handler = ThreadedServer(RPI_HOST,RPI_PORT)
        client_handler = ThreadedServer(CLIENT_HOST,CLIENT_PORT)

        t = Thread(target=rpi_handler.rpi_listener())
        t.daemon = True # means, background thread - will be closed once the program is closed
        t.start()
        client_handler.client_listener()

    except:
        sys.exit()


if __name__ == "__main__":
    main()
