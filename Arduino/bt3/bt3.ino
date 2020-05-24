#include <SoftwareSerial.h>
const int RX_PIN = 10;
const int TX_PIN = 11;
SoftwareSerial blueTooth(RX_PIN, TX_PIN);
char commandChar;

void setup () {
  Serial.begin(9600);
  Serial.println("Let's start!");
  blueTooth.begin(9600);
  blueTooth.println("Hello, world?");
}

void loop () {
 if (blueTooth.available())
    Serial.write(blueTooth.read());
  if (blueTooth.available())
    blueTooth.write(Serial.read());
}
