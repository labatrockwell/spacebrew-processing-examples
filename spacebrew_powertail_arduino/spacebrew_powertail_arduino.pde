String server="localhost";
String name="processingPowerTail";
String description ="This is an example client that has a powertail and a light atteched and it turns on the light via the power tail.";
import processing.serial.*;

Serial myPort;  // Create object from Serial class
boolean powerSwitchState = false;

SpacebrewClient c;
int sec0;

boolean toSend = false;

void setup() {
  frameRate(240);
  size(600, 400);
  
  c = new SpacebrewClient( this );
 
  c.addSubscribe( "power", "boolean" );
  
  // connect!
  c.connect("ws://"+server+":9000", name, description );
  
  // connect to serial
  myPort = new Serial(this, Serial.list()[0], 9600);
  myPort.bufferUntil('\n');
}

void draw() {
  background( 255 );
  fill(20);
  textSize(30);
  text("Listening for power messages", 20, 320);
}
  

void onIntMessage( String name, int value ){
  println("got int message "+name +" : "+ value);
     powerSwitchState = !powerSwitchState;
          if (powerSwitchState == true){
             myPort.write('H');
          }
          else{
            myPort.write('L');
          } 
}

void onBooleanMessage( String name, boolean value ){
  println("got bool message "+name +" : "+ value); 
 
   powerSwitchState = !powerSwitchState;
          if (powerSwitchState == true){
             myPort.write('H');
          }
          else{
            myPort.write('L');
          } 
}

void onStringMessage( String name, String value ){
  println("got string message "+name +" : "+ value);  
     powerSwitchState = !powerSwitchState;
          if (powerSwitchState == true){
             myPort.write('H');
          }
          else{
            myPort.write('L');
          } 
}
