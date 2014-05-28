import opendmx.*;
OpenDmx openDmx = null;
void setup() {
  try {
    openDmx = new OpenDmx(0);
  } catch (FTDIException e) {
    println(e);
    // do something reasonable other than exit..
    exit();
  }
}

int size = 256;
byte brightness = 0;
int increment = 1;

// we're going to send data to a universize, scaling the values from 0 to 255
// for channels 1 and 4, and every other channel is random. Obviously, change
// this to do what you need it to do.
void draw() {
  byte[] data = new byte[size];
  for (int i = 1; i < size; ++i) {
    data[i] = (byte)random(256);  
  }
  data[0] = brightness;
  data[3] = brightness;  
  
  openDmx.sendData(data, size);
  brightness += increment;
  if (brightness < 0 || brightness > 255) {
    increment *= -1;
    brightness += increment;  
  } 
  delay(5);  
}
