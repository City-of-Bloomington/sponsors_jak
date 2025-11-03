
package city.sponsor.model;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class SponFile{
	
    boolean debug;
    static Logger logger = LogManager.getLogger(SponFile.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", spon_id ="", errors="", 
	notes="", name="", old_name="",
	date="";
    Sponsor sponsor = null;
    //
    //
    // basic constructor
    public SponFile(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public SponFile(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setId(val);
    }
    public SponFile(boolean deb,
		    String val,
		    String val2,
		    String val3,
		    String val4,
		    String val5,
		    String val6
		    ){

	debug = deb;
	setId(val);
	setSpon_id(val2);
	setName(val3);
	setDate(val4);
	setNotes(val5);
	setOldName(val6);
    }	
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setSpon_id(String val){
	if(val != null)		
	    spon_id = val;
    }
    public void setDate(String val){
	if(val != null)
	    date = val;
    }
    public void setNotes(String val){
	if(val != null)
	    notes = val;
    }
    public void setName(String val){
	if(val != null)
	    name = val;
    }
    public void setOldName(String val){
	if(val != null)
	    old_name = val.replace(" ","_");
    }		
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getSpon_id(){
	return spon_id;
    }
    public String  getNotes(){
	return notes;
    }
    public String  getName(){
	return name;
    }
    public String  getOldName(){
	return old_name;
    }	
    public String  getDate(){
	if(id.equals("")){
	    date = Helper.getToday();
	}
	return date;
    }
    public String  getErrors(){
	return errors;
    }
	
    public Sponsor getSponsor(){
	if(sponsor == null){
	    if(!spon_id.equals("")){
		Sponsor spon = new Sponsor(debug, spon_id);
		String back = spon.doSelect();
		if(back.equals("")){
		    sponsor = spon;
		}
	    }
	}
	return sponsor;
    }
    public String getFullPath(String dir, String ext, String url){
	String path = dir;
	String yy="", separator="/"; // linux
	if(url.indexOf("8080") > -1){
	    separator = "\\"; // windows
	}
	if(name.equals("")){
	    if(id.equals("")){
		composeName(ext);
	    }
	    else{
		doSelect();
	    }
	}
	if(!date.equals("")){
	    yy = date.substring(6);
	}
	if(!yy.equals("")){
	    path += yy;
	}
	path += separator;
	File myDir = new File(path);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	return path;
    }
    /**
     * for download purpose
     */
    public String getPath(String dir, String url){
	String path = dir;
	String yy="", separator="/"; // linux
	if(url != null && url.indexOf("8080") > -1){
	    separator = "\\"; // windows
	}
	if(!date.equals("")){
	    yy = date.substring(6);
	}
	if(!yy.equals("")){
	    path += yy;
	}
	path += separator;
	return path;
    }	
    public String composeName(String ext){
	String back = getNewIndex();
	if(back.equals("")){
	    name = "spon_"+id+ext;
	    date = Helper.getToday();
	}
	return back;
    }
    public String getNewIndex(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	date = Helper.getToday();
	String qq = "insert into spons_sponsor_file_s values(0)";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
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
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
    public String doSave(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	date = Helper.getToday();
	String qq = "insert into spons_sponsor_files values(?,?,?,now(),"+
	    "?,?)";
	con = Helper.getConnection();
	if(name.equals("")){
	    back = "File name not set ";
	    logger.error(back);
	    return back;
	}
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return back;
	}
	else{
	    try{
		pstmt = con.prepareStatement(qq);
		if(debug){
		    logger.debug(qq);
		}
		pstmt.setString(1,id);		
		if(spon_id.equals(""))
		    pstmt.setString(2,null);
		else
		    pstmt.setString(2,spon_id);
		pstmt.setString(3,name);
		if(notes.equals(""))
		    pstmt.setString(4,null);
		else
		    pstmt.setString(4,notes);
		if(old_name.equals(""))
		    pstmt.setString(5,null);
		else
		    pstmt.setString(5,old_name);		
		pstmt.executeUpdate();
		//
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(back);
	    }
	    finally{
		Helper.databaseDisconnect(con, pstmt, rs);
	    }
	}
	return back;
    }
    public String doUpdate(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	date = Helper.getToday();
	String qq = "update spons_sponsor_files set notes=? ";
	if(!name.equals("")){
	    qq += ", name=? ";
	}
	qq += " where id=? ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return back;
	}
	else{
	    try{
		pstmt = con.prepareStatement(qq);
		if(debug){
		    logger.debug(qq);
		}
		int jj=1;
		pstmt.setString(jj++,notes);
		if(!name.equals("")){ // if the file was replaced
		    pstmt.setString(jj++,name);
		}
		pstmt.setString(jj,id);
		pstmt.executeUpdate();
		back += doSelect();
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(back);
	    }
	    finally{
		Helper.databaseDisconnect(con, pstmt, rs);
	    }
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
	else{
	    try{
		qq = "delete from spons_sponsor_files where id=?";
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
	}
	return back;

    }
	
    //
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select id,"+
	    "spon_id,name,"+
	    "notes,"+
	    "date_format(date,'%m/%d/%Y'),old_name "+
	    " from spons_sponsor_files where id=?";		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		if(debug){
		    logger.debug(qq);
		}				
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    String str = rs.getString(2);
		    if(str != null) spon_id = str;
		    str = rs.getString(3);
		    if(str != null) name = str;
		    str = rs.getString(4);
		    if(str != null) notes = str;
		    str = rs.getString(5);
		    if(str != null) date = str;
		    str = rs.getString(6);
		    if(str != null) old_name = str;	
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
	}
	return back;
    }	

}






















































