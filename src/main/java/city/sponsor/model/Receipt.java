package city.sponsor.model;

import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Receipt {
    boolean debug;
    static Logger logger = LogManager.getLogger(Receipt.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", inv_id="", value="", received="", check_no="",
	pay_type  = "", receipt_no="", voided="", recu_by="";
    //
    // one or more for multiple receipts per payment
    //	
    Invoice invoice = null; 
    User receiver = null;
    public Receipt(boolean deb) {
	debug = deb;
    }
    public Receipt(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
    public Receipt(boolean deb,
		   String id,
		   String receipt_no,
		   String inv_id,
		   String value,
		   String check_no,
		   String received,	                             
		   String pay_type,
		   String voided,
		   String recu_by) {
	debug = deb;
	setId(id);
	setReceipt_no(receipt_no);
	setInv_id(inv_id);		
	setValue(value);
	setCheck_no(check_no);
	setReceived(received);
	setPay_type(pay_type);
	setVoided(voided);
	setRecu_by(recu_by);
    }
	

    public String getId() {
	return id;
    }

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }
    public String getReceipt_no() {
	if(id.equals("") && receipt_no.equals("")){
	    generateReceipt_no();
	}
	return receipt_no;
    }

    public void setReceipt_no(String val) {
	if(val != null)
	    this.receipt_no = val;
    }
	
    public String getInv_id() {
	return inv_id;
    }

    public void setInv_id(String inv_id) {
	if(inv_id != null)
	    this.inv_id = inv_id;
    }
    public String getRecu_by() {
	return recu_by;
    }

    public void setRecu_by(String recu_by) {
	if(recu_by != null)
	    this.recu_by = recu_by;
    }
    public String getRecuByFullName() {
	String ret = "";
	if(receiver == null && !recu_by.equals("")){
	    User tmpUser = new User(debug, recu_by);
	    String back = tmpUser.doSelect();
	    if(back.equals("")){
		receiver = tmpUser;
	    }
	}
	if(receiver != null){
	    ret = receiver.getFullName();
	}
	return ret;
    }	
    public String getValue() {
	return value;
    }
    public double getValueDbl() {
	double val = 0.;
	try{
	    val = Double.parseDouble(value);
	}catch(Exception ex){}
	return val;
    }
    public void setValue(String value) {
	if(value != null)
	    this.value = value;
    }

    public String getCheck_no() {
	return check_no;
    }

    public void setCheck_no(String val) {
	if(val != null)
	    this.check_no = val;
    }

    public String getReceived() {
	return received;
    }

    public void setReceived(String val) {
	if(val != null)
	    this.received = val;
    }
    public String getPay_type() {
	return pay_type;
    }

    public void setPay_type(String val) {
	if(val != null)
	    this.pay_type = val;
    }
    public String getVoided() {
	return voided;
    }

    public void setVoided(String val) {
	if(val != null)
	    this.voided = val;
    }
    public boolean isVoided(){
	return !voided.equals("");
    }
    public boolean validate(){
	if(value.equals("") ||
	   inv_id.equals("")) return false;
	return true;
    }
    public Invoice getInvoice(){
	if(invoice == null && !inv_id.equals("")){
	    Invoice inv =  new Invoice(debug, inv_id);
	    String back = inv.doSelect();
	    if(back.equals("")){
		invoice = inv;
	    }
	}
	return invoice;
    }
    public synchronized String generateReceipt_no(){
	String back="";
	String qq = "insert into spons_receipt_seq values(0)";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	int r_no = 0;
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);				
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		r_no = rs.getInt(1);
	    }
	    receipt_no = ""+r_no;
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_receipts values(0,"+
	    "?,?,?,"+
	    "?,?,?,?,?)";
	if(!validate()){
	    back = " one of the critical values are missing ";
	    logger.error(back);
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt.setString(1, receipt_no);
	    pstmt.setString(2, inv_id);
	    pstmt.setString(3, value);
	    if(check_no.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,check_no);
	    if(received.equals("")){
		received = Helper.getToday();
	    }
	    pstmt.setDate(5, new java.sql.Date(dateFormat.parse(received).getTime()));				
	    if(pay_type.equals(""))
		pstmt.setNull(6,Types.DATE);
	    else
		pstmt.setString(6, pay_type);
	    if(voided.equals(""))
		pstmt.setNull(7,Types.CHAR);
	    else
		pstmt.setString(7, "y");
	    if(recu_by.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,recu_by);	
	    pstmt.executeUpdate();
	    //
	    // get the id of the new record
	    //
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);				
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    back = updatePaymentsBalance();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
    public String doUpdate(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String str="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "update spons_receipts set "+
		"value=?,received=?,check_no=?,pay_type=?,voided=?, "+
		"recu_by=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,value);
	    if(received.equals("")) received = Helper.getToday();
	    pstmt.setDate(2, new java.sql.Date(dateFormat.parse(received).getTime()));				
	    if(check_no.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3, check_no);
	    if(pay_type.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4, pay_type);
	    if(voided.equals(""))
		pstmt.setNull(5,Types.CHAR);
	    else
		pstmt.setString(5, "y");	
	    if(recu_by.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6, recu_by);
	    pstmt.setString(7,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
	
    public String doDelete(){
		
	String back = "", qq = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "delete from  spons_receipts where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
	
    //
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select "+
	    "receipt_no,inv_id, value,check_no,"+
	    "date_format(received,'%m/%d/%Y'), "+
	    "pay_type, voided, recu_by "+
	    "from spons_receipts where id=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setReceipt_no(rs.getString(1));
		setInv_id(rs.getString(2));
		setValue(rs.getString(3));
		setCheck_no(rs.getString(4));
		setReceived(rs.getString(5));
		setPay_type(rs.getString(6));
		setVoided(rs.getString(7));
		setRecu_by(rs.getString(8));
	    }
	    else{
		return "Record "+id+" Not found";
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);			
	}
	return back;
    }	
    public String updatePaymentsBalance(){
	double val = 0.;
	String back = "";
	PaymentList pays = null;
	if(!value.equals("")){
	    if(invoice == null){
		getInvoice();
	    }
	    try{
		val = Double.parseDouble(value);
	    }catch(Exception ex){
		logger.error(ex);
	    }
	    if(invoice != null){
		pays = invoice.getPayments();
	    }
	    if(val > 0){
		if(pays != null && pays.size() > 0){
		    for(Payment pay:pays){
			double bal = pay.getBalance();
			if(bal > 0){
			    if(val >= bal){
				pay.setBalance(0);
				pay.doUpdate();
				val = val - bal;
			    }
			    else{
				bal = bal - val;
				pay.setBalance(bal);
				pay.doUpdate();
			    }
			}
		    }
		}
	    }
	}
	return back;
    }
	
}
