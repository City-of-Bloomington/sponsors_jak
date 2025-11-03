package city.sponsor.model;
import java.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 *
 */

public class BenefitType extends Type{

    static final long serialVersionUID = 91L;	
    static Logger logger = LogManager.getLogger(BenefitType.class);
    //
    public BenefitType(boolean deb, String val, String val2){
	//
	// initialize
	//
	super(deb, val, val2);
    }	
    public BenefitType(boolean deb, String val){
	//
	// initialize
	//
	super(deb, val);
    }
    //
    public BenefitType(boolean deb){
	//
	// initialize
	//
	super(deb);
    }

    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_benefit_types values(0,"+
	    "?)";
		
	if(name.equals("")){
	    back = "Benefit name not set ";
	    return back;
	}
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
	    pstmt.setString(1,name);
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
    public String doSelect(){
	String back = "", qq = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(id.equals("")){
	    back = " id value not set ";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		qq = "select * from spons_benefit_types where id=?";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    String str = rs.getString(2);
		    if(str != null) name = str;
		}
		else{
		    back = " No match found for "+id;
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
    public String doUpdate(){
	String back = "", qq = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(name.equals("") || id.equals("")){
	    back = " id or name not set ";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		qq = "update spons_benefit_types set name = ? where id=?";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, name);
		pstmt.setString(2, id);
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
		qq = "delete from  spons_benefit_types where id=?";
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

}
