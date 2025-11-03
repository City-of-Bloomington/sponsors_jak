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

public class GenTypeList extends ArrayList<Type>{

    static final long serialVersionUID = 40L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(GenTypeList.class);
    String id = "";
    String table_name="spons_";
    public GenTypeList(boolean val){
	debug = val;
    }
    public GenTypeList(boolean val, String val2){
	debug = val;
	setTableName(val2);
    }	
    //
    public void setTableName(String val){
	if(val != null && !val.equals(""))
	    table_name += val;
    }
	
    public String find(){
		
	String back = "";
	Statement stmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = " select id, name from "+table_name;
	String qw = "", qo = "";
	qo = " order by name ";
	qq += qw + qo;
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
		if(str != null && str2 != null){
		    Type tt = new Type(debug, str, str2);
		    this.add(tt);
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






















































