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

public class ReceiptList extends ArrayList<Receipt>{

    static final long serialVersionUID = 61L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(ReceiptList.class);
    String spon_id="", inv_id="";
    public ReceiptList(boolean val){
	debug = val;
    }
	
    public ReceiptList(boolean val, String val2){
	debug = val;
	setInv_id(val2);
    }
    public ReceiptList(boolean val, String val2, String val3){
	debug = val;
	setSpon_id(val3);
    }
    public void setInv_id(String val){
	if(val != null)
	    inv_id = val;
    }	
    public void setSpon_id(String val){
	if(val != null)
	    spon_id = val;
    }
	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String q = "select r.id,r.receipt_no,r.inv_id,r.value,"+
	    "r.check_no, "+
	    "date_format(r.received,'%m/%d/%Y'), r.pay_type,r.voided,r.recu_by ";
	String qf = " from spons_receipts r ";
	String qw = "", qq = "";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!inv_id.equals("")){
		qw += " r.inv_id = ? ";
	    }
	    if(!qw.equals("")){
		qw = " where "+qw;
	    }
	    qq = q + qf + qw;
	    if(debug){
				
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!inv_id.equals("")){
		pstmt.setString(jj, inv_id);
	    }
	    jj++;
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Receipt recp = new Receipt(debug,
					   rs.getString(1),
					   rs.getString(2),
					   rs.getString(3),
					   rs.getString(4),
					   rs.getString(5),
					   rs.getString(6),
					   rs.getString(7),
					   rs.getString(8),
					   rs.getString(9));
		this.add(recp);
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






















































