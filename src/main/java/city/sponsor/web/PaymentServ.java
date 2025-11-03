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
@WebServlet(urlPatterns = {"/PaymentServ"})
public class PaymentServ extends TopServlet{

    static final long serialVersionUID = 59L;
    static Logger logger = LogManager.getLogger(PaymentServ.class);		
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
    
	String id="", sponship_id="";
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
	Payment pay =  new Payment(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		pay.setId(value);
		id = value;
	    }
	    else if (name.equals("sponship_id")) {
		pay.setSponship_id(value);
	    }	
	    else if (name.equals("value")) {
		pay.setValue(value);
	    }
	    else if (name.equals("balance")) {
		pay.setBalance(value);
	    }	
	    else if (name.equals("dueDate")) {
		pay.setDueDate(value);
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(!id.equals("") && action.equals("")){
	    action = "zoom";
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    pay.setBalance(pay.getValue());
	    String back = pay.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = pay.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = pay.doUpdate();
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
	    pay.doSelect();
	    sponship_id = pay.getSponship_id();
	    String back = pay.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		pay = new Payment(debug);
		pay.setSponship_id(sponship_id);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = pay.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	sponship = pay.getSponship();
	sponship_id = pay.getSponship_id();
	Opportunity opport = null;
	Sponsor sponsor = null;
	if(sponship != null){
	    opport = sponship.getOppt();
	    sponsor = sponship.getSponsor();
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
	out.println("   var v = dueDate.value;                       ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Due Date is a required field');       ");
	out.println("           dueDate.focus();                       ");
	out.println("     return false;                              ");
	out.println("    }                                           ");	
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                    ");
	out.println("	}	             		     ");
	out.println("  }	             		     ");
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2>New Payment</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Payment </h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"PaymentServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Payment Info</legend>");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	    out.println("<input type=\"hidden\" name=\"balance\" value=\""+pay.getBalance()+"\" />");
	}
	if(!sponship_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"sponship_id\" value=\""+sponship_id+"\" />");
	}
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Payment Info</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	if(!id.equals("")){
	    out.println("<tr><th>ID</th>");
	    out.println("<td class=\"left\">");
	    out.println(id);
	    out.println("</td></tr>");
	}
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">");
	if(sponsor != null){
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+sponsor+"</a>");
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
	out.println("<tr><th>Payment Amount $</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value\" size=\"8\" maxlength=\"8\" "+
		    " value=\""+pay.getValue()+"\" />");
	if(pay.getBalance() > 0){
	    out.println("&nbsp;&nbsp;<b>Balance </b>"+pay.getBalance());
	}
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th>Due Date</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"dueDate\" size=\"10\" maxlength=\"10\" "+
		    " id=\"dueDate\" value=\""+pay.getDueDate()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Status </th>");
	out.println("<td class=\"left\">"+pay.getStatus());
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	if(id.equals("")){
	    out.println("</table></td></tr>");							
	    if(user.canEdit()){
		out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			    " name=\"action\" value=\"Save\" />");
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	    out.println("</fieldset>");			
	    out.println("</form>");
	}
	else{ // Save, Update
	    out.println("<tr><td valign=\"top\" class=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\"><tr><td>");
	    out.println("<table width=\"100%\">");	
	    out.println("<tr>");
	    if(user.canEdit()){
		out.println("<td valign=\"top\"><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"InvoiceServ?spon_id="+sponsor.getId()+"'\" "+
			"value=\"Invoice\" />");
	    out.println("</td>");
	    //			
	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("</table></td></tr>");			
	    out.println("</table>");
	    out.println("</td></tr></table>");	
	    out.println("</fieldset>");
	    out.println("</form>");
	}
	if(sponship.hasPayments()){
	    PaymentList pays = sponship.getPayments();
	    if(pays != null && pays.size() > 0){
		Helper.writePayments(out, pays, url);
	    }
	}
	out.println(Inserts.footer(url));	
	out.println("<script>");
	//out.println(" $(function() { ");
	out.println("  $( \"#dueDate\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	// out.println(" }); ");
	// out.println(" $(function() { ");
	out.println("  $( \"#invoiceDate\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	//out.println(" }); ");
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






















































