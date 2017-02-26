import socket
import sys
import sqlite3
import time
from threading import Thread, Lock

RPI_HOST = ''  # Symbolic name, meaning all available interfaces. TODO: need to change it
RPI_PORT = 8887  # TODO: Need to change it

CLIENT_HOST = ''  # TODO: need to change it
CLIENT_PORT = 8888  # TODO: need to change it

# kind of a mutex lock, will be used for DataBase access
lock = Lock()


def handle_client(option, Client_socket):
    if option is "1":  # If the client sent a seats request-message to the server ("1" - protocol message for request for the seats' status)
        Client_socket.send(get_seats_from_database())


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
        conn = sqlite3.connect('DataBase.db')  # connection to the database
        db = conn.cursor()
        i = 0  # Reset the value of 'i'
        try:

            # first of all, if an error has been occured in the process, get the data before changing it so if something happened we can restore the OLD_DATA
            seat_results = db.execute("SELECT * FROM seats")
            for row in seat_results:
                old_data += str(row[0])  # append the seat's number, after converting from int to string
                old_data += str(row[1])  # append the seat's status, after converting from int to string

            # after we stored the backup data, we can change to the new data
            while i < len(data):  # For each seat (seat number and status), do:
                to_executre = "UPDATE seats SET status = " + data[i + 1] + " WHERE seat_num = " + data[i]
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 2  # The next seat's number-status element in the list

            #save changes and close db
            conn.commit()
            conn.close()

        except:  # restoring the old data
            data = old_data
            while i < len(data):  # For each seat (seat number and status), do:
                to_executre = "UPDATE seats SET status = " + data[i + 1] + " WHERE seat_num = " + data[i]
                db.execute(to_executre)  # Updating the data for each seat's data
                i += 2  # The next seat's number-status element in the list

            # save changes and close db
            conn.commit()
            conn.close()


def get_seats_from_database():  # return the data about the seats, from the database, as a string
    seats = ""

    with lock.acquire():  # tries to gain access to the data base. WITH - whenever it's done, release the lock - it's like the state "USING" in c#
        try:
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


class ThreadedServer:
    def __init__(self, host):
        self.host = host
        self.port = CLIENT_PORT
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.host, self.port))

    def listen(self):
        self.sock.listen(5)
        while True:
            client, address = self.sock.accept()
            client.settimeout(60)
            Thread(target=self.listen_to_client, args=(client, address)).start()

    def listen_to_client(self, client, address):
        size = 1024
        while True:
            try:
                data = client.recv(size)
                if data:
                    if data.startswith("2"):  # exit
                        client.close()
                        break
                    else:
                        handle_client(data, client)
                else:
                    raise Exception('Client disconnected')
            except:
                client.close()
                break
        return


def handle_rpi():
    size = 1024  # maximum of message size - TODO: define a size
    rpi_listener = socket.socket(socket.AF_INET,socket.SOCK_STREAM)  # The socket between the server and between the Raspberry pi
    rpi_socket_rdy = False
    s_to_rpi = None

    # Bind socket to local host and port
    while not (rpi_socket_rdy is True):
        try:
            rpi_listener.bind(('', RPI_PORT))
            print 'Socket bind complete'
            print 'Socket now listening'
            rpi_socket_rdy = True  # break
        except socket.error:
            print 'Bind to rpi socket failed.'
            time.sleep(1)

    while True:
		#accept new RPI connection
		s_to_rpi = rpi_listener.accept()
        try:
            seat_data = s_to_rpi.recv(size)
            if seat_data:
                if seat_data is not "EXIT":  # TODO: define protocol
                    update_database(seat_data)
                else:
                    #finish
					s_to_rpi.close()
                    break
        except:  # TODO: define what to do when error has been occurred
            pass


def main():
    try:
        rpi_handler = Thread(target=handle_rpi, args=())
        rpi_handler.run()
        ThreadedServer('').listen()
        rpi_handler.join()

    except:
        sys.exit()


if __name__ == "__main__":
    main()
