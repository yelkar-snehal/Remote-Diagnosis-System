#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11); // RX, TX
byte bytes1[] = {'s','t','a','r','t'};
byte bytes2[] = {'s','t','o','p'};
byte c; 

static int count = 1;

void setup() {
  // Open serial communications and wait for port to open:
  //Serial.begin(9600);
  /*while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }*/


  //Serial.println("Goodnight moon!");

  // set the data rate for the SoftwareSerial port
  mySerial.begin(9600);
  //mySerial.println("Hello, world?");
}

void loop() 
{ 
  // run over and over
  
  /*if (mySerial.available()) 
  {
    Serial.println("here");
    c = mySerial.read();
    Serial.write(c);
    if(c == 's')
    {
      Serial.println("reading");
      if (count == 1)
      {
        count = count + 1;
        mySerial.write("hello");
      }
    }

    if(c == 't')
    {
      Serial.println("data transfer stopped");
    }

    mySerial.write("hio");
  }*/

  //if (count == 1)
  //{
  if (c != 't' && mySerial.available())
  {
    c = mySerial.read();
    //Serial.println("here");
    //Serial.write(c);
  }
  if (c == 's')
  {
    //Serial.println("hereee");
    mySerial.write("Hello my name is arduino and i suck#");
    c = 't';
  }
  //count = 2;
  //}
}
