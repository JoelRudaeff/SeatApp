#include <Arduino.h>
#include <NewPing.h>
     
#define TRIGGER_PIN1  13  
#define ECHO_PIN1     12
#define TRIGGER_PIN2 9   
#define ECHO_PIN2  8
#define TRIGGER_PIN3 5
#define ECHO_PIN3 4
#define MAX_DISTANCE 200 

#define SONAR_NUM 3      // Number of sensors
#define SERIAL_PORT 9600

NewPing sonar[SONAR_NUM] = {NewPing(TRIGGER_PIN1, ECHO_PIN1, MAX_DISTANCE), NewPing(TRIGGER_PIN2, ECHO_PIN2, MAX_DISTANCE),  NewPing(TRIGGER_PIN3, ECHO_PIN3, MAX_DISTANCE)}; 

//previous seats data
String old_data_to_send;

//which lines does the current arduino provides
String responsible_line = "1";

void setup() 
{
  Serial.begin(SERIAL_PORT); // The serial is actually the usb, which we want to send the data to. The usb will send it forward to the Raspberry Pi. Open serial monitor at 9600 baud to see ping results
}

void loop() 
{
  int i; 
  int distance;
  String data_to_send;
  String distance_flag; //The distance checking
  String data_to_RPi = "";
  
  for (i = 0; i < SONAR_NUM; i++)
  {
    distance = sonar[i].ping_cm(); //returns 0 if nothing found in range

    if (distance > 0 && distance < 200)   
      distance_flag = "1"; // person was found inrange    
    else  
      distance_flag = "0"; // person wasn't found
    
    data_to_send += distance_flag; //+= it's like ".append()" on c++
  }

  if (old_data_to_send != data_to_send) //if the new data isn't the same as the old data
  {
    //by the protocol
    data_to_RPi += "s;";
    data_to_RPi += responsible_lines.length();
    data_to_RPi += ";" + responsible_line;
    data_to_RPi += data_to_send.length();
    data_to_RPi += ";";
    data_to_RPi += data_to_send + ";";


    Serial.println(data_to_RPI); //send data to the rpi
    old_data_to_send = data_to_send;
  }
  
  delay(1000); // Wait 1s between pings
}
