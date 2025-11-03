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
 * find the income that was received witthin certain period
 * using receipts that are paid
 */

public class IncomeList extends ArrayList<Income>{

    static final long serialVersionUID = 41L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(IncomeList.class);
    String spon_id="", oppt_id="", sponship_id="", date_from="", date_to="";
    public IncomeList(boolean val){
	debug = val;
    }
	
    public IncomeList(boolean val, String val2){
	debug = val;
	setOppt_id(val2);
    }
    public void setSpon_id(String val){
	if(val != null)
	    spon_id = val;
    }
    public void setSponship_id(String val){
	if(val != null)
	    sponship_id = val;
    }
    public void setOppt_id(String val){
	if(val != null)
	    oppt_id = val;
    }
    public void setDate_from(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDate_to(String val){
	if(val != null)
	    date_to = val;
    }	
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String q = "select r.id,r.inv_id,r.value,"+
	    "date_format(r.received,'%m/%d/%Y'),"+
	    " p.sponship_id,sp.oppt_id ";

	String qf = " from spons_receipts r,spons_invoices i,"+
	    "spons_payments p, "+			
	    "spons_invoice_pays ip,spons_sponsorships sp ";

	String qw = "where r.inv_id=i.id and "+
	    " i.id=ip.inv_id and "+
	    " p.id=ip.pay_id and "+
	    " p.sponship_id=sp.id and r.voided is null ";
	String qq = "";
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(!date_from.equals("")){
		qw += " and r.received >= str_to_date('"+date_from+"','%m/%d/%Y')";
	    }
	    if(!date_to.equals("")){
		qw += " and r.received <= str_to_date('"+date_to+"','%m/%d/%Y')";
	    }
	    if(!oppt_id.equals("")){
		qw += " and sp.oppt_id = ?";
	    }
	    qw += " order by sp.oppt_id ";
	    qq = q + qf + qw;
	    if(debug){
				
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Income one = new Income(debug,
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getString(4),
					rs.getString(5),
					rs.getString(6));
		this.add(one);
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






















































