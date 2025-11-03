package city.sponsor.model;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//
// not used
public class Donation {

    boolean debug;
    static Logger logger = LogManager.getLogger(Donation.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
    String id="", event_id="", spon_id="", details="", don_type="",
	start_date="", end_date="", terms="", totalValue="", spon_level="",
	ben_other="";
	
	
    public Donation(boolean deb) {
	debug = deb;
    }
    public Donation(boolean deb, String id) {
	debug = deb;
	setId(id);
    }	

    public String getId() {
	return id;
    }


    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }


    public String getEvent_id() {
	return event_id;
    }


    public void setEvent_id(String event_id) {
	if(event_id != null)
	    this.event_id = event_id;
    }


    public String getSpon_id() {
	return spon_id;
    }


    public void setSpon_id(String spon_id) {
	if(spon_id != null)
	    this.spon_id = spon_id;
    }


    public String getDetails() {
	return details;
    }


    public void setDetails(String details) {
	if(details != null)
	    this.details = details;
    }


    public String getDon_type() {
	return don_type;
    }


    public void setDon_type(String don_type) {
	if(don_type != null)
	    this.don_type = don_type;
    }


    public String getStart_date() {
	return start_date;
    }


    public void setStart_date(String start_date) {
	if(start_date != null)
	    this.start_date = start_date;
    }


    public String getEnd_date() {
	return end_date;
    }


    public void setEnd_date(String end_date) {
	if(end_date != null)
	    this.end_date = end_date;
    }


    public String getTerms() {
	return terms;
    }


    public void setTerms(String terms) {
	if(terms != null)
	    this.terms = terms;
    }


    public String getTotalValue() {
	return totalValue;
    }


    public void setTotalValue(String totalValue) {
	if(totalValue != null)
	    this.totalValue = totalValue;
    }


    public String getSpon_level() {
	return spon_level;
    }


    public void setSpon_level(String spon_level) {
	if(spon_level != null)
	    this.spon_level = spon_level;
    }


    public String getBen_other() {
	return ben_other;
    }


    public void setBen_other(String ben_other) {
	if(ben_other != null)
	    this.ben_other = ben_other;
    }
    public String toString(){
		
	return details;
    }

    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_donations values(0,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?)";

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt.setString(1,event_id);
	    pstmt.setString(2,spon_id);
			
	    if(details.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,details);
	    if(don_type.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,don_type);
	    if(start_date.equals(""))
		pstmt.setNull(5,Types.DATE);
	    else
		pstmt.setDate(5, new java.sql.Date(dateFormat.parse(start_date).getTime()));
	    if(end_date.equals(""))
		pstmt.setNull(6,Types.DATE);
	    else
		pstmt.setDate(6, new java.sql.Date(dateFormat.parse(end_date).getTime()));
	    if(terms.equals(""))
		pstmt.setNull(7,Types.INTEGER);
	    else
		pstmt.setString(7,terms);				
	    if(totalValue.equals(""))
		pstmt.setNull(8,Types.DOUBLE);
	    else
		pstmt.setString(8,totalValue);
	    if(spon_level.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,spon_level);					
	    if(ben_other.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,ben_other);
	    pstmt.executeUpdate();
	    //
	    // get the id of the new record
	    //
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);				
	    pstmt.executeQuery();
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
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    qq = "update spons_contacts set event_id=?,"+
		"spon_id=?,details=?,don_type=?,start_date=?,"+
		"end_date=?,terms=?,totalValue=?,spon_level=?,ben_other=?"+
		"phone=?,phone2=?,email=?,pref_con_time=?,"+
		" where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,event_id);
	    pstmt.setString(2,spon_id);
	    if(details.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,details);
	    if(don_type.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,don_type);
	    if(start_date.equals(""))
		pstmt.setNull(5,Types.DATE);
	    else
		pstmt.setDate(5, new java.sql.Date(dateFormat.parse(start_date).getTime()));
	    if(end_date.equals(""))
		pstmt.setNull(6,Types.DATE);
	    else
		pstmt.setDate(6, new java.sql.Date(dateFormat.parse(end_date).getTime()));
	    if(terms.equals(""))
		pstmt.setNull(7,Types.INTEGER);
	    else
		pstmt.setString(7,terms);				
	    if(totalValue.equals(""))
		pstmt.setNull(8,Types.DOUBLE);
	    else
		pstmt.setString(8,totalValue);
	    if(spon_level.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,spon_level);					
	    if(ben_other.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,ben_other);				
	    pstmt.setString(11,id);
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
		
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
				
	    qq = "delete from  spons_donations where id=?";
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
	String qq = "select event_id,spon_id,"+
	    "details, don_type,"+
	    "date_format(start_date,'%m/%d/%Y'), "+
	    "date_format(end_date,'%m/%d/%Y'), "+	
	    "terms,totalValue, spon_level,ben_other "+
	    "from spons_donations where id=?";


	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }				
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setEvent_id(rs.getString(1));
		setSpon_id(rs.getString(2));
		setDetails(rs.getString(3));
		setDon_type(rs.getString(4));
		setStart_date(rs.getString(5));  
		setEnd_date(rs.getString(6));
		setTerms(rs.getString(7));  
		setTotalValue(rs.getString(8));
		setSpon_level(rs.getString(9));
		setBen_other(rs.getString(10));
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
