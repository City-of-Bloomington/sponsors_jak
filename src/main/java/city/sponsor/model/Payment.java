package city.sponsor.model;

import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Payment {
    boolean debug;
    static Logger logger = LogManager.getLogger(Payment.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", sponship_id="", value="", dueDate="";
    double balance = 0., val = 0.;
    String status = "Unpaid";
    //
    // one or more for multiple receipts per payment
    //	
    Sponsorship sponship = null;
    public Payment(boolean deb) {
	debug = deb;
    }
    public Payment(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
    public Payment(boolean deb,
		   String id,
		   String sponship_id,
		   String value,
		   String dueDate,
		   String balance
		   ) {
	debug = deb;
	setId(id);
	setSponship_id(sponship_id);
	setValue(value);
	setDueDate(dueDate);
	setBalance(balance);
    }
	

    public String getId() {
	return id;
    }

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }

    public String getSponship_id() {
	return sponship_id;
    }

    public void setSponship_id(String sponship_id) {
	if(sponship_id != null)
	    this.sponship_id = sponship_id;
    }

    public String getValue() {
	return value;
    }
    public double getValueDbl() {
	return val;
    }

    public void setValue(String value) {
	if(value != null && !value.equals("")){
	    this.value = value;
	    try{
		val = Double.parseDouble(value);
	    }catch(Exception ex){}
	}
    }

    public String getDueDate() {
	return dueDate;
    }

    public void setDueDate(String dueDate) {
	if(dueDate != null)
	    this.dueDate = dueDate;
    }

    public String getStatus() {
	if(id.equals("")) return "Unpaid";
	return (balance <= 0) ?"Paid":"Unpaid";
    }
    boolean isPaid(){
	return (balance <= 0 );
    }
    boolean hasBalance(){
	return balance > 0;
    }	
    public double getBalance(){
	return balance;
    }
    public void setBalance(String val) {
	if(val != null){
	    try{
		this.balance = Double.parseDouble(val);
	    }catch(Exception ex){}
	}
    }
    public void setBalance(double val) {
	balance = val;
    }	
    /**
     * The status is "Paid" if the total paid equals amount requested
     */
    //
    public Sponsorship getSponship(){
	if(sponship == null && !sponship_id.equals("")){
	    Sponsorship sps = new Sponsorship(debug, sponship_id);
	    String back = sps.doSelect();
	    if(back.equals("")){
		sponship = sps;
	    }
	}
	return sponship;
    }
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_payments values(0,"+
	    "?,?,?,"+
	    "?)";
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
	    pstmt.setString(1, sponship_id);
	    if(value.equals(""))
		pstmt.setNull(2,Types.DOUBLE);
	    else
		pstmt.setString(2,value);
	    if(dueDate.equals(""))
		pstmt.setNull(3,Types.DATE);
	    else
		pstmt.setDate(3, new java.sql.Date(dateFormat.parse(dueDate).getTime()));				
	    pstmt.setDouble(4,balance);
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
	try{
	    qq = "update spons_payments set sponship_id=?,"+
		"value=?,dueDate=?,balance=? "+
		"where id=?";
				
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, sponship_id);
	    if(value.equals(""))
		pstmt.setNull(2,Types.DOUBLE);
	    else
		pstmt.setString(2,value);
	    if(dueDate.equals(""))
		pstmt.setNull(3,Types.DATE);
	    else
		pstmt.setDate(3, new java.sql.Date(dateFormat.parse(dueDate).getTime()));				
	    pstmt.setDouble(4,balance);
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
	    qq = "delete from  spons_payments where id=?";
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
	    "sponship_id, value,"+
	    "date_format(dueDate,'%m/%d/%Y'), "+
	    "balance "+
	    "from spons_payments where id=?";
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
		setSponship_id(rs.getString(1));
		setValue(rs.getString(2));
		setDueDate(rs.getString(3));
		setBalance(rs.getString(4));
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
