/*
 *
 * Project Name:     Neural Network based Motorcycle Ecosystem
 * Author List:      Rohan Mahajan
 * Filename:         Getting_BPM_to_Monitor_BL.ino
 * Function:         void setup(), void loop()
 * Global Variables: const int PulseWire, const int LED13, int Threshold, PulseSensorPlayground pulseSensor
 *
 */

#define USE_ARDUINO_INTERRUPTS true    // low-level interrupts for most acurate BPM math.
#include <PulseSensorPlayground.h>     // Includes the PulseSensorPlayground Library. 
#include <SoftwareSerial.h>

SoftwareSerial mySerial(6,5);

//  Variables
const int PulseWire = 0;       // PulseSensor PURPLE WIRE connected to ANALOG PIN 0
const int LED13 = 13;          // The on-board Arduino LED, close to PIN 13.
int Threshold = 550;           // Determine which Signal to "count as a beat" and which to ignore.
                               
PulseSensorPlayground pulseSensor;  // Creates an instance of the PulseSensorPlayground object called "pulseSensor"


void setup() {   

  mySerial.begin(9600);          // For Bluetooth  Serial Communication

  // Configure the PulseSensor object, by assigning our variables to it. 
  pulseSensor.analogInput(PulseWire);   
  pulseSensor.blinkOnPulse(LED13);
  pulseSensor.setThreshold(Threshold);   

   if (pulseSensor.begin()) {
  }
}



void loop() {

 int myBPM = pulseSensor.getBeatsPerMinute();  // Calls function on our pulseSensor object that returns BPM as an "int".
                                               // "myBPM" hold this BPM value now. 

if (pulseSensor.sawStartOfBeat()) {// Constantly test to see if "a beat happened".                        
 if(myBPM > 130){
   mySerial.println(myBPM); // Print BPM value
   delay(5000);
 }
 
}

  delay(20);

}

  
