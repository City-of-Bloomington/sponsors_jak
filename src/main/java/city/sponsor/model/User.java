package city.sponsor.model;

import java.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User class
 *
 */

public class User implements java.io.Serializable{

    String userid="", fullName="", dept="", role="", manager="";
    String fname="", lname="", phone="";
    static final long serialVersionUID = 87L;	
    boolean debug = false, userExist = false;
    String errors = "";
    static Logger logger = LogManager.getLogger(User.class);
    //
    public User(boolean deb, Statement stmt, String _userid){
	debug = deb;
	userid=_userid;
	errors += doSelect();
    }
    public User(boolean deb, String val){
	//
	// initialize
	//
	debug = deb;
	userid = val;
    }
    public User(boolean deb, String val, String val2,
		String val3,
		String val4,
		String val5,
		String val6
		){
	//
	// initialize
	//
	debug = deb;
	setUserid(val);
	setRole(val2);
	setFname(val3);
	setLname(val4);
	setManager(val5);
	setPhone(val6);
    }	
    //
    public User(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
	
    //
    public User(String val){
	//
	// initialize
	//
	userid = val;
    }
    public boolean userExists(){
	return userExist;
    }
    //
    public String doSave(){
	String msg = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;	
	String qq = " insert into users values('"+userid+"','"+role+"','"+
	    Helper.escapeIt(fname)+"',";
	if(manager.equals(""))
	    qq += "null,";
	else
	    qq += "'y',";
	if(lname.equals(""))
	    qq += "null";
	else
	    qq += "'"+Helper.escapeIt(lname)+"'";		
	qq += ")";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}					
	try{
	    stmt = con.createStatement();				
	    stmt.executeUpdate(qq);
	}catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ""+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return "";
    }
    //
    public String doSelect(){
	String msg = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = " select * from users where userid like ? ";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}			
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, userid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(2);
		if(str !=null) role = str;
		str = rs.getString(3);
		if(str !=null) fname = str;
		str = rs.getString(4);
		if(str !=null) manager = str;
		str = rs.getString(5);
		if(str !=null) lname = str;
		str = rs.getString(6);
		if(str !=null) phone = str;				
		userExist = true;
				
	    }
	    else{
		msg = " No such user";
	    }
	    rs = null;
	}catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    msg += " "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg;
    }
    //
    public String doUpdate(){
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String msg = "";
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}			
	String str="", back = "";
	String qq = "";
	qq = "update users set ";
	if(fname.equals(""))
	    qq += "fname=null,";
	else
	    qq += "fname='"+Helper.escapeIt(fname)+"',";
	if(lname.equals(""))
	    qq += "lname=null,";
	else
	    qq += "lname='"+Helper.escapeIt(lname)+"',";	
	if(manager.equals(""))
	    qq += "manager=null,";
	else
	    qq += "manager='y',";		
	if(role.equals(""))
	    qq += "role=null";
	else
	    qq += "role='"+role+"'";
	qq += " where userid="+userid;
	//
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    //
	}
	catch(Exception ex){
	    msg = ex+":"+qq;
	    logger.error(msg);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back; 
    }
    //
    public String doDelete(){

	String str="", msg="";
	String qq = "";
	qq = "delete from  users where userid='"+userid+"'";
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}					
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    //
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    msg = ex.toString()+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg; // success
    }
    //
    public boolean hasRole(String val){
	if(role != null && role.indexOf(val) > -1) return true;
	return false;
    }
    public boolean canEdit(){
	return hasRole("Edit") || hasRole("Admin");
    }
    public boolean canDelete(){
	return hasRole("Delete");
    }
    public boolean isAdmin(){
	return hasRole("Admin");
    }
    //
    // getters
    //
    public String getUserid(){
	return userid;
    }
    public String getFullName(){
	if(fullName.equals("")){
	    fullName = fname;
	    if(!lname.equals("")){
		if(!fullName.equals("")) fullName += " ";
		fullName += lname;
	    }
	}
	return fullName;
    }
    public String getFname(){
	return fname;
    }
    public String getLname(){
	return lname;
    }	
    public String getDept(){
	return dept;
    }
    public String getRole(){
	return role;
    }
    public String getPhone(){
	return phone;
    }	
    public String getManager(){
	return manager;
    }	
    public String getErrors(){
	return errors;
    }
    //
    // setters
    //
    public void setUserid (String val){
	if (val != null)
	    userid = val;
    }
    public void setFname (String val){
	if (val != null)
	    fname = val;
    }
    public void setLname (String val){
	if (val != null)
	    lname = val;
    }	
    public void setRole (String val){
	if (val != null)
	    role = val;
    }
    public void setManager (String val){
	if (val != null)
	    manager = val;
    }	
    public void setDept (String val){
	if (val != null)
	    dept = val;
    }
    public void setPhone (String val){
	if (val != null)
	    phone = val;
    }	
    public String toString(){
	getFullName();
	if(fullName.equals("")) return userid;
	return fullName;
    }

}
