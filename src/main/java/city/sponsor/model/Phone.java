package city.sponsor.model;

import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Phone {
    boolean debug;
    static Logger logger = LogManager.getLogger(Phone.class);
    public final static String phoneTypes[] = {"work","home","cell"};
    String id="";
    String number = "";
    String type =   "";
    String own_type = "";
    String own_id = "";
    public Phone(boolean deb) {
	debug = deb;
    }
    public Phone(boolean deb, String val) {
	debug = deb;
	setId(val);
    }	
	
    public Phone(boolean deb,
		 String val,
		 String val2,
		 String val3,
		 String val4,
		 String val5) {
	debug = deb;
	setId(val);
	setNumber(val2);
	setType(val3);
	setOwn_type(val4);
	setOwn_id(val5);
    }	
    public String getId() {
	return id;
    }

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	if(number != null)
	    this.number = number;
    }

    public String getType() {
	return type;
    }


    public void setType(String val) {
	if(val != null)
	    this.type = val;
    }
	
    public String getOwn_type() {
	return own_type;
    }


    public void setOwn_type(String val) {
	if(val != null)
	    this.own_type = val;
    }
    public String getOwn_id() {
	return own_id;
    }

    public void setOwn_id(String val) {
	if(val != null)
	    this.own_id = val;
    }	
    public String toString(){
	String ret = "";
	if(!type.equals("")){
	    ret += type.substring(0,1);
	}	
	if(!number.equals("")){
	    if(!ret.equals("")) ret += ":";
	    ret += number;
	}
	return ret;
    }

    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_phones values(0,"+
	    "?,?,?,?)";
	if(number.equals("")){
	    back = "phone number not set ";
	    logger.error(back);
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt.setString(1,number);
	    if(type.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2, type);				
	    if(own_type.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,own_type);
	    if(own_id.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,own_id);		
	    pstmt.executeUpdate();
	    //
	    // get the id of the new record
	    //
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);				
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
    public String doUpdate(){
		
	String back = "";
	if(number.equals("")){
	    back = "phone number not set ";
	    logger.error(back);
	    return back;
	}
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String str="";
	String qq = "";
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "update spons_phones set phone=?,"+
		"phone_type=?,own_type=?,own_id=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,number);
	    if(type.equals("")){
		pstmt.setNull(2,Types.VARCHAR);
	    }
	    else{
		pstmt.setString(2,type);
	    }
	    if(own_type.equals("")){
		pstmt.setNull(3,Types.VARCHAR);
	    }
	    else{
		pstmt.setString(3,own_type);
	    }
	    if(own_id.equals("")){
		pstmt.setNull(4,Types.INTEGER);
	    }
	    else{
		pstmt.setString(4,own_id);
	    }	
	    pstmt.setString(5,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
	
    public String doDelete(){
		
	String back = "", qq = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "delete from  spons_phones where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
	
    //
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select "+
	    "phone,phone_type,own_type,own_id "+
	    "from spons_phones where id=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setNumber(rs.getString(1));
		setType(rs.getString(2));
		setOwn_type(rs.getString(3));
		setOwn_id(rs.getString(4));
	    }
	    else{
		return "Record "+id+" Not found";
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);			
	}
	return back;
    }	

	


}
