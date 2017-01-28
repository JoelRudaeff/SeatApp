import socket
import sys
import sqlite3
import threading
from threading import Thread

RPI_HOST = ''	# Symbolic name, meaning all available interfaces. TODO: need to change it
RPI_PORT = 8888	# TODO: Need to change it

CLIENT_HOST = '' #TODO: need to change it
CLIENT_PORT = 8888 #TODO: need to change it

def handle_client(option,Client_socket,lock):
    if (option == "1"):  # If the client sent a seats request-message to the server ("1" - protocol message for request for the seats' status)
        Client_socket.send(get_seats_from_DataBase(lock))


#function that will update the seats data inside the data-base. will be used during communication with the RPI
def update_DataBase(data,lock):
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
        conn = sqlite3.connect('DataBase.db')  # connection to the database
        db = conn.cursor()
        i = 0  # Reset the value of 'i'
        try:

            #first of all, if an error has been occured in the process, get the data before changing it so if something happened we can restore the OLD_DATA
            seat_results = db.execute("SELECT * FROM seats")
            for row in seat_results:
                old_data += str(row[0]) #append the seat's number, after converting from int to string
                old_data += str(row[1]) #append the seat's status, after converting from int to string

            #after we stored the backup data, we can change to the new data
            while i < len(data):  # For each seat (seat number and status), do:
                to_executre = "UPDATE seats SET status = " + data[i + 1] + " WHERE seat_num = " + data[i]
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 2  # The next seat's number-status element in the list
            db.commit
            conn.close()
        except: #restoring the old data
            data = old_data
            while i < len(data):  # For each seat (seat number and status), do:
                to_executre = "UPDATE seats SET status = " + data[i + 1] + " WHERE seat_num = " + data[i]
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 2  # The next seat's number-status element in the list
            db.commit
            conn.close()



def get_seats_from_DataBase(lock): #return the data about the seats, from the database, as a string
    seats = ""

    with lock.acquire():#tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
            conn = sqlite3.connect('DataBase.db') #connection to the database
            db = conn.cursor()

            data = db.execute("SELECT * FROM seats")

            for row in data:
                seats += str(row[0]) #append the seat's number, after converting from int to string
                seats += str(row[1]) #append the seat's status, after converting from int to string

            conn.close()
        except:
            seats = "" # an error has been occured
    return seats
	
	
class ThreadedServer(object):
    def __init__(self, host, port,lock):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))
        self.lock = lock

    def listen(self):
        self.sock.listen(5)
        while True:
            client, address = self.sock.accept()
            client.settimeout(60)
            threading.Thread(target = self.listenToClient,args = (client,address)).start()

    def listenToClient(self, client, address):
        size = 1024
        while True:
            try:
                data = client.recv(size)
                if data:
                    if data.startswith("2"): #exit
                        client.close()
                        break
                    else:
                        handle_client(data,client,self.lock)
                else:
                    raise Exception('Client disconnected')
            except:
                client.close()
                break
        return

def Handle_RPI(socket,lock):
    size = 1024
    while True:
        try:
            seat_data = socket.recv(size)
            if (seat_data):
                update_DataBase(seat_data,lock)
        except:
            pass

def main():
    try:
        data = ""
        s_to_RPI = socket.socket(socket.AF_INET,socket.SOCK_STREAM)  # The socket between the server and between the Raspberry pi

        # Bind socket to local host and port
        try:
            s_to_RPI.bind((RPI_HOST, RPI_PORT))

        except socket.error as msg:
            print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
            s_to_RPI.close()
            sys.exit()

        print 'Sockets bind complete'

        db_lock = threading.Lock
        thread = Thread(target=Handle_RPI, args=(s_to_RPI, db_lock))

        print 'Sockets now listening'
        thread.run()
        ThreadedServer('', CLIENT_PORT, db_lock).listen()

    except:
        sys.exit()


	
if __name__ == "__main__":
    main()
