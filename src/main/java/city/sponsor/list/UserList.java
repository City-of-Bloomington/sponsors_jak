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

public class UserList extends ArrayList<User>{


    boolean debug = false;
    static final long serialVersionUID = 88L;	
    static Logger logger = LogManager.getLogger(UserList.class);
    String manager = "", role = "";
	
    public UserList(boolean val){
	debug = val;
    }
    //
    public void setManager(String val){
	if(val != null)
	    manager = val;
    }
    public void setRole(String val){
	if(val != null)
	    role = val;
    }	
    public String find(){
		
	String back = "";
	Statement stmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qw = "";
	String qq = " select userid, role, fname, lname, manager,phone from users ";
	if(!role.equals("")){
	    qw += " role='"+role+"' ";
	}
	if(!manager.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " manager is not null ";
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += " order by userid ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3  = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		String str6 = rs.getString(6);
		if(str != null){
		    User user = new User(debug, str, str2, str3,
					 str4, str5, str6);
		    this.add(user);
		}
	    }
	}
	catch(Exception ex){
	    back += ex+" : "+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
}






















































