package city.sponsor.model;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sponsor {
		
    boolean debug;
    static Logger logger = LogManager.getLogger(Sponsor.class);
    String errors = "";
    String id="",
	type="",  typeName="",          
	orgname="",         
	address="",         
	city="",            
	state="",           
	zip="",             
	pobox="",           
	email="",           
	pref_con_time="",   
	interests="",       
	target_pop="",      
	spon_status="",            
	con_means="",       
	fax="",
	website="",
	spon_id="", // link another sponsor to this sponsor
	acc_manager = "",
	notes="";
    String bni="", chapter="", referral_from="", spon_link="";
    // added on 5/19/2021
    String facebook="", instagram="";
    ContactList contacts = null;
    PhoneList phones = null;
    List<Sponsor> sponsorLinks = null;
    SponFileList sponFiles = null;
    public Sponsor(boolean deb) {
	debug = deb;
    }
    public Sponsor(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
		
    /**
     * needed for service
     */ 
    public Sponsor(boolean deb, String id, String orgname) {
	debug = deb;
	setId(id);
	setOrgname(orgname);
    }

    public Sponsor(boolean deb,
		   String var,
		   String var2,
		   String var3,
		   String var4,
		   String var5,
		   String var6,
		   String var7,
		   String var8,
		   String var9,
		   String var10,
		   String var11,
		   String var12,
		   String var13,
		   String var14,
		   String var15,
		   String var16,
		   String var17,
		   String var18,
		   String var19,
		   String var20,
		   String var21,
		   String var22,
		   String var23
		   ) {
	debug = deb;
	setVals(var,   var2,
		var3,   var4,
		var5,   var6,
		var7,   var8,
		var9,   var10,
		var11,   var12,
		var13,   var14,
		var15,   var16,
		var17,   var18,
		var19,   var20,
		var21, var22,var23);
    }
    void setVals(String var, String var2,
		 String var3, String var4,
		 String var5, String var6,
		 String var7, String var8,
		 String var9, String var10,
		 String var11, String var12,
		 String var13, String var14,
		 String var15, String var16,
		 String var17, String var18,
		 String var19, String var20,
		 String var21,
		 String var22,
		 String var23
		 ){
	setId(var);
	setType(var2);
	setOrgname(var3);
	setAddress(var4);
	setCity(var5);
	setState(var6);
	setZip(var7);
	setPobox(var8);
	setEmail(var9);
	setPref_con_time(var10);
	setInterests(var11);
	setTarget_pop(var12);
	setSpon_status(var13);
	setCon_means(var14);
	setFax(var15);
	setWebsite(var16);
	setAcc_manager(var17);
	setNotes(var18);
	setBni(var19);
	setChapter(var20);
	setReferral_from(var21);
	setFacebook(var22);
	setInstagram(var23);
    }
	
    public String getId() {
	return id;
    }
	

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }
    public void setSpon_id(String var) {
	if(id != null)
	    this.spon_id = var;
    }	
    public void setType(String type) {
	if(type != null)
	    this.type = type;
    }
    public void setOrgname(String orgname) {
	if(orgname != null)
	    this.orgname = orgname.trim();
    }

    public void setAddress(String address) {
	if(address != null)
	    this.address = address.trim();
    }

    public void setCity(String city) {
	if(city != null)
	    this.city = city.trim();
    }

    public void setState(String state) {
	if(state != null)
	    this.state = state;
    }

    public void setZip(String zip) {
	if(zip != null)
	    this.zip = zip;
    }

    public void setPobox(String pobox) {
	if(pobox != null)
	    this.pobox = pobox;
    }
    public void setFacebook(String val) {
	if(val != null)
	    facebook = val;
    }
    public void setInstagram(String val) {
	if(val != null)
	    instagram = val;
    }		

    public void setPref_con_time_arr(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(pref_con_time.equals("")) pref_con_time += ":";
		pref_con_time += str+":";
	    }
	}
    }
    public void setPref_con_time(String val) {
	if(val != null){
	    pref_con_time = val.trim();
	}
    }			
    /*
      public void setPhone(String phone) {
      if(phone != null)
      this.phone = phone;
      }
    */
    public void setInterests(String interests) {
	if(interests != null)
	    this.interests = interests;
    }
    public void setInterests_arr(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!interests.equals("")) interests +=", ";
		this.interests += str;
	    }
	}
    }		
	
    public void setTarget_pop(String target_pop) {
	if(target_pop != null)
	    this.target_pop = target_pop;
    }
    public void setTarget_pop_arr(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!target_pop.equals("")) target_pop += ", ";
		target_pop += str;
	    }
	}
    }		

    public void setSpon_status(String val) {
	if(val != null)
	    this.spon_status = val;
    }

    public void setCon_means(String con_means) {
	if(con_means != null)
	    this.con_means = con_means.trim();
    }
    public void setCon_means_arr(String[] vals) {
	if(con_means != null){
	    for(String str:vals){
		if(con_means.equals("")) con_means +=", ";
		con_means += str;
	    }
	}
    }		

    public void setAcc_manager(String val) {
	if(val != null)
	    this.acc_manager = val.trim();
    }	
	
    public String getType() {
	return type;
    }
		
    public void setBni(String val) {
	if(val != null)
	    bni = val;
    }
    public void setChapter(String val) {
	if(val != null)
	    chapter = val;
    }
    public void setReferral_from(String val) {
	if(val != null)
	    referral_from = val;
    }
    public void setSpon_link(String val) {
	// for auto_complete spon_id
    }
    public String getBni() {
	return bni;
    }
    public String getChapter() {
	return chapter;
    }
    public String getFacebook() {
	return facebook;
    }
    public String getInstagram() {
	return instagram;
    }
    public String getReferral_from() {
	return referral_from;
    }

    public void setNotes(String val) {
	if(val != null)
	    this.notes = val.trim();
    }

    public String getTypeName() {
	if(typeName.equals("") && !type.equals("")){
	    OrgType ot = new OrgType(debug, type);
	    String str = ot.doSelect();
	    System.err.println(" str "+str);
	    if(str.equals("") && ot != null){
		typeName = ot.getName();
	    }
	}
	return typeName;
    }
	
    public String getErrors() {
	return errors;
    }
	
    public boolean hasErrors(){
	return !errors.equals("");
    }

    public String getOrgname() {
	return orgname;
    }

    public String getAddress() {
	return address;
    }

    public String getCity() {
	return city;
    }
	

    public String getState() {
	return state;
    }

    public String getZip() {
	return zip;
    }

    public String getPobox() {
	return pobox;
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
	

    public String getInterests() {
	return interests;
    }

    public void setInterests(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!interests.equals("")) interests +=",";
		interests += str;
	    }
	}
    }
    public void setPref_con_time(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!pref_con_time.equals("")){
		    pref_con_time += ",";
		}	
		pref_con_time += str;
	    }
	}
    }

    public String getTarget_pop() {
	return target_pop;
    }


    public void setTarget_pop(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!target_pop.equals("")) target_pop +=",";
		target_pop += str;
	    }
	}
    }	

    public String getSpon_status() {
	return spon_status;
    }

    public String getCon_means() {
	return con_means;
    }
	
    public void setCon_means(String[] vals) {
	if(vals != null){
	    for(String str:vals){
		if(!con_means.equals(""))con_means +=",";
		con_means += str;
	    }
	}
    }

    public String getFax() {
	return fax;
    }

    public void setFax(String fax) {
	if(fax != null)
	    this.fax = fax;
    }

    public String getWebsite() {
	return website;
    }

    public void setWebsite(String website) {
	if(website != null)
	    this.website = website;
    }
    public String getAcc_manager() {
	return acc_manager;
    }
    public String getNotes() {
	return notes;
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
    //
    public Contact getFirstContact(){ // primar contact
	Contact first = null;
	if(contacts == null && !id.equals("")){
	    getContacts();
	}
	if(contacts != null){
	    first = contacts.get(0);
	    if(contacts.size() == 1){
		return first;
	    }
	    else{
		for(Contact one:contacts){
		    if(one.isPrimary()){
			return one;
		    }
		}
	    }
	}
	return first;
    }
    public ContactList getContacts(){
	if(contacts == null && !id.equals("")){
	    ContactList cc = new ContactList(debug, id);
	    String back = cc.find();
	    if(back.equals("")){
		if(cc.size() > 0){
		    contacts = cc;
		}
	    }
	    else{
		errors = back;
	    }
	}
	return contacts;
    }
    public boolean hasPhones(){
	getPhones();
	return phones != null && phones.size() > 0;
    }
    public PhoneList getPhones() {
	if(!id.equals("") && phones == null){
	    PhoneList pps = new PhoneList(debug, "s", id);
	    String back = pps.find();
	    if(back.equals("")){
		if(pps.size() > 0) phones = pps;
	    }
	    else{
		System.err.println("sponsor "+back);
	    }
	}
	return phones;
    }
    public String getPhonesAbbr() {
	String ret = "";
	if(phones == null){
	    getPhones();
	}
	if(phones != null){
	    for(Phone pp:phones){
		// String str = pp.getType();
		if(!ret.equals("")) ret += ", ";
		ret += pp.getNumber();
	    }
	}
	return ret;
    }
    public String addPhones(String[] numbers, String[] types){
	String back="";
	for(int j=0;j<numbers.length;j++){
	    if(!numbers[j].equals("")){
		Phone pp = new Phone(debug, null, numbers[j],types[j],"s", id);
		back = pp.doSave();
	    }
	}
	return back;
    }
    public String toString() {
	return orgname;
    }
    public boolean equals(Object obj){
	if(obj instanceof Sponsor){
	    Sponsor one =(Sponsor)obj;
	    return id.equals(one.getId());
	}
	return false;				
    }

    public int hashCode() 
    {
        final int prime = 37;
        int result = 1;
	if(!id.equals("")){
	    try{
		result = Integer.parseInt(id);
	    }catch(Exception ex){

	    }
	}
        result = prime + result ;
        return result;
    }		
		
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "insert into spons_sponsors values(0,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?,"+			
	    "?,?,?,?,?,?,?)";
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
	    if(type.equals(""))
		pstmt.setNull(1,Types.INTEGER);
	    else
		pstmt.setString(1, type);
	    if(orgname.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2,orgname);
	    if(address.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,address);				
	    if(city.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,city);
	    if(state.equals(""))
		pstmt.setNull(5,Types.VARCHAR);
	    else
		pstmt.setString(5,state);
	    if(zip.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,zip);
	    if(pobox.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,pobox);
	    if(email.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,email);
	    if(pref_con_time.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,pref_con_time);
	    if(interests.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,interests);
	    if(target_pop.equals(""))
		pstmt.setNull(11,Types.VARCHAR);
	    else
		pstmt.setString(11,target_pop);
	    if(spon_status.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,spon_status);
	    if(con_means.equals(""))
		pstmt.setNull(13,Types.VARCHAR);
	    else
		pstmt.setString(13,con_means);
	    if(fax.equals(""))
		pstmt.setNull(14,Types.VARCHAR);
	    else
		pstmt.setString(14,fax);
	    if(website.equals(""))
		pstmt.setNull(15,Types.VARCHAR);
	    else
		pstmt.setString(15,website);
	    if(acc_manager.equals(""))
		pstmt.setNull(16,Types.VARCHAR);
	    else
		pstmt.setString(16,acc_manager);
	    if(notes.equals(""))
		pstmt.setNull(17,Types.VARCHAR);
	    else
		pstmt.setString(17,notes);
	    if(bni.equals(""))
		pstmt.setNull(18,Types.CHAR);
	    else
		pstmt.setString(18,"y");
	    if(chapter.equals(""))
		pstmt.setNull(19,Types.VARCHAR);
	    else
		pstmt.setString(19,chapter);
	    if(referral_from.equals(""))
		pstmt.setNull(20,Types.VARCHAR);
	    else
		pstmt.setString(20,referral_from);
	    if(facebook.equals(""))
		pstmt.setNull(21,Types.VARCHAR);
	    else
		pstmt.setString(21,facebook);
	    if(instagram.equals(""))
		pstmt.setNull(22,Types.VARCHAR);
	    else
		pstmt.setString(22,instagram);						
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
	addLink();
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
	    qq = "update spons_sponsors set type=?,"+
		"orgname=?,address=?,city=?,state=?,"+
		"zip=?,pobox=?,email=?,pref_con_time=?, "+
		"interests=?,target_pop=?,"+
		"spon_status=?,"+
		"con_means=?,fax=?,website=?, "+
		"acc_manager=?,notes=?, "+
		"bni=?,chapter=?,referral_from=?, "+
		"facebook=?,instagram=? "+
		"where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    if(type.equals(""))
		pstmt.setNull(1,Types.INTEGER);
	    else
		pstmt.setString(1, type);
	    if(orgname.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2,orgname);
	    if(address.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,address);				
	    if(city.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,city);
	    if(state.equals(""))
		pstmt.setNull(5,Types.VARCHAR);
	    else
		pstmt.setString(5,state);
	    if(zip.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,zip);
	    if(pobox.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,pobox);
	    if(email.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,email);
	    if(pref_con_time.equals(""))
		pstmt.setNull(9,Types.VARCHAR);
	    else
		pstmt.setString(9,pref_con_time);
	    if(interests.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,interests);
	    if(target_pop.equals(""))
		pstmt.setNull(11,Types.VARCHAR);
	    else
		pstmt.setString(11,target_pop);
	    if(spon_status.equals(""))
		pstmt.setNull(12,Types.VARCHAR);
	    else
		pstmt.setString(12,spon_status);
	    if(con_means.equals(""))
		pstmt.setNull(13,Types.VARCHAR);
	    else
		pstmt.setString(13,con_means);
	    if(fax.equals(""))
		pstmt.setNull(14,Types.VARCHAR);
	    else
		pstmt.setString(14,fax);
	    if(website.equals(""))
		pstmt.setNull(15,Types.VARCHAR);
	    else
		pstmt.setString(15,website);
	    if(acc_manager.equals(""))
		pstmt.setNull(16,Types.VARCHAR);
	    else
		pstmt.setString(16,acc_manager);					
	    if(notes.equals(""))
		pstmt.setNull(17,Types.VARCHAR);
	    else
		pstmt.setString(17,notes);
	    if(bni.equals(""))
		pstmt.setNull(18,Types.VARCHAR);
	    else
		pstmt.setString(18,bni);
	    if(chapter.equals(""))
		pstmt.setNull(19,Types.VARCHAR);
	    else
		pstmt.setString(19,chapter);
	    if(referral_from.equals(""))
		pstmt.setNull(20,Types.VARCHAR);
	    else
		pstmt.setString(20,referral_from);
	    if(facebook.equals(""))
		pstmt.setNull(21,Types.VARCHAR);
	    else
		pstmt.setString(21,facebook);
	    if(instagram.equals(""))
		pstmt.setNull(22,Types.VARCHAR);
	    else
		pstmt.setString(22,instagram);
	    pstmt.setString(23,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	addLink();
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
	    qq = "delete from  spons_sponsors where id=?";
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
	String qq = "select type,"+
	    "orgname,address,city,state,"+
	    "zip,pobox,email,pref_con_time, "+
	    "interests,target_pop,spon_status,"+
	    "con_means,fax,website,acc_manager, "+
	    "notes, "+
	    "bni,chapter,referral_from,facebook,instagram "+
	    "from spons_sponsors where id=?";
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
		setVals(id,
			rs.getString(1),
			rs.getString(2),
			rs.getString(3),
			rs.getString(4),					
			rs.getString(5),
			rs.getString(6),  
			rs.getString(7),
			rs.getString(8),
			rs.getString(9),
			rs.getString(10),
			rs.getString(11),
			rs.getString(12),
			rs.getString(13),
			rs.getString(14),
			rs.getString(15),
			rs.getString(16),
			rs.getString(17),
			rs.getString(18),
			rs.getString(19),
			rs.getString(20),
			rs.getString(21),
			rs.getString(22));
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
     * link a sponsor to another one
     */
    public String addLink(){
	String back="";
	if(!id.equals("") && !spon_id.equals("")){
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String qq = "insert into spons_sponsor_links values(?,?) ";
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
		pstmt.setString(1, id);
		pstmt.setString(2, spon_id);
		pstmt.executeUpdate();
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
	return back;
    }
    public String deleteLinks(String[] vals){
	String back = "";
	if(vals == null) return back;
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	String qq = "delete from spons_sponsor_links "+
	    " where (spon_id=? and spon_id2=?) or "+
	    " (spon_id=? and spon_id2=?) ";

	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    for(String str:vals){
		pstmt.setString(1, id);
		pstmt.setString(2, str);
		pstmt.setString(3, str);
		pstmt.setString(4, id);
		pstmt.executeUpdate();
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
    public boolean hasSponsorLinks(){
	getSponsorLinks();
	return sponsorLinks != null && sponsorLinks.size() > 0;
    }
    public List<Sponsor> getSponsorLinks(){
	if(sponsorLinks == null){
	    findSponsorLinks();
	}
	return sponsorLinks;
    }
    public SponFileList getSponFiles(){
	if(!id.equals("") && sponFiles == null){
	    SponFileList spl = new SponFileList(debug,id);
	    String back = spl.find();
	    if(back.equals("") && spl.size() > 0){
		sponFiles = spl;
	    }
	}
	return sponFiles;
    }
    public String findSponsorLinks(){
		
	String qq = " select spon_id2 from spons_sponsor_links where "+
	    " spon_id=? "+
	    " union select spon_id from spons_sponsor_links where "+
	    " spon_id2=? ";
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    List<Sponsor> sponsors = new ArrayList<Sponsor>(); 
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.setString(2,id);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String spon_id=rs.getString(1);
		Sponsor spon = new Sponsor(debug, spon_id);
		back += spon.doSelect();
		if(back.equals("")) sponsors.add(spon);
	    }
	    if(sponsors.size() > 0){
		sponsorLinks = sponsors;
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
	
}
