import serial #will be valid inside the rpi
import time
import socket

PORT = 5839 #TODO: port to send data to server - DEFINE A DEFAULT PORT FOR THE SERVER
HOST = "127.0.0.1" #TODO: ip of server - DEFINE IP FOR THE SERVER
PROTOCOL_UPDATE_KEY = "9" #TODO: number of message that the server will understand that the RPI sends updated data - DEFINE A KEY FOR UPDATE THE DATA
PROTOCOL_CLOSE_KEY = "10" #TODO: message that will be sent to the server if the RPI is closing the code - DEFINE A KEY FOR CLOSING THE COMMUNICATION
SENSORS_MARK = "2" #TODO: in a string from the arduino, mentions that after the number:x comes a sensors data - DEFINE A NUMBER FOR THE MARK IN BOTH RPI AND ARDO

""" list given with the data: 1 - if a person was detected, 0 - if a person wasn't detected in sensor no.[i]
every cell in the list belongs to a sensor and its result, for example: [0] - result of first sensor, [1] - result of second sensor and so on"""
def send_sensors_data_to_server(sensors_data,stored_sensors_data,socket_to_server):
    sensors_data_in_string = "".join(sensors_data)  # turn the list to a string
    stored_sensors_data_in_string = "".join(stored_sensors_data) # turn the list to a string
    if (stored_sensors_data_in_string == "-1-1-1-1") or (stored_sensors_data_in_string != sensors_data_in_string): # if stored data is different than the new data or it's first time, else don't do anything
        server_replay  = ""
        socket_to_server.sendall(PROTOCOL_UPDATE_KEY + sensors_data_in_string)
        socket_to_server.recv(server_replay)
        print server_replay
        stored_sensors_data = sensors_data # LIST - is a mutable type - passes as a reference and can be changed, SO if the new sensors data is different than the older data, redirect the to the new list of data

"""function that will do all the work, get data from the arduino (serial) and update data to server"""
def handle_missions(socket_to_server):
    #serial - our "physical socket" to the sensors and the arduino - gets the data of the sensors from the output of the arduino code we made
    ser = serial.Serial('/dev/ttyACM0', 9600)
    stored_sensors_data = [-1,-1,-1,-1] #for the first run of the functions, then, it changes to the results themselves
    while True:
        list_of_sensors = ser.readline().split(SENSORS_MARK) # turn the given string into a list
        send_sensors_data_to_server(list_of_sensors.pop(0),stored_sensors_data,socket_to_server) #first cell in the list is always '', so remove it.
        time.sleep(2) #sleep for TWO seconds ( 2s )

def main():
    # start the socket and connect to server
    socket_to_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # setup the socket for future use
    try:
        socket_to_server.connect((HOST,PORT))
        handle_missions(socket_to_server) # pass the socket
    except:
        print "encountered an error"
    finally:
        socket_to_server.sendall(PROTOCOL_CLOSE_KEY)
        socket_to_server.close()

if __name__ == "__main__":
    main()