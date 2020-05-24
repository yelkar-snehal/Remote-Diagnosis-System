#include <OneWire.h>
#include <DallasTemperature.h>
#include <SoftwareSerial.h>


SoftwareSerial EEBlue(10, 11); // RX | TX
 
// Data wire is plugged into pin 2 on the Arduino
#define ONE_WIRE_BUS 2
 
// Setup a oneWire instance to communicate with any OneWire devices 
// (not just Maxim/Dallas temperature ICs)
OneWire oneWire(ONE_WIRE_BUS);
 
// Pass our oneWire reference to Dallas Temperature.
DallasTemperature sensors(&oneWire);

//leds condn
int led1 = 3;
int led2 = 4;
int led3 = 5;
int temp = 6;

//analog ips
int pulsein = A5;

//comparator threshold
float th;

//buffer
int Vata = 76;
int Pitta = 74;
int Kapha = 72;
int far = 97;

//period
uint32_t per = 60000L;

//flag
bool f = false;
bool l = false;

//
int cnt = 0;
int var = -1;
float cel = 0.0;
float tcnt = 0.0;
byte c;

//data str
String str;


void setup() 
{
  
  pinMode(led1, INPUT);
  pinMode(led2, INPUT);
  pinMode(led3, INPUT);
  pinMode(temp, INPUT);

  pinMode(pulsein, INPUT);
  

  th = analogRead(A0);

  // Start up the library
  sensors.begin();

  //Serial.begin(9600);
  EEBlue.begin(9600);  //Default Baud for comm, it may be different for your Module. 
  //Serial.println("The bluetooth gates are open.\n Connect to HC-05 from any other bluetooth device with 1234 as pairing key!.");

}

int CountBPM()
{
  int ret = 0;
  
    for(uint32_t tStart = millis(); (millis()-tStart) < per ;)
    {
        if(analogRead(pulsein) >= th && !f)
        {
                ret++;
                f = true;
        }
        if(analogRead(pulsein) < th)
        {
                f = false;
        }
    }
    
    l = true;

 return ret;
}

float CountTemp()
{
  //wait for 30secs before counting
  delay(15000);
  
  // call sensors.requestTemperatures() to issue a global temperature
  // request to all devices on the bus
  //Serial.print(" Requesting temperatures...");
  sensors.requestTemperatures(); // Send the command to get temperatures
  //Serial.println("DONE");

  //Serial.print("Temperature is: ");
  cel = (sensors.getTempCByIndex(0)); // Why "byIndex"? 
  far = (cel*1.8) + 32;
  //Serial.print(far);
  //Serial.println(" °F");
    // You can have more than one IC on the same bus. 
    // 0 refers to the first IC on the wire

  return far; 
    
}

void TransferData(int v, int p, int k, float far)
{
  //Serial.println("begin!");
  String s = String(v) +","+ String(p) +","+ String(k) +","+ String(far) + "#";
  //delay(1000);
  if (c != 't' && EEBlue.available())
  {
    c = EEBlue.read();
    //Serial.println("here");
    //Serial.write(c);
  }
  if (c == 's')
  {
    //Serial.println("hereee");
    
    //byte b[5];
//    const char* str = char(s);
    EEBlue.print(s);
    c = 't';
  }
}

void loop() 
{
  //Serial.println("loop");
  //Serial.println(digitalRead());
  

     

      //depending on switch state assgin the varibales
      if (digitalRead(led1) == HIGH)
      {
        var  = 0;
       
      }
      else if (digitalRead(led2) == HIGH)
      {
        var = 1;
      }
      
      else if (digitalRead(led3) == HIGH)
      {
       var = 2;
      }
     
      else if (digitalRead(temp) == HIGH)
      {
       var = 3;
      }

      else
      {
        var = 4;
      }

      //based on the variable measure V/P/K
      switch(var)
      {
        case 0:
        
           cnt = CountBPM();
           if (cnt != 0)
           {
            Vata = cnt;
           }
           /*Serial.print("Vata measured: ");
           Serial.println(cnt);
           Serial.println("Wait for 1 min");*/
           delay(60000);
           //Serial.println("Give next input");
           break;
           
        case 1:
        
           cnt = CountBPM();
           if (cnt != 0)
           {
            Pitta = cnt;
           }
           /*Serial.print("Pitta measured: ");
           Serial.println(cnt);
           Serial.println("Wait for 1 min");*/
           delay(60000);
           //Serial.println("Give next input");
           break;

        case 2:
        
           cnt = CountBPM();
           if (cnt != 0)
           {
            Kapha = cnt;
           }
           /*Serial.print("Kapha measured: ");
           Serial.println(cnt);
           Serial.println("Wait for 1 min");*/
           delay(60000);
           //Serial.println("Give next input");
           break;

        case 3:

           tcnt = CountTemp();
           if (tcnt != -196.00)
           {
            //Serial.println("here");
            far = tcnt;
           }
           //Serial.println("Turn off the switch");
           delay(10000);
           
           break;

        default:

          //Serial.println("No input given");
          break;
      }

      
      TransferData(Vata,Pitta,Kapha,far);
}














//////////////////////////////////////////////////////////////////////
/*
 *
              Serial.print("if high ret");
              Serial.println(ret);

              
              Serial.print("if high ret");
              Serial.println(ret);

              
              Serial.print("if high ret");
              Serial.println(ret);

              
              Serial.print("if high ret");
              Serial.println(ret);

              
              Serial.print("if high ret");
              Serial.println(ret);

              Serial.print("tStart");
      Serial.println(tStart);
      Serial.print("ms");
      Serial.println(millis());
      Serial.print("diff");
      Serial.println(millis() - tStart);

      
       
    
*/
