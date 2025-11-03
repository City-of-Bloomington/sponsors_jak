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

public class VendorList extends ArrayList<Vendor>{

    boolean debug = false;
    static final long serialVersionUID = 90L;	
    static Logger logger = LogManager.getLogger(VendorList.class);
    String id="", name="";
    public VendorList(boolean val){
	debug = val;
    }
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select id,name from spons_vendors ";
	String qw = "";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!id.equals("")){
		qw = " id = ? ";
	    }
	    if(!name.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " name like ? ";
	    }
	    if(!qw.equals("")) qw = " where "+qw;
	    qq += qw + " order by name ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj=1;
	    if(!id.equals("")){
		pstmt.setString(jj++, id);
	    }
	    if(!name.equals("")){
		pstmt.setString(jj++, "%"+name+"%");
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Vendor vv = new Vendor(debug,
				       rs.getString(1),
				       rs.getString(2));
		this.add(vv);
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






















































