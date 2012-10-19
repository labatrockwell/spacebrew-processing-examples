String server="lab-server.local";
String name="givemeabettername";
String description ="This is an example client which .... It also listens to...";
import processing.serial.*;


SpacebrewClient c;

void setup() {
  size(600, 400);
  
  c = new SpacebrewClient( this );
  
  // add each thing you publish to
  // c.addPublish( "buttonPress", buttonSend ); 

  // add each thing you subscribe to
  // c.addSubscribe( "color", "range" );
  
  // connect!
  c.connect("ws://"+server+":9000", name, description );
  
}

void draw() {

}

//void mousePressed() {
//  c.send( "buttonPress", buttonSend);
//}

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
