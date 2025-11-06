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
@WebServlet(urlPatterns = {"/OpportSearchServ"})
public class OpportSearchServ extends TopServlet{

    static final long serialVersionUID = 50L;	
    static Logger logger = LogManager.getLogger(OpportSearchServ.class);
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
    
	boolean success=true;
	//
	String message="", action="";
	String            
	    opport_name="",         
	    date_from="",         
	    date_to="",            
	    program_area="",           
	    season_id ="",             
	    year="",           
	    instructions="",  event_id="", id="",        
	    spon_id="";           
	String pageSize = "50", pageNumber = "1";
		
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
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
	OpportList opports = new OpportList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("name")) {
		// opports.setName(value);
		opport_name = value;
	    }
	    else if (name.equals("date_from")) {
		opports.setDate_from(value);
		date_from = value;
	    }
	    else if (name.equals("date_to")) {
		opports.setDate_to(value);
		date_to = value;
	    }	
	    else if (name.equals("season_id")) {
		opports.setSeason_id(value);
		season_id = value;
	    }
	    else if (name.equals("year")) {
		opports.setYear(value);
		year = value;
	    }
	    else if (name.equals("event_id")) {
		opports.setEvent_id(value);
		event_id = value;
	    }
	    else if (name.equals("program_area")) {
		opports.setProgram_area(value);
		program_area = value;
	    }
	    else if (name.equals("instructions")) {
		opports.setInstructions(value);
		instructions = value;
	    }
	    else if (name.equals("spon_id")) {
		opports.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("id")) {
		opports.setId(value);
		id = value;
	    }	
	    else if (name.equals("pageSize")) {
		pageSize = value;
	    }
	    else if (name.equals("pageNumber")) {
		pageNumber = value;
	    }			
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	opports.setPageSize(pageSize);
	opports.setPageNumber(pageNumber);
	//
	SponOptionList progAreas = new SponOptionList(debug, "program_area");
	EventList events = new EventList(debug);
	SeasonTypeList seasons = new SeasonTypeList(debug);
	if(true){
	    String back = progAreas.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    back = events.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    back = seasons.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }	
	}
	if(!action.equals("")){
	    String back = opports.find();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
	    }
	    else{
		if(opports.size() == 1){
		    Opportunity oport = opports.get(0);
		    String str = url+"OpportServ?id="+oport.getId();
		    res.sendRedirect(str);
		    return; 
		}
	    }
	}
	//
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
	out.println(" //]]>                         ");
	out.println(" </script>				        ");
	//
	// delete startNew
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Search Opportunities</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"get\" "+
		    " action=\""+url+"OpportSearchServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"pageNumber\" value=\""+pageNumber+"\" />");

	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<caption>Search Options</caption>");	
	out.println("<tr><th><label for=\"opport_name\">Opportunity</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"name\" size=\"30\" id=\"opport_name\" "+
		    " maxlength=\"50\" value=\""+opport_name+"\" />");
	out.println("<label for=\"oppt_id\">ID</label><input id=\"oppt_id\" name=\"id\" size=\"4\" value=\""+id+"\" />");				
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
	out.println("<tr><th><label for=\"year\">Year</label></th>");
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
	out.println("<tr><th><label for=\"area\">Program Area</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"program_area\" id=\"area\">");
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
		
	out.println("<tr><th><label for=\"date_from\">Start Date From</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date_from\" size=\"10\" id=\"date_from\" "+
		    " maxlength=\"10\" value=\""+date_from+"\" />");
	out.println("&nbsp;&nbsp;");
	out.println("<label for=\"date_to\"> To </label>");
	out.println("<input name=\"date_to\" size=\"10\" id=\"date_to\" "+
		    " maxlength=\"10\" value=\""+date_to+"\" />");
	out.println("(mm/dd/yyyy)");
	out.println("</td></tr>");	
	out.println("<tr><th><label for=\"pageSize\">Records Per Page </label></th>");
	out.println("<td class=\"left\">");		
	out.println("<input name=\"pageSize\" size=\"3\" maxlength=\"3\" value=\""+pageSize+"\" id=\"pageSize\" /></th></tr>");
	out.println("<tr><th><label for=\"sortBy\">Sort By </label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"sortBy\" id=\"sortBy\">");
	out.println("<option value=\"name\">Name</option>");
	out.println("<option value=\"start_date DESC\" selected=\"selected\">Start Date</option>");
	out.println("<select></th>");	
	out.println("<tr><th>&nbsp;</th><td><input type=\"submit\" "+
		    " name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table>");							
	out.println("</form>");

	if(!action.equals("")){
	    int cnt = opports.size();
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<caption>Found "+cnt+" Opportunities</caption>");
	    if(cnt == 0){
		out.println("<tr><td class=\"center\">No opportunity found</td></tr>"); 
	    }
	    else{
		out.println("<tr>"+
			    "<th>Event </th>"+
			    "<th>Season </th>"+
			    "<th>Year</th>"+
			    "<th>Start, End Dates</th>"+
			    "<th>Program Area</th>"+
			    "</tr>");
		for(Opportunity op:opports){
		    out.println("<tr>");
		    Type event = op.getEvent();
		    String str = op.getName();						
		    if(event != null && !event.getName().equals("Unknown")){
			str = ""+event;
		    }
		    out.println("<td><a href=\""+url+
				"OpportServ?action=zoom&amp;"+
				"id="+op.getId()+"\">"+
				str+"</a></td>");
		    Season season = op.getSeason();
		    String seasonName="&nbsp;";
		    if(season != null){
			seasonName = ""+season;
		    }
		    out.println("<td>"+seasonName+
				"</td>");
		    out.println("<td>"+op.getYear()+
				"</td>");
		    out.println("<td>"+op.getStart_end_date()+
				"</td>");
		    out.println("<td>"+op.getProgram_area()+
				"</td>");						
		    out.println("</tr>");
		}
	    }
	    out.println("</table>");
	    PageList pages = opports.buildPages(url+"OpportSearchServ?action=Submit");
	    String all = pages.getPagesStr();
	    if(!all.equals("")){
		out.println("<center>"+all+"</center>");
	    }
	}
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker(); ");
	out.println("  $( \"#date_to\" ).datepicker(); ");
	out.println(" $(\"#opport_name\").autocomplete({ ");
	out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");		
	out.println("                                  ");
	out.println("</script>");			
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































