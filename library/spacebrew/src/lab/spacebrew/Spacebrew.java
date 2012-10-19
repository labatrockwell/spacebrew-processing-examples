/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Brett Renfer
 * @modified    10/19/20120
 * @version     ##library.prettyVersion## (##library.version##)
 */

package spacebrew;


import processing.core.*;

/**
 * This is a template class and can be used to start a new processing library or tool.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own library or tool naming convention.
 * 
 * @example Hello 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

import org.json.*; //https://github.com/agoransson/JSON-processing
import java.lang.reflect.Method;
import java.lang.String;
import java.util.ArrayList;

public class Spacebrew {

  private PApplet parent;
  private Method  onRangeMessageMethod, onStringMessageMethod, onBooleanMessageMethod;
  private WsClient wsClient;
  private JSONObject tConfig = new JSONObject();
  private JSONObject nameConfig = new JSONObject();

  private ArrayList<SpacebrewMessage>   publishes, subscribes;
  public String      name, description;
  
  //------------------------------------------------
  public Spacebrew( PApplet _parent ){
    publishes = new ArrayList<SpacebrewMessage>();
    subscribes = new ArrayList<SpacebrewMessage>();
    parent = _parent; 
    setupMethods();   
  }
  
  //------------------------------------------------
  private void setupMethods(){
    try {
      onRangeMessageMethod = parent.getClass().getMethod("onRangeMessage", new Class[]{String.class, int.class});
    } catch (Exception e){
      System.err.println("no onRangeMessage method implemented");
    }

    try {
      onStringMessageMethod = parent.getClass().getMethod("onStringMessage", new Class[]{String.class, String.class});
    } catch (Exception e){
      System.err.println("no onStringMessage method implemented");
    }

    try {
      onBooleanMessageMethod = parent.getClass().getMethod("onBooleanMessage", new Class[]{String.class, boolean.class});
    } catch (Exception e){
      System.err.println("no onBooleanMessage method implemented");
    }    
  }
  
  //------------------------------------------------
  public void addPublish( String name, boolean _default ){
    SpacebrewMessage m = new SpacebrewMessage();
    m.name = name; 
    m.type = "boolean"; 
    if ( _default){
      m._default = "true";
    } else {
      m._default = "false";
    }
    publishes.add(m);
  }
  
  //------------------------------------------------
  public void addPublish( String name, int _default ){
    SpacebrewMessage m = new SpacebrewMessage();
    m.name = name; 
    m.type = "range"; 
    m._default = PApplet.str(_default);
    publishes.add(m);
  }
  
  //------------------------------------------------
  public void addPublish( String name, String _default ){
    SpacebrewMessage m = new SpacebrewMessage();
    m.name = name; 
    m.type = "string"; 
    m._default = _default;
    publishes.add(m);
  }
  
  //------------------------------------------------
  public void addPublish( String name, String type, String _default ){
    SpacebrewMessage m = new SpacebrewMessage();
    m.name = name; 
    m.type = type; 
    m._default = _default;
    publishes.add(m);
  }
  
  //------------------------------------------------
  // RIGHT NOW THIS JUST ADDS TO THE MESSAGE SENT ONOPEN
  // in the future, could be something like name, type, default, CALLBACK
  public void addSubscribe( String name, String type ){
    SpacebrewMessage m = new SpacebrewMessage();
    m.name = name;
    m.type = type;
    subscribes.add(m);
  }
  
  //------------------------------------------------
  // ASSUMES YOU'VE SET UP YOUR PUBLISHES + SUBSCRIBES
  public void connect( String url, String _name, String _description ){
    name = _name;
    description = _description;
    try {
      System.err.println("connecting "+url);
      wsClient = new WsClient( this, url );    
      wsClient.connect();
    }
    catch (Exception e){
      System.err.println("ERROR!");
      System.err.println(e.getMessage());
    }  
    JSONArray publishers = new JSONArray();
  
    // LOAD IN PUBLISH INFO
    for (int i=0, len=publishes.size(); i<len; i++){
        SpacebrewMessage m = publishes.get(i);
        JSONObject pub = new JSONObject();
        pub.put("name",m.name);
        pub.put("type",m.type);
        pub.put("default",m._default);
        
        publishers.put(pub);      
    }
      
    // LOAD IN SUBSCRIBE INFO
    JSONArray subscribers = new JSONArray();
      
   for (int i=0; i<subscribes.size(); i++){
        SpacebrewMessage m = subscribes.get(i);
        JSONObject subs = new JSONObject();
        subs.put("name",m.name);
        subs.put("type",m.type);
        
        subscribers.put(subs);      
    }
      
    JSONObject mObj = new JSONObject();
    JSONObject tMs1 = new JSONObject();
    JSONObject tMs2 = new JSONObject();
    tMs1.put("messages",subscribers);
    tMs2.put("messages",publishers);
    mObj.put("name", name);
    mObj.put("description", description);
    mObj.put("subscribe", tMs1);
    mObj.put("publish", tMs2);    
    tConfig.put("config", mObj);    
    
    // SETUP NAME MESSAGE
    JSONObject nm = new JSONObject();
    nm.put("name", name);
    JSONArray arr = new JSONArray();
    arr.put(nm);
    nameConfig.put("name", arr);
  }
  
  //------------------------------------------------
  public void send( String messageName, String type, String value ){
    
    JSONObject m = new JSONObject();
    m.put("clientName", name);
    m.put("name", messageName);
    m.put("type", type);
    m.put("value", value);
    
    JSONObject sM = new JSONObject();
    sM.put("message", m);
    
    wsClient.send( sM.toString() );
  }
  
  //------------------------------------------------
  public void send( String messageName, int value ){
    
    JSONObject m = new JSONObject();
    m.put("clientName", name);
    m.put("name", messageName);
    m.put("type", "range");
    m.put("value", PApplet.str(value));
    
    JSONObject sM = new JSONObject();
    sM.put("message", m);
    
    wsClient.send( sM.toString() );
  }
  
  //------------------------------------------------
  public void send( String messageName, boolean value ){
    
    System.err.println(name);
    
    JSONObject m = new JSONObject();
    m.put("clientName", name);
    m.put("name", messageName);
    m.put("type", "boolean");
    m.put("value", PApplet.str(value));
    
    JSONObject sM = new JSONObject();
    sM.put("message", m);
    
    wsClient.send( sM.toString() );
    System.err.println(sM.toString());
  }
  
  //------------------------------------------------
  public void send( String messageName, String value ){
    
    JSONObject m = new JSONObject();
    m.put("clientName", name);
    m.put("name", messageName);
    m.put("type", "string");
    m.put("value", value);
    
    JSONObject sM = new JSONObject();
    sM.put("message", m);
    
    wsClient.send( sM.toString() );
  }
  
  //------------------------------------------------
  public void onOpen(){
    System.err.println("connection open!");
    // send config
    wsClient.send(nameConfig.toString());
    wsClient.send(tConfig.toString());
  }
  
  //------------------------------------------------
  public void onClose(){
    System.err.println("connection closed!");
  }
  
  //------------------------------------------------
  public void onMessage( String message ){
    JSONObject m = new JSONObject( message ).getJSONObject("message");
    
    String name = m.getString("name");
    String type = m.getString("type");
    
    if ( type.equals("string") ){
      if ( onStringMessageMethod != null ){
        try {
          onStringMessageMethod.invoke( parent, name, m.getString("value"));
        } catch( Exception e ){
          System.err.println("onStringMessageMethod invoke failed, disabling :(");
          onStringMessageMethod = null;
        }
      }
    } else if ( type.equals("boolean")){
      if ( onBooleanMessageMethod != null ){
        try {
          onBooleanMessageMethod.invoke( parent, name, m.getBoolean("value"));
        } catch( Exception e ){
          System.err.println("onBooleanMessageMethod invoke failed, disabling :(");
          onBooleanMessageMethod = null;
        }
      }
    } else if ( type.equals("range")){
      if ( onRangeMessageMethod != null ){
        try {
          onRangeMessageMethod.invoke( parent, name, m.getInt("value"));
        } catch( Exception e ){
          System.err.println("onRangeMessageMethod invoke failed, disabling :(");
          onRangeMessageMethod = null;
        }
      }
    } else {
      System.err.println("Received message of unknown type "+type);
    }
  }
 
};

class SpacebrewMessage {
  // to-do: this is weird!
  String name, type, _default;
  int       intValue;
  String    stringValue;
  boolean   boolValue;
}

