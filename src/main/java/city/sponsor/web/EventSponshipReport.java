package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.text.*;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/EventSponshipReport"})
public class EventSponshipReport extends TopServlet{

    static final long serialVersionUID = 33L;	
    static Logger logger = LogManager.getLogger(EventSponshipReport.class);
    static NumberFormat cf = NumberFormat.getCurrencyInstance();
    /**
     * @param req
     * @param res
     */
    

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
	String pageSize = "50", pageNumber = "1", report="";
	//
	// opportunity
	//
	String date_from="", date_to="", oppt_id="", opport_name="",
	    event_id= "", season_id="", year="", program_area="",
	    which_date="cont_start_date";
	//
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
	    if (name.equals("opport_name")) {
		opport_name = value;
	    }
	    else if (name.equals("oppt_id")) {
		oppt_id = value;
	    }	
	    else if (name.equals("event_id")) {
		event_id = value;
	    }
	    else if (name.equals("season_id")) {
		season_id = value;
	    }
	    else if (name.equals("year")) {
		year = value;
	    }
	    else if (name.equals("which_date")) {
		which_date = value;
	    }						
	    else if (name.equals("program_area")) {
		program_area = value;
	    }	                           // up to here opportunities
	    else if (name.equals("date_from")) {
		date_from = value;
	    }
	    else if (name.equals("date_to")) {
		date_to = value;
	    }	
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	//
	EventList events = new EventList(debug);
	SeasonTypeList seasons = new SeasonTypeList(debug);
	SponOptionList progAreas = new SponOptionList(debug, "program_area");
	UserList managers = new UserList(debug);
	managers.setManager("y");
	int count = 0;
	if(true){
	    String back = "";
	    back += managers.find();
	    back += seasons.find();
	    back += events.find();
	    back += progAreas.find();
	    if(!back.equals("")){
		message += back;
		success = false;
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
	out.println("<h2>Sponsorship Report</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"EventSponshipReport\""+
		    " onsubmit=\"return validateForm()\">");
	//
	out.println("<fieldset><legend>Sponsorships Report</legend>");
	//
	// Add/Edit record
	//
	out.println("<table width=\"80%\">");
	out.println("<caption>Opportunity and Event</caption>");
	out.println("<tr><th><label for=\"opport_name\">Opportunity</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"name\" size=\"30\" id=\"opport_name\" "+
		    " maxlength=\"50\" value=\""+opport_name+"\" />");
	out.println("<label for=\"oppt_id\">ID</label><input id=\"oppt_id\" name=\"oppt_id\" size=\"4\" value=\""+oppt_id+"\" />");				
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"event_id\">Event</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"event_id\" id=\"event_id\">");
	out.println("<option value=\"\">All</option>");
	for(Type event: events){
	    String selected="";
	    if(event_id.equals(event.getId())){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+event.getId()+"\" "+selected+">"+event+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><th><label for=\"season_id\">Season</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"season_id\" id=\"season_id\">");
	out.println("<option value=\"\">All</option>");
	for(Type season:seasons){
	    String selected="";
	    if(season.getId().equals(season_id)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+season.getId()+"\" "+selected+">"+season+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><th><label for=\"year\">Year</lable></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"year\" id=\"year\">");
	out.println("<option value=\"\">All</option>");
	for(String str: Helper.getYearsArr()){
	    String selected="";
	    if(year.equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><th><label for=\"area_id\">Program Area</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"program_area\" id=\"area_id\">");
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
	out.println("</table>");
	out.println("<br />");
	//
	// contact table
	//
	out.println("<table width=\"80%\">");
	out.println("<caption> Date Type and Range</caption>");
	out.println("<tr><td colspan=\"3\">Pick the date type to use in date range below</td></tr>");				
	out.println("<tr><th>Date Type</th><td>");
	String checked = which_date.equals("cont_start_date")?"checked=\"checked\"":"";
	out.println("<input type=\"radio\" id=\"date_type\" name=\"which_date\" value=\"cont_start_date\" "+checked+" /><label for=\"date_type\">Contract Start Date</label></td> ");
	checked = which_date.equals("cont_end_date")?"checked=\"checked\"":"";				
	out.println("<td><input type=\"radio\" id=\"date_type2\" name=\"which_date\" value=\"cont_end_date\" "+checked+" /><label for=\"date_type2\">Contract End Date</label></td></tr> ");				
	out.println("<tr><th><label for=\"date_from\">Date From</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("<label for=\"date_to\"> To </label></td>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("</td></tr>");	

	out.println("<tr><td class=\"center\"><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");	
	out.println("</table>");
	out.println("</form>");
	out.println("</fieldset>");
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#date_to\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("                                  ");
	out.println(" $(\"#opport_name\").autocomplete({ ");
	out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println("</script>");
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	//
	// display the report here
	//
	String id="";
	boolean success=true;
	//
	String message="", action="";
	String pageSize = "50", pageNumber = "1", report="";
	//
	// opportunity
	//
	String o_date_from="", o_date_to="", oppt_id="", opport_name="",
	    event_id= "", season_id="", year="", program_area="";
	String name, value;
	User user = null;
		
	if(url.equals("")){
	    url    = getServletContext().getInitParameter("url");
	    url2   = getServletContext().getInitParameter("url2");
	    //
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
	    debug2 = getServletContext().getInitParameter("activeMail");
	    if(debug2.equals("true")) activeMail = true;
	    String str = getServletContext().getInitParameter("emailStr");
	    if(str != null) emailStr = "@"+str;
	}
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	Enumeration<String> values = req.getParameterNames();		
	String [] vals;
	SponsorshipList sponList = new SponsorshipList(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("name")) {
		opport_name = value; // we ignore, we use oppt_id
	    }
	    else if (name.equals("oppt_id")) {
		oppt_id = value;
		sponList.setOppt_id(value);
	    }
	    else if (name.equals("event_id")) {
		event_id = value;
		sponList.setEvent_id(value);
	    }
	    else if (name.equals("year")) {
		year = value;
		sponList.setYear(value);
	    }
	    else if (name.equals("season_id")) {
		season_id = value;
		sponList.setSeason_id(value);
	    }
	    else if (name.equals("program_area")) {
		program_area = value;
		sponList.setProgram_area(value);								
	    }
	    else if (name.equals("date_from")) {
		o_date_from = value;
		sponList.setDate_from(value);
	    }
	    else if (name.equals("date_to")) {
		o_date_to = value;
		sponList.setDate_to(value);								
	    }
	    else if (name.equals("which_date")) {
		sponList.setWhich_date(value);								
	    }						
	    else if (name.equals("report")){ 
		report = value;  
	    }
	}
	/*
	  Opportunity oppt = null;
	  if(!oppt_id.equals("")){
	  oppt = new Opportunity(debug, oppt_id);
	  String back = oppt.doSelect();
	  if(!back.equals("")){
	  message += back;
	  success = false;
	  }
	  }
	*/
	if(true){
	    String back = sponList.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	//
	// display data
	//
	if(sponList != null && sponList.size() > 0){
	    if(report.equals("csv")){
		generateCsvFile(res,
				sponList);
		return;
	    }
	    res.setContentType("text/html");
	    PrintWriter out = res.getWriter();
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println("<div id=\"mainContent\">");
	    out.println("<center><h2>Sponsorship Report </h2></center>");
	    // out.println("Event Name: "+oppt.getName()+"<br />");
	    // out.println("Year: "+oppt.getYear()+"<br /><br />");
	    out.println("<b>Sponsorships</b><br />");
	    out.println("<table width=\"95%\"><tr><td align=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<tr>"+
			"<th>Opportunity</th>"+
			"<th>Sponsorship</th>"+
			"<th>Sponsor</th>"+
			"<th>Contact</th>"+
			"<th>Contract Start Date</th>"+						
			"<th>Contract End Date</th>"+
			"<th>Pay Type</td>"+
			"<th>Request Amount</th>"+
			"<th>Total Paid</th></tr>");
	    double total=0, totalPaid = 0, totalExp=0;
	    for(Sponsorship cc:sponList){
		// total += cc.getTotalPayFromPayments();
		// totalPaid += cc.getTotalPaid();
		Opportunity oppt = cc.getOppt();
		Contact one = cc.getSponsor().getFirstContact();
		out.println("<tr>");
		out.println("<td>"+oppt+"</td>");
		out.println("<td><a href=\""+url+"SponsorshipServ?id="+cc.getId()+"\">"+cc.getId()+"</a></td>");
		out.println("<td><a href=\""+url+"SponsorServ?id="+cc.getSpon_id()+"\">"+cc.getSponsor()+"</a></td>");								
		// out.println("<td>"+cc.getSponsor()+"</td>");
		out.println("<td>");
		if(one != null){
		    out.println("<a href=\""+url+"ContactServ?id="+one.getId()+"\">");
		    out.println(one.getFullName());
		    out.println("</a>");
		}
		out.println("</td>");
		out.println("<td>"+cc.getCont_start_date()+"</td>");
		out.println("<td>"+cc.getCont_end_date()+"</td>");
		out.println("<td>"+cc.getDon_type()+
			    "</td>");
		out.println("<td align=\"right\">&nbsp;");
		double val = cc.getValueDbl();
		if(val > 0){
		    out.println(cf.format(val));
		    total += val;
		}
		out.println("</td><td align=\"right\">&nbsp;");
		val = cc.getTotalPaid();
		if(val > 0){
		    out.println(cf.format(val));
		    totalPaid += val;
		}								
		out.println("&nbsp;</td></tr>");
	    }
	    if(total > 0){
		out.println("<tr><td colspan=\"7\" align=\"right\">Total</td>");
		out.println("<td align=\"right\">"+cf.format(total)+"</td>");
		out.println("<td align=\"right\">"+cf.format(totalPaid)+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	    out.println("<br />");

	    out.print("<br /><br />");
	    out.print("</body></html>");
	    out.flush();
	    out.close();
	}
	else{
	    res.setContentType("text/html");
	    PrintWriter out = res.getWriter();
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println("<div id=\"mainContent\">");			
	    if(!success){
		out.println("<h3>"+message+"</h3>");
	    }
	    out.println("<h3>No sponsorships found</h3>");
	    out.print("<br /><br />");
	    out.print("</body></html>");
	    out.flush();
	    out.close();			
	}
    }
	
    void generateCsvFile(HttpServletResponse res,
			 SponsorshipList list
			 // Opportunity oppt
			 ){
	ServletOutputStream out;
	try{
	    res.setHeader("Expires", "0");
	    res.setHeader("Cache-Control", 
			  "must-revalidate, post-check=0, pre-check=0");
	    res.setHeader("Content-Disposition","attachment; filename=event_sponsorships.csv");
	    res.setHeader("Pragma", "public");
	    //
	    // setting the content type
	    res.setContentType("application/csv");
	    // header row
	    String line = "";
	    line = "\"EVENT SPONSORSHIP REPORT\",\"\",\"\",\"\",\"\"\n";
	    // line += "\"Event Name: "+oppt.getName()+"\",\"\",\"\",\"\",\"\"\n";
	    // line += "\"Year: "+oppt.getYear()+"\",\"\",\"\",\"\",\"\"\n";
	    line += "\"Sponsor\",\"Details\",\"Type\",\"Value\",\"Total Paid\"\n";
	    StringBuffer buf = new StringBuffer(line);
	    double total=0, totalPaid = 0, totalExp=0;
	    for(Sponsorship cc:list){
		total += cc.getTotalPayFromPayments();
		totalPaid += cc.getTotalPaid();
		line = "\""+cc.getSponsor()+"\",\""+cc.getDetails()+"\",\""+cc.getDon_type()+"\",";
		line += "\""+cf.format(cc.getTotalPayFromPayments())+"\",";
		line += "\""+cf.format(cc.getTotalPaid())+"\"\n";
		buf.append(line);
	    }
	    line = "\"Income\",\"\",\"\",\""+cf.format(total)+"\",\""+cf.format(totalPaid)+"\"\n";
	    buf.append(line);
	    line = "\"EXPENSES\",\"\",\"\",\"\",\"\"\n";
	    buf.append(line);
	    ExpenseList expens = new ExpenseList(debug);
	    // expens.setOppt_id(oppt.getId());
	    expens.find();
	    if(expens != null && expens.size() > 0){
		line = "\"Vendor\",\"Details\",\"Expense\",\"\",\"\"\n";
		buf.append(line);
		for(Expense item:expens){
		    Sponsorship sponship = item.getSponship();
		    line = "\""+item.getVendor()+" ("+sponship+")\",";
		    line += "\""+item.getDetails()+"\",";
		    totalExp += item.getValueDbl();
		    line += "\""+cf.format(item.getValueDbl())+"\",";
		    line += "\"\",\"\"\n";
		    buf.append(line);
		}
		line = "\"Total Expenses\",\"\",\""+cf.format(totalExp)+"\",";
		line += "\"\",\"\",\"\"\n";
		buf.append(line);
		line = "\"Net Income\",\"\",\""+cf.format(total)+"\",";
		line += "\"\",\"\",\"\"\n";
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






















































