package city.sponsor.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import city.sponsor.model.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class PhoneList extends ArrayList<Phone>{

    static final long serialVersionUID = 60L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(PhoneList.class);
    String own_id="", own_type="";
    public PhoneList(boolean val){
	debug = val;
    }
	
    public PhoneList(boolean val, String val2, String val3){
	debug = val;
	setOwn_type(val2);
	setOwn_id(val3);
    }
    public void setOwn_id(String val){
	if(val != null)
	    own_id = val;
    }
    public void setOwn_type(String val){
	if(val != null)
	    own_type = val;
    }	
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select id,phone,phone_type,own_type,own_id "+
	    " from spons_phones ";
	String qw = "";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!own_id.equals("")){
		qw = " own_id = ? ";
	    }
	    if(!own_type.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " own_type = ? ";
	    }
	    if(!qw.equals("")) qw = " where "+qw;
	    qq += qw + " order by phone ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj=1;
	    if(!own_id.equals("")){
		pstmt.setString(jj++, own_id);
	    }
	    if(!own_type.equals("")){
		pstmt.setString(jj++, own_type);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Phone pp = new Phone(debug,
				     rs.getString(1),
				     rs.getString(2),
				     rs.getString(3),
				     rs.getString(4),
				     rs.getString(5));
		this.add(pp);
	    }
	}
	catch(Exception ex){
	    back += ex+" : "+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
}






















































