import serial
import time
import socket

PORT = 8886  # TODO: port to send data to server - DEFINE A DEFAULT PORT FOR THE SERVER
HOST = '10.10.0.14'  # TODO: ip of server - DEFINE IP FOR THE SERVER
PROTOCOL_UPDATE_KEY = "u"  # TODO: number of message that the server will understand that the RPI sends updated data - DEFINE A KEY FOR UPDATE THE DATA
PROTOCOL_CLOSE_KEY = "c"  # TODO: message that will be sent to the server if the RPI is closing the code - DEFINE A KEY FOR CLOSING THE COMMUNICATION
SERIAL_PORT = 9600  # port for communication between the RPI and the Arduino
AMOUNT_OF_LINES = 10  #how many lines are in the vehicle
VEHICLE_TYPE = "BUS" #if it's a train, we won't use the configuration file
SEAT_LIST = []
#
def read_configuration_file():
    content = ""
    with open("RPI_Configuration.txt") as f:
        content = f.readlines()

    lines = [x.strip() for x in content]

    passed_lines = 0
    size = 0
    index = 0
    for line in lines:
        if passed_lines >=2: # amount of lines needed to be passed
            if "Amount of lines: " in line:
                size = line.split("Amount of lines: ")[1]
            else:
                SEAT_LIST.insert(index, [0 for x in xrange(line.split(",")[1])]) # creates an empty list with the amount of seats for each line
                index+=1
        passed_lines += 1


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


def parse_server_response (server_reply):
    if server_reply is "r": #server got the data
        print "Server got the data"


""" list given with the data: 1 - if a person was detected, 0 - if a person wasn't detected in sensor no.[i]
every cell in the list belongs to a sensor and its result, for example: [0] - result of first sensor, [1] - result of second sensor and so on"""


def send_sensors_data_to_server(sensors_data, stored_sensors_data, socket_to_server):
    sensors_data_len = int(sensors_data[2]) #what is the number of the seats which were sent to the RPi
    stored_sensors_data_len = len(stored_sensors_data) #what is the number of the seats which were sent to the RPi
    sensors_data_in_string = "".join(sensors_data)  # turn the list to a string
    stored_sensors_data_in_string = "".join(stored_sensors_data)  # turn the list to a string

    if(stored_sensors_data_in_string != "-1-1-1-1"):
        stored_sensors_data_len = int(stored_sensors_data[2]) #what is the number of the seats which were sent to the RPi
        stored_sensors_data_in_string = stored_sensors_data_in_string[4:4+sensors_data_len]


    sensors_data_in_string = sensors_data_in_string[4:4+sensors_data_len]
    print "stored sensors data = " + stored_sensors_data_in_string
    print "sensors_data_in_string = " + sensors_data_in_string

    if (stored_sensors_data_in_string == "-1-1-1-1") or (stored_sensors_data_in_string != sensors_data_in_string):  # if stored data is different than the new data or it's first time, else don't do anything
        server_reply = ""
        socket_to_server.sendall(PROTOCOL_UPDATE_KEY + sensors_data_in_string)
        server_reply = socket_to_server.recv(1024) #1024 - the size of the buffer which recieves the message (int bytes)
        parse_server_response(server_reply)
        print "server replay = " + server_reply + "\n"
        stored_sensors_data = sensors_data  # LIST - is a mutable type - passes as a reference and can be changed, SO if the new sensors data is different than the older data, redirect the to the new list of data
        return stored_sensors_data
    socket_to_server.sendall(PROTOCOL_CLOSE_KEY)
    socket_to_server.close()


"""function that will do all the work, get data from the arduino (serial) and update data to server"""


def handle_missions():
    ser = serial.Serial("COM5", SERIAL_PORT) # serial - our "physical socket" to the sensors and the arduino - gets the data of the sensors from the output of the arduino code we made
    stored_sensors_data = ["-1","-1","-1","-1"]  # for the first run of the functions, then, it changes to the results themselves

    while True:
        list_of_sensors = list(ser.readline())  # turn the given string into a list of chars
        print "".join(list_of_sensors)
        stored_sensors_data = send_sensors_data_to_server(list_of_sensors, stored_sensors_data,try_connecting_to_server())  # first cell in the list is always '', so remove it.
        time.sleep(1)  # sleep for ONE second ( 1s )


def main():
    try:
        #if VEHICLE_TYPE is not "train":
            #read_configuration_file()
        handle_missions()
    except:
        print "encountered an error"


if __name__ == "__main__":
    main()
