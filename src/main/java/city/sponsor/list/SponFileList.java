package city.sponsor.list;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import city.sponsor.model.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class SponFileList extends ArrayList<SponFile>{

    boolean debug;
    static final long serialVersionUID = 68L;	
    static Logger logger = LogManager.getLogger(SponFileList.class);
    String spon_id="";
    int count = 0;
    //
    // basic constructor
    //
    public SponFileList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public SponFileList(boolean deb,
			String spon_id){

	debug = deb;
	setSpon_id(spon_id);
	//
	// initialize
	//
    }
    //
    // setters
    //
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	}
    }
    public int getCount(){
	return count;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq ="", qo = "";
	String qc = " select count(*) ";
	String qf =	"from spons_sponsor_files ";		
	String q = "select id,spon_id,name,"+
	    "date_format(date,'%m/%d/%Y'), "+
	    "notes,old_name ";

	String back="", qw = "";
		
	if(!spon_id.equals("")){
	    qw = " spon_id = ? ";
	}
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	qq = qc + qf + qw;			
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
	    if(!spon_id.equals(""))
		pstmt.setString(1, spon_id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		count = rs.getInt(1);
	    }
	    if(count > 0){
		qq = q+qf+qw;
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		if(!spon_id.equals(""))
		    pstmt.setString(1, spon_id);				
		rs = pstmt.executeQuery();
		while(rs.next()){
		    String str  = rs.getString(1);
		    String str2 = rs.getString(2);
		    String str3 = rs.getString(3);
		    String str4 = rs.getString(4);
		    String str5 = rs.getString(5);
		    String str6 = rs.getString(6);
		    SponFile file = new SponFile(debug, str, str2, str3, str4, str5, str6);
		    this.add(file);
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return "";
    }

	

}






















































