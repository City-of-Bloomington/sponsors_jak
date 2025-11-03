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

public class SponOptionList extends ArrayList<String>{


    boolean debug = false;
    static final long serialVersionUID = 71L;	
    static Logger logger = LogManager.getLogger(SponOptionList.class);
    boolean noSort = false;
    //
    // type values that are included in the DB are
    //
    String type = "";// con_means, con_time,interests,pref_con_time,pref_type_spon,program_area,reasons,target_pop
	
    public SponOptionList(boolean val){
	debug = val;
    }
    public SponOptionList(boolean deb, String val){
	debug = deb;
	setType(val);
    }
    public SponOptionList(boolean deb, String val, boolean noSort){
	debug = deb;
	setType(val);
	setNoSort(noSort);
    }	
    public void setType(String val){
	if(val != null)
	    type = val;
    }
    public void setNoSort(boolean val){
	noSort = val;
    }
    public String getType(){
	return type;
    }
    //	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = " select type,description from spons_type_options ";
	if(!type.equals("")){
	    qq += " where type like ? ";
	}
	if(!noSort){
	    qq += " order by description ";
	}
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!type.equals("")){
		pstmt.setString(1, type);
	    }	
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		if(str2 != null){
		    this.add(str2);
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






















































