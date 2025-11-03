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
@WebServlet(urlPatterns = {"/InvoiceServ"})
public class InvoiceServ extends TopServlet{

    static final long serialVersionUID = 45L;	
    static Logger logger = LogManager.getLogger(InvoiceServ.class);
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
    
	String id="", spon_id="";
	boolean success=true;
	//
	String message="", action="", date_to="";
	NumberFormat cf = NumberFormat.getCurrencyInstance();	
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
	Sponsor sponsor = null;
	PaymentList pays = null;
	Invoice invoice =  new Invoice(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals, pay_ids = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		invoice.setId(value);
		id = value;
	    }
	    else if (name.equals("spon_id")) {
		invoice.setSpon_id(value);
		spon_id=value;
	    }	
	    else if (name.equals("dueDate")) {
		invoice.setDueDate(value);
	    }
	    else if (name.equals("invoiceDate")) {
		invoice.setInvoiceDate(value);
	    }
	    else if (name.equals("voided")) {
		invoice.setVoided(value);
	    }
	    else if (name.equals("remit_notes")) {
		invoice.setRemit_notes(value);
	    }
	    else if (name.equals("deposit_notes")) {
		invoice.setDeposit_notes(value);
	    }
	    else if (name.equals("attention")) {
		invoice.setAttention(value);
	    }
	    else if (name.equals("staff_id")) {
		invoice.setStaff_id(value);
	    }			
	    else if (name.equals("pay_ids")){ 
		pay_ids = vals;  
	    }
	    else if (name.equals("date_to")){ 
		date_to = value;  
	    }	
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(action.equals("")){
	    if(!id.equals("")){
		action = "zoom";
	    }
	    else{
		action= "Refresh";
	    }
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    invoice.setStaff_id(user.getUserid());
	    String back = invoice.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		id = invoice.getId();
		if(pay_ids != null){
		    back = invoice.addPayments(pay_ids);
		    if(!back.equals("")){
			message += back;
			success = false;
		    }
		    else{
			message += " Saved Successfully ";
		    }
		}
		else{
		    message += " Saved Successfully ";
		}
		pays = invoice.getPayments();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = invoice.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		if(!invoice.isVoided() && pay_ids != null){
		    back = invoice.addPayments(pay_ids);
		    if(!back.equals("")){
			message += back;
			success = false;
		    }
		}				
		message += " Updated Successfully ";
		pays = invoice.getPayments();
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    invoice.doSelect();
	    spon_id = invoice.getSpon_id();
	    String back = invoice.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		invoice.setSpon_id(spon_id);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = invoice.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    pays = invoice.getPayments();
	}
	else if(action.equals("Refresh")){
	    if(date_to.equals("")){
		date_to = Helper.getEndNextMonth();
	    }
	    pays = new PaymentList(debug, true); // all
	    pays.setDate_to(date_to);
	    pays.setSpon_id(spon_id);
	    pays.setNotInvoicedBefore();
	    pays.hasBalance();
	    String back = pays.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	sponsor = invoice.getSponsor();
	spon_id = invoice.getSpon_id();
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
	out.println("   var v = dueDate.value;                       ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Due Date is a required field');     ");
	out.println("           dueDate.focus();                     ");
	out.println("     return false;                              ");
	out.println("    }                                           ");
	out.println("   v = invoiceDate.value;                       ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Invoice Date is a required field'); ");
	out.println("           invoiceDate.focus();                 ");
	out.println("     return false;                              ");
	out.println("    }                                           ");
	out.println("   v = date_to.value;                           ");
	out.println("   if(v == ''){                                 ");
	out.println("     alert('Up to Date is a required field');   ");
	out.println("           date_to.focus();                     ");
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
	out.println("    return true;                  ");
	out.println("	}	             		       ");
	out.println("  }	             		       ");
	out.println(" //]]>   ");
	out.println(" </script>				           ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2>New Invoice</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Invoice </h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"InvoiceServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Payment Info</legend>");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	    if(invoice.hasStaff()){
		out.println("<input type=\"hidden\" name=\"staff_id\" value=\""+invoice.getStaff_id()+"\" />");
	    }
	}
	if(!spon_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"spon_id\" value=\""+spon_id+"\" />");
	}	
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"80%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Invoice Info</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	if(!id.equals("")){
	    out.println("<tr><th>Invoice #</th>");
	    out.println("<td class=\"left\">");
	    out.println(id);
	    String voided = invoice.isVoided()?"checked=\"checked\"":"";
	    out.println("&nbsp;<input type=\"checkbox\" name=\"voided\" value=\"y\" "+voided+" />Void this invoice");
	    out.println("</td></tr>");
			
	}
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">");
	if(sponsor != null){
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+sponsor+"</a>");
	}
		
	if(id.equals("")){
	    out.println("<tr><th>Payment Due Date Up to </th>");	
	    out.println("<td class=\"left\">");
	    out.println("<input name=\"date_to\" size=\"10\" maxlength=\"10\" "+
			" id=\"date_to\" value=\""+date_to+"\" />");
	    out.println("<input type=\"submit\" "+
			" name=\"action\" value=\"Refresh\" />");		
	    out.println("</td></tr>");
	}
	else{
	    out.println("<tr><th>Invoice Total</th>");
	    out.println("<td class=\"left\">$"+invoice.getTotal());
	    out.println("</td></tr>");
	}
	out.println("<tr><th>Invoice Date</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"invoiceDate\" size=\"10\" maxlength=\"10\" "+
		    " id=\"invoiceDate\" value=\""+invoice.getInvoiceDate()+"\" />");
	out.println("</td></tr>");		
	out.println("<tr>");
	out.println("<th>Invoice Due Date</th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"dueDate\" size=\"10\" maxlength=\"10\" "+
		    " id=\"dueDate\" value=\""+invoice.getDueDate()+"\" />");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th colspan=\"2\">Remittance Instructions (250 characters)</th></td></tr>");
	out.println("<tr>");
	out.println("<td colspan=\"2\" class=\"left\">");
	out.println("<textarea name=\"remit_notes\" rows=\"3\" cols=\"70\">");
	out.println(invoice.getRemit_notes());
	out.println("</textarea></td></tr>");
	out.println("<th colspan=\"2\">Deposit Revenue Lines (250 characters)</th></td></tr>");		
	out.println("<tr>");
	out.println("<td colspan=\"2\" class=\"left\">");
	out.println("<textarea name=\"deposit_notes\" rows=\"3\" cols=\"70\">");
	out.println(invoice.getDeposit_notes());
	out.println("</textarea></td></tr>");
	out.println("<tr>");
	out.println("<th>Controller Attention</th>");
	out.println("<td class=\"left\">");
	String attention = defaultAttention;
	if(invoice.hasAttention()){
	    attention = invoice.getAttention();
	}
	out.println("<input name=\"attention\" size=\"30\" maxlength=\"50\" "+
		    " id=\"attention\" value=\""+attention+"\" />");
	out.println("</td></tr>");
		
	out.println("</table></td></tr>");
	out.println("</table>");
	if(pays != null && pays.size() > 0){
			
	    out.println("<tr><td align=\"center\"><table width=\"80%\" border=\"1\">");
	    if(id.equals("")){
		out.println("All the following payment requests will be included in this invoice, you can exclude certain ones by unchecking.<br />");
	    }
	    out.println("<caption>Sponsorship Payment Requests</caption>");
	    out.println("<tr><th>Due Date</th><th>Amount</th><th>Balance</th></tr>");
	    String checked="checked=\"checked\"";
	    for(Payment pay:pays){
		out.println("<tr>");
		if(!invoice.hasReceipts()){
		    out.println("<td><input type=\"checkbox\" name=\"pay_ids\" value=\""+pay.getId()+"\" "+checked+" />"+pay.getDueDate()+"</td>");
		}
		else{
		    out.println("<td>"+pay.getDueDate()+"</td>");
		}
		out.println("<td class=\"money\">"+cf.format(pay.getValueDbl())+"</td>");
		out.println("<td class=\"money\">"+cf.format(pay.getBalance())+"</td>");		
		out.println("</tr>");
	    }
	    out.println("</table></td></tr>");
	    if(id.equals("")){
		if(user.canEdit()){
		    out.println("<tr><td class=\"center\"><input type=\"submit\" "+
				" name=\"action\" value=\"Save\" />");
		    out.println("</td></tr>");
		}
		out.println("</table>");
		out.println("</form>");
	    }			
	}
	else{
	    out.println("<tr><td>No payments due</td></tr>");
	}
	if(!id.equals("")){ // Save, Update
	    out.println("<tr><td valign=\"top\" class=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\"><tr><td>");
	    out.println("<table width=\"100%\">");	
	    out.println("<tr>");
	    if(user.canEdit() && !invoice.hasReceipts()){
		out.println("<td valign=\"top\"><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    out.println("<td valign=\"top\">");
	    out.println("<input type=\"button\" "+
			" onclick=\"javascript:window.open('"+url+
			"InvoicePrint?id="+id+
			"','Print','menubar=1,toolbar=1,location=0,"+
			"directories=0,status=0,"+
			"scrollbars=2,top=200,left=200,"+
			"resizable=1');\" value=\"Printable\" />");
			
	    out.println("</td>");	
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"InvoiceServ?spon_id="+spon_id+"'\" "+
			"value=\"New Invoice\" />");
	    out.println("</td>");
	    if(!invoice.isPaid()){
		out.println("<td valign=\"top\"><input type=\"button\" "+
			    "onclick=\"document.location='"+url+"ReceiptServ?inv_id="+id+"'\" "+
			    "value=\"New Receipt\" />");
		out.println("</td>");
	    }
	    //			
	    if(user.canDelete() && !invoice.hasReceipts()){
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
	    out.println("</form>");
	    if(invoice.hasReceipts()){
		out.println("<table width=\"90%\" border=\"1\">");
		out.println("<caption>Receipts for Invoice # "+id+"</caption>");
		out.println("<tr><td align=\"center\">");
		out.println("<table width=\"100%\" >");
		out.println("<tr><th>Receipt</th>"+
			    "<th>Total Paid </th>"+
			    "<th>Received Date</th>"+
			    "</tr>");
		ReceiptList receipts = invoice.getReceipts();
		for(Receipt rpt:receipts){
		    out.println("<tr>");
		    String voided = "";
		    if(rpt.isVoided()) voided=" Voided";
		    out.println("<td><a href=\""+url+"ReceiptServ?id="+rpt.getId()+"\">"+rpt.getReceipt_no()+" "+voided+"</a></td>");
		    out.println("<td>$"+rpt.getValue()+"</td>");
		    out.println("<td>"+rpt.getReceived()+"</td>");
		    out.println("</tr>");
		}
		out.println("</table></td></tr>");			
		out.println("</table>");
	    }
	}
	if(!spon_id.equals("")){
	    InvoiceList invs = new InvoiceList(debug, true);
	    invs.setSpon_id(spon_id);
	    invs.hasBalance();
	    String back = invs.find();
	    if(back.equals("") && invs.size() > 0){
		out.println("<table width=\"90%\" border=\"1\">");
		out.println("<caption>Current Outstanding Invoices </caption>");
		out.println("<tr><td align=\"center\">");
		out.println("<table width=\"100%\" >");
		out.println("<tr><th>Invoice ID</th>"+
			    "<th>Total </th>"+
			    "<th>Invoice Date</th>"+
			    "<th>Due Date</th>"+
			    "<th>Invoice Balance</th>"+
			    "</tr>");
		for(Invoice inv:invs){
		    out.println("<tr>");
		    String voided = "";
		    if(inv.isVoided()) voided = "Voided";
		    out.println("<td><a href=\""+url+"InvoiceServ?id="+inv.getId()+"\">"+inv.getId()+" "+voided+"</a></td>");
		    out.println("<td>$"+inv.getTotal()+"</td>");
		    out.println("<td>"+inv.getInvoiceDate()+"</td>");
		    out.println("<td>"+inv.getDueDate()+"</td>");
		    out.println("<td>$"+inv.getInvoiceBalance()+"</td>");
		    out.println("</tr>");
		}
		out.println("</table>");
		out.println("</td></tr></table>");
	    }
	}
	out.println(Inserts.footer(url));		
	out.println("<script>");
	out.println("  $( \"#dueDate\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#invoiceDate\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	if(id.equals(""))
	    out.println("  $( \"#date_to\" ).datepicker("+Inserts.jqDateStr(url)+"); ");		
	out.println("                                  ");
	out.println("</script>");		
	//
	// include the partners here
	//
	out.println("</fieldset>");			
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































