#include <Arduino.h>
#include <NewPing.h>
#include <String.h>

#define TRIGGER_PIN  12  
#define ECHO_PIN     11  
#define MAX_DISTANCE 200 

#define SONAR_NUM 1      // Number of sensors
#define START_PIN 11     // From which pin does the sequence of sensors start ( input, output )
#define SERIAL_PORT 9600

NewPing sonar[] = {NewPing(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE)}; 

//previous seats data
String old_data_to_send;

//which lines does the arduino backups
String responsible_lines = "1:2";

void setup() 
{
  Serial.begin(SERIAL_PORT); // The serial is actually the usb, which we want to send the data to. The usb will send it forward to the Raspberry Pi. Open serial monitor at 115200 baud to see ping results
}

void loop() 
{
  int i; 
  int distance;
  String data_to_send;
  String dis; //The distance checking
  String send_to_RPi = "";
  
  for (i = 0; i < SONAR_NUM; i++)
  {
    distance = sonar[i].ping_cm(); //returns 0 if nothing found in range

    if (distance > 0 && distance < 200)
    {
      dis = "1"; // person was found inrange 
      
      Serial.print("Distance: ");
      Serial.print(distance);
      Serial.println("cm");
    }
    
    else
    {
      dis = "0"; // person wasn't found
    }
        
    data_to_send += dis; //+= it's like ".append()" in c++
  }

  if (old_data_to_send != data_to_send) //if the new data isn't the same as the old data
  {
    //by the protocol
    send_to_RPi += "s;";
    send_to_RPi += data_to_send.length();
    send_to_RPi += ";";
    send_to_RPi += data_to_send + ";";
    send_to_RPi += responsible_lines.length();
    send_to_RPi += ";" + responsible_lines;

    Serial.println(send_to_RPi); //send data to the rpi
    old_data_to_send = data_to_send;
  }
  
  delay(1000); // Wait 1s between pings
}