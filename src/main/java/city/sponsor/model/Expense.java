package city.sponsor.model;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Expense {
    boolean debug;
    static Logger logger = LogManager.getLogger(Expense.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id = "", details = "", value = "", date = "";
    String vendor_id = "", sponship_id="";
    double val = 0.;
    //
    // one or more for multiple receipts per payment
    //	
    Sponsorship sponship = null;
    Vendor vendor = null;
    public Expense(boolean deb) {
	debug = deb;
    }
    public Expense(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
    public Expense(boolean deb,
		   String id,
		   String sponship_id,
		   String vendor_id,
		   String value,
		   String date,
		   String details
		   ) {
	debug = deb;
	setId(id);
	setSponship_id(sponship_id);
	setVendor_id(vendor_id);	
	setValue(value);
	setDate(date);
	setDetails(details);
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
    public String getVendor_id() {
	return vendor_id;
    }
    public void setVendor_id(String vendor_id) {
	if(vendor_id != null)
	    this.vendor_id = vendor_id;
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

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	if(date != null)
	    this.date = date;
    }
    public String getDetails() {
	return details;
    }

    public void setDetails(String val) {
	if(val != null)
	    this.details = val;
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
    public Vendor getVendor(){
	if(vendor == null && !vendor_id.equals("")){
	    Vendor vv = new Vendor(debug, vendor_id);
	    String back = vv.doSelect();
	    if(back.equals("")){
		vendor = vv;
	    }
	}
	return vendor;
    }	
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_expenses values(0,"+
	    "?,?,?,?,?)";
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
	    pstmt.setString(2, vendor_id);	
	    if(value.equals(""))
		pstmt.setNull(3,Types.DOUBLE);
	    else
		pstmt.setString(3,value);
	    if(date.equals(""))
		pstmt.setNull(4,Types.DATE);
	    else
		pstmt.setDate(4, new java.sql.Date(dateFormat.parse(date).getTime()));
	    if(details.equals(""))
		pstmt.setNull(5,Types.VARCHAR);
	    else
		pstmt.setString(5,details);
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
	if(vendor_id.equals("") ||
	   sponship_id.equals("") ||
	   details.equals("")){
	    return "Not all required fields are set";
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "update spons_expenses set sponship_id=?,"+
		"vendor_id=?,"+
		"value=?,date=?,details=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, sponship_id);
	    pstmt.setString(2, vendor_id);	
	    if(value.equals(""))
		pstmt.setNull(3,Types.DOUBLE);
	    else
		pstmt.setString(3,value);
	    if(date.equals(""))
		pstmt.setNull(4,Types.DATE);
	    else
		pstmt.setDate(4, new java.sql.Date(dateFormat.parse(date).getTime()));				
	    pstmt.setString(5,details);
	    pstmt.setString(6,id);
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
	    qq = "delete from  spons_expenses where id=?";
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
	    "sponship_id, vendor_id, value,"+
	    "date_format(date,'%m/%d/%Y'), "+
	    "details "+
	    "from spons_expenses where id=?";
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
		setVendor_id(rs.getString(2));
		setValue(rs.getString(3));
		setDate(rs.getString(4));
		setDetails(rs.getString(5));
	    }
	    else{
		back = "Record "+id+" Not found";
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
