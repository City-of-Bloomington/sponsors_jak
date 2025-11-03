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

public class SponshipAbbrList extends ArrayList<Sponsorship>{


    boolean debug = false;
    static final long serialVersionUID = 75L;	
    static Logger logger = LogManager.getLogger(SponshipAbbrList.class);
    String details="";
    public SponshipAbbrList(boolean val){
	debug = val;
    }
	
    public SponshipAbbrList(boolean val, String val2){
	debug = val;
	setDetails(val2);
    }
    public void setDetails(String val){
	if(val != null)
	    details = val;
    }	
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select id,details  "+
	    "from spons_sponsorships ";
	if(!details.equals("")){
	    qw += " details like ? "; 
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}

	qq += " order by details ";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj  = 1;
	    if(!details.equals("")){
		pstmt.setString(jj, "%"+details+"%");
		jj++;
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Sponsorship sponship = new Sponsorship(debug,
						       rs.getString(1),
						       rs.getString(2));
		this.add(sponship);
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






















































