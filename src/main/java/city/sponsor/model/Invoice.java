package city.sponsor.model;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Invoice {
    boolean debug;
    static Logger logger = LogManager.getLogger(Invoice.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", sponship_id="", value="", dueDate="",
	invoiceDate  = "", spon_id="", voided="", remit_notes="";
    String staff_id="", // userid
	deposit_notes="", attention="";
    String date_from="", date_to=""; // to look for payments
    double total = 0., val = 0.;
    Sponsor sponsor = null;
    PaymentList payments = null;
    Map<String, String> pay_balances = null;
    ReceiptList receipts = null;
    User staff = null;
    //
    // one or more for multiple receipts per payment
    //	
    public Invoice(boolean deb) {
	debug = deb;
	payments = new PaymentList(debug);
    }
    public Invoice(boolean deb, String id) {
	debug = deb;
	setId(id);
	payments = new PaymentList(debug);
    }
    public Invoice(boolean deb,
		   String id,
		   String spon_id,
		   String total,
		   String dueDate,
		   String invoiceDate,
		   String voided,
		   String remit_notes,
		   String val8,
		   String val9,
		   String val10
		   ) {
	debug = deb;
	setId(id);
	setSpon_id(spon_id);
	setTotal(total);
	setDueDate(dueDate);
	setInvoiceDate(invoiceDate);
	setVoided(voided);
	setRemit_notes(remit_notes);
	setDeposit_notes(val8);
	setStaff_id(val9);
	setAttention(val10);
    }
	

    public String getId() {
	return id;
    }

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }

    public String getSpon_id() {
	return spon_id;
    }

    public void setSpon_id(String spon_id) {
	if(spon_id != null)
	    this.spon_id = spon_id;
    }	
    public double getTotal() {
	return total;
    }
    public void setTotal(String value) {
	if(value != null){
	    try{
		this.total = Double.parseDouble(value);
	    }catch(Exception ex){}
	}
    }

    public String getDueDate() {
	return dueDate;
    }

    public void setDueDate(String dueDate) {
	if(dueDate != null)
	    this.dueDate = dueDate;
    }
    public void setVoided(String val) {
	if(val != null && !val.equals(""))
	    setVoided();
    }	
    public void setVoided() {
	this.voided = "y";
    }
    public boolean isVoided(){
	return !voided.equals("");
    }
    public boolean hasStaff(){
	return !staff_id.equals("");
    }
    public boolean hasAttention(){
	return !attention.equals("");
    }	
    public String getInvoiceDate() {
	return invoiceDate;
    }
	
    public void setInvoiceDate(String invoiceDate) {
	if(invoiceDate != null)
	    this.invoiceDate = invoiceDate;
    }

    public void setDate_from(String val) {
	if(val != null)
	    this.date_from = val;
    }
    public void setDate_to(String val) {
	if(val != null)
	    this.date_to = val;
    }
    public void setRemit_notes(String val) {
	if(val != null)
	    this.remit_notes = val;
    }
    public String getDeposit_notes() {
	return deposit_notes;
    }

    public void setDeposit_notes(String val) {
	if(val != null)
	    deposit_notes = val;
    }
    public String getStaff_id() {
	return staff_id;
    }

    public void setStaff_id(String val) {
	if(val != null)
	    staff_id = val;
    }
    public String getAttention() {
	return attention;
    }

    public void setAttention(String val) {
	if(val != null)
	    attention = val;
    }
	
    public String getRemit_notes() {
	return remit_notes;
    }	
    public boolean equals(Invoice inv){
	return id.equals(inv.id);
    }
    public boolean hasReceipts(){
	if(id.equals("")) return false;
	if(receipts == null) getReceipts();
	return (receipts != null);
    }
	
    public double getInvoiceBalance(){
	double current_balance = total;
	if(receipts == null) getReceipts();
	if(receipts != null && receipts.size() > 0){
	    for(Receipt rpt:receipts){
		double val = rpt.getValueDbl();
		current_balance = current_balance - val;
	    }
	}
	if(current_balance < 0) current_balance = 0;
	return current_balance;
    }
	
    //
    public Sponsor getSponsor(){
	if(sponsor == null && !spon_id.equals("")){
	    Sponsor sp = new Sponsor(debug, spon_id);
	    String back = sp.doSelect();
	    if(back.equals("")){
		sponsor = sp;
	    }
	}
	return sponsor;
    }
    public User getStaff(){
	if(staff == null && !staff_id.equals("")){
	    User one = new User(debug, staff_id);
	    String back = one.doSelect();
	    if(back.equals("")){
		staff = one;
	    }
	}
	return staff;

    }
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_invoices values(0,"+
	    "?,?,?,?,?,?,?,?,?)";
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
	    pstmt.setString(1, spon_id);
	    pstmt.setDouble(2,total);				
	    if(dueDate.equals(""))
		pstmt.setNull(3,Types.DATE);
	    else
		pstmt.setDate(3, new java.sql.Date(dateFormat.parse(dueDate).getTime()));				
	    if(invoiceDate.equals(""))
		pstmt.setNull(4,Types.DATE);
	    else
		pstmt.setDate(4, new java.sql.Date(dateFormat.parse(invoiceDate).getTime()));
	    pstmt.setNull(5,Types.CHAR);
	    if(remit_notes.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,remit_notes); 
	    if(deposit_notes.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,deposit_notes);
	    if(staff_id.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,staff_id);
	    if(attention.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,attention); 
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
	    qq = "update spons_invoices set spon_id=?,"+
		"dueDate=?,invoiceDate=?,total=?,voided=?, "+
		"remit_notes=?,deposit_notes=?,staff_id=?,attention=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, spon_id);
	    if(dueDate.equals(""))
		pstmt.setNull(2,Types.DATE);
	    else
		pstmt.setDate(2, new java.sql.Date(dateFormat.parse(dueDate).getTime()));				
	    if(invoiceDate.equals(""))
		pstmt.setNull(3,Types.DATE);
	    else
		pstmt.setDate(3, new java.sql.Date(dateFormat.parse(invoiceDate).getTime()));
	    pstmt.setDouble(4,total);
	    if(isVoided())
		pstmt.setString(5,"y");
	    else
		pstmt.setNull(5,Types.CHAR);
	    if(remit_notes.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,remit_notes);
	    if(deposit_notes.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,deposit_notes);
	    if(staff_id.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,staff_id);
	    if(attention.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,attention); 				
	    pstmt.setString(10,id);
	    pstmt.executeUpdate();
	    if(isVoided()){
		deleteOldPids();
	    }
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

    public String updateTotal(){
		
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
	    qq = "update spons_invoices set total=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setDouble(1, total);
	    pstmt.setString(2, id);
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
	    qq = "delete from  spons_invoice_pays where inv_id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();				
	    qq = "delete from  spons_invoices where id=?";
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
	    "spon_id, "+
	    "date_format(dueDate,'%m/%d/%Y'), "+
	    "date_format(invoiceDate,'%m/%d/%Y'), "+
	    "total,voided,remit_notes, "+
	    "deposit_notes,staff_id,attention "+
	    "from spons_invoices where id=?";
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
		setSpon_id(rs.getString(1));
		setDueDate(rs.getString(2));
		setInvoiceDate(rs.getString(3));
		setTotal(rs.getString(4));
		setVoided(rs.getString(5));
		setRemit_notes(rs.getString(6));
		setDeposit_notes(rs.getString(7));
		setStaff_id(rs.getString(8));
		setAttention(rs.getString(9));
	    }
	    else{
		back = "Record "+id+" Not found";
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
    public String addPidToInvoice(Payment pay){
		
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
	    qq = "insert into spons_invoice_pays values(?,?,?)";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.setString(2, pay.getId());
	    pstmt.setDouble(3, pay.getBalance());
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    /**
     * in case of deleting an invoice or updating it, we need to delete
     * its pids from the table
     */
    public String deleteOldPids(){
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
	    qq = "delete from spons_invoice_pays where inv_id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String addPayments(String[] pay_ids){
	String msg = "";
	double old_total = total;
	total = 0;
	if(pay_ids != null && pay_ids.length > 0){
	    // first delete the old ones (if any);
	    msg = deleteOldPids();
	    for(String pid:pay_ids){
		Payment pay = new Payment(debug, pid);
		String back = pay.doSelect();
		if(back.equals("")){
		    back = addPidToInvoice(pay);
		    if(back.equals("")){ // avoid double entry
			double val = pay.getBalance();
			if(val > 0){
			    total += val;
			    payments.add(pay);
			}
		    }
		    else{
			msg += back;
		    }
		}
		else{
		    msg += back;
		}
	    }
	    if(total != old_total)
		msg += updateTotal();
	}
	return msg;
    }
    public PaymentList getPayments(){
	if(payments == null || payments.size() == 0){
	    PaymentList pl = new PaymentList(debug, true); // for all
	    if(!id.equals("")){
		pl.setInv_id(id);
	    }
	    else {
		pl.setSpon_id(spon_id);
	    }
	    String back = pl.find();
	    if(back.equals("")){
		payments = pl;
	    }
	    else{
		logger.error(back);
	    }
	}
	return payments;
    }
    public boolean isPaid(){
	if(payments == null || payments.size() == 0){
	    getPayments();
	}
	if(payments != null && payments.size() > 0){
	    for(Payment pay: payments){
		if(pay.hasBalance()) return false;
	    }
	}
	return true;
    }
	
    public Map<String, String> getInvoicePayBalances(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;		
	if(pay_balances == null){
	    pay_balances = new HashMap<String, String>();
	    String qq = "select pay_id,inv_balance from spons_invoice_pays where inv_id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    con = Helper.getConnection();
	    if(con == null){
		back = "No db connection";
		logger.error(back);
		return null;
	    }			
	    try{
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, id);
		rs = pstmt.executeQuery();
		while(rs.next()){
		    String str = rs.getString(1);
		    String str2 = rs.getString(2);
		    if(str != null && str2 != null){
			pay_balances.put(str,str2);
		    }
		}
	    }
	    catch(Exception ex){
		back += ex+":"+qq;
		logger.error(ex+":"+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, pstmt, rs);
	    }
	}
	return pay_balances;
    }
    public ReceiptList getReceipts(){
	if(receipts == null && !id.equals("")){
	    ReceiptList rcs = new ReceiptList(debug, id);
	    String back = rcs.find();
	    if(back.equals("")){
		if(rcs.size() > 0){
		    receipts = rcs;
		}
	    }
	}
	return receipts;
    }
}
