package city.sponsor.model;

import java.util.Calendar;
import java.util.Map;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sponsorship {

    boolean debug;
    static Logger logger = LogManager.getLogger(Sponsorship.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
    String id="", oppt_id="", // old event_id
	spon_id="", details="", don_type="",
	start_date="", terms="", value="", spon_level="",
	pay_type="", 
	ben_other="", notes="";
    String cont_start_date="", cont_end_date=""; // dates
    String end_date=""; // we need it for agreement purpose
    // we get from the last payment
    String start_date2=""; // for Mon d, Y format
    String errors = "";
    Opportunity oppt = null;
    Sponsor sponsor = null;
    PaymentList payments = null;
    DonBenTypeList benefits = null;
    ExpenseList expenses = null;
    InvoiceList invoices = null;
    String[] del_bens = null; // to delete benefits
    String[] ful_bens = null; // to fulfill benefits
    public Sponsorship(boolean deb) {
	debug = deb;
    }
    public Sponsorship(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
	
    // neeeded for service purpose
    public Sponsorship(boolean deb,
		       String id,
		       String details){
	debug = deb;
	setId(id);
	setDetails(details);
    }
		
    public Sponsorship(boolean deb,
		       String id,
		       String oppt_id,
		       String spon_id,
		       String details,
		       String don_type,
		       String start_date,
		       String terms,
		       String value,
		       String spon_level,
		       String pay_type,
		       String cont_start_date,
		       String cont_end_date,
		       String notes
		       ) {
	debug = deb;
	setId(id);
	setSpon_id(spon_id);
	setOppt_id(oppt_id);
	setDetails(details);
	setDon_type(don_type);
	setStart_date(start_date);
	setTerms(terms);
	setValue(value);
	setSpon_level(spon_level);
	setPay_type(pay_type);
	setCont_start_date(start_date);
	setCont_end_date(end_date);
	setNotes(notes);
    }	

    public String getId() {
	return id;
    }


    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }
    public String getNotes() {
	return notes;
    }


    public void setNotes(String var) {
	if(var != null)
	    this.notes = var;
    }

    public String getOppt_id() {
	return oppt_id;
    }


    public void setOppt_id(String oppt_id) {
	if(oppt_id != null)
	    this.oppt_id = oppt_id;
    }
    public Opportunity getOpportunity(){
	if(oppt == null && !oppt_id.equals("")){
	    Opportunity opp = new Opportunity(debug, oppt_id);
	    String back = opp.doSelect();
	    if(back.equals("")){
		oppt = opp;
	    }
	    else{
		errors += back;
	    }
	}
	return oppt;
    }
    public Opportunity getOppt(){
	return getOpportunity();
    }
    public Sponsor getSponsor(){
	if(sponsor == null && !spon_id.equals("")){
	    Sponsor spon = new Sponsor(debug, spon_id);
	    String back = spon.doSelect();
	    if(back.equals("")){
		sponsor = spon;
	    }
	    else{
		errors += back;
	    }
	}
	return sponsor;
    }
	
    public String getSpon_id() {
	return spon_id;
    }


    public void setSpon_id(String spon_id) {
	if(spon_id != null)
	    this.spon_id = spon_id;
    }


    public String getDetails() {
	return details;
    }


    public void setDetails(String details) {
	if(details != null)
	    this.details = details;
    }


    public String getDon_type() {
	return don_type;
    }


    public void setDon_type(String don_type) {
	if(don_type != null)
	    this.don_type = don_type;
    }


    public String getStart_date() {
	return start_date;
    }
	
    public String getStart_date2() {
	return start_date2;
    }
    public String getCont_start_date() {
	return cont_start_date;
    }
    public String getCont_end_date() {
	return cont_end_date;
    }
	
    public void setStart_date(String start_date) {
	if(start_date != null)
	    this.start_date = start_date;
    }
    public void setCont_start_date(String val) {
	if(val != null)
	    this.cont_start_date = val;
    }
    public void setCont_end_date(String val) {
	if(val != null)
	    this.cont_end_date = val;
    }

    public String getPay_type() {
	return pay_type;
    }


    public void setPay_type(String pay_type) {
	if(pay_type != null)
	    this.pay_type = pay_type;
    }


    public String getTerms() {
	return terms;
    }


    public void setTerms(String terms) {
	if(terms != null)
	    this.terms = terms;
    }


    public String getValue() {
	return value;
    }
    public double getValueDbl(){
	double val = 0;
	try{
	    val = Double.valueOf(value).doubleValue();
	}
	catch(Exception ex){

	}
	return val;
    }


    public void setValue(String value) {
	if(value != null)
	    this.value = value;
    }


    public String getSpon_level() {
	return spon_level;
    }

    public void setSpon_level(String spon_level) {
	if(spon_level != null)
	    this.spon_level = spon_level;
    }

    public void setDel_bens(String[] vals) {
	this.del_bens = vals;
    }
    public void setFul_bens(String[] vals) {
	this.ful_bens = vals;
    }
    public String getContractStartEndDates(){
	String ret = "";
	if(!cont_start_date.equals("")) 
	    ret += cont_start_date;
	if(!cont_end_date.equals("")){
	    if(!ret.equals("")) ret += ", "; 
	    ret += cont_end_date;
	}
	return ret;
    }
    public String getTotalValue(){
	String str = value;
	double total = 0.;
	if(!terms.equals("") && !value.equals("")){
	    try{
		double v = Double.parseDouble(value);
		int n = Integer.parseInt(terms);
		total = v*n;
	    }catch(Exception ex){
		logger.error(ex);
	    }
	}
	double total2 = getTotalPayFromPayments();
	return total2 > total ? ""+total2:""+total;
    }	
    public String toString(){
		
	return details;
    }
    public String getEnd_date(){
	if(end_date.equals("")){
	    if(payments == null){
		getPayments();
	    }
	    if(payments != null){
		int size = payments.size(); // we want the last one
		Payment pay = payments.get(size-1);
		end_date = pay.getDueDate();
	    }
	}
	return end_date;
    }
    public PaymentList getPayments(){
	if(payments == null && !id.equals("")){
	    PaymentList pays = new PaymentList(debug, id);
	    String back = pays.find();
	    if(back.equals("")){
		payments = pays;
	    }
	    else{
		errors += back;
	    }
	}
	return payments;
    }
    public DonBenTypeList getBenefits(){
	if(id.equals("")) return null;
	DonBenTypeList dbtl = new DonBenTypeList(debug, id);
	String back = dbtl.find();
	if(back.equals("") && dbtl.size() > 0){
	    benefits = dbtl;
	}
	return benefits;
    }
    public ExpenseList getExpenses(){
	if(expenses == null && !id.equals("")){
	    ExpenseList rows = new ExpenseList(debug, id);
	    String back = rows.find();
	    if(back.equals("")){
		expenses = rows;
	    }
	    else{
		errors += back;
	    }
	}
	return expenses;
    }
    public InvoiceList getInvoices(){
	if(invoices == null && !id.equals("")){
	    InvoiceList rows = new InvoiceList(debug);
	    rows.setSponship_id(id);
	    String back = rows.find();
	    if(back.equals("")){
		invoices = rows;
	    }
	    else{
		errors += back;
	    }
	}
	return invoices;
    }
    public boolean hasInvoices(){
		
	if(id.equals("")) return false;
	getInvoices();
		
	if(invoices == null || invoices.size() == 0){
	    return false;
	}
	return true;
    }	
    /**
     * This sponsorship has not payments,
     * if no amount value is entered
     * or payments are not generated
     */
    public boolean hasPayments(){
		
	if(value.equals("")) return false;
	getPayments();
		
	if(payments == null || payments.size() == 0){
	    return false;
	}
	return true;
    }
    public boolean hasExpenses(){
		
	getExpenses();
	if(expenses == null || expenses.size() == 0){
	    return false;
	}
	return true;
    }	
    /**
     * number of payments that already paid
     */
    public int getPaidCount(){
	int count = 0;
	if(hasPayments()){
	    for(Payment pay:payments){
		if(!pay.hasBalance()){
		    count++;
		}
	    }
	}
	return count;
    }
    public double getTotalPayFromPayments(){
	double total = 0;
	if(hasPayments()){
	    for(Payment pay:payments){
		total += pay.getValueDbl();
	    }
	}
	return total;
    }
    public double getTotalPaid(){
	double total = 0;
	if(hasPayments()){
	    for(Payment pay:payments){
		// we want the payment that are already paid
		if(!pay.hasBalance())
		    total += pay.getValueDbl();
	    }
	}
	return total;
    }	
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_sponsorships values(0,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?,?,?)";
	con = Helper.getConnection();
	if(spon_id.equals("") || oppt_id.equals("")){
	    back = "sponsor or opportunity not set ";
	    logger.error(back);
	    return back;
	}
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt.setString(1,oppt_id);
	    pstmt.setString(2,spon_id);
			
	    if(details.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,details);
	    if(don_type.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,don_type);
	    if(start_date.equals(""))
		pstmt.setNull(5,Types.DATE);
	    else
		pstmt.setDate(5, new java.sql.Date(dateFormat.parse(start_date).getTime()));
	    if(terms.equals(""))
		pstmt.setNull(6,Types.INTEGER);
	    else
		pstmt.setString(6,terms);				
	    if(value.equals(""))
		pstmt.setNull(7,Types.DOUBLE);
	    else
		pstmt.setString(7,value);
	    if(spon_level.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,spon_level);					
	    if(pay_type.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,pay_type);
	    if(cont_start_date.equals(""))
		pstmt.setNull(10,Types.DATE);
	    else
		pstmt.setDate(10, new java.sql.Date(dateFormat.parse(cont_start_date).getTime()));
	    if(cont_end_date.equals(""))
		pstmt.setNull(11,Types.DATE);
	    else
		pstmt.setDate(11, new java.sql.Date(dateFormat.parse(cont_end_date).getTime()));
	    if(notes.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,notes);						
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
	    qq = "update spons_sponsorships set oppt_id=?,"+
		"spon_id=?,details=?,don_type=?,start_date=?,"+
		"pay_type=?,terms=?,value=?,spon_level=?, "+
		"cont_start_date=?,cont_end_date=?,notes=? "+
		" where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    // System.err.println(" spon id, oppt id "+oppt_id+" "+spon_id);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,oppt_id);
	    pstmt.setString(2,spon_id);
	    if(details.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,details);
	    if(don_type.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,don_type);
	    if(start_date.equals(""))
		pstmt.setNull(5,Types.DATE);
	    else
		pstmt.setDate(5, new java.sql.Date(dateFormat.parse(start_date).getTime()));
	    if(pay_type.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6, pay_type);
	    if(terms.equals(""))
		pstmt.setNull(7,Types.INTEGER);
	    else
		pstmt.setString(7,terms);				
	    if(value.equals(""))
		pstmt.setNull(8,Types.DOUBLE);
	    else
		pstmt.setString(8,value);
	    if(spon_level.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,spon_level);
	    if(cont_start_date.equals(""))
		pstmt.setNull(10,Types.DATE);
	    else
		pstmt.setDate(10, new java.sql.Date(dateFormat.parse(cont_start_date).getTime()));
	    if(cont_end_date.equals(""))
		pstmt.setNull(11,Types.DATE);
	    else
		pstmt.setDate(11, new java.sql.Date(dateFormat.parse(cont_end_date).getTime()));
	    if(notes.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,notes);						
	    pstmt.setString(13,id);
	    pstmt.executeUpdate();
	    deleteBenefits();
	    updateFlfldBenefits();
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
		
	String back = "", qq="";
		
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	String qs = "select id from spons_payments where sponship_id=?";
	// we get pay_id
	String qs2 = "select inv_id from spons_invoice_pays where pay_id=?";
	// we get inv_id
	// if we have receipts we can not delete
	String qs3 = "select count(*) from spons_receipts where inv_id=?";
	String qd = "delete from spons_invoice_pays where inv_id=?";
	String qd2 = "delete from spons_invoices where id=? ";
	String qd3 = "delete from spons_payments where sponship_id=?";
	String qd4 = "delete from  spons_sponsorships where id=?";		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{

	    qq = qs;
	    pstmt = con.prepareStatement(qs);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    int pay_id=0, inv_id=0, cnt=0;
	    if(rs.next()){
		pay_id = rs.getInt(1);
	    }
	    if(pay_id > 0){
		qq = qs2;
		pstmt = con.prepareStatement(qs2);
		pstmt.setInt(1, pay_id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    inv_id = rs.getInt(1);
		}
		if(inv_id > 0){
		    qq = qs3;
		    pstmt = con.prepareStatement(qs3);
		    pstmt.setInt(1, inv_id);
		    rs = pstmt.executeQuery();
		    if(rs.next()){
			cnt = rs.getInt(1);
		    }
		}
	    }
	    if(cnt > 0){
		back = "Since there are receipts we can not delete";
		return back;
	    }
	    if(inv_id > 0){
		qq = qd;
		pstmt = con.prepareStatement(qd);
		pstmt.setInt(1,inv_id);
		pstmt.executeUpdate();
		qq = qd2;
		pstmt = con.prepareStatement(qd2);
		pstmt.setInt(1,inv_id);
		pstmt.executeUpdate();		
	    }
	    qq = qd3;
	    pstmt2 = con.prepareStatement(qd3);
	    pstmt2.setString(1,id);
	    pstmt2.executeUpdate();
	    qq = qd4;
	    pstmt2 = con.prepareStatement(qd4);
	    pstmt2.setString(1,id);
	    pstmt2.executeUpdate();	    
	    
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
	String qq = "select oppt_id,spon_id,"+
	    "details, don_type,"+
	    "date_format(start_date,'%m/%d/%Y'), "+
	    "pay_type, "+	
	    "terms,value, spon_level,"+
	    "date_format(start_date,'%M %d, %Y'), "+
	    "date_format(cont_start_date,'%m/%d/%Y'), "+
	    "date_format(cont_end_date,'%m/%d/%Y'), "+
	    "notes "+
	    "from spons_sponsorships where id=?";
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
		setOppt_id(rs.getString(1));
		setSpon_id(rs.getString(2));
		setDetails(rs.getString(3));
		setDon_type(rs.getString(4));
		setStart_date(rs.getString(5));  
		setPay_type(rs.getString(6));
		setTerms(rs.getString(7));  
		setValue(rs.getString(8));
		setSpon_level(rs.getString(9));
		String str = rs.getString(10);
		if(str != null)
		    start_date2 = str;
		setCont_start_date(rs.getString(11));
		setCont_end_date(rs.getString(12));
		setNotes(rs.getString(13));
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
    /**
     * generate payments from the available info in this class
     * 
     */
    public String generatePayments(){
		
	String back = "";
	int cnt = 0;
	double val = 0.0;
	try{
	    if(!terms.equals("")){
		cnt = Integer.parseInt(terms);
	    }
	    val = Double.parseDouble(value);
	    if(val <= 0){
		back = "no valid amount to be paid ";
		return back;
	    }
	    Payment pay = new Payment(debug);
	    if(cnt <= 1 ){
		pay.setSponship_id(id);
		pay.setValue(value);
		pay.setDueDate(start_date);
		pay.setBalance(value); // in the beginning value = balance
		back = pay.doSave();
		return back;
	    }
	    // first payment
	    pay.setSponship_id(id);
	    pay.setValue(value);
	    pay.setDueDate(start_date);
	    pay.setBalance(value); 
	    back = pay.doSave();
	    Calendar cal = Calendar.getInstance();
	    int mm = 0;
	    int dd = 1;
	    int yy = 2001;
	    mm = Integer.parseInt(start_date.substring(0,2));
	    dd = Integer.parseInt(start_date.substring(3,5));
	    yy = Integer.parseInt(start_date.substring(6));
	    // System.err.println(str+" "+str2+" "+str3);
	    cal.set(Calendar.MONTH, (mm-1));
	    cal.set(Calendar.DATE, dd);
	    cal.set(Calendar.YEAR, yy);			
	    for(int i=1;i<cnt;i++){
		if(pay_type.equals("Monthly")){
		    cal.add(Calendar.MONTH, 1);
		}
		else if(pay_type.equals("Quarterly")){
		    cal.add(Calendar.MONTH, 3);
		}
		else if(pay_type.equals("Semi-annual")){
		    cal.add(Calendar.MONTH, 6);
		}
		String nextDate = (cal.get(Calendar.MONTH)+1)+"/"+
		    cal.get(Calendar.DATE)+"/"+
		    cal.get(Calendar.YEAR);
		pay = new Payment(debug);
		pay.setSponship_id(id);
		pay.setValue(value);
		pay.setDueDate(nextDate);
		pay.setBalance(value);
		back += pay.doSave();
	    }
	}
	catch(Exception ex){
	    back += ex;
	}
	return back;
    }
    /**
     *
     */
    public String addBenefit(String b_id, String b_name, String b_flfld, String b_date){
	String back = "";
	if(b_name != null && !b_name.equals("")){
	    if(b_id == null || b_id.equals("")){
		BenefitType bt = new BenefitType(debug, null, b_name);
		back = bt.doSave();
		if(back.equals("")){
		    b_id = bt.getId();
		}
	    }
	    //
	    DonBenType dbt = new DonBenType(debug, null, b_id, b_name, id, b_flfld,  b_date);
	    back += dbt.add();
	    if(!back.equals("")){
		logger.error(back);
	    }
	}
	return back;
    }
    public String deleteBenefits(){
	String back = "";
	if(del_bens != null){
	    for(String str:del_bens){
		if(!str.equals("")){
		    DonBenType dbt = new DonBenType(debug, str);
		    back += dbt.remove();
		}
	    }
	}
	return back;
    }
    public String deleteBenefits(String [] del_bens){
	this.del_bens = del_bens;
	return deleteBenefits();
    }
    /**
     * this updates the fulfilled field, since this is a checkbox
     * this can be sent only when checked, so if a check box is unchecked
     * we get no input, therefore we need to uncheck all first then
     * check the ones that are checked
     */
    public String updateFlfldBenefits(){
	String back = "";
	//
	// first uncheck all benefits that we got so far
	//
	if(true){
	    DonBenType dbt = new DonBenType(debug);
	    dbt.setSponship_id(id);
	    back = dbt.uncheckAllFulfilled();
	}
	if(ful_bens != null){
	    for(String str:ful_bens){
		if(!str.equals("")){
		    DonBenType dbt = new DonBenType(debug, str);
		    dbt.setFulfilled("y");
		    back += dbt.updateFulfilled();
		}
	    }
	}
	return back;
    }
    public String updateBenFlfld_dates(Map<String, String> map){

	String back = "";
	DonBenType dbt = new DonBenType(debug);
	// Iterator it = map.entrySet().iterator();
	for(String key:map.keySet()){
	    String str = map.get(key);
	    dbt.setId(key);
	    dbt.setFlfld_date(str);
	    back += dbt.updateFlfld_date();
	}
	return back;
    }
}
