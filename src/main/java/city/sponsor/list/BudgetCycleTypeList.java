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

public class BudgetCycleTypeList extends ArrayList<Type>{


    boolean debug = false;
    static final long serialVersionUID = 22L;	
    static Logger logger = LogManager.getLogger(BudgetCycleTypeList.class);
    String id = "";
    public BudgetCycleTypeList(boolean val){
	debug = val;
    }
    public BudgetCycleTypeList(boolean val, String val2){
	debug = val;
	setId(val2);
    }	
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
	
    public String find(){
		
	String back = "";
	Statement stmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qq = " select id, name from spons_budget_cycles  ";
	String qw = "", qo = "";
	if(!id.equals("")){
	    qw = " where id="+id;
	}
	qo = " order by id ";
	qq += qw + qo;

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }			
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






















































