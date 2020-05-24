#include <elapsedMillis.h>
const int pulsePin = A5; // Input signal connected to Pin 8 of Arduino

int pulseHigh; // Integer variable to capture High time of the incoming pulse
int pulseLow; // Integer variable to capture Low time of the incoming pulse
float pulseTotal; // Float variable to capture Total time of the incoming pulse
float frequency; // Calculated Frequency

bool f = false;

elapsedMillis timeElapsed;
unsigned int interval = 60000;
int cnt = 0;

void setup() {
  Serial.begin(9600);
  //pinMode(3,OUTPUT);
  pinMode(pulsePin,INPUT);
 // pinMode(2,INPUT);
  //digitalWrite(13, LOW);
 /* delay(60000);
  digitalWrite(3, LOW);*/

}

void loop() 
{
  int avg = 0;
  int x = 0;

  float reade = analogRead(A0);
    avg+=analogRead(A5);
    //x += digitalRead(A0);
  
  /*Serial.print(0);  // To fre);
  /*for(int i=0;i<90;i++){eze the lower limit
Serial.print(" ");
Serial.print(1);  // To freeze the upper limit
Serial.print(" ");*///
Serial.println(analogRead(A5)*0.0048);
//Serial.print(digitalRead(pulsePin));
//Serial.print(" ");
//Serial.println(analogRead(A0)*0.0048);


//ardprintf("var %d %d %d", cnt,x,avg);

/*pulseHigh = pulseIn(pulsePin,HIGH);
    pulseLow = pulseIn(pulsePin,LOW);
   
    Serial.println(pulseHigh);
  Serial.println(pulseLow);
  
    pulseTotal = pulseHigh + pulseLow; // Time period of the pulse in microseconds
    frequency=1000000/pulseTotal; // Frequency in Hertz (Hz)
 Serial.print("Frequency:");
 Serial.println(frequency);
  
  //Serial.ppulserintln(x/90);*/
//delay(10);
/*while(timeElapsed < interval)
{
 if(analogRead(pulsePin) >= reade && !f)
 {
  cnt++;
  f = true;
  }
  if(analogRead(pulsePin) < reade)
  {
    f = false;
    }
}

 Serial.println(cnt);
/*
  if (digitalRead(2) == HIGH)
  {
    Serial.println("high");
  }
  else
  {
    Serial.println("low");
  }*/
}
