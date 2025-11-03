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

public class SponsorList extends ArrayList<Sponsor>{

    static final long serialVersionUID = 79L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(SponsorList.class);
    String orgname="",
	type="",            
	address="",         
	city="",            
	state="",           
	zip="",             
	pobox="",           
	phone="",           
	pref_con_time="",   
	interests="",       
	target_pop="",      
	spon_status="",            
	con_means="",       
	pref_type_spon="",  
	acc_manager = "",id="";
    //
    // contact parameters
    //
    String c_name="", lname="", fname="",c_address="",c_city="",c_phone="",
	prim_cont="", cont_id="";
    //
    // opportunities parameters
    //
    String o_date_from="", o_date_to="", oppt_id="", 
	event_id= "", season_id="", year="", program_area="";
    //
    // sponsorships
    //
    String don_type="", spon_level="";	
    //
    boolean all = false; 
    int pageSize = 10, pageNumber =1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
    public SponsorList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);
    }
    public SponsorList(boolean val, boolean all){
	debug = val;
	this.all = all;
	pages = new PageList(val);
	pages.setPageSize(pageSize);
    }	

    public void setOrgname(String val){
	if(val != null && !val.equals("")){
	    orgname = val;
	    pages.addPair("orgname",val);
	}
    }
    public void setType(String val) {
	if(val != null && !val.equals("")){
	    this.type = val;
	    pages.addPair("type",val);
	}
    }
    public void setId(String val) {
	if(val != null && !val.equals("")){
	    this.id = val;
	    pages.addPair("id",val);
	}
    }	
    public void setAddress(String val) {
	if(val != null && !val.equals("")){
	    this.address = val;
	    pages.addPair("address",val);
	}
    }
	
    public void setCity(String val) {
	if(val != null && !val.equals("")){
	    this.city = val;
	    pages.addPair("city",val);
	}
    }
	
    public void setState(String val) {
	if(val != null && !val.equals("")){
	    this.state = val;
	    pages.addPair("state",val);
	}
    }

    public void setZip(String val) {
	if(val != null && !val.equals("")){
	    this.zip = val;
	    pages.addPair("zip",val);
	}
    }
    public void setC_name(String val) {
	if(val != null && !val.equals("")){
	    this.c_name = val;
	    pages.addPair("c_name",val);
	}
    }		
		

    public void setPobox(String val) {
	if(val != null && !val.equals("")){
	    this.pobox = val;
	    pages.addPair("pobox",val);
	}
    }

    public void setPref_con_time(String val) {
	if(val != null && !val.equals("")){
	    pref_con_time = val;
	    pages.addPair("pref_con_time",val);
	}
    }	

    public void setPhone(String val) {
	if(val != null && !val.equals("")){
	    this.phone = val;
	    pages.addPair("phone",val);
	}
    }

    public void setInterests(String val) {
	if(val != null && !val.equals("")){
	    this.interests = val;
	    pages.addPair("interests",val);
	}
    }
	
    public void setTarget_pop(String val) {
	if(val != null && !val.equals("")){
	    this.target_pop = val;
	    pages.addPair("target_pop",val);
	}
    }

    public void setSpon_status(String val) {
	if(val != null && !val.equals("")){
	    this.spon_status = val;
	    pages.addPair("spon_status",val);
	}
    }

    public void setCon_means(String val) {
	if(val != null && !val.equals("")){
	    this.con_means = val;
	    pages.addPair("con_means",val);
	}
    }

    public void setPref_type_spon(String val) {
	if(val != null && !val.equals("")){
	    this.pref_type_spon = val;
	    pages.addPair("pref_type_spon",val);
	}
    }
	
    public void setAcc_manager(String val) {
	if(val != null && !val.equals("")){
	    this.acc_manager = val;
	    pages.addPair("acc_manager",val);
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
    public void setSortBy(String val) {
	if(val != null && !val.equals("")){
	    this.sortBy = val;
	    pages.addPair("sortBy", val);
	}
    }
    public void setC_address(String val) {
	if(val != null && !val.equals("")){
	    this.c_address = val;
	    pages.addPair("c_address",val);
	}
    }
    public void setC_city(String val) {
	if(val != null && !val.equals("")){
	    this.c_city = val;
	    pages.addPair("c_city",val);
	}
    }
    public void setPrim_cont(String val) {
	if(val != null && !val.equals("")){
	    this.prim_cont = val;
	    pages.addPair("prim_cont",val);
	}
    }	
    public void setLname(String val) {
	if(val != null && !val.equals("")){
	    this.lname = val;
	    pages.addPair("lname",val);
	}
    }
    public void setFname(String val) {
	if(val != null && !val.equals("")){
	    this.fname = val;
	    pages.addPair("fname",val);
	}
    }	
    public void setC_phone(String val) {
	if(val != null && !val.equals("")){
	    this.c_phone = val;
	    pages.addPair("c_phone",val);
	}
    }
    public void setCont_id(String val) {
	if(val != null && !val.equals("")){
	    this.cont_id = val;
	    pages.addPair("cont_id",val);
	}
    }

    //
    // opportunity vars
    //
    public void setOppt_id(String val) {
	if(val != null && !val.equals("")){
	    this.oppt_id = val;
	    pages.addPair("oppt_id",val);
	}
    }
    public void setEvent_id(String val) {
	if(val != null && !val.equals("")){
	    this.event_id = val;
	    pages.addPair("event_id",val);
	}
    }
    public void setSeason_id(String val) {
	if(val != null && !val.equals("")){			
	    this.season_id = val;
	    pages.addPair("season_id",val);
	}
    }	
    public void setYear(String val) {
	if(val != null && !val.equals("")){
	    this.year = val;
	    pages.addPair("year",val);
	}
    }
    public void setProgram_area(String val) {
	if(val != null && !val.equals("")){
	    this.program_area = val;
	    pages.addPair("program_area",val);
	}
    }
    public void setDon_type(String val) {
	if(val != null && !val.equals("")){
	    this.don_type = val;
	    pages.addPair("don_type",val);
	}
    }
    public void setSpon_level(String val) {
	if(val != null && !val.equals("")){
	    this.spon_level = val;
	    pages.addPair("spon_leval",val);
	}
    }
    public void setO_date_from(String val) {
	if(val != null && !val.equals("")){
	    o_date_from = val;
	    pages.addPair("o_date_from",val);
	}
    }
    public void setO_date_to(String val) {
	if(val != null && !val.equals("")){
	    o_date_to = val;
	    pages.addPair("o_date_to",val);
	}
    }	
    public int getCount(){
	return count;
    }
    /**
     * mean search function
     */
    public String find(){
		
	String back = "";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = null;
	String qc = "select count(*) ";
	String q = " select s.id,s.type,"+
	    "s.orgname,s.address,s.city,s.state,"+
	    "s.zip,s.pobox,s.email,s.pref_con_time, "+
	    "s.interests,s.target_pop,s.spon_status,"+
	    "s.con_means,s.fax,s.website,s.acc_manager, "+
	    "s.notes, "+
	    "s.bni,s.chapter,s.referral_from,s.facebook,s.instagram ";
				
	String qf = "from spons_sponsors s ";
	String qw = "", qo = "", qq="";
	boolean contactTbl = false, phoneTbl = false, phoneTbl2=false,
	    opptTbl=false, sponshipTbl=false;

	if(!id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.id = ? "; 
	}	
	else if(!orgname.equals("")){
	    qw += " s.orgname like ? "; 
	}
	if(!type.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.type = ? "; 
	}
	if(!address.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.address like ? "; 
	}
	if(!pobox.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.pobox like ? "; 
	}	
	if(!city.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.city like ? "; 
	}
	if(!state.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.state like ? "; 
	}
	if(!zip.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.zip like ? "; 
	}
	if(!phone.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    phoneTbl = true;
	    qw += " p.phone like ? ";
	    qw += " and p.own_id = s.id and p.own_type = 's' "; 			
	}
	if(!pref_con_time.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.pref_con_time like ? "; 
	}
	if(!interests.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.interests like ? "; 
	}
	if(!target_pop.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.target_pop like ? "; 
	}
	if(!spon_status.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.spon_status = ? "; 
	}
	if(!pref_type_spon.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.pref_type_spon like ? ";
	}
	if(!acc_manager.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.acc_manager = ? "; 
	}
	if(!c_name.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " (c.lname like ? or c.fname like ?) "; 
	}				
	if(!lname.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.lname like ? "; 
	}
	if(!fname.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.fname like ? "; 
	}
	if(!c_address.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.address like ? "; 
	}
	if(!c_city.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.city like ? "; 
	}
	if(!prim_cont.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.prim_cont = 'y' "; 
	}
	if(!c_phone.equals("")){
	    contactTbl = true;
	    phoneTbl2 = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " p2.phone like ? ";
	    qw += " and p2.own_id = c.id and p2.own_type = 'c' "; 
	}
	if(!cont_id.equals("")){
	    contactTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " c.id = ? "; 
	}
	//
	// opportunity vars
	//
	if(!o_date_from.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.start_date >= str_to_date('"+o_date_from+"','%m/%d/%Y') "; 
	}
	if(!o_date_to.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.start_date <= str_to_date('"+o_date_to+"','%m/%d/%Y') "; 
	}
	if(!oppt_id.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.id = ? ";
	}
	if(!event_id.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.event_id = ? ";
	}
	if(!season_id.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.season = ? ";
	}
	if(!year.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.year = ? ";
	}
	if(!program_area.equals("")){
	    opptTbl = true;
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.program_area = ? ";
	}
	// sponsorship related
	if(!don_type.equals("")){
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " sp.don_type = ? ";
	}
	if(!spon_level.equals("")){
	    sponshipTbl = true;
	    if(!qw.equals("")) qw += " and ";
	    qw += " sp.spon_level = ? ";
	}	
	if(contactTbl){
	    qf += ", spons_contacts c ";
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.id = c.spon_id ";
	}
	if(phoneTbl){
	    qf += ", spons_phones p ";
	}
	if(phoneTbl2){
	    qf += ", spons_phones p2 ";
	}
	if(sponshipTbl){
	    qf += ", spons_sponsorships sp ";
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.id = sp.spon_id ";
	}
	if(opptTbl){
	    qf += ", spons_opportunities o ";
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.id = sp.oppt_id ";
	}
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	if(sortBy.equals("")){
	    sortBy = "orgname ";
	}
	qo += " order by s."+sortBy;
	qq = qc + qf + qw;
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
			Sponsor one =
			    new Sponsor(debug,
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
					rs.getString(22),
					rs.getString(23));
			if(!this.contains(one)){
			    this.add(one);
			}
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
	    else if(!orgname.equals("")){
		pstmt.setString(jj++, "%"+orgname+"%");
	    }
	    if(!type.equals("")){
		pstmt.setString(jj++, type);
	    }
	    if(!address.equals("")){
		pstmt.setString(jj++, "%"+address+"%");
	    }
	    if(!pobox.equals("")){
		pstmt.setString(jj++, "%"+pobox+"%");
	    }
	    if(!city.equals("")){
		pstmt.setString(jj++, ""+city+"");
	    }
	    if(!state.equals("")){
		pstmt.setString(jj++, ""+state+"");
	    }
	    if(!zip.equals("")){
		pstmt.setString(jj++, ""+zip+"");
	    }
	    if(!phone.equals("")){
		pstmt.setString(jj++, "%"+phone+"%");
	    }
	    if(!pref_con_time.equals("")){
		pstmt.setString(jj++, "%"+pref_con_time+"%");
	    }
	    if(!interests.equals("")){
		pstmt.setString(jj++, "%"+interests+"%");
	    }
	    if(!target_pop.equals("")){
		pstmt.setString(jj++, "%"+target_pop+"%");
	    }
	    if(!spon_status.equals("")){
		pstmt.setString(jj++, spon_status);
	    }					
	    if(!pref_type_spon.equals("")){
		pstmt.setString(jj++, "%"+pref_type_spon+"%");
	    }
	    if(!acc_manager.equals("")){
		pstmt.setString(jj++, acc_manager);
	    }
	    if(!lname.equals("")){
		pstmt.setString(jj++, "%"+lname+"%");
	    }
	    if(!c_name.equals("")){
		pstmt.setString(jj++, "%"+c_name+"%");
		pstmt.setString(jj++, "%"+c_name+"%");								
	    }						
	    if(!fname.equals("")){
		pstmt.setString(jj++, "%"+fname+"%");
	    }
	    if(!c_address.equals("")){
		pstmt.setString(jj++, "%"+c_address+"%");
	    }
	    if(!c_city.equals("")){
		pstmt.setString(jj++, "%"+c_city+"%");
	    }
	    if(!c_phone.equals("")){
		pstmt.setString(jj++, "%"+c_phone+"%");
	    }
	    if(!cont_id.equals("")){
		pstmt.setString(jj++, cont_id);
	    }
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }
	    if(!event_id.equals("")){
		pstmt.setString(jj++, event_id);
	    }
	    if(!season_id.equals("")){
		pstmt.setString(jj++, season_id);
	    }
	    if(!year.equals("")){
		pstmt.setString(jj++, year);
	    }
	    if(!program_area.equals("")){
		pstmt.setString(jj++, program_area);
	    }
	    if(!don_type.equals("")){
		pstmt.setString(jj++, don_type);
	    }
	    if(!spon_level.equals("")){
		pstmt.setString(jj++, spon_level);
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






















































