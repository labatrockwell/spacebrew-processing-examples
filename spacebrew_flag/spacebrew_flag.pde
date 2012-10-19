String server="lab-server.local";
String name="flag_controller";
String description ="A tiny desktop client to get people's attention via the servo flag.";
import processing.serial.*;

SpacebrewClient c;

int helloRange = 0;      // "hello!" servo position
int pstRange   = 1024;   // "pst! hey!" servo position

void setup() {
  size(600, 400);
  
  c = new SpacebrewClient( this );
  
  // add each thing you publish to
  c.addPublish( "sayHello", 0 ); 
  c.addPublish( "getAttention", 0 ); 
  
  // connect!
  c.connect("ws://"+server+":9000", name, description );  
}

void draw() {
  fill(120,0,0);
  rect(0,0,width,height);
}

void mousePressed(){
    c.send( "sayHello", helloRange);
}

void onIntMessage( String name, int value ){
  println("got int message "+name +" : "+ value);
  //  // check name by using equals
  //  if (name.equals("color") == true) {
  //      currentColor = value;
  //  }
}

void onBooleanMessage( String name, boolean value ){
  println("got bool message "+name +" : "+ value);  
}

void onStringMessage( String name, String value ){
  println("got string message "+name +" : "+ value);  
}
