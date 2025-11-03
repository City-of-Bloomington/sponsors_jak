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

public class ContactAbbrList extends ArrayList<Contact>{

    static final long serialVersionUID = 25L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(ContactAbbrList.class);
    String lname;
    public ContactAbbrList(boolean val){
	debug = val;
    }
	
    public ContactAbbrList(boolean val, String val2){
	debug = val;
	setLname(val2);
    }
    public void setLname(String val){
	if(val != null)
	    lname = val;
    }	
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qq = "select id,lname,fname "+
	    "from spons_contacts ";
	if(!lname.equals("")){
	    qw += " lname like ? "; 
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}

	qq += " order by lname,fname ";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    con = Helper.getConnection();
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj  = 1;
	    if(!lname.equals("")){
		pstmt.setString(jj, "%"+lname+"%");
		jj++;
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Contact cc = new Contact(debug,
					 rs.getString(1),
					 rs.getString(2),
					 rs.getString(3));
		this.add(cc);
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






















































