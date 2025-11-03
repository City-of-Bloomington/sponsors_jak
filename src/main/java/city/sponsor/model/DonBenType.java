package city.sponsor.model;
import java.sql.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class DonBenType extends Type{

    static final long serialVersionUID = 29L;	
    static Logger logger = LogManager.getLogger(DonBenType.class);
    String sponship_id="", fulfilled="", flfld_date="";
    String ben_id="";
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    //
    public DonBenType(boolean deb,
		      String val, // id
		      String val2, // ben_id //id
		      String val3, // name
		      String val4, // sponship_id
		      String val5,  // fulfilled
		      String val6 // flfld_date
		      ){
		
		
	//
	// initialize
	//
	super(deb, val, val3);
	setBen_id(val2);
	setSponship_id(val4);
	setFulfilled(val5);
	setFlfld_date(val6);
    }
    //
    public DonBenType(boolean deb, String val){
	//
	// initialize
	//
	super(deb, val);
    }	
    public DonBenType(boolean deb){
	//
	// initialize
	//
	super(deb);
    }
    public void setBen_id(String val){
	if(val != null)
	    ben_id = val;
    }	
    public void setSponship_id(String val){
	if(val != null)
	    sponship_id = val;
    }
    public void setFulfilled(String val){
	if(val != null)
	    fulfilled = val;
    }
	
    public void setFlfld_date(String val){
	if(val != null)
	    flfld_date = val;
    }
    public String getBen_id(){
	return ben_id;
    }
    public String getSponship_id(){
	return sponship_id;
    }
    public String getFulfilled(){
	return fulfilled;
    }
    public String getFlfld_date(){
	return flfld_date;
    }
    public boolean isFulfilled(){
	return !fulfilled.equals("");
    }
    public boolean hasNotes(){
	return false; // not needed
    }	
    public String doSelect(){
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(id.equals("")){
	    back = " id or sponship_id not set ";
	    return back;
	}
	String qq = " select t.id,t.name,b.sponship_id,b.fulfilled,date_format(flfld_date,'%m/%d/%Y') from spons_benefits b,spons_benefit_types t where b.ben_id=t.id and b.id = ? ";

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
	    pstmt.setString(1, id); 		
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setBen_id(rs.getString(1));
		setName(rs.getString(2));
		setSponship_id(rs.getString(3));
		setFulfilled(rs.getString(4));
		setFlfld_date(rs.getString(5));
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
    public String add(){
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_benefits values("+
	    "0,?,?,?,?)";
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
	    pstmt.setString(1, sponship_id);
	    pstmt.setString(2, ben_id); // ben_id
	    if(fulfilled.equals(""))
		pstmt.setNull(3,Types.CHAR);
	    else
		pstmt.setString(3,"y");
	    if(flfld_date.equals(""))
		pstmt.setNull(4,Types.DATE);
	    else
		pstmt.setDate(4, new java.sql.Date(dateFormat.parse(flfld_date).getTime()));
	    pstmt.executeUpdate();
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
    public String remove(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "delete from spons_benefits where id=?";
	String qq2 = "delete from spons_actions where ben_id = ?";

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }			
	    pstmt = con.prepareStatement(qq2);
	    if(debug){
		logger.debug(qq2);
	    }
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();			
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }			
	    pstmt.setString(1, id); 
	    pstmt.executeUpdate();
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
    public String updateFulfilled(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "update spons_benefits set fulfilled = ? where id=? ";

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
	    if(fulfilled.equals(""))
		pstmt.setString(1, null);
	    else
		pstmt.setString(1, "y");
	    pstmt.setString(2, id);
	    pstmt.executeUpdate();
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
    public String uncheckAllFulfilled(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(sponship_id.equals("")){
	    back = "sponship_id not set ";
	    logger.error(back);
	    return back;
	}
	String qq = "update spons_benefits set fulfilled = null where sponship_id=? ";

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
	    pstmt.setString(1, sponship_id);
	    pstmt.executeUpdate();
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
    public String updateFlfld_date(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "update spons_benefits set flfld_date = ? where id=? ";

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
	    if(flfld_date.equals(""))
		pstmt.setNull(1,Types.DATE);
	    else
		pstmt.setDate(1, new java.sql.Date(dateFormat.parse(flfld_date).getTime()));				
	    pstmt.setString(2, id); 
	    pstmt.executeUpdate();
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
}
