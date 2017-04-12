import serial
import time
import socket

PORT = 8886  # TODO: port to send data to server - DEFINE A DEFAULT PORT FOR THE SERVER
HOST = '127.0.0.1'  # TODO: ip of server - DEFINE IP FOR THE SERVER
PROTOCOL_UPDATE_KEY = "u"  # TODO: number of message that the server will understand that the RPI sends updated data - DEFINE A KEY FOR UPDATE THE DATA
PROTOCOL_CLOSE_KEY = "c"  # TODO: message that will be sent to the server if the RPI is closing the code - DEFINE A KEY FOR CLOSING THE COMMUNICATION
SERIAL_PORT = 9600  # port for communication between the RPI and the Arduino
AMOUNT_OF_LINES = 10  #how many lines are in the vehicle
SEATS = {}

#vehicle information - hard coded
VEHICLE_TYPE = "bus"
VEHICLE_COMPANY = "egged"
VEHICLE_NUMBER = "263"

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


def send_sensors_data_to_server(line_number,sensors_data, stored_sensors_data, socket_to_server):
    stored_sensors_data = ""
    
    if line_number in seats: #if seats were already init on that line
        stored_sensors_data = seats[line_number]
    else:
        seats[line_number] = "-1-1-1-1" #init 
        stored_sensors_data = seats[line_number]

    sensors_data_len = len(sensors_data) #what is the number of the seats which were sent to the RPi
    stored_sensors_data_len = len(stored_sensors_data) #what is the number of the seats which were sent to the RPi
    sensors_data_in_string = "".join(sensors_data)  # turn the list to a string
    stored_sensors_data_in_string = "".join(stored_sensors_data)  # turn the list to a string


    print "stored sensors data = " + stored_sensors_data_in_string
    print "new sensors data = " + sensors_data_in_string

    if (stored_sensors_data_in_string is "-1-1-1-1") or (stored_sensors_data_in_string is not sensors_data_in_string):  # if stored data is different than the new data or it's first time, else don't do anything
        server_reply = ""
        socket_to_server.sendall(PROTOCOL_UPDATE_KEY + VEHICLE_TYPE + VEHICLE_COMPANY + VEHICLE_NUMBER + len(sensors_data_in_string) + sensors_data_in_string)
        server_reply = socket_to_server.recv(1024) #1024 - the size of the buffer which recieves the message (int bytes)
        
        if server_reply is "r": #server got the data
            print "Server got the data"
        elif server_reply is "e": #error - not being used, for future goals
            #recurrsion - try again to send data to server
            send_sensors_data_to_server(sensors_data, stored_sensors_data, socket_to_server)
        
        seats[line_number] = sensors_data_in_string #store the new results 
        
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
            list_of_sensors = list(data[4])# turn the given string into a list of chars
            print "".join(list_of_sensors)
            send_sensors_data_to_server(line_number,list_of_sensors,try_connecting_to_server())  # first cell in the list is always '', so remove it.
            time.sleep(1)  # sleep for ONE second ( 1s )
        else:
            print "error with Arduino's query"

def main():
    try:
        handle_missions()
    except:
        print "encountered an error"


if __name__ == "__main__":
    main()
