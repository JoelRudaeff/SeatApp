[sourcecode language=”cpp”]

/*
TRIGGER_PIN - Arduino pin tied to trigger pin on the ultrasonic sensor
ECHO_PIN - Arduino pin tied to echo pin on the ultrasonic sensor
*/

#include <NewPing.h>
#include <string>

#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm
#define SONAR_NUM 4      // Number of sensors
#dfine SENSORS_MARK 2   // a number which states that in a string to the rpi, after the x number comes a sensor's result- x0x1x1x1

//Array of sensors (objects)
NewPing sonar[SONAR_NUM]; // Each sensor's trigger pin, echo pin, and max distance to ping

using namespace std; // just in case

void setup() 
{
	for ( int i= 4; i< SONAR_NUM + 4*2 ; i+=2)
		//currently ports 4,5 : 6,7 : 8,9 : 10,11 
		sonar[i] = new NewPing(i, i+1, MAX_DISTANCE)
		
	Serial.begin(9600); // The serial is actually the usb, which we want to send the data to. The usb will send it forward to the Raspberry Pi. Open serial monitor at 115200 baud to see ping results
}

void loop() 
{
	int i; 
	int distance;
	string data_to_send;
	
	for( i = 0; i < SONAR_NUM; i++)
	{
		distance = sonar[i].ping_cm();
		if ( distance > 0 && distance < 200)
			distance = "1"; // person was found inrange	
		else
			distnace = "0"; // person wasn't found
		
		data_to_send.append(SENSORS_MARK + distance);
	}
	
	//send data to the rpi
	serial.println(data_to_send);
	
	delay(2000); // Wait 2000ms between pings
}


[/sourcecode]


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