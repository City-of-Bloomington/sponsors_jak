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

public class SponAbbrList extends ArrayList<Sponsor>{


    boolean debug = false;
    static final long serialVersionUID = 67L;	
    static Logger logger = LogManager.getLogger(SponAbbrList.class);
    String orgname = "";
    public SponAbbrList(boolean val){
	debug = val;
    }
	
    public SponAbbrList(boolean val, String val2){
	debug = val;
	setOrgname(val2);
    }
    public void setOrgname(String val){
	if(val != null)
	    orgname = val;
    }	
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select id,orgname "+
	    "from spons_sponsors ";
	if(!orgname.equals("")){
	    qw += " orgname like ? "; 
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}

	qq += " order by orgname ";
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
	    if(!orgname.equals("")){
		pstmt.setString(jj, "%"+orgname+"%");
		jj++;
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Sponsor sponsor = new Sponsor(debug,
					      rs.getString(1),
					      rs.getString(2));
		this.add(sponsor);
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






















































