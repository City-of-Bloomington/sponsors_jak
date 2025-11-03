package city.sponsor.model;

import java.sql.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class OrgType extends Type{

    static final long serialVersionUID = 54L;	
    static Logger logger = LogManager.getLogger(OrgType.class);
    //
    public OrgType(boolean deb, String val, String val2){
	//
	// initialize
	//
	super(deb, val, val2);
    }	
    public OrgType(boolean deb, String val){
	//
	// initialize
	//
	super(deb, val);
    }
    //
    public OrgType(boolean deb){
	//
	// initialize
	//
	super(deb);
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
	try{
	    qq = "select * from spons_org_types where id=?";
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
	return back;
		
    }

}
