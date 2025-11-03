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

public class OpptAbbrList extends ArrayList<Opportunity>{

    static final long serialVersionUID = 52L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(OpptAbbrList.class);
    String name;
    public OpptAbbrList(boolean val){
	debug = val;
    }
	
    public OpptAbbrList(boolean val, String val2){
	debug = val;
	setName(val2);
    }
    public void setName(String val){
	if(val != null)
	    name = val;
    }	
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select id,name,season,year,event_id,date_format(start_date,'%m/%d/%Y') "+
	    "from spons_opportunities ";
	if(!name.equals("")){
	    qw += " name like ? "; 
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}

	qq += " order by start_date DESC ";
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
	    if(!name.equals("")){
		pstmt.setString(jj, "%"+name+"%");
		jj++;
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Opportunity oppt = new Opportunity(debug,
						   rs.getString(1),
						   rs.getString(2),
						   rs.getString(3),
						   rs.getString(4),
						   rs.getString(5),
						   rs.getString(6));
		this.add(oppt);
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






















































