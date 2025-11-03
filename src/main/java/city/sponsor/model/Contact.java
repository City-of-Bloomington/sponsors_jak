package city.sponsor.model;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Contact {
    boolean debug = false;
    static Logger logger = LogManager.getLogger(Contact.class);	
    String id="";
    String spon_id="";
    String occupation="";
    String lname="";
    String fname="", prim_cont=""; // primary is reserved
    String address="";
    String pobox="";
    String city=""; 
    String state="";
    String zip="";
    String phone="", phone2=""; // will be deleted  
    String email="";
    String pref_con_time=""; 
    String con_means="", notes="";
    Sponsor sponsor = null;
    PhoneList phones = null;
    public Contact(boolean deb) {
	debug = deb;
    }
    public Contact(boolean deb, String val) {
	debug = deb;
	setId(val);
    }
    public Contact(boolean deb, String val, String val2, String val3) {
	debug = deb;
	setId(val);
	setLname(val2);
	setFname(val3);
    }
	
    public Contact(boolean deb,
		   String id,
		   String spon_id,
		   String occupation,
		   String lname,
		   String fname,
		   String address,
		   String pobox,
		   String city, 
		   String state,
		   String zip,
		   String email,
		   String pref_con_time,
		   String con_means,
		   String notes,
		   String prim_cont
		   ) {
	debug = deb;
				
	setId(id);
	setSpon_id(spon_id);
	setOccupation(occupation);
	setLname(lname);
	setFname(fname);
				
	setAddress(address);
	setPobox(pobox);
	setCity(city); 
	setState(state);
	setZip(zip);
				
	setEmail(email);
	setPref_con_time(pref_con_time); 
	setCon_means(con_means);
	setNotes(notes);
	setPrimary(prim_cont);
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

    public String getOccupation() {
	return occupation;
    }

    public void setOccupation(String occupation) {
	if(occupation != null)
	    this.occupation = occupation;
    }
	
    public String getFullName() {
	String ret = lname;
	if(!fname.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += fname;
	}
	return ret;
    }

    public String getLname() {
	return lname;
    }

    public void setLname(String lname) {
	if(lname != null)
	    this.lname = lname;
    }

    public String getFname() {
	return fname;
    }

    public void setFname(String fname) {
	if(fname != null)
	    this.fname = fname;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	if(address != null)		
	    this.address = address;
    }

    public String getPobox() {
	return pobox;
    }

    public void setPobox(String pobox) {
	if(pobox != null)		
	    this.pobox = pobox;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	if(city != null)	
	    this.city = city;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	if(state != null)	
	    this.state = state;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	if(zip != null)	
	    this.zip = zip;
    }
	
    public String getCityStateZip(){
	String ret = city;
	if(!state.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += state;
	}
	if(!zip.equals("")){
	    if(!ret.equals("")) ret += " ";
	    ret += zip;
	}	
	return ret;
    }

    public String getPhone() {
	return phone;
		
    }

    public String getPhonesAbbr() {
	String ret = "";
	if(phones == null){
	    getPhones();
	}
	if(phones != null){
	    for(Phone pp:phones){
		String str = pp.getType();
		if(!ret.equals("")) ret += ", ";
		ret += pp;
	    }
	}
	return ret;
    }

    public void setPhone(String phone) {
	if(phone != null)	
	    this.phone = phone;
    }

    public String getPhone2() {
	return phone2;
    }

    public void setPhone2(String phone2) {
	if(phone2 != null)	
	    this.phone2 = phone2;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	if(email != null)	
	    this.email = email;
    }

    public String getPref_con_time() {
	return pref_con_time;
    }

    public void setPref_con_time(String pref_con_time) {
	if(pref_con_time != null)	
	    this.pref_con_time = pref_con_time;
    }
		
    public void setPref_con_time(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		pref_con_time +=":";
		pref_con_time += str;
	    }
	}
	if(!pref_con_time.equals("")){
	    pref_con_time += ":";
	}
    }	

    public String getCon_means() {
	return con_means;
    }

    public void setCon_means(String con_means) {
	if(con_means != null)	
	    this.con_means = con_means;
    }

    public void setCon_means(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!con_means.equals(""))con_means +=":";
		con_means += str;
	    }
	}
    }
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	if(notes != null)	
	    this.notes = notes;
    }
		
    public String getPrimary() {
	return prim_cont;
    }

    public void setPrimary(String val) {
	if(val != null)
	    this.prim_cont = val;
    }

    public boolean isPrimary(){
	return !prim_cont.equals("");
    }
	
    public String getInfo(){
	String ret = toString();
	String str = getPhonesAbbr();
	if(!str.equals("")){
	    ret += " Phones: "+str;
	}
	if(!email.equals("")){
	    ret += " Email: "+email;
	}
	return ret;
    }
		
    public String toString(){
	String ret = lname;
	if(!fname.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += fname;
	}
	return ret;
    }
		
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
		
    public void poplateFromSponsor(Sponsor sponsor){
	if(sponsor != null){
	    setAddress(sponsor.getAddress());
	    setCity(sponsor.getCity());
	    setZip(sponsor.getZip());
	    setState(sponsor.getState());
	    setEmail(sponsor.getEmail());
	    /*
	     * since we do not have contact id yet, we can not save
	     if(sponsor.hasPhones()){
	     PhoneList pps = sponsor.getPhones();
	     Phone one = pps.get(0);
	     addPhone(one);
	     }
	    */
	}
    }
		
    public PhoneList getPhones() {
	if(!id.equals("") && phones == null){
	    PhoneList pps = new PhoneList(debug, "c", id);
	    String back = pps.find();
	    if(back.equals("")){
		if(pps.size() > 0) phones = pps;
	    }
	}
	return phones;
    }
		
    public void addPhone(Phone pp){
	if(pp != null){
	    Phone pp2 = new Phone(debug, null, pp.getNumber(), pp.getType(),"c",id);
	    pp2.doSave();
	}
    }
		
    public String addPhones(String[] numbers, String[] types){
	String back="";
	for(int j=0;j<numbers.length;j++){
	    if(!numbers[j].equals("")){
		Phone pp = new Phone(debug, null, numbers[j],types[j],"c", id);
		back = pp.doSave();
	    }
	}
	return back;
    }
		
    public String setDel_phones(String[] vals){
	String back = "";
	if(vals != null){
	    for(String str:vals){
		Phone pp = new Phone(debug, str);
		back = pp.doDelete();
	    }
	}
	return back;
    }
		
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_contacts values(0,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?,"+
	    "?)";

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }				
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt.setString(1,spon_id);
	    if(occupation.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2,occupation);
	    if(lname.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,lname);
	    if(fname.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,fname);
	    if(address.equals(""))
		pstmt.setNull(5,Types.VARCHAR);
	    else
		pstmt.setString(5,address);
	    if(pobox.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,pobox);
	    if(city.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,city);				
	    if(state.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,state);
	    if(zip.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,zip);					
	    if(phone.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,phone);
	    if(phone2.equals(""))
		pstmt.setNull(11,Types.VARCHAR);
	    else
		pstmt.setString(11,phone2);				
	    if(email.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,email);
	    if(pref_con_time.equals(""))
		pstmt.setNull(13,Types.VARCHAR);
	    else
		pstmt.setString(13,pref_con_time);				
	    if(con_means.equals(""))
		pstmt.setNull(14,Types.VARCHAR);
	    else
		pstmt.setString(14,con_means);
	    if(notes.equals(""))
		pstmt.setNull(15,Types.VARCHAR);
	    else
		pstmt.setString(15,notes);
	    if(prim_cont.equals(""))
		pstmt.setNull(16,Types.VARCHAR);
	    else
		pstmt.setString(16,"y");	
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
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }				
	    qq = "update spons_contacts set "+
		"spon_id=?,occupation=?,lname=?,fname=?,"+
		"address=?,pobox=?,city=?,state=?,zip=?,"+
		"email=?,pref_con_time=?,"+
		"con_means=?,notes=?,prim_cont=? "+
		"where id=?";
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,spon_id);
	    if(occupation.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2,occupation);
	    if(lname.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,lname);
	    if(fname.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,fname);
	    if(address.equals(""))
		pstmt.setNull(5,Types.VARCHAR);
	    else
		pstmt.setString(5,address);
	    if(pobox.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,pobox);
	    if(city.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,city);				
	    if(state.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,state);
	    if(zip.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,zip);
	    if(email.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,email);
	    if(pref_con_time.equals(""))
		pstmt.setNull(11,Types.VARCHAR);
	    else
		pstmt.setString(11,pref_con_time);				
	    if(con_means.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,con_means);
	    if(notes.equals(""))
		pstmt.setNull(13,Types.VARCHAR);
	    else
		pstmt.setString(13,notes);
	    if(prim_cont.equals(""))
		pstmt.setNull(14,Types.VARCHAR);
	    else
		pstmt.setString(14,"y");
			
	    pstmt.setString(15,id);
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
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }				
	    qq = "delete from  spons_contacts where id=?";
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
	String qq = "select spon_id,occupation,lname,fname,address,pobox,"+
	    "city, state, zip,"+
	    "email,pref_con_time, con_means,notes,prim_cont "+
	    " from spons_contacts where id=?";

	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }				
	    if(debug){
		logger.debug(qq);
	    }				
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setSpon_id(rs.getString(1));
		setOccupation(rs.getString(2));
		setLname(rs.getString(3));
		setFname(rs.getString(4));  
		setAddress(rs.getString(5));
		setPobox(rs.getString(6));  
		setCity(rs.getString(7));
		setState(rs.getString(8));
		setZip(rs.getString(9));
		setEmail(rs.getString(10));
		setPref_con_time(rs.getString(11));
		setCon_means(rs.getString(12));
		setNotes(rs.getString(13));
		setPrimary(rs.getString(14));	
				
	    }
	    else{
		back= "Record "+id+" Not found";
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
	

}
