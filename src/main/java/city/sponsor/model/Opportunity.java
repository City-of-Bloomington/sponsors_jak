package city.sponsor.model;

import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import city.sponsor.list.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Opportunity {
    boolean debug;
    static Logger logger = LogManager.getLogger(Opportunity.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    String id="";
    String name = "", event_id=""; // name will be removed
    String start_date = "", end_date="";
    String program_area = "";
    String lead = "";
    String season_id = "";
    String year = "";
    String park_cont = "";
    String instructions = "";
    Event event = null;
    Season season = null;
    SponsorshipList sponships = null;
    public Opportunity(boolean deb) {
	debug = deb;
    }
    public Opportunity(boolean deb, String id) {
	debug = deb;
	setId(id);
    }
    public Opportunity(boolean deb, String id,
		       String name,
		       String season_id,
		       String year,
		       String event_id,
		       String start_date) {
	debug = deb;
	setId(id);
	setName(name);
	setSeason_id(season_id);
	setYear(year);
	setEvent_id(event_id);
	setStart_date(start_date);
    }	
	
    public String getId() {
	return id;
    }

    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	if(name != null)
	    this.name = name;
    }
    public void setEvent_id(String val) {
	if(val != null)
	    this.event_id = val;
    }

    public String getStart_date() {
	return start_date;
    }


    public void setStart_date(String date) {
	if(date != null)
	    this.start_date = date;
    }
	
    public String getEnd_date() {
	return end_date;
    }
    public String getEvent_id() {
	return event_id;
    }
	
    public String getStart_end_date() {
	String ret = start_date;
	if(!end_date.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += end_date;
	}
	return ret;
    }	


    public void setEnd_date(String date) {
	if(date != null)
	    this.end_date = date;
    }

    public String getProgram_area() {
	return program_area;
    }


    public void setProgram_area(String program_area) {
	if(program_area != null)
	    this.program_area = program_area;
    }


    public String getLead() {
	return lead;
    }


    public void setLead(String lead) {
	if(lead != null)
	    this.lead = lead;
    }


    public String getSeason_id() {
	return season_id;
    }


    public void setSeason_id(String season_id) {
	if(season_id != null)
	    this.season_id = season_id;
    }


    public String getYear() {
	return year;
    }


    public void setYear(String year) {
	if(year != null)
	    this.year = year;
    }


    public String getPark_cont() {
	return park_cont;
    }


    public void setPark_cont(String park_cont) {
	if(park_cont != null)
	    this.park_cont = park_cont;
    }


    public String getInstructions() {
	return instructions;
    }


    public void setInstructions(String instructions) {
	if(instructions != null)
	    this.instructions = instructions;
    }

    public String getInfo(){
	return toString();
    }
    /**
     * event_id = 4 is the Unknown event from import
     */
    public boolean hasEvent(){
	return event_id.equals("") || !event_id.equals("4");
    }
    public String toString(){
	String ret = "";
	if(event == null && !event_id.equals(""))
	    getEvent();
	if(event != null && !event_id.equals("4"))
	    ret += event;
	else
	    ret += name;
	if(!start_date.equals("")){
	    ret += " "+start_date;
	}
	else{
	    if(season == null && !season_id.equals(""))
		getSeason();
	    if(season != null){
		if(!ret.equals("")) ret += " ";
		ret += season;
	    }
	    if(!year.equals("")){
		if(!ret.equals("")) ret += " ";
		ret += year;
	    }
	}
	return ret;
    }
    public Event getEvent(){
	if(event == null && !event_id.equals("")){
	    Event eve = new Event(debug, event_id);
	    String back = eve.doSelect();
	    if(back.equals("")) event = eve;
	}
	return event;
    }
    public Season getSeason(){
	if(season == null && !season_id.equals("")){
	    Season sea = new Season(debug, season_id);
	    String back = sea.doSelect();
	    if(back.equals("")) season = sea;
	}
	return season;
    }
    public SponsorshipList getSponsorships(){
	if(sponships == null && !id.equals("")){
	    SponsorshipList list = new SponsorshipList(debug);
	    list.setOppt_id(id);
	    String back = list.find();
	    if(back.equals("") && list.size() > 0){
		sponships = list;
	    }
	}
	return sponships;
    }
    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	// this temporary so that the new opportunities show on
	// the auto complete feature
	if(name.equals("") && !event_id.equals("")){
	    Event eve = new Event(debug, event_id);
	    back = eve.doSelect();
	    if(back.equals("")){
		name =  eve.getName();
	    }
	}
	String qq = "insert into spons_opportunities values(0,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?,?)";
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
	    if(event_id.equals(""))
		pstmt.setString(1,"4"); // Unkoown
	    else
		pstmt.setString(1,event_id);
	    if(start_date.equals(""))
		pstmt.setNull(2,Types.DATE);
	    else
		pstmt.setDate(2, new java.sql.Date(dateFormat.parse(start_date).getTime()));				
	    if(program_area.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,program_area);
	    if(lead.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,lead);
	    if(season_id.equals(""))
		pstmt.setNull(5,Types.INTEGER);
	    else
		pstmt.setString(5,season_id);
			
	    if(year.equals(""))
		pstmt.setNull(6,Types.INTEGER);
	    else
		pstmt.setString(6,year);
	    if(park_cont.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,park_cont);					
	    if(instructions.equals(""))
		pstmt.setNull(8,Types.VARCHAR);
	    else
		pstmt.setString(8,instructions);
	    if(end_date.equals(""))
		pstmt.setNull(9, Types.DATE);
	    else
		pstmt.setDate(9, new java.sql.Date(dateFormat.parse(end_date).getTime()));
	    if(name.equals(""))
		pstmt.setNull(10,Types.VARCHAR);
	    else
		pstmt.setString(10,name);					
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
	    qq = "update spons_opportunities o set "+
		"o.start_date=?,o.program_area=?,o.lead=?,o.season=?,"+
		"o.year=?,o.park_cont=?,o.instructions=?,o.end_date=? "+
		"where o.id=?";
	    String qq2 = "select name from spons_opportunities where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(start_date.equals(""))
		pstmt.setNull(1,Types.DATE);
	    else
		pstmt.setDate(1, new java.sql.Date(dateFormat.parse(start_date).getTime()));				
	    if(program_area.equals(""))
		pstmt.setNull(2,Types.VARCHAR);
	    else
		pstmt.setString(2,program_area);
	    if(lead.equals(""))
		pstmt.setNull(3,Types.VARCHAR);
	    else
		pstmt.setString(3,lead);
	    if(season_id.equals(""))
		pstmt.setNull(4,Types.INTEGER);
	    else
		pstmt.setString(4,season_id);
				
	    if(year.equals(""))
		pstmt.setNull(5,Types.INTEGER);
	    else
		pstmt.setString(5,year);
	    if(park_cont.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6,park_cont);					
	    if(instructions.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,instructions);
	    if(end_date.equals(""))
		pstmt.setNull(8, Types.DATE);
	    else
		pstmt.setDate(8, new java.sql.Date(dateFormat.parse(end_date).getTime()));
			
	    pstmt.setString(9,id);
	    pstmt.executeUpdate();
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq2);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		if(rs.getString(1) != null){
		    name = rs.getString(1);
		}
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
	else{
	    try{
		qq = "delete from  spons_opportunities where id=?";
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
	}
	return back;
    }
    /**
       select o.event_id,name,date_format(o.start_date,'%m/%d/%Y'),o.program_area,o.lead,o.year,o.park_cont,o.instructions,date_format(o.end_date,'%m/%d/%Y') from spons_opportunities o where o.id=810;

       
     */
	
    //
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select o.event_id,o.name,date_format(o.start_date,'%m/%d/%Y'), "+
	    "o.program_area,o.lead,o.season,o.year,o.park_cont,o.instructions,"+
	    "date_format(o.end_date,'%m/%d/%Y') "+
	    "from spons_opportunities o where o.id=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		if(debug){
		    logger.debug(qq);
		}				
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    setEvent_id(rs.getString(1));
		    setName(rs.getString(2));
		    setStart_date(rs.getString(3));
		    setProgram_area(rs.getString(4));
		    setLead(rs.getString(5));
		    setSeason_id(rs.getString(6));  
		    setYear(rs.getString(7));
		    setPark_cont(rs.getString(8));  
		    setInstructions(rs.getString(9));
		    setEnd_date(rs.getString(10));
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
	}
	return back;
    }	

	


}
