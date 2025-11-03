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

public class BenTypeList extends ArrayList<Type>{


    boolean debug = false;
    static final long serialVersionUID = 19L;	
    static Logger logger = LogManager.getLogger(BenTypeList.class);
    String name = "";
    public BenTypeList(boolean val){
	debug = val;
    }
    public BenTypeList(boolean val, String val2){
	debug = val;
	setName(val2);
    }
    private void setName(String val){
	if(val != null)
	    name = val;
    }
    //
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qq = " select id, name from spons_benefit_types ";
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    if(!name.equals("")){
		qq += "where name like ? ";
	    }
	    qq += " order by name ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!name.equals("")){
		pstmt.setString(1,"%"+name+"%");
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		if(str != null && str2 != null){
		    Type tt = new Type(debug, str, str2);
		    this.add(tt);
		}
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






















































