package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 */
@WebServlet(urlPatterns = {"/ReportServ"})
public class ReportServ extends TopServlet{

    static final long serialVersionUID = 63L;
    static Logger logger = LogManager.getLogger(ReportServ.class);		

    final static String profArr[] = {"Address",
	"City, State Zip",
	"Industry Category",
	"Sponsorship Status",
	"Phones",
	"Email",
	"Web Address",
	"Budget Cycle",
	"Pref. Contact Method",
	"Community Focus",
	"Target Market",
	"Contact Name",
	"Contact Address",
	"Contact City State Zip",
	"Contact Phones",
	"Contact Email",
	"BNI",
	"Chapter",
	"Referral from"
    };
	
    /**
     * @param req
     * @param res
     */
    
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	doGet(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="";
	boolean success=true;
	//
	String message="", action="";
	String type="",       
	    orgname="",         
	    address="",         
	    city="",            
	    state="",           
	    zip="",             
	    pobox="",           
	    email="",           
	    phone="",           
	    pref_con_time="",   
	    interests="",       
	    target_pop="",      
	    spon_status="",            
	    con_means="",       
	    pref_type_spon="",  
	    acc_manager = "",
	    bni="", chapter="", referral_from="";
	String pageSize = "50", pageNumber = "1", report="";
	//
	// opportunity
	//
	String o_date_from="", o_date_to="", oppt_id="", opport_name="",
	    event_id= "", season_id="", year="", program_area="";
	//
	// sponsorships
	//
	String don_type="", spon_level="";
		

	String name, value;
	User user = null;
		
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	SponsorList sponsors = new SponsorList(debug, true);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	String [] show ={""};
	Set<String> showSet = new HashSet<String>();
	showSet.add("Address");
	showSet.add("Industry Category");
	showSet.add("Sponsorship Status");
	showSet.add("Phones");
	showSet.add("Email");
	showSet.add("Web Address");
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("type")) {
		sponsors.setType(value);
		type = value;
	    }
	    else if (name.equals("interests")){
		sponsors.setInterests(value);
		interests = value;
	    }
	    else if (name.equals("target_pop")) {
		sponsors.setTarget_pop(value);
		target_pop = value;
	    }
	    else if (name.equals("spon_status")) {
		sponsors.setSpon_status(value);
		spon_status = value;
	    }			
	    else if (name.equals("con_means")) {
		sponsors.setCon_means(value);
		con_means = value;
	    }
	    else if (name.equals("pref_type_spon")) {
		sponsors.setPref_type_spon(value);
		pref_type_spon = value;
	    }			
	    else if (name.equals("acc_manager")) {
		sponsors.setAcc_manager(value);
		acc_manager = value;
	    }			
	    else if (name.equals("oppt_id")) {
		sponsors.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("opport_name")) {
		// sponsors.setOpport_name(value);
		opport_name = value;
	    }
	    else if (name.equals("event_id")) {
		sponsors.setEvent_id(value);
		event_id = value;
	    }
	    else if (name.equals("season_id")) {
		sponsors.setSeason_id(value);
		season_id = value;
	    }
	    else if (name.equals("year")) {
		sponsors.setYear(value);
		year = value;
	    }
	    else if (name.equals("program_area")) {
		sponsors.setProgram_area(value);
		program_area = value;
	    }	                           // up to here opportunities
	    else if (name.equals("o_date_from")) {
		sponsors.setO_date_from(value); // we do not send this
		o_date_from = value;
	    }
	    else if (name.equals("o_date_to")) {
		sponsors.setO_date_from(value); 
		o_date_to = value;
	    }	
	    else if (name.equals("don_type")) { 
		sponsors.setDon_type(value);
		don_type = value;
	    }
	    else if (name.equals("spon_level")) {
		sponsors.setSpon_level(value);
		spon_level = value;
	    }
	    else if(name.equals("show")){
		show = vals; // array
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("report")){ 
		report = value;  
	    }			
	}
	if(show != null && show.length > 1){
	    showSet =  new HashSet<String>();
	    for(int i=0;i<show.length;i++)
		if(!show[i].equals(""))
		    showSet.add(show[i]);
	}
	//
	SponOptionList interestList = new SponOptionList(debug, "interests");
	SponOptionList targetPops = new SponOptionList(debug, "target_pop");
	SponOptionList prefTypes = new SponOptionList(debug, "pref_type_spon");
	EventList events = new EventList(debug);
	SeasonTypeList seasons = new SeasonTypeList(debug);
	OrgTypeList orgTypes = new OrgTypeList(debug);
	SponOptionList progAreas = new SponOptionList(debug, "program_area");
	UserList managers = new UserList(debug);
	managers.setManager("y");
	int count = 0;
	if(true){
	    String back = "";
	    back += orgTypes.find();
	    back += interestList.find();
	    back += targetPops.find();
	    back += prefTypes.find();
	    back += managers.find();
	    back += seasons.find();
	    back += events.find();
	    back += progAreas.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    if(!action.equals("")){
		back = sponsors.find();
		if(!back.equals("")){
		    message += back;
		    logger.error(back);
		}
		else{
		    count = sponsors.size();
		}
	    }
	    if(!action.equals("") && report.equals("csv")){
		generateCsvFile(res, sponsors, showSet);
		return;
	    }
	}
	//
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
	// delete startNew
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Reports</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"ReportServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");
	out.println("<fieldset><legend>Report</legend>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Business</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Sponsorship Status</th><th>Account Manager</th>");
	out.println("<th>Industry Category</th>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_status\">");
	out.println("<option value=\"\">All</option>");	
	for(String status:Helper.statusArr){
	    String selected = "";
	    if(spon_status.equals(status)) selected="selected=\"selected\"";
	    out.println("<option "+selected+" >"+status+"</option>");			
	}
	out.println("</select></td>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"acc_manager\">");
	out.println("<option value=\"\">All</option>");	
	for(User muser:managers){
	    String selected = "";
	    if(muser.getUserid().equals(acc_manager)) selected = "selected=\"selected\" "; 
	    out.println("<option value=\""+muser.getUserid()+"\" "+selected+">"+muser+"</option>");			
	}
	out.println("</select></td>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"type\">");
	out.println("<option value=\"\">All</option>");
	for(Type tt:orgTypes){
	    String selected = type.equals(tt.getId())?"selected=\"selected\"":"";
	    out.println("<option value=\""+tt.getId()+"\" "+selected+">"+tt+"</option>");			
	}
	out.println("</select></td>");		
	out.println("</tr>");
	//
	// contact info
	out.println("<tr><th>Community Focus</th><th colspan=\"2\">Target Market</th>");
	out.println("</tr>");
	out.println("<tr><td class=\"left\">");
	out.println("<select name=\"interests\">");
	out.println("<option value=\"\">All</option>");
		
	for(String str: interestList){
	    String selected="";
	    if(interests.indexOf(str) > -1){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}			
	out.println("<select></td>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"target_pop\">");
	out.println("<option value=\"\">All</option>");
	for(String str: targetPops){
	    String selected="";
	    if(target_pop.indexOf(str) > -1){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}			
	out.println("<select>");	
	out.println("</td>");
	out.println("</tr>");
	out.println("</table></td></tr>");
	out.println("</table></td></tr>");
	//
	// contact table
	out.println("<tr><td><table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Opportunities Related</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Opportunity</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"name\" size=\"30\" id=\"opport_name\" "+
		    " maxlength=\"50\" value=\""+opport_name+"\" />");
	out.println("<th>ID</th>");
	out.println("<td class=\"left\">");
	out.println("<input id=\"oppt_id\" name=\"id\" size=\"4\" value=\""+id+"\" />");				
	out.println("</td></tr>");
	out.println("<tr><th>Event</th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"event_id\">");
	out.println("<option value=\"\">All</option>");
	for(Type event: events){
	    String selected="";
	    if(event_id.equals(event.getId())){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+event.getId()+"\" "+selected+">"+event+"</option>");
	}
	out.println("</select></td>");
	out.println("<th>Program Area</th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"program_area\">");
	out.println("<option value=\"\">All</option>");
	for(String str: progAreas){
	    String selected="";
	    if(program_area.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option "+selected+">"+str+"</option>");
	}
	out.println("</select></td>");
	out.println("</tr>");
	//		
	out.println("<tr><th>Season</th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"season_id\">");
	out.println("<option value=\"\">All</option>");
	for(Type season:seasons){
	    String selected="";
	    if(season.getId().equals(season_id)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+season.getId()+"\" "+selected+">"+season+"</option>");
	}
	out.println("</select></td>");
	out.println("<th> Year </th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"year\">");
	out.println("<option value=\"\">All</option>");
	for(String str: Helper.getYearsArr()){
	    String selected="";
	    if(year.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select></td></tr>");

	out.println("<tr><th>Start Date From</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"o_date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+o_date_from+"\" /></td>");
	out.println("<th> To </th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"o_date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+o_date_to+"\" />");
	out.println("</td></tr>");	
		
	out.println("</table></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("<tr><td><table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");		
	out.println("Sponsorships Related</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>Sponsorship Type</th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"don_type\">");
	out.println("<option value=\"\"></option>");
	for(String str: Helper.donTypeArr){
	    String selected="";
	    if(don_type.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select>");
	out.println("</td>");
	out.println("<th>Sponsorship Level</th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_level\">");
	out.println("<option value=\"\"></option>");
	for(String str: Helper.sponLevelArr){
	    String selected="";
	    if(spon_level.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");			
	out.println("</table></td></tr>");
	out.println("</table></td></tr>");
	//		
	out.println("<tr><td><table width=\"100%\">");
	out.println("<tr>");
	out.println("<th>Sort By</th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\">");
	out.println("<option value=\"orgname\">Business Name</option>");
	out.println("<option value=\"address\">Business Address</option>");
	out.println("<select> &nbsp;&nbsp;");
	String checked = (report.equals("csv"))? checked="checked=\"checked\"":"";
	out.println("<th><input type=\"checkbox\" name=\"report\" value=\"csv\" "+checked+" /> Export to csv file. It may take some time to finish, please wait.</th> ");		
	out.println("</tr></table></td></tr>");
	out.println("<tr><td class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");
	//
	out.println("<tr><td><button id=\"showr\">Show Output Profile</button>");
	out.println("<button id=\"hider\">Hide Output Profile</button></td></tr>");
	out.println("<tr id=\"profile\"><td>");
	out.println("<table width=\"100%\"><tr><td class=\"center title\">");
	out.println("Output Profile</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");		
	int jj=0;
	for(int j=0;j<profArr.length;j++){
	    String str = profArr[j];
	    if(jj == 0) out.println("<tr>");
	    checked = (showSet.contains(str))?"checked=\"checked\"":""; 
	    out.println("<td><input type=\"checkbox\" name=\"show\" value=\""+str+"\" "+checked+">"+str+"</td>");
	    jj++;
	    if(jj == 3){
		out.println("</tr>");
		jj=0;
	    }
	}
	if(jj > 0) out.println("</tr>");
	out.println("</table></td></tr>");
	out.println("</table></td></tr>");
	out.println("</table>");
	out.println("</fieldset>");			
	out.println("</form>");

	if(!action.equals("")){
	    if(count == 0){
		out.println("<p class=\"center\">No match found</p>"); 
	    }
	    else{
		out.println("<table width=\"90%\"><tr><td align=\"center\">");
		out.println("<table width=\"100%\" border=\"1\">");
		out.println("<caption>Found "+count+" Sponsor(s)</caption>");			
		out.println("<tr><th>Business</th>");
		for(String str:profArr){
		    if(showSet.contains(str)){
			out.println("<th>"+str+"</th>");
		    }
		}
		out.println("</tr>");	
		for(Sponsor sp:sponsors){
		    Contact ct = sp.getFirstContact();
		    String fullName = "",ctPhones="",ctEmail="";
		    if(ct != null){
			fullName = ct.getFullName();
			ctPhones = ct.getPhonesAbbr();
			ctEmail = ct.getEmail();
		    }
		    out.println("<tr>");
		    out.println("<td><a href=\""+url+
				"SponsorServ?action=zoom&amp;"+
				"id="+sp.getId()+"\">"+
				sp.getOrgname()+"</a></td>");
		    if(showSet.contains("Address"))
			out.println("<td>"+sp.getAddress()+"</td>");
		    if(showSet.contains("City, State Zip"))
			out.println("<td>"+sp.getCityStateZip()+"</td>");
		    if(showSet.contains("Industry Category"))					
			out.println("<td>"+sp.getTypeName()+"</td>");
		    if(showSet.contains("Sponsorship Status"))
			out.println("<td>"+sp.getSpon_status()+"</td>");
		    if(showSet.contains("Phones"))					
			out.println("<td>"+sp.getPhonesAbbr()+"</td>");
		    if(showSet.contains("Email"))
			out.println("<td>"+sp.getEmail()+"</td>");
		    if(showSet.contains("Web Address"))
			out.println("<td>"+sp.getWebsite()+"</td>");
		    if(showSet.contains("Budget Cycle"))
			out.println("<td>"+sp.getPref_con_time()+"</td>");
		    if(showSet.contains("Pref. Contact Method"))
			out.println("<td>"+sp.getCon_means()+"</td>");
		    if(showSet.contains("Community Focus"))
			out.println("<td>"+sp.getInterests()+"</td>");
		    if(showSet.contains("Target Market"))
			out.println("<td>"+sp.getTarget_pop()+"</td>");
		    if(showSet.contains("Contact Name"))
			out.println("<td>"+fullName+"</td>");
		    if(showSet.contains("Contact Address"))
			out.println("<td>"+ct.getAddress()+"</td>");
		    if(showSet.contains("Contact City State Zip"))
			out.println("<td>"+ct.getCityStateZip()+"</td>");
		    if(showSet.contains("Contact Phones"))
			out.println("<td>"+ctPhones+"</td>");
		    if(showSet.contains("Contact Email"))					
			out.println("<td>"+ctEmail+"</td>");
		    if(showSet.contains("BNI"))					
			out.println("<td>"+sp.getBni()+"</td>");
		    if(showSet.contains("Chapter"))					
			out.println("<td>"+sp.getChapter()+"</td>");
		    if(showSet.contains("Referral from"))					
			out.println("<td>"+sp.getReferral_from()+"</td>");
		    out.println("</tr>");
		}
		out.println("</table>");
		out.println("</td></tr></table>");
	    }
	}
	out.println(Inserts.footer(url));	
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#date_to\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("                                  ");
	out.println(" $(\"#orgname\").autocomplete({ ");
	out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println(" $(\"#opport_name\").autocomplete({ ");
	out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println("  $('#profile').hide(); ");
	out.println("  $('#hider').hide(); ");
	out.println("  $('#showr').click(function (event) { ");
	out.println("    event.preventDefault(); ");
	out.println("  $('#profile').show(); ");
	out.println("  $('#hider').show(); ");
	out.println("  $('#showr').hide(); ");
	out.println("  });   ");
	out.println("  $('#hider').click(function (event) { ");
	out.println("    event.preventDefault(); ");		
	out.println("  $('#profile').hide(); ");
	out.println("  $('#showr').show(); ");
	out.println("  $('#hider').hide(); ");
	out.println("  });   ");
	out.println("</script>");
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    void generateCsvFile(HttpServletResponse res,
			 SponsorList sponsors,
			 Set<String> showSet){
	ServletOutputStream out;
	try{
	    res.setHeader("Expires", "0");
	    res.setHeader("Cache-Control", 
			  "must-revalidate, post-check=0, pre-check=0");
	    res.setHeader("Content-Disposition","attachment; filename=sponsors.csv");
	    res.setHeader("Pragma", "public");
	    //
	    // setting the content type
	    res.setContentType("application/csv");
	    // header row
	    String line = "\"ID\",\"Business\"";
	    for(String str:profArr){
		if(showSet.contains(str)){
		    line += ",\""+str+"\"";
		}
	    }
	    line += "\n";
	    StringBuffer buf = new StringBuffer(line);
	    //
	    for(Sponsor sp:sponsors){
		Contact ct = sp.getFirstContact();
		String fullName = "",ctPhones="",ctEmail="";
		if(ct != null){
		    fullName = ct.getFullName();
		    ctPhones = ct.getPhonesAbbr();
		    ctEmail = ct.getEmail();
		}
		line = "\""+sp.getId()+"\"";
		line += ",\""+sp.getOrgname()+"\"";
		if(showSet.contains("Address")){
		    line += ",\""+sp.getAddress()+"\"";
		}
		if(showSet.contains("City, State Zip"))
		    line += ",\""+sp.getCityStateZip()+"\"";
		if(showSet.contains("Industry Category"))					
		    line += ",\""+sp.getTypeName()+"\"";
		if(showSet.contains("Sponsorship Status"))
		    line += ",\""+sp.getSpon_status()+"\"";
		if(showSet.contains("Phones"))			
		    line += ",\""+sp.getPhonesAbbr()+"\"";
		if(showSet.contains("Email"))		
		    line += ",\""+sp.getEmail()+"\"";
		if(showSet.contains("Web Address"))		
		    line += ",\""+sp.getWebsite()+"\"";
		if(showSet.contains("Budget Cycle"))
		    line += ",\""+sp.getPref_con_time()+"\"";
		if(showSet.contains("Pref. Contact Method"))
		    line += ",\""+sp.getCon_means()+"\"";
		if(showSet.contains("Community Focus"))
		    line += ",\""+sp.getInterests()+"\"";
		if(showSet.contains("Target Market"))
		    line += ",\""+sp.getTarget_pop()+"\"";
		if(showSet.contains("Contact Name"))
		    line += ",\""+fullName+"\"";
		if(showSet.contains("Contact Address"))
		    line += ",\""+ct.getAddress()+"\"";
		if(showSet.contains("Contact City State Zip"))
		    line += ",\""+ct.getCityStateZip()+"\"";				
		if(showSet.contains("Contact Phones"))
		    line += ",\""+ctPhones+"\"";
		if(showSet.contains("Contact Email"))	
		    line += ",\""+ctEmail+"\"";
		line += "\n";
		System.err.println(line);
		buf.append(line);
	    }
	    res.setContentLength(buf.length());
	    out = res.getOutputStream();
	    out.print(buf.toString());
	    out.flush();
	    out.close();			
	}
	catch(Exception ex){
	    System.err.println(ex);
	    logger.error(ex);
	}
    }

}






















































