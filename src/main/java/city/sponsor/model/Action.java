
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

public class Action{
	
    boolean debug;
    static Logger logger = LogManager.getLogger(Action.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", spon_id ="", errors="", oppt_id="", sponship_id="",
	notes="", actionBy="", byName="", cont_id="", ben_id="",
	followup="", status="Ongoing", alert="", alert_date="",
	date="", another_userid="";
    boolean critical = false;
    Sponsor sponsor = null;
    Opportunity oppt = null;
    Sponsorship sponship = null;
    DonBenType benefit = null;
    Contact contact = null;
	
    //
    // basic constructor
    public Action(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public Action(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setId(val);
    }
    public Action(boolean deb,
		  String val,
		  String val2,
		  String val3,
		  String val4,
		  String val5,
		  String val6,
		  String val7,
		  String val8,
		  String val9,
		  String val10,
		  String val11,
		  String val12,
		  String val13,
		  String val14
		  ){

	debug = deb;
	setId(val);
	setSpon_id(val2);
	setOppt_id(val3);
	setSponship_id(val4);
	setBy(val5);
	setDate(val6);
	setNotes(val7);
	setFollowup(val8);
	setCont_id(val9);
	setStatus(val10);
	setAlert(val11);
	setAlert_date(val12);
	setBen_id(val13);
	setAnotherUserid(val14);		
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
    public void setOppt_id(String val){
	if(val != null)		
	    oppt_id = val;
    }
    public void setSponship_id(String val){
	if(val != null)		
	    sponship_id = val;
    }
    public void setCont_id(String val){
	if(val != null)		
	    cont_id = val;
    }	
    public void setBy(String val){
	if(val != null)		
	    actionBy = val;
    }
    public void setAnotherUserid(String val){
	if(val != null)		
	    another_userid = val;
    }	
    public void setByName(String val){
	if(val != null)
	    byName = val;
    }
    public void setDate(String val){
	if(val != null)
	    date = val;
    }
    public void setFollowup(String val){
	if(val != null)
	    followup = val;
    }	
    public void setNotes(String val){
	if(val != null)
	    notes = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setAlert(String val){
	if(val != null)
	    alert = val;
    }
    public void setAlert_date(String val){
	if(val != null)
	    alert_date = val;
    }
    public void setBen_id(String val){
	if(val != null)		
	    ben_id = val;
    }	
    public void setCritical(){
	critical = true;
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
    public String  getOppt_id(){
	return oppt_id;
    }
    public String  getSponship_id(){
	return sponship_id;
    }
    public String  getCont_id(){
	return cont_id;
    }
    public String  getBen_id(){
	return ben_id;
    }
    public String  getNotes(){
	return notes;
    }
    public String  getBy(){
	return actionBy;
    }
    public String  getStatus(){
	return status;
    }
    public String  getAnotherUserid(){
	return another_userid;
    }	
    //
    public String  getByName(){
	if(byName.equals("") && !actionBy.equals("")){
	    User user = new User(debug, actionBy);
	    errors = user.doSelect();
	    byName = user.getFullName();
	}
	return byName;
    }
    public String  getDate(){
	if(id.equals("")){
	    date = Helper.getToday();
	}
	return date;
    }
    public String  getFollowup(){
	return followup;
    }
    public String  getAlert(){
	return alert;
    }
    public String  getAlert_date(){
	return alert_date;
    }
    public boolean  hasAlert(){
	return !alert.equals("");
    }
    public boolean  isCritical(){
	return critical;
    }	
    public String  getErrors(){
	return errors;
    }
    //
    public Opportunity getOppt(){
		
	if(oppt == null){
	    if(oppt_id.equals("") && !sponship_id.equals("")){
		getSponship();
	    }
	    if(!oppt_id.equals("")){
		Opportunity opp = new Opportunity(debug, oppt_id);
		String back = opp.doSelect();
		if(back.equals("")){
		    oppt = opp;
		}
	    }
	}
	return oppt;
    }
    public Sponsor getSponsor(){
	if(sponsor == null){
	    if(spon_id.equals("") && !sponship_id.equals("")){
		getSponship();
	    }
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
    public Sponsorship getSponship(){
	if(sponship == null && !sponship_id.equals("")){
	    Sponsorship sponsh = new Sponsorship(debug, sponship_id);
	    String back = sponsh.doSelect();
	    if(back.equals("")){
		sponship = sponsh;
		spon_id = sponsh.getSpon_id();
		oppt_id = sponsh.getOppt_id();
	    }
	}
	return sponship;
    }
    public Contact getContact(){
	if(contact == null && !cont_id.equals("")){
	    Contact cc = new Contact(debug, cont_id);
	    String back = cc.doSelect();
	    if(back.equals("")){
		contact = cc;
	    }
	}
	return contact;
    }
    public DonBenType getBenefit(){
	if(benefit == null && !ben_id.equals("")){
	    DonBenType bb = new DonBenType(debug, ben_id);
	    String back = bb.doSelect();
	    if(back.equals("")){
		benefit = bb;
	    }
	}
	return benefit;
    }
    public void prepare(){
	if(!sponship_id.equals("")){
	    getSponship();
	    if(sponship != null){
		spon_id = sponship.getSpon_id();
		oppt_id = sponship.getOppt_id();
	    }
	}
    }
    /**
     * we need to know the order of this action in the
     * list of actions on that date
     */
    public int getDaySeq(){
	int seq = 1; // start 1 for no other action before this one
	//
	// we want to count how many are before (if any) for this date
	//
	String qq = "select count(*) from spons_actions where date = ? and id  < ? ";
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return seq;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }			
	    pstmt = con.prepareStatement(qq);
	    pstmt.setDate(1, new java.sql.Date(dateFormat.parse(date).getTime()));					
	    pstmt.setString(2, id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		seq += rs.getInt(1);
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return seq;
    }
    public String doSave(){
		
	String back = "";
	prepare();
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	date = Helper.getToday();
	String qq = "insert into spons_actions values(0,?,?,?,?,now(),"+
	    "?,?,?,?,?,?,?,?)";
	con = Helper.getConnection();
	if(notes.equals("")){
	    back = " No notes entered ";
	    logger.error(back);
	    return back;
	}
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    if(spon_id.equals(""))
		pstmt.setString(1,null);
	    else
		pstmt.setString(1,spon_id);
	    if(oppt_id.equals(""))
		pstmt.setString(2,null);
	    else
		pstmt.setString(2,oppt_id);
	    if(sponship_id.equals(""))
		pstmt.setString(3,null);
	    else
		pstmt.setString(3,sponship_id);				
	    pstmt.setString(4,actionBy);
	    pstmt.setString(5, notes);
	    if(followup.equals("")){
		pstmt.setString(6,null);
	    }
	    else{
		pstmt.setDate(6,new java.sql.Date(dateFormat.parse(followup).getTime()));		
	    }
	    if(cont_id.equals(""))
		pstmt.setString(7,null);
	    else	
		pstmt.setString(7, cont_id);
	    pstmt.setString(8,status);
	    if(alert.equals(""))
		pstmt.setString(9,null);
	    else	
		pstmt.setString(9, "y");
	    if(alert_date.equals("")){
		pstmt.setString(10,null);
	    }
	    else{
		pstmt.setDate(10,new java.sql.Date(dateFormat.parse(alert_date).getTime()));		
	    }
	    if(ben_id.equals(""))
		pstmt.setString(11,null);
	    else	
		pstmt.setString(11, ben_id);
	    if(another_userid.equals(""))
		pstmt.setString(12,null);
	    else	
		pstmt.setString(12, another_userid);				
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
	else{
	    try{
		qq = "update spons_actions set ";
		qq += "notes = ?, followup = ?, status = ?, ";
		qq += "alert = ?, alert_date = ?, another_userid=? ";
		qq += " where id = ? ";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		int jj = 1;
		pstmt.setString(jj++,notes);
		if(followup.equals("")){
		    pstmt.setString(jj++, null);
		}
		else{
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(followup).getTime()));
		}
		if(status.equals("")){
		    pstmt.setString(jj++, null);
		}
		else{
		    pstmt.setString(jj++, status);
		}
		if(alert.equals("")){
		    pstmt.setString(jj++, null);
		}
		else{
		    pstmt.setString(jj++, "y");
		}
		if(alert_date.equals("")){
		    pstmt.setString(jj++, null);
		}
		else{
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(alert_date).getTime()));
		}
		if(another_userid.equals("")){
		    pstmt.setString(jj++, null);
		}
		else{
		    pstmt.setString(jj++, another_userid);
		}				
		pstmt.setString(jj,id);
		pstmt.executeUpdate();
		//
		// date is not updated, therefore we need to read it from DB
		//
		qq = "select "+
		    "date_format(date,'%m/%d/%Y'),actionBy "+
		    " from spons_actions where id=?";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    str = rs.getString(1);
		    if(str != null) date = str;
		    str = rs.getString(2);
		    if(str != null) actionBy = str;	
		}
	    }
	    catch(Exception ex){
		back += ex+":"+qq;
		logger.error(qq);
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
		qq = "delete from spons_actions where id=?";
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
	String qq = "select "+
	    "spon_id,oppt_id,sponship_id,"+
	    "notes,"+
	    "actionBy,"+
	    "date_format(date,'%m/%d/%Y'), "+
	    "date_format(followup,'%m/%d/%Y'), "+
	    "cont_id, status,alert, "+
	    "date_format(alert_date,'%m/%d/%Y'), "+
	    "ben_id,another_userid "+
	    " from spons_actions where id=?";		
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
		    String str = rs.getString(1);
		    if(str != null) spon_id = str;
		    str = rs.getString(2);
		    if(str != null) oppt_id = str;
		    str = rs.getString(3);
		    if(str != null) sponship_id = str;	
		    str = rs.getString(4);
		    if(str != null) notes = str;
		    str = rs.getString(5);
		    if(str != null) actionBy = str;
		    str = rs.getString(6);
		    if(str != null) date = str;
		    str = rs.getString(7);  
		    if(str != null) followup = str;
		    str = rs.getString(8);  
		    if(str != null) cont_id = str;
		    str = rs.getString(9);  
		    if(str != null) status = str;
		    str = rs.getString(10);  
		    if(str != null) alert = str;
		    str = rs.getString(11);  
		    if(str != null) alert_date = str;
		    str = rs.getString(12);  
		    if(str != null) ben_id = str;
		    str = rs.getString(13);  
		    if(str != null) another_userid = str;					
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






















































