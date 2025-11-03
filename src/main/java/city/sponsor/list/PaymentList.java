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

public class PaymentList extends ArrayList<Payment>{

    static final long serialVersionUID = 57L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(PaymentList.class);
    String sponship_id="", oppt_id="", spon_id="",
	date_from="", date_to="", id="", inv_id="",
	value_from = "", value_to = "",
	whichDate = "dueDate";

    int pageSize = 50, pageNumber = 1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
    boolean all = false, hasBalance = false, notInvoicedBefore = false;
	
    public PaymentList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);			
    }
    public PaymentList(boolean val, boolean all){
	debug = val;
	pages = new PageList(val);
	this.all = all;
	pages.setPageSize(pageSize);			
    }	
	
    public PaymentList(boolean val, String val2){
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
    public void setOppt_id(String val){
	if(val != null){
	    oppt_id = val;
	    pages.addPair("oppt_id",val);			
	}
    }
    public void setInv_id(String val){
	if(val != null){
	    inv_id = val;
	    pages.addPair("inv_id",val);			
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
    public void setNotInvoicedBefore(){
	notInvoicedBefore = true;
    }
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String q = "select p.id,p.sponship_id,p.value,"+
	    "date_format(p.dueDate,'%m/%d/%Y'),balance ";
	String qf =	" from spons_payments p ";
	String qw = "", qo = "", qq = "", qc="select count(*) ";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!id.equals("")){
		qw = " p.id = ? ";
	    }
	    if(!sponship_id.equals("")){
		qw = " p.sponship_id = ? ";
	    }
	    if(!value_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " p.value >= ? ";
	    }
	    if(!value_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " p.value <= ? ";
	    }
	    if(hasBalance){
		if(!qw.equals("")) qw += " and ";
		qw += " p.balance > 0 ";
	    }	
	    if(!date_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " p."+whichDate+" >= str_to_date('"+date_from+"','%m/%d/%Y')";
	    }
	    if(!date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " p."+whichDate+" <= str_to_date('"+date_to+"','%m/%d/%Y')";
	    }
	    if(!oppt_id.equals("")){
		qf += ", spons_sponsorships s ";
		if(!qw.equals("")) qw += " and ";
		qw += " s.id = p.sponship_id ";
		qw += " and s.oppt_id = ? ";
		if(!spon_id.equals("")){
		    qw += " and s.spon_id = ? ";
		}
	    }
	    if(!spon_id.equals("")){
		qf += ", spons_sponsorships s ";
		if(!qw.equals("")) qw += " and ";
		qw += " s.id = p.sponship_id ";
		qw += " and s.spon_id = ? ";
	    }
	    if(!inv_id.equals("")){
		qf += ", spons_invoice_pays ip ";
		if(!qw.equals("")) qw += " and ";
		qw += " ip.pay_id = p.id ";
		qw += " and ip.inv_id = ? ";
	    }
	    if(notInvoicedBefore){
		qw += " and not p.id in (select pay_id from spons_invoice_pays) ";
	    }
	    if(!qw.equals("")){
		qw = " where "+qw;
	    }
	    qq = qc+qf+qw;
	    if(sortBy.equals("")) sortBy="dueDate";
	    qo = " order by p."+sortBy;
			
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
			Payment pay = new Payment(debug,
						  rs.getString(1),
						  rs.getString(2),
						  rs.getString(3),
						  rs.getString(4),
						  rs.getString(5));
			this.add(pay);
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
	    if(!value_from.equals("")){
		pstmt.setString(jj++, value_from);
	    }
	    if(!value_to.equals("")){
		pstmt.setString(jj++, value_to);
	    }
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }			
	    if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }
	    if(!inv_id.equals("")){
		pstmt.setString(jj++, inv_id);
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






















































