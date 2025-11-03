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

public class DonBenTypeList extends ArrayList<DonBenType>{

    static final long serialVersionUID = 30L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(DonBenTypeList.class);
    String sponship_id = "", fulfilled = "";
	
    public DonBenTypeList(boolean val){
	debug = val;
    }
    public DonBenTypeList(boolean deb, String val){
	debug = deb;
	setSponship_id(val);
    }
    public void setSponship_id(String val){
	if(val != null)
	    sponship_id = val;
    }
    public void setFulfilled(String val){
	if(val != null)
	    fulfilled = val;
    }	
    //
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qq = " select b.id,t.id,t.name,b.sponship_id,b.fulfilled,date_format(flfld_date,'%m/%d/%Y') from spons_benefits b,spons_benefit_types t where b.ben_id=t.id ";
	if(!sponship_id.equals("")){
	    qq += " and b.sponship_id= ? ";
	}
	if(!fulfilled.equals("")){
	    qq += " and b.fulfilled is not null ";
	}	
	// qq += " order by t.name ";
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!sponship_id.equals("")){
		pstmt.setString(1, sponship_id);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3  = rs.getString(3);
		String str4 = rs.getString(4);
		String str5  = rs.getString(5);
		String str6 = rs.getString(6);	
		if(str != null && str2 != null){
		    DonBenType tt = new DonBenType(debug, str, str2, str3, str4, str5, str6);
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






















































