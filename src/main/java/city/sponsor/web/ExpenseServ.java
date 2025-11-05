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
 *
 */
@WebServlet(urlPatterns = {"/ExpenseServ"})
public class ExpenseServ extends TopServlet{

    static final long serialVersionUID = 36L;	
    static Logger logger = LogManager.getLogger(ExpenseServ.class);
    /**
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="", sponship_id="", vendor_id="", addVendor="";
	boolean success=true;
	//
	String message="", action="";
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
	Sponsorship sponship = null;
	Vendor vendor = null;
	Expense expen =  new Expense(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		expen.setId(value);
		id = value;
	    }
	    else if (name.equals("sponship_id")) {
		expen.setSponship_id(value);
	    }
	    else if (name.equals("vendor_id")) {
		vendor_id = value;
		expen.setVendor_id(value);
	    }	
	    else if (name.equals("value")) {
		expen.setValue(value);
	    }
	    else if (name.equals("details")) {
		expen.setDetails(value);
	    }	
	    else if (name.equals("date")) {
		expen.setDate(value);
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("addVendor")){ 
		addVendor = value;  
	    }	
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(!id.equals("") && action.equals("")){
	    action = "zoom";
	}
	if(action.equals("Save") && user.canEdit()){
	    //
	    String back = "";
	    if(!addVendor.equals("")){
		Vendor vv = new Vendor(debug);
		vv.setName(addVendor);
		back = vv.doSave();
		if(back.equals("")){
		    vendor = vv;
		    vendor_id = vv.getId();					
		    expen.setVendor_id(vendor_id);
		}
	    }
	    back += expen.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = expen.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = expen.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Updated Successfully ";
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    expen.doSelect();
	    sponship_id = expen.getSponship_id();
	    vendor_id = expen.getVendor_id();
	    String back = expen.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		expen = new Expense(debug);
		expen.setSponship_id(sponship_id);
		expen.setVendor_id(vendor_id);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = expen.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	VendorList vendors = new VendorList(debug);
	vendors.find();
	sponship = expen.getSponship();
	sponship_id = expen.getSponship_id();
	Opportunity opport = null;
	Sponsor sponsor = null;
	vendor = expen.getVendor();
	vendor_id = expen.getVendor_id();
	if(sponship != null){
	    opport = sponship.getOppt();
	    sponsor = sponship.getSponsor();
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	//
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	out.println("   var v = value.value;                         ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Amount is a required field');       ");
	out.println("           value.focus();                       ");
	out.println("     return false;                              ");
	out.println("    }                                           ");
	out.println("   if(isNaN(v)){                                ");
	out.println("     alert('Amount is not a valid number ');    ");
	out.println("           value.focus();                       ");
	out.println("     return false;                              ");
	out.println("    }                                           ");
	out.println("   var v = date.value;                          ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Due Date is a required field');     ");
	out.println("           dueDate.focus();                     ");
	out.println("     return false;                              ");
	out.println("    }                                           ");
	out.println("   v = details.value;                           ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Details is a required field');      ");
	out.println("           details.focus();                     ");
	out.println("     return false;                     ");
	out.println("    }                                  ");	
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			                ");
	out.println(" }}	         		                ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                               ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();         ");
	out.println("    return true;                        ");
	out.println("	}	             		             ");
	out.println("  }	             		             ");
	out.println(" //]]>                                  ");
	out.println(" </script>				                 ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2>New Expenses</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Expenses </h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"ExpenseServ?\""+
		    " onsubmit=\"return validateForm()\">");

	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	if(!sponship_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"sponship_id\" value=\""+sponship_id+"\" />");
	}
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<caption>Expenses Info</caption>");	
	if(!id.equals("")){
	    out.println("<tr><th>ID</th>");
	    out.println("<td class=\"left\">");
	    out.println(id);
	    out.println("</td></tr>");
	}
	out.println("<tr><th>Opportunity</th>");
	out.println("<td class=\"left\">");
	if(opport != null){
	    out.println("<a href=\""+url+"OpportServ?id="+opport.getId()+"\">"+opport+"</a>");
	}
	out.println("</td></tr>");
	out.println("<tr><th>Sponsorship</th>");
	out.println("<td class=\"left\">");
	if(sponship != null){
	    out.println("<a href=\""+url+"SponsorshipServ?id="+sponship.getId()+"\">"+sponship+"</a>");
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"vend_id\">Vendor</label></th>");
	out.println("<td class=\"left\">");
	out.println("<select name=\"vendor_id\" id=\"vend_id\">");
	out.println("<option value=\"\"></option>");
	if(vendors != null && vendors.size() > 0){
	    for(Vendor vv:vendors){
		String selected="";				
		if(vv.getId().equals(vendor_id)){
		    selected="selected=\"selected\"";
		}
		out.println("<option "+selected+" value=\""+vv.getId()+"\">"+vv);
	    }
	}
	out.println("</select>");
	if(vendor_id.equals("")){
	    out.println("<label for=\"addNew\">Add New Vendor</label>");
	    out.println("<input name=\"addVendor\" size=\"20\" maxlength=\"30\" "+
			" value=\"\" id=\"addNew\" />");
	}
	out.println("</td></tr>");
	// 
	out.println("<tr><th><label for=\"value\" Expenses Amount $</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value\" size=\"8\" maxlength=\"8\" id=\"value\" "+
		    " value=\""+expen.getValue()+"\" />");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th><label for=\"date\"> Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"date\" size=\"10\" maxlength=\"10\" "+
		    " id=\"date\" value=\""+expen.getDate()+"\" />");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th><label for=\"det_id\">Details</label></th>");
	out.println("<td class=\"left\">");
	out.println("<textarea name=\"details\" rows=\"5\" cols=\"70\" id=\"det_id\">");
	out.println(expen.getDetails());
	out.println("</textarea></td></tr>");
	if(id.equals("")){
	    if(user.canEdit()){
		out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			    " name=\"action\" value=\"Save\" />");
		out.println("</td></tr>");
	    }
	}
	else{ // Save, Update
	    out.println("<tr>");
	    if(user.canEdit()){
		out.println("<td valign=\"top\"><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    //			
	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr>");

	}
	if(sponship.hasExpenses()){
	    ExpenseList rows = sponship.getExpenses();
	    if(rows != null && rows.size() > 0){
		Helper.writeExpenses(out, rows, url);
	    }
	}
	out.println("</table>");
	out.println("</form>");	
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("  $( \"#date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("                                  ");
	out.println("</script>");		
	//
	// include the partners here
	//
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































