
/*
TRIGGER_PIN - Arduino pin tied to trigger pin on the ultrasonic sensor
ECHO_PIN - Arduino pin tied to echo pin on the ultrasonic sensor
*/

#include <Arduino.h>
#include <NewPing.h>
#include <string>
#include <sstream> 

#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm
#define SONAR_NUM 4      // Number of sensors
#define START_PIN 4     // From which pin does the sequence of sensors start ( input, output )

//Array of sensors (objects)
NewPing sonar[SONAR_NUM]; // Each sensor's trigger pin, echo pin, and max distance to ping

//previous seats data
stringstream old_data_to_send;

//which lines does the arduino backups
string responsible_lines = "1:2";

using namespace std; // just in case

void setup() 
{
	int i;// for accessing the array of sensors
	int j;// for pin numbers

	for (i = 0, j = START_PIN; i < START_PIN * 3; i += 1, j += 2)
	{
		try
		{
			sonar[i] = new NewPing(j, j + 1, MAX_DISTANCE) //currently ports 4,5 : 6,7 : 8,9 : 10,11 
		}
		catch // if for some reason the pins weren't availbe or something like that
		{
			i -= 1; //next sensors will be init instead of the current 
		}
	}
		
		
		
	Serial.begin(9600); // The serial is actually the usb, which we want to send the data to. The usb will send it forward to the Raspberry Pi. Open serial monitor at 115200 baud to see ping results
}

void loop() 
{
	int i; 
	int distance;
	string data_to_send;
	
	for( i = 0; i < SONAR_NUM; i++)
	{
		distance = sonar[i].ping_cm(); //returns 0 if nothing found in range

		if ( distance > 0 && distance < 200)
			distance = "1"; // person was found inrange	
		else
			distnace = "0"; // person wasn't found
		
		data_to_send.append(distance);
	}
	
	if (old_data_to_send.str() != data_to_send) //if the new data isn't the same as the old data
	{
		//by the protocol
		serial.println('S' + data_to_send.length() + data_to_send + responsible_lines.length() + responsible_lines); //send data to the rpi
		old_data_to_send.str(data_to_send);
	}
		
	
	delay(500); // Wait 0.5s between pings
}


/* The Raspberry Pi code (an example of recieving messages from the arduino):

What we need at the beginning:
The first argument – /dev/ttyACM0 is the name for the USB interface used.
To find out the port name, we need to run this command in terminal without Arduino plugged in: ls /dev/tty*

ser = serial.Serial('/dev/ttyAMA0', 9600) #The connection to our arduino.

while(True):
	message1 = ser.readline() # Reading the data that sent from the arduino
	message2 = ser.readline()
	message3 = ser.readline()

	print message1, message2, message3
---------------------------------------------------------------------------
	
*/