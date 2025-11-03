package city.sponsor.model;
import java.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class GenType extends Type{

    static final long serialVersionUID = 39L;	
    static Logger logger = LogManager.getLogger(GenType.class);
    //
    String table_name="spons_";
    public GenType(boolean deb, String val, String val2){
	//
	// initialize
	//
	super(deb, val, val2);
    }	
    public GenType(boolean deb, String val){
	//
	// initialize
	//
	super(deb, val);
    }
    //
    public GenType(boolean deb){
	//
	// initialize
	//
	super(deb);
    }
    public void setTableName(String val){
	if(val != null)
	    table_name += val;
    }

    public String addNew(){
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
	    qq = "insert into "+table_name+" values (0,?)";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,name);
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
    public String doReplace(){
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
	    qq = "update "+table_name+" set name=? where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,name);
	    pstmt.setString(2,id);
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

}
