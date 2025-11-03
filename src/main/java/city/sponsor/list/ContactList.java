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

public class ContactList extends ArrayList<Contact>{

    static final long serialVersionUID = 26L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(ContactList.class);
    String spon_id="";
    public ContactList(boolean val){
	debug = val;
    }
	
    public ContactList(boolean val, String val2){
	debug = val;
	setSpon_id(val2);
    }
    public void setSpon_id(String val){
	if(val != null)
	    spon_id = val;
    }
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qq = "select id,spon_id,occupation,lname,fname,address,pobox,"+
	    "city, state, zip,"+
	    "email,pref_con_time, con_means,notes,prim_cont "+
	    " from spons_contacts ";		

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    if(!spon_id.equals("")){
		qq += " where spon_id = ? ";
	    }
	    qq += " order by lname, fname ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!spon_id.equals("")){
		pstmt.setString(1, spon_id);
	    }	
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Contact cont = new Contact(debug,
					   rs.getString(1),
					   rs.getString(2),
					   rs.getString(3),
					   rs.getString(4),
					   rs.getString(5),
					   rs.getString(6),  
					   rs.getString(7),
					   rs.getString(8),
					   rs.getString(9),
					   rs.getString(10),
					   rs.getString(11),
					   rs.getString(12),
					   rs.getString(13),
					   rs.getString(14),
					   rs.getString(15));
		this.add(cont);
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






















































