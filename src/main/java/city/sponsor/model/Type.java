package city.sponsor.model;
import java.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class Type implements java.io.Serializable{

    String id="", name="";
    static final long serialVersionUID = 86L;
    static Logger logger = LogManager.getLogger(Type.class);		
    boolean debug = false;
    String errors = "";

    //
    public Type(boolean deb, String val, String val2){
	//
	// initialize
	//
	debug = deb;
	setId(val);
	setName(val2);
    }	
    public Type(boolean deb, String val){
	//
	// initialize
	//
	debug = deb;
	setId(val);
    }
	
    //
    public Type(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
	
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getName(){
	return name;
    }
    //
    // setters
    //
    public void setId (String val){
	if(val != null)
	    id = val;
    }
    public void setName (String val){
	if(val != null)
	    name = val;
    }
    public String toString(){
	return name;
    }

}
