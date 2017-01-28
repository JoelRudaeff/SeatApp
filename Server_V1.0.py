import socket
import sys
import sqlite3


RPI_HOST = ''	# Symbolic name, meaning all available interfaces. TODO: need to change it
RPI_PORT = 8888	# TODO: Need to change it

CLIENT_HOST = '' #TODO: need to change it
CLIENT_PORT = 8888 #TODO: need to change it

def handle_client(option,Client_socket, RPI_socket):
    if (option == "1"):  # If the client sent a seats request-message to the server ("1" - protocol message for request for the seats' status)
        Client_socket.send(get_seats_from_RPI(RPI_socket))

def get_seats_from_RPI(RPI_socket):

    #TODO: maybe it's needed - RPI_socket.send("100") #A protocol message for request the status of the seats
    i = 0

    #seats : strings like: x1y0z1
    seats = get_seats_from_DataBase()

    while i < len(seats)-1:
        if not(seats[i+1] == "1" or seats[i+1] == "0"): #checking if the seat's status is ok for each seat
            return "-1"
        i += 2

    """ seats = RPI_socket.recv(1024).split() #Turn the string, which the Raspberry-Pi sent to the server, to a list
    for seat in seats:
        if not(seat == "1" or seat == "0"): #checking if the seat's mark is ok
            return "-1" # "-1" it's a protocol error message if there was an error in the data of the seat's status"""

    return seats

def update_DataBase(data):

    length = len(data)

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

    conn = sqlite3.connect('DataBase.db') #connection to the database
    db = conn.cursor()
    i = 0 # Reset the value of 'i'

    while i < length: # For each seat (seat number and status), do:
        db.execute("UPDATE seats SET status = " + data[i+1] + "WHERE seat_num = " + data[i] ) #Updating the data for each seat's data
        db.commit
        i+=2 # The next seat's number-status element in the list

    conn.close()

def get_seats_from_DataBase(): #return the data about the seats, from the database, as a string
    seats = ""

    conn = sqlite3.connect('DataBase.db') #connection to the database
    db = conn.cursor()

    data = db.execute("SELECT * FROM seats")

    for row in data:
        seats += str(row[0]) #append the seat's number, after converting from int to string
        seats += str(row[1]) #append the seat's status, after converting from int to string

    conn.close()
    return seats

def main():
    data = "0"

    s_to_RPI = socket.socket(socket.AF_INET, socket.SOCK_STREAM) #The socket between the server and between the Raspberry pi
    s_to_Client = socket.socket(socket.AF_INET, socket.SOCK_STREAM) #The socket between the server and between the client

    #Bind socket to local host and port
    try:
        s_to_RPI.bind((RPI_HOST, RPI_PORT))
        s_to_Client.bind((CLIENT_HOST, CLIENT_PORT))

    except socket.error as msg:
        print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
        s_to_Client.close()
        s_to_RPI.close()
        sys.exit()

    print 'Sockets bind complete'

    #Start listening on socket
    s_to_RPI.listen(5)
    s_to_Client.listen(5)

    print 'Sockets now listening'

    #The accepting
    conn1, addr1 = s_to_RPI.accept()
    print 'Connected with ' + addr1[0] + ':' + str(addr1[1])

    conn2, addr2 = s_to_Client.accept()
    print 'Connected with ' + addr2[0] + ':' + str(addr2[1])

    #now keep talking with the client
    while data != "2": #while the client doesn't quit ("2" - protocol message for quit)
        data = s_to_Client.recv(1024) #Recieving the data which sent from the client, who wants to get the data about the seats
        
        seats = s_to_RPI.recv(1024)
        update_DataBase(seats) #just updating the seats database
        handle_client(data,s_to_Client,s_to_RPI)

    #Closing the connection between the server and between the Client and the Raspberry Pi
    conn1.close()
    conn2.close()

    s_to_RPI.close()
    s_to_Client.close()

if __name__ == "__main__":
    main()