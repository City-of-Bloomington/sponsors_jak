package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;
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
 *
 */
@WebServlet(urlPatterns = {"/IncomeReport"})
public class IncomeReport extends TopServlet{

    static final long serialVersionUID = 42L;	
    static Logger logger = LogManager.getLogger(IncomeReport.class);
    static NumberFormat cf = NumberFormat.getCurrencyInstance();
    /**
     * @param req
     * @param res
     */
    
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	String id="";
	boolean success=true;
	//
	String message="", action="", oppt_id="", output="", title="";
	String type="", quarter="", date_from="", date_to="";
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
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("type")) {
		type = value;
	    }
	    else if(name.equals("quarter")){
		quarter = value;
	    }
	    else if(name.equals("date_from")){
		date_from = value;
	    }
	    else if(name.equals("date_to")){
		date_to = value;
	    }
	    else if(name.equals("oppt_id")){
		oppt_id = value;
	    }
	    else if(name.equals("output")){
		output = value;
	    }		
	}
	String year = Helper.getThisYear();
	Opportunity oppt = null;
	if(!oppt_id.equals("")){
	    oppt = new Opportunity(debug, oppt_id);
	    String back = oppt.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		String str = oppt.getYear();
		if(!str.equals("")){
		    year = str;
		}
	    }
	}

	//
	// an opportunity is needed to run the report
	//
	/*
	  if(oppt == null){
	  message = "You need to pick an opportunity ";
	  success = false;
	  }
	*/
	if(type.equals("quarter")){
	    if(quarter.equals("1")){
		date_from="01/01/"+year;
		date_to="03/31/"+year;
		title = "First Quarter January-March "+year;
	    }
	    else if(quarter.equals("2")){
		date_from="04/01/"+year;
		date_to="06/30/"+year;
		title = "Second Quarter April-June "+year;
	    }
	    else if(quarter.equals("3")){
		date_from="07/01/"+year;
		date_to="09/30/"+year;
		title = "Third Quarter July-September "+year;				
	    }
	    else{
		date_from="10/01/"+year;
		date_to="12/31/"+year;
		title = "Fourth Quarter October-December "+year;
	    }			
	}
	else if(type.equals("annual")){
	    date_from="01/01/"+year;
	    date_to="12/31/"+year;
	    title = "Annual "+year;
	}
	else{
	    // specific date, assuming start_date, end_date are set
	    if(date_from.equals("") && date_to.equals("")){
		message ="Start Date or End Date must be set";
		success = false;
	    }
	    else{
		title = "Period report "+date_from+" - "+date_to;
	    }
	}
	if(!success){
	    String str = url+"IncomeReport?message="+URLEncoder.encode(message, "UTF-8");
	    res.sendRedirect(str);
	    return; 
	}
	//
	IncomeList list = new IncomeList(debug);
	if(!oppt_id.equals("")){
	    list.setOppt_id(oppt_id);
	}
	if(!date_from.equals(""))
	    list.setDate_from(date_from);
	if(!date_to.equals(""))
	    list.setDate_to(date_to);
	String back = list.find();
	if(!back.equals("")){
	    message = back;
	    success = false;
	}
	/*
	  SponshipList list = new SponshipList(debug, true); // all = true
	  list.setOppt_id(oppt_id);
	  if(!date_from.equals(""))
	  list.setDate_from(date_from);
	  if(!date_to.equals(""))
	  list.setDate_to(date_to);
	  String back = list.find();
	  if(!back.equals("")){
	  message = back;
	  success = false;
	  }
	*/
	ExpenseList expens = new ExpenseList(debug);
	expens.setOppt_id(oppt_id);
	expens.setDate_from(date_from);
	expens.setDate_to(date_to);		
	back = expens.find();
	if(!back.equals("")){
	    message = back;
	    success = false;
	}
	if(list.size() == 0 && expens.size() == 0){
	    message = "No record found";
	    success = false;
	}
		
	if(!message.equals("")){
	    String str = url+"IncomeReport?message="+URLEncoder.encode(message,"UTF-8");
	    res.sendRedirect(str);
	    return; 
	}
	if(output.equals("csv")){
	    generateCsvFile(res, list, expens, oppt, title);
	}
	else{
	    PrintWriter out = res.getWriter();
	    res.setContentType("text/html");
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println("<div id=\"mainContent\">");			
	    out.println("<h3>"+title+"</h2>");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<caption>Income report</caption>");
	    out.println("<tr>"+
			"<th>Opportunity</th>"+
			"<th>Corporate Partner</th>"+
			"<th>Income</th>"+						
			"<th>Expense</th>"+
			"<th>Net Income</th></tr>");						
	    String old_oppt_id="";
	    double totalPaid = 0, totalExp = 0;

	    for(Income one:list){
		oppt = one.getOppt();
		double paid = one.getValueDbl();				
		if(paid > 0){
		    totalPaid += one.getValueDbl();
		    out.println("<tr>");
		    out.println("<td>"+one.getOppt()+"</td>");
		    out.println("<td>"+one.getSponsor()+"</td>");
		    out.println("<td align=\"right\">"+cf.format(paid)+
				"</td>");
		    out.println("<td>&nbsp;</td><td>&nbsp;</td>");
		    out.println("</tr>");
		}
	    }
	    out.println("<tr><td>&nbsp;</td><td>&nbsp;</td>"+
			"<td>&nbsp;</td><td>&nbsp;</td>"+
			"<td>&nbsp;</td>"+
			"</tr>");
	    for(Expense item:expens){
		Sponsorship sponship = item.getSponship();
		out.println("<tr>");
		out.println("<td>"+sponship.getOppt()+"</d>");
		out.println("<td>"+item.getVendor()+" ("+sponship+")</td>");
		out.println("<td>&nbsp;</td>");
		totalExp += item.getValueDbl();
		out.println("<td align=\"right\">"+cf.format(item.getValueDbl())+"</td>");
		out.println("<td>&nbsp;</td>");	
		out.println("</tr>");
	    }			
	    double net = totalPaid - totalExp;
	    out.println("<tr>");
	    out.println("<td colspan=\"2\">Total Income/Expense</td>");
	    out.println("<td align=\"right\">"+cf.format(totalPaid)+"</td>");
	    out.println("<td align=\"right\">"+cf.format(totalExp)+"</td>");	
	    out.println("<td align=\"right\">"+cf.format(net)+"</td>");	
	    out.println("</tr>");
	    out.println("</table>");
	    out.print("<br /><br />");
	    out.print("</body></html>");
	    out.flush();
	    out.close();
	}
    }
    /**
     * @link #doPost
     *
     */

    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="";
	boolean success=true;
	//
	String message="", action="";
	String type="";
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
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("type")) {
		sponsors.setType(value);
		type = value;
	    }
	    else if(name.equals("message")){
		message = value;
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
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"IncomeReport?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset>");
	out.println("<legend>Periodic Income Report</legend>");	
	//
	out.println("<table width=\"80%\">");
	out.println("<caption>Report options</caption>");	
	out.println("<tr><th><label for=\"oppt_id\">Opportunity</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"name\" size=\"30\" id=\"opport_name\" "+
		    " maxlength=\"50\" value=\"\" />");
	out.println("<b>ID</b><input id=\"oppt_id\" name=\"oppt_id\" size=\"4\" value=\"\" />");				
	out.println("</td></tr>");		
	out.println("<tr><th><label for=\"type\">Period Type</label></th></tr>");
	out.println("<tr>");
	out.println("<th><input type=\"radio\" name=\"type\" value=\"quarter\" checked=\"checked\" id=\"type\"/>Quarter Report</th>");
	out.println("<td class=\"left\">Pick one</td></tr>");
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");
	out.println("<input type=\"radio\" name=\"quarter\" value=\"1\" checked=\"checked\" id=\"q1\"/><label for=\"q1\">First Quarter</label></td></tr>");
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");		
	out.println("<input type=\"radio\" name=\"quarter\" value=\"2\" id=\"q2\"/><label for=\"q2\">Second Quarter</label></td></tr>");
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");		
	out.println("<input type=\"radio\" name=\"quarter\" value=\"3\" id=\"q3\"/><label for=\"q3\">Third Quarter</label></td></tr>");
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");		
	out.println("<input type=\"radio\" name=\"quarter\" value=\"4\" id=\"q4\"/><label for=\"q4\">Fourth Quarter</label></td></tr>");
	out.println("<tr><th><input type=\"radio\" name=\"type\" value=\"annual\" id=\"annual\"/><label for=\"annual\">Annual Report</label></th><td>&nbsp;</td></tr>");	
	out.println("<tr><th><input type=\"radio\" name=\"type\" value=\"specific\" id=\"spec\" /><label for=\"spec\">Specific Period</label></th><td class=\"left\">Set start and end dates</td></tr>");	
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");	
	out.println("<label for=\"date_from\">Start Date:</label><input id=\"date_from\" name=\"date_from\" value=\"\" size=\"10\" maxlength=\"10\" /></td></tr>");
	out.println("<tr><th>&nbsp;</th>");
	out.println("<td class=\"left\">");			
	out.println("<label for=\"date_to\">End Date</label><input id=\"date_to\" name=\"date_to\" value=\"\" size=\"10\" maxlength=\"10\" /></td></tr>");	
	out.println("<tr><th>Output Format</th>");
	out.println("<td class=\"left\">");
	out.println("<input type=\"radio\" name=\"output\" value=\"\" checked=\"checked\" id=\"html\" /><label for=\"html\">HTML</label></th>");
	out.println("<input type=\"radio\" name=\"output\" value=\"csv\" id=\"csv\" /><label for=\"csv\">CSV File (Excel)</label>");
	out.println("</td></tr>");
	out.println("<tr><td class=\"right\"><input type=\"submit\" name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");		
	out.println("</table>");
	out.println("</fieldset>");
	out.println("</form>");
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("  $( \"#date_from\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#date_to\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
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
    void generateCsvFile(HttpServletResponse res,
			 IncomeList slist,
			 ExpenseList expens,
			 Opportunity oppt,
			 String title
			 ){
	ServletOutputStream out;
	try{
	    res.setHeader("Expires", "0");
	    res.setHeader("Cache-Control", 
			  "must-revalidate, post-check=0, pre-check=0");
	    res.setHeader("Content-Disposition","attachment; filename=income_report.csv");
	    res.setHeader("Pragma", "public");
	    //
	    // setting the content type
	    res.setContentType("application/csv");
	    // header row
	    String line = "";
	    line = "\""+title+"\",\"\",\"\",\"\",\"\"\n";
	    StringBuffer buf = new StringBuffer(line);
	    buf.append(line);
	    line = "\"Opportunity\",\"Corporate Partener\",\"Income\",\"Expense\",\"Net Income\"\n";
	    buf.append(line);
	    //
	    double totalPaid = 0, totalExp = 0;
	    for(Income cc:slist){
		double paid = cc.getValueDbl();				
		if(paid > 0){
		    totalPaid += cc.getValueDbl();
		    line = "\""+cc.getOppt()+"\",";
		    line += "\""+cc.getSponsor()+"\",";
		    line += "\""+cf.format(paid)+"\",\"\",\"\"";
		    line += "\n";
		    buf.append(line);
		}
	    }
	    for(Expense item:expens){
		Sponsorship sponship = item.getSponship();
		line = "\""+sponship.getOppt()+"\",";
		line += "\""+item.getVendor()+" ("+sponship+")\",\"\",";
		line += "\""+cf.format(item.getValueDbl())+"\",\"\"";
		line += "\n";
		totalExp += item.getValueDbl();				
		buf.append(line);
	    }
	    double net = totalPaid - totalExp;
	    line = "\"Total Income/Expense\",\"\",";
	    line += "\""+cf.format(totalPaid)+"\",\""+cf.format(totalExp)+"\",\""+cf.format(net)+"\"";
	    line += "\n";
	    buf.append(line);
			
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






















































