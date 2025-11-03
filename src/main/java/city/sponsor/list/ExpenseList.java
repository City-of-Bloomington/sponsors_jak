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

public class ExpenseList extends ArrayList<Expense>{

    static final long serialVersionUID = 34L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(ExpenseList.class);
    String sponship_id="", oppt_id="", spon_id="",
	date_from="", date_to="", id="", vendor_id="",
	value_from = "", value_to = "",
	whichDate = "date";

    int pageSize = 50, pageNumber = 1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
    boolean all = false;
	
    public ExpenseList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);			
    }
    public ExpenseList(boolean val, boolean all){
	debug = val;
	pages = new PageList(val);
	this.all = all;
	pages.setPageSize(pageSize);			
    }	
	
    public ExpenseList(boolean val, String val2){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);
	setSponship_id(val2);
    }
    public void setSponship_id(String val){
	if(val != null){
	    sponship_id = val;
	    pages.addPair("sponship_id",val);
	}
    }
    public void setId(String val){
	if(val != null){
	    id = val;
	    pages.addPair("id",val);
	}
    }	
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	    pages.addPair("spon_id",val);
	}
    }
    public void setVendor_id(String val){
	if(val != null){
	    vendor_id = val;
	    pages.addPair("vendor_id",val);
	}
    }	
    public void setOppt_id(String val){
	if(val != null){
	    oppt_id = val;
	    pages.addPair("oppt_id",val);			
	}
    }
    public void setDate_from(String val){
	if(val != null){
	    date_from = val;
	    pages.addPair("date_from",val);
	}
    }
    public void setDate_to(String val){
	if(val != null){
	    date_to = val;
	    pages.addPair("date_to",val);
	}
    }
    public void setWhichDate(String val){
	if(val != null){
	    whichDate = val;
	    pages.addPair("whichDate",val);
	}
    }
    public void setValue_from(String val){
	if(val != null){
	    value_from = val;
	    pages.addPair("value_from",val);
	}
    }
    public void setValue_to(String val){
	if(val != null){
	    value_to = val;
	    pages.addPair("value_to",val);
	}
    }
    public void setSortBy(String val){
	if(val != null){
	    sortBy = val;
	    pages.addPair("sortBy",val);
	}
    }
    public void setPageSize(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageSize = Integer.parseInt(val);
	    }
	    catch(Exception ex){}
	    pages.setPageSize(val);
	}
    }
    public void setPageNumber(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageNumber = Integer.parseInt(val);
	    }
	    catch(Exception ex){}		
	    pages.setPageNumber(val);
	}
    }
    public int getCount(){
	return count;
    }	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String q = "select e.id,e.sponship_id,e.vendor_id,e.value,"+
	    "date_format(e.date,'%m/%d/%Y'),e.details ";
	String qf =	" from spons_expenses e ";
	String qw = "", qo = "", qq = "", qc="select count(*) ";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!id.equals("")){
		qw = " e.id = ? ";
	    }
	    if(!sponship_id.equals("")){
		qw = " e.sponship_id = ? ";
	    }
	    if(!vendor_id.equals("")){
		qw = " e.vendor_id = ? ";
	    }	
	    if(!value_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " e.value >= ? ";
	    }
	    if(!value_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " e.value <= ? ";
	    }
	    if(!date_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " e."+whichDate+" >= str_to_date('"+date_from+"','%m/%d/%Y')";
	    }
	    if(!date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " e."+whichDate+" <= str_to_date('"+date_to+"','%m/%d/%Y')";
	    }
	    if(!oppt_id.equals("")){
		qf += ", spons_sponsorships s ";
		if(!qw.equals("")) qw += " and ";
		qw += " s.id = e.sponship_id ";
		qw += " and s.oppt_id = ? ";
		if(!spon_id.equals("")){
		    qw += " and s.spon_id = ? ";
		}
	    }
	    else if(!spon_id.equals("")){
		qf += ", spons_sponsorships s ";
		if(!qw.equals("")) qw += " and ";
		qw += " s.id = e.sponship_id ";
		qw += " and s.spon_id = ? ";
	    }
	    if(!qw.equals("")){
		qw = " where "+qw;
	    }
	    qq = qc+qf+qw;
	    if(sortBy.equals("")) sortBy="date";
	    qo = " order by e."+sortBy;
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
			
	    back = setPStatement(pstmt);
	    if(back.equals("")){
		rs = pstmt.executeQuery();
		if(rs.next()){
		    count = rs.getInt(1);
		}
	    }
	    if(count > 0){
		if(!all){
		    if(pageSize > 1){
			if(pageNumber > 1){
			    int offset = pageSize*(pageNumber - 1);
			    qo += " limit "+offset+","+pageSize;
			}
			else{
			    qo += " limit "+pageSize;
			}
		    }
		}
		qq = q+qf+qw+qo;
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		back = setPStatement(pstmt);
		if(back.equals("")){
		    rs = pstmt.executeQuery();
		    while(rs.next()){
			Expense ee = new Expense(debug,
						 rs.getString(1),
						 rs.getString(2),
						 rs.getString(3),
						 rs.getString(4),
						 rs.getString(5),
						 rs.getString(6));
			this.add(ee);
		    }
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
    private String setPStatement(PreparedStatement pstmt){
	String back = "";
	try{
	    int jj  = 1;
	    if(!id.equals("")){
		pstmt.setString(jj++, id);
	    }
	    if(!sponship_id.equals("")){
		pstmt.setString(jj++, sponship_id);
	    }
	    if(!vendor_id.equals("")){
		pstmt.setString(jj++, vendor_id);
	    }	
	    if(!value_from.equals("")){
		pstmt.setString(jj++, value_from);
	    }
	    if(!value_to.equals("")){
		pstmt.setString(jj++, value_to);
	    }
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }			
	    else if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }

	}catch(Exception ex){
	    back += ex;
	    logger.error(back);
	}
	return back;
    }
    public PageList buildPages(String url){
	if(pages != null && !url.equals("")){
	    pages.setCount(count);
	    pages.setUrl(url);
	    pages.build();
	}
	return pages;
    }
	
}






















































