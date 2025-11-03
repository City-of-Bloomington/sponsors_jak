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

public class InvoiceList extends ArrayList<Invoice>{

    static final long serialVersionUID = 43L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(InvoiceList.class);
    String sponship_id="", oppt_id="", spon_id="", id="",
	date_from="", date_to="",
	value_from = "", value_to = "",
	whichDate = "dueDate";

    int pageSize = 50, pageNumber = 1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
    boolean all = false, hasBalance = false;
	
    public InvoiceList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);			
    }
    public InvoiceList(boolean val, boolean all){
	debug = val;
	pages = new PageList(val);
	this.all = all;
	pages.setPageSize(pageSize);			
    }	
	
    public InvoiceList(boolean val, String val2){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);
	setSpon_id(val2);
    }
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	    pages.addPair("spon_id",val);
	}
    }
    public void setId(String val){
	if(val != null){
	    id = val;
	    pages.addPair("id",val);
	}
    }	
    public void setSponship_id(String val){
	if(val != null){
	    sponship_id = val;
	    pages.addPair("sponship_id",val);
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
    public void hasBalance(){
	hasBalance = true;
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
	String q = "select i.id,i.spon_id,i.total,"+
	    "date_format(i.dueDate,'%m/%d/%Y'), "+
	    "date_format(i.invoiceDate,'%m/%d/%Y'),i.voided,i.remit_notes, "+
	    "i.deposit_notes,i.staff_id,i.attention ";
	String qf =	" from spons_invoices i ";
	String qw = "", qo = "", qq = "", qc="select count(*) ";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!id.equals("")){
		qw = " i.id = ? ";
	    }		
	    if(!spon_id.equals("")){
		qw = " i.spon_id = ? ";
	    }				
	    if(!value_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " i.total >= ? ";
	    }
	    if(!value_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " i.total <= ? ";
	    }
	    if(!date_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " i."+whichDate+" >= str_to_date('"+date_from+"','%m/%d/%Y')";
	    }
	    if(!date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " i."+whichDate+" <= str_to_date('"+date_to+"','%m/%d/%Y')";
	    }
	    if(hasBalance){
		qf += ", spons_payments p,spons_invoice_pays ps ";
		if(!qw.equals("")) qw += " and ";
		qw += " i.voided is null ";
		qw += " and p.id = ps.pay_id and i.id=ps.inv_id ";
		qw += " and p.balance > 0 ";
		if(!sponship_id.equals("")){
		    qw += " p.sponship_id = ? ";
		}
	    }	
	    else if(!sponship_id.equals("")){
		qf += ", spons_payments p,spons_invoice_pays ps ";
		if(!qw.equals("")) qw += " and ";
		qw += " p.id = ps.pay_id and i.id=ps.inv_id ";
		qw += " and p.sponship_id = ? ";
	    }
	    if(!qw.equals("")){
		qw = " where "+qw;
	    }
	    qq = qc+qf+qw;
	    if(sortBy.equals("")) sortBy="dueDate";
	    qo = " order by i."+sortBy+" DESC";
			
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
		    Set<String> set = new HashSet<String>();
		    while(rs.next()){
			if(!set.contains(rs.getString(1))){
			    set.add(rs.getString(1));
			    Invoice inv = new Invoice(debug,
						      rs.getString(1),
						      rs.getString(2),
						      rs.getString(3),
						      rs.getString(4),
						      rs.getString(5),
						      rs.getString(6),
						      rs.getString(7),
						      rs.getString(8),
						      rs.getString(9),
						      rs.getString(10)
						      );
			    this.add(inv);
			}
		    }
		    set = null;
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
	    if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }
	    if(!value_from.equals("")){
		pstmt.setString(jj++, value_from);
	    }
	    if(!value_to.equals("")){
		pstmt.setString(jj++, value_to);
	    }
	    /*
	      if(!oppt_id.equals("")){
	      pstmt.setString(jj++, oppt_id);
	      }
	    */
	    if(!sponship_id.equals("")){
		pstmt.setString(jj++, sponship_id);
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






















































