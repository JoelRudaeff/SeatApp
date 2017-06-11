import serial
import time
import socket
import atexit

PORT = 8888  # TODO: port to send data to server - DEFINE A DEFAULT PORT FOR THE SERVER
HOST = '10.10.0.14'  # TODO: ip of server - DEFINE IP FOR THE SERVER
PROTOCOL_UPDATE_KEY = "u"  # TODO: number of message that the server will understand that the RPI sends updated data - DEFINE A KEY FOR UPDATE THE DATA
PROTOCOL_CLOSE_KEY = "c"  # TODO: message that will be sent to the server if the RPI is closing the code - DEFINE A KEY FOR CLOSING THE COMMUNICATION
SERIAL_PORT = 9600  # port for communication between the RPI and the Arduino
AMOUNT_OF_LINES = 10  #how many lines are in the vehicle
SEATS = {}

#vehicle information - hard coded
VEHICLE_TYPE = "Bus"
VEHICLE_COMPANY = "Egged"
CITY = "Karmiel"
VEHICLE_NUMBER = "263"
VEHICLE_ID = ""


def init_number():
    pass

def init_city():
    pass

def init_vehicle(socket):
    #i;vehicle_type;vehicle_company;city;vehicle_number
    socket.sendall("i;" + VEHICLE_TYPE + ";" + VEHICLE_COMPANY + ";" + CITY + ";" + VEHICLE_NUMBER + ";" + AMOUNT_OF_LINES)
    flag = False
    while (flag == False):
        data = socket.recv(1024)
        if data.startswith("i"):
            #i;id
            VEHICLE_ID = data.split(";")[1]
            if VEHICLE_ID != 0:
                flag = True
    
    
def destroy_vehicle(socket):
    #d;vehicle_type;vehicle_company;city;vehicle_number;vehicle_id
    socket.sendall("d;" + VEHICLE_TYPE + ";" + VEHICLE_COMPANY + ";" + CITY + ";" + VEHICLE_NUMBER + ";" + VEHICLE_ID)
    flag = False
    while (flag == False):
        data = socket.recv(1024)
        if data.startswith("d"):
            #d;id
            if VEHICLE_ID == data.split(";")[1]
                flag = True






# will try to connect to the server
def try_connecting_to_server():
    # start the socket to the server
    socket_to_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # setup the socket for future use
    connected = False

    while connected == False:
        try:
            socket_to_server.connect((HOST, PORT))  # connect to the server
            connected = True
            print "connected to server!"
        except:
            print "couldn't connect to server, sleeping for 0.5 seconds"
            time.sleep(0.5)

    return socket_to_server


    
""" list given with the data: 1 - if a person was detected, 0 - if a person wasn't detected in sensor no.[i]
every cell in the list belongs to a sensor and its result, for example: [0] - result of first sensor, [1] - result of second sensor and so on"""


def send_sensors_data_to_server(line_number,sensors_data, socket_to_server):
    stored_sensors_data = ""

    if line_number in SEATS: #if seats were already init on that line
        stored_sensors_data = SEATS[line_number]
    else:
        SEATS[line_number] = "-1-1-1-1" #init
        stored_sensors_data = SEATS[line_number]

    print "stored sensors data = " + stored_sensors_data
    print "new sensors data = " + sensors_data

    if (stored_sensors_data is "-1-1-1-1") or (stored_sensors_data is not sensors_data):  # if stored data is different than the new data or it's first time, else don't do anything
        server_reply = ""
        socket_to_server.sendall(PROTOCOL_UPDATE_KEY + ";" + VEHICLE_TYPE + ";" + VEHICLE_COMPANY + ";" + VEHICLE_NUMBER + ";" + VEHICLE_ID + ";" +str(len(line_number)) + ";" + line_number + ";" + str(len(sensors_data)) + ";" + sensors_data)
        server_reply = socket_to_server.recv(1024) #1024 - the size of the buffer which recieves the message (int bytes)
        
        if server_reply == "r": #server got the data
            print "Server got the data"
        elif server_reply == "e": #error - not being used, for future goals
            #recurrsion - try again to send data to server
            send_sensors_data_to_server(line_number,sensors_data, socket_to_server)
        
        SEATS[line_number] = sensors_data #store the new results
        
    socket_to_server.sendall(PROTOCOL_CLOSE_KEY)
    socket_to_server.close()


"""function that will do all the work, get data from the arduino (serial) and update data to server"""


def handle_missions():
    ser = serial.Serial("COM5", SERIAL_PORT) # serial - our "physical socket" to the sensors and the arduino - gets the data of the sensors from the output of the arduino code we made

    while True:
        #s;L1;R;L2;A
        data = ser.readline()
        if data.startswith('s'):
            data = data.split(';') #split the data into the relevant parts
            line_number = str(data[2]) # save in string, helps with the insertions proccess 
            list_of_sensors = str(data[4])# turn the given string into a list of chars

            print list_of_sensors
            send_sensors_data_to_server(line_number,list_of_sensors, try_connecting_to_server())  # first cell in the list is always '', so remove it.
            time.sleep(1)  # sleep for ONE second ( 1s )
        else:
            print "error with Arduino's query"

def main():
    try:
        init_vehicle()
        handle_missions()
    except:
        print "encountered an error"

    atexit.register(destroy_vehicle,try_connecting_to_server())

if __name__ == "__main__":
    main()
