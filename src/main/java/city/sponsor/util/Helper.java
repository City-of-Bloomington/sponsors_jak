package city.sponsor.util;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import java.io.*;
import java.text.*;
import city.sponsor.model.*;
import city.sponsor.list.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.security.MessageDigest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class Helper{

    static Logger logger = LogManager.getLogger(Helper.class);
    static int c_con = 0;
    public final static Locale local = new Locale("en","US");
    public final static TimeZone tzone = TimeZone.getTimeZone("America/Indiana/Indianapolis");		
    public final static int maxActionCount = 2;
    public final static int maxSponsorshipCount = 3;
    public final static int maxOpportCount = 3;
    public final static int criticalDays = 3; // for notes followup date
    public final static String seasonArr[] = {"Summer","Fall/Winter","Winter/Spring","Ongoing"};
    public final static String statusArr[] = {"Prospect","Active","Inactive"};
    public final static String actionStatusArr[] = {"Ongoing","Completed"};
    public final static String sponLevelIdArr[] = { "Donor","Patron","Supporter","Contributer","Partner"};
    public final static String sponLevelArr[] = { "Donor 0-50","Patron 51-150","Supporter 151-250","Contributor 251-500","Partner >500 "};
    public final static String donTypeArr[] = {"tangible goods","monetary","gift certificate","service" };
    public final static String payTypeArr[] = {"Monthly","Quarterly","Semi-annual","Other"};
    public final static String payMethodArr[] = {"Cash","Check","Money Order","Credit Card","EFT"};
    // public final static String yearArr[] = {"2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021"};	
    // xhtmlHeader.inc
    public final static String xhtmlHeaderInc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"+
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    //
    // Non static variables
    //
    static NumberFormat cf = NumberFormat.getCurrencyInstance(local);	
    boolean debug = false;

    String [] deptIdArr = null;
    String [] deptArr = null;

    //
    // basic constructor
    public Helper(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
    public final static String getHashCodeOf(String buffer){

	String key = "Apps Secret Key "+getToday();
	byte[] out = performDigest(buffer.getBytes(),buffer.getBytes());
	String ret = bytesToHex(out);
	return ret;
	// System.err.println(ret);

    }
    public final static byte[] performDigest(byte[] buffer, byte[] key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return md5.digest(key);
        } catch (Exception e) {
	    System.err.println(e);
        }
        return null;
    }

    public final static String bytesToHex(byte in[]) {
	byte ch = 0x00;
	int i = 0; 
	if (in == null || in.length <= 0)
	    return null;
	String pseudo[] = {"0", "1", "2",
	    "3", "4", "5", "6", "7", "8",
	    "9", "A", "B", "C", "D", "E",
	    "F"};
	StringBuffer out = new StringBuffer(in.length * 2);
	while (i < in.length) {
	    ch = (byte) (in[i] & 0xF0); // Strip off high nibble
		
	    ch = (byte) (ch >>> 4);
	    // shift the bits down
	    
	    ch = (byte) (ch & 0x0F);    
	    // must do this is high order bit is on!

	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    i++;
	}
	String rslt = new String(out);
	return rslt;
    }    
    //
    /**
     * Adds escape character before certain characters
     *
     */
    public final static String escapeIt(String s) {
		
	StringBuffer safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	boolean noSlashBefore = true;
	while (c < len) {                           
	    if ((safe.charAt(c) == '\'' ||
		 safe.charAt(c) == '"') && noSlashBefore){
		safe.insert(c, '\\');
		c += 2;
		len = safe.length();
		noSlashBefore = true;
	    }
	    else if(safe.charAt(c) == '\\'){
		c++;
		noSlashBefore = false;
	    }
	    else {
		c++;
		noSlashBefore = true;
	    }
	}
	return safe.toString();
    }
    //
    // users are used to enter comma in numbers such as xx,xxx.xx
    // as we can not save this in the DB as a valid number
    // so we remove it 
    public final static String cleanNumber(String s) {

	if(s == null) return null;
	String ret = "";
	int len = s.length();
	int c = 0;
	int ind = s.indexOf(",");
	if(ind > -1){
	    ret = s.substring(0,ind);
	    if(ind < len)
		ret += s.substring(ind+1);
	}
	else
	    ret = s;
	return ret;
    }
    /**
     * replaces the special chars that has certain meaning in html
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final static String replaceSpecialChars(String s) {
	char ch[] ={'\'','\"','>','<'};
	String entity[] = {"&#39;","&#34;","&gt;","&lt;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String replaceAmp(String s) {
	char ch[] ={'&'};
	String entity[] = {"&amp;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String replaceQuote(String s) {
	char ch[] ={'\''};
	String entity[] = {"_"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    //
    //
    public final static Connection getConnection(){
	return getConnectionProd();
    }
    public final static String[] getYearsArr(){
	int year=2018;
	GregorianCalendar cal = new GregorianCalendar(tzone, local);		
	year = cal.get(Calendar.YEAR);
	String[] years = {"","","","","", "","","","",""};
	int jj=0;
	for(int yy=year;yy > year-10;yy--){
	    years[jj] = ""+yy;
	    jj++;
	}
	return years;
    }
    //	
	
    public final static Connection getConnectionProd(){ // pooling

	Connection con = null;
	int trials = 0;
	boolean pass = false;
	while(trials < 3 && !pass){
	    try{
		trials++;
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL_sponsor");
		con = ds.getConnection();
		if(con == null){
		    String str = " Could not connect to DB ";
		    logger.error(str);
		}
		else{
		    pass = testCon(con);
		    if(pass){
			c_con++;
			logger.debug("Got connection: "+c_con);
			logger.debug("Got connection at try "+trials);
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return con;
    }
	
    public final static boolean testCon(Connection con){
		
	boolean pass = false;
	Statement stmt  = null;
	ResultSet rs = null;
	String qq = "select 1+1";		
	try{
	    if(con != null){
		stmt = con.createStatement();
		logger.debug(qq);
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    pass = true;
		}
	    }
	    rs.close();
	    stmt.close();
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	return pass;
    }	
    /**
     * Connect to Oracle database
     *
     * @param dbStr database connect string
     * @param dbUser database user string
     * @param dbPass database password string
     */
    public final static Connection databaseConnect(String dbStr, 
						   String dbUser, 
						   String dbPass) {
	Connection con=null;
	try {
	    Class.forName("oracle.jdbc.driver.OracleDriver");
	    con = DriverManager.getConnection(dbStr,
					      dbUser,dbPass);

	}
	catch (Exception sqle) {
	    System.err.println(sqle);
	}
	return con;
    }
    /**
     * Disconnect the database and related statements and result sets
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Connection con,Statement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	    if(con != null) con.close();
	    con = null;
			
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }
    public final static void databaseDisconnect(Connection con,
						PreparedStatement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	    if(con != null) con.close();
	    con = null;
			
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }	
    /**
     * Write the number in bbbb.bb format needed for currency.
     * = toFixed(2)
     * @param dd the input double number
     * @returns the formated number as string
     */
    public final static String formatNumber(double dd){
	//
	String str = ""+dd;
	String ret="";
	int l = str.length();
	int i = str.indexOf('.');
	int r = i+3;  // required length to keep only two decimal
	// System.err.println(str+" "+l+" "+r);
	if(i > -1 && r<l){
	    ret = str.substring(0,r);
	}
	else{
	    ret = str;
	}
	return ret;
    }
    /**
     *
     */
    public final static String fillUpTo(int skip, String str, int size, String with){
	int len = str.length();
	int end = size-skip-len;
	String ret = "";
	if(with.equals("")) with=" "; // space
	if(end > 0){
	    for(int j=0;j<end;j++) ret += with;
	}
	return ret;
    }
    public final static String fillUpTo(int skip, String str, int size){
	return fillUpTo(skip, str, size, " ");
    }		
    //
    // format a number with only 2 decimal
    // usefull for currency numbers
    //
    public final static String formatNumber(String that){

	int ind = that.indexOf(".");
	int len = that.length();
	String str = "";
	if(ind == -1){  // whole integer
	    str = that + ".00";
	}
	else if(len-ind == 2){  // one decimal
	    str = that + "0";
	}
	else if(len - ind > 3){ // more than two
	    str = that.substring(0,ind+3);
	}
	else str = that;

	return str;
    }

    //
    // main page banner
    //
    public final static String banner(String url){

	String banner = "<head>\n"+
	    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"+
	    "<meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\">"+
	    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+	    
	    "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n"+
	    "<link rel=\"SHORTCUT ICON\" href=\"/favicon.ico\" />\n"+
	    "<style type=\"text/css\" media=\"screen\">\n"+
	    "@import url(\"/skins/default/skin.css\");\n"+
	    "</style>\n"+
	    "<style type=\"text/css\" media=\"print\">@import url(\"/skins/default/print.css\");</style>\n"+
	    "<script src=\"/functions.js\" type=\"text/javascript\"></script>\n"+
	    "<title>Sponsors - City of Bloomington, Indiana</title>\n"+
	    "</head>\n"+
	    "<body>\n"+
	    "<div id=\"banner\">\n"+
	    "<h1><a href=\""+url+"Sponsors\">Sponsors</a></h1><h2>City of Bloomington, Indiana</h2>\n"+
	    "</div>";
	return banner;
    }
    //
    public final static String menuBar(String url, boolean logged){
	String menu = "<div class=\"menuBar\">\n"+
	    "<a href=\""+url+"\">Home</a>\n";
	if(logged){
	    menu += "<a href=\""+url+"Sponsors/Logout\">Logout</a>\n";
	}
	menu += "</div>\n";
	return menu;
    }
    public final static String getThisYear(){

	String year="";
	GregorianCalendar cal = new GregorianCalendar(tzone, local);		
	year = ""+ cal.get(Calendar.YEAR);
	return year;
    }	
    //
    // Non static methods and variables
    //
    public final static String getToday(){

	String day="",month="",year="";
	GregorianCalendar cal = new GregorianCalendar(tzone, local);		
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.get(Calendar.DATE);
	year = ""+ cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static int[] getYearList(){
	GregorianCalendar cal = new GregorianCalendar(tzone, local);		
	int year = cal.get(Calendar.YEAR);
	int years[] = new int[11];
	int i=0;
	for(int jj= year+1;jj >= year - 10;jj--){
	    years[i++] = jj;
	    if(i > 10) break;
	}
	return years;
    }
    public final static String getTimeMilitary(){

	int hour=0,minute=0;
	String timeformat = "HH:mm";		
	SimpleDateFormat dateFormat = new SimpleDateFormat(timeformat);
	GregorianCalendar cal = new GregorianCalendar(tzone, local);		
	String time =  dateFormat.format(cal.getTime());
	return time;
    }	
    //
    public final static String getToday2(){

	String months[] = {"","Jan","Feb","March","April","May","June","July",
	    "Aug","Sept","Oct","Nov","Dec"};
	String day="",month="",year="";
	GregorianCalendar cal = new GregorianCalendar(tzone, local);
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.get(Calendar.DATE);
	year = ""+ cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return months[mm]+" "+day+", "+year;
    }
	
    public final static String getEndNextMonth(){

	String day="",month="",year="";
	GregorianCalendar cal = new GregorianCalendar(tzone, local);
	cal.add(Calendar.MONTH, 1);
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	year = ""+ cal.get(Calendar.YEAR);
		
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static String initCapWord(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){  
		if(str_in.charAt(1) == '\'' && str_in.length() > 2){ // O'Donell
		    ret = str_in.substring(0,3).toUpperCase()+
			str_in.substring(3).toLowerCase();
		}
		else{
		    ret = str_in.substring(0,1).toUpperCase()+
			str_in.substring(1).toLowerCase();
		}
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	return ret;
    }	
    //
    // initial cap a word
    //
    public final static String initCapWord2(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){
		ret = str_in.substring(0,1).toUpperCase()+
		    str_in.substring(1).toLowerCase();
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	return ret;
    }
    //
    // init cap a phrase
    //
    public final static String initCap(String str_in){
	String ret = "";
	if(str_in != null){
	    if(str_in.indexOf(" ") > -1){
		String[] str = str_in.split("\\s"); // any space character
		for(int i=0;i<str.length;i++){
		    if(i > 0) ret += " ";
		    ret += initCapWord(str[i]);
		}
	    }
	    else
		ret = initCapWord(str_in);// it is only one word
	}
	return ret;
    }
    public final static User findUserFromList(List<User> users, String empid){
	User foundUser = null;
	if(users != null && users.size() > 0){ 
	    for (int i=0;i<users.size();i++){
		User user = users.get(i);
		if(user != null && user.getUserid().equals(empid)){
		    foundUser = user;
		    break;
		}
	    }
	}
	return foundUser;
    }
    /**
     * table format, used in need attention and search
     */ 
    public final static void writeActions(PrintWriter out,
				   ActionList actions,
				   String url){
	int cnt = actions.getCount();
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Found "+cnt+"  notes </caption>");			
	if(cnt > 0){
	    out.println("<tr>"+
			"<th>Date</th>"+
			"<th>Issued By</th>"+
			"<th>Sponsor</th>"+
			"<th>Opportunity </th>"+
			"<th>Sponsorship </th>"+
			"<th>Contact</th>"+
			"<th>Benefit</th>"+
			"<th>Followup Date</th>"+
			"<th>Notes</th>"+
			"<th>Status</th>"+
			"</tr>");
	    int jj = 0;
	    for(Action act:actions){
		out.println("<tr>");
		Opportunity oppt = act.getOppt();
		Sponsor sponsor = act.getSponsor();
		Contact contact = act.getContact();
		Sponsorship sponship = act.getSponship();
		DonBenType benefit = act.getBenefit();
		boolean critical = act.isCritical();
		String bgcolor="";
		out.println("<tr bgcolor="+bgcolor+">");
		out.println("<td><a href=\""+url+
			    "ActionServ?id="+act.getId()+"\">"+act.getDate()+
			    "</a></td>");
		out.println("<td>"+act.getByName()+"</td>");
		if(sponsor == null){
		    out.println("<td>&nbps;</td>");
		}
		else{
		    out.println("<td><a href=\""+url+
				"SponsorServ?action=zoom&amp;"+
				"id="+sponsor.getId()+"\">"+
				sponsor.getOrgname()+"</a></td>");
		}
		if(oppt == null){
		    out.println("<td>&nbsp;</td>");
		}
		else{
		    out.println("<td><a href=\""+url+
				"OpportServ?action=zoom&amp;"+
				"id="+oppt.getId()+"\">"+
				oppt.getInfo()+"</a></td>");
		}
		if(sponship == null){
		    out.println("<td>&nbsp;</td>");
		}
		else{
		    out.println("<td><a href=\""+url+
				"SponsorshipServ?action=zoom&amp;"+
				"id="+sponship.getId()+"\">"+
				sponship.getDetails()+"</a></td>");
		}
		if(contact == null){
		    out.println("<td>&nbsp;</td>");
		}
		else{
		    out.println("<td><a href=\""+url+
				"ContactServ?action=zoom&amp;"+
				"id="+contact.getId()+"\">"+
				contact+"</a></td>");
		}
		if(benefit == null){
		    out.println("<td>&nbsp;</td>");
		}
		else{
		    out.println("<td>"+benefit.getName()+"</td>");
		}
		out.println("<td>"+act.getFollowup()+
			    "</td>");
		out.println("<td>"+act.getNotes()+
			    "</td>");	
		out.println("<td>"+act.getStatus()+
			    "</td>");
		out.println("</tr>");
		if(jj >= 2) jj = 0;
		else jj++;
	    }
	}
	out.println("</table>");
    }
    public final static void writeActionsVertical(PrintWriter out,
					   ActionList actions,
					   String url){
	if(actions == null || actions.size() < 1){
	    return;
	}
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Recent Notes </caption>");
	int jj = 0;		
	for(Action actt:actions){
	    Sponsorship sponship = null;
	    Sponsor sponsor = null;
	    Opportunity oppt = null;
	    DonBenType benefit = null;
	    Contact contact = null;
	    sponship = actt.getSponship();
	    sponsor = actt.getSponsor();
	    oppt = actt.getOppt();
	    benefit = actt.getBenefit();
	    contact = actt.getContact();
	    out.println("<tr><th>Date</th><td class=\"left\"><a href=\""+
			url+"ActionServ?id="+actt.getId()+"\">"+
			actt.getDate()+"</a></td></tr>");
	    out.println("<tr><th>Initiated By</th><td class=\"left\">"+actt.getByName()+"</td></tr>");		
	    if(sponsor != null){
		out.println("<tr><th>Sponsor</th><td class=\"left\">"+sponsor+"</td></tr>");
	    }
	    if(oppt != null){
		out.println("<tr><th>Opportunity</th><td class=\"left\">"+oppt+"</td></tr>");
	    }
	    if(sponship != null){
		out.println("<tr><th>Sponsorship</th><td class=\"left\">"+sponship+"</td></tr>");
	    }
	    if(contact != null){
		out.println("<tr><th>Contact</th><td class=\"left\">"+contact.getInfo()+"</td></tr>");
	    }
	    if(benefit != null){
		out.println("<tr><th>Benefit</th><td class=\"left\">"+benefit.getName()+"</td></tr>");
	    }				
	    String alert_date = actt.hasAlert()?actt.getAlert_date():"";
	    if(!alert_date.equals("")){
		out.println("<tr><th>Has Alert on</th><td class=\"left\">"+alert_date+"</td></tr>");
	    }
	    if(!actt.getAnotherUserid().equals("")){
		out.println("<tr><th>Notify Another User</th><td class=\"left\">"+actt.getAnotherUserid()+"</td></tr>");
	    }						
	    out.println("<tr><th>Notes</th><td class=\"left\">"+actt.getNotes()+"</td></tr>");
			
	    String follow = actt.getFollowup();
	    if(!follow.equals("")){
		out.println("<tr><th>Followup Date</th><td class=\"left\">"+follow+"</td></tr>");
	    }
			
	    out.println("<tr><td colspan=\"2\" class=\"center\"><hr width=\"80%\" /></td></tr>");
	    jj++;
	    if(jj > Helper.maxActionCount) break;
	}
	out.println("</table>");
		
    }
    public final static void writePayments(PrintWriter out,
				    PaymentList pays,
				    String url){
	if(pays != null && pays.size() > 0){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Payments </caption>");
	    out.println("<tr><th>Payment</th>"+
			"<th>Due Date</th>"+
			"<th>Amount</th>"+
			"<th>Balance</th>"+
			"<th>Status</th>"+
			"</tr>");
	    double total = 0;
	    double balance = 0;
	    for(Payment pay:pays){
		out.println("<tr>");
		out.println("<td><a href=\""+url+"PaymentServ?id="+pay.getId()+"\">"+pay.getId()+"</a></td>");
		out.println("<td>"+pay.getDueDate()+"</td>");
		out.println("<td class=\"money\">"+cf.format(pay.getValueDbl())+"</td>");
		out.println("<td class=\"money\">"+cf.format(pay.getBalance())+"</td>");
		out.println("<td>"+pay.getStatus()+"</td>");
		out.println("</tr>");
		total += pay.getValueDbl();						
		balance += pay.getBalance();
	    }
	    out.println("<tr><td colspan=\"2\" class=\"money\"><b>Total</b></td>");
	    out.println("<td class=\"money\">"+cf.format(total)+"</td>");
	    out.println("<td class=\"money\">"+cf.format(balance)+"</td>");
	    out.println("<td>&nbsp;</td></tr>");
	    out.println("</table>");
	}
    }
    public final static void writeExpenses(PrintWriter out,
				    ExpenseList rows,
				    String url){
	if(rows != null && rows.size() > 0){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Expenses </caption>");
	    out.println("<tr><th>Expense</th>"+
			"<th>Vendor</th>"+
			"<th>Date</th>"+
			"<th>Amount</th>"+
			"<th>Details</th>"+
			"</tr>");
	    double total = 0;
	    for(Expense expen:rows){
		out.println("<tr>");
		out.println("<td><a href=\""+url+"ExpenseServ?id="+expen.getId()+"\">"+expen.getId()+"</a></td>");
		out.println("<td>"+expen.getVendor()+"</td>");
		out.println("<td>"+expen.getDate()+"</td>");
		total += expen.getValueDbl();
		out.println("<td class=\"money\">"+cf.format(expen.getValueDbl())+"</td>");
		out.println("<td>"+expen.getDetails()+"</td>");
				
		out.println("</tr>");
	    }
	    if(rows.size() > 1){
		out.println("<tr><td colspan=\"3\" class=\"money\"><b>Total</b></td>");
		out.println("<td class=\"money\">"+cf.format(total)+"</td>");
		out.println("<td>&nbsp;</td></tr>");
	    }
	    out.println("</table>");
	}
    }	
    public final static void writeContacts(PrintWriter out,
				    ContactList contacts,
				    String url){
	if(contacts != null && contacts.size() > 0){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Current Contacts</caption>");
	    out.println("<tr>"+
			"<th>Name</th>"+
			"<th>Title</th>"+
			"<th>Phones</th>"+
			"<th>Email</th></tr>");
	    for(Contact cc:contacts){
		out.println("<tr>");
		String primary = cc.isPrimary()?" (Primary)":"";
		out.println("<td><a href=\""+url+
			    "ContactServ?action=zoom&amp;"+
			    "id="+cc.getId()+"&amp;spon_id="+cc.getSpon_id()+"\">"+
			    cc.getFullName()+" "+primary+"</a></td>");
		out.println("<td>"+cc.getOccupation()+
			    "</td>");
		out.println("<td>"+cc.getPhonesAbbr()+
			    "</td>");
		out.println("<td>"+cc.getEmail()+
			    "</td>");
		out.println("</tr>");
	    }
	    out.println("</table>");
	}
    }
    public final static void writeSponsorships(PrintWriter out,
					SponsorshipList donors,
					String url){
	if(donors == null || donors.size() < 1){
	    return;
	}
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Sponsorships</caption>");
	out.println("<tr>"+
		    "<th>Sponsorship</th>"+
		    "<th>Opportunity</th>"+
		    "<th>Sponsor</th>"+
		    "<th>Type</th>"+
		    "<th>Level</th>"+
		    "<th>Value $</th>"+
		    "<th>Misc Notes</th></tr>");
	int jj = 1;
	for(Sponsorship cc:donors){
	    out.println("<tr>");
	    String str = cc.getDetails();
	    if(str.equals("")) str = cc.getId();
	    out.println("<td><a href=\""+url+
			"SponsorshipServ?action=zoom&amp;"+
			"id="+cc.getId()+"\">"+
			str+"</a></td>");
	    out.println("<td><a href=\""+url+
			"OpportServ?action=zoom&amp;"+
			"id="+cc.getOppt_id()+"\">"+
			cc.getOpportunity()+"</a></td>");
	    out.println("<td><a href=\""+url+
			"SponsorServ?action=zoom&amp;"+
			"id="+cc.getSpon_id()+"\">"+
			cc.getSponsor()+"</a></td>");
	    out.println("<td>"+cc.getDon_type()+
			"</td>");
	    out.println("<td>"+cc.getSpon_level()+
			"</td>");
	    out.println("<td>"+cc.getTotalValue()+
			"</td>");
	    out.println("<td>"+cc.getNotes()+
			"</td>");
	    out.println("</tr>");
	    if(jj > Helper.maxSponsorshipCount) break;
	}
	out.println("</table>");
    }
    public final static void writeSponsorLinks(PrintWriter out,
					List<Sponsor> sponLinks,
					String url){
	if(sponLinks == null || sponLinks.size() < 1){
	    return;
	}
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Related Sponsors</caption>");
	out.println("<tr>"+
		    "<th>Sponsor</th>"+
		    "<th>Address</th>"+
		    "<th>Sponsorship Status</th></tr>");
	int jj = 1;
	for(Sponsor cc:sponLinks){
	    out.println("<tr>");
	    out.println("<td><a href=\""+url+
			"SponsorServ?action=zoom&amp;"+
			"id="+cc.getId()+"\">"+
			cc.getOrgname()+"</a></td>");
	    out.println("<td>"+cc.getAddress()+
			"</td>");
	    out.println("<td>"+cc.getSpon_status()+
			"</td>");
	    out.println("</tr>");
	}
	out.println("</table>");
    }		
    public final static void writeInvoices(PrintWriter out,
				    InvoiceList rows,
				    String url){
	if(rows != null && rows.size() > 0){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Invoices </caption>");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<tr><th>Invoice ID</th>"+
			"<th>Total</th>"+
			"<th>Invoice Date</th>"+
			"<th>Due Date</th>"+
			"<th>Invoice Balance</th>"+
			"</tr>");
	    for(Invoice one:rows){
		out.println("<tr>");
		String voided = "";
		if(one.isVoided()) voided = "Voided";
		out.println("<td><a href=\""+url+"InvoiceServ?id="+one.getId()+"\">"+one.getId()+" "+voided+"</a></td>");
		out.println("<td class=\"money\">"+cf.format(one.getTotal())+"</td>");
		out.println("<td>"+one.getInvoiceDate()+"</td>");
		out.println("<td>"+one.getDueDate()+"</td>");
		out.println("<td class=\"money\">"+cf.format(one.getInvoiceBalance())+"</td>");
		out.println("</tr>");				
	    }
	    out.println("</table>");
	}
    }
}






















































