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
@WebServlet(urlPatterns = {"/ReceiptServ"})
public class ReceiptServ extends TopServlet{

    static final long serialVersionUID = 63L;	
    static Logger logger = LogManager.getLogger(ReceiptServ.class);
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
    
	String id="", inv_id="";
	boolean success = true;
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
	Payment pay =  null;
	Receipt receipt = new Receipt(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		receipt.setId(value);
		id = value;
	    }
	    else if (name.equals("inv_id")) {
		receipt.setInv_id(value);
	    }
	    else if (name.equals("receipt_no")) {
		receipt.setReceipt_no(value);
	    }			
	    else if (name.equals("value")) {
		receipt.setValue(value);
	    }
	    else if (name.equals("received")) {
		receipt.setReceived(value);
	    }
	    else if (name.equals("check_no")) {
		receipt.setCheck_no(value);
	    }
	    else if (name.equals("pay_type")) {
		receipt.setPay_type(value);
	    }
	    else if (name.equals("voided")) {
		receipt.setVoided(value);
	    }
	    else if (name.equals("recu_by")) {
		receipt.setRecu_by(value);
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
	    String back = receipt.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = receipt.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = receipt.doUpdate();
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
	    receipt.doSelect();
	    inv_id = receipt.getInv_id();
	    String back = receipt.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		receipt = new Receipt(debug);
		receipt.setInv_id(inv_id);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = receipt.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	Invoice invoice = receipt.getInvoice();
	inv_id = receipt.getInv_id();
	Sponsor sponsor = null;
	if(invoice != null){
	    inv_id = invoice.getId();
	    sponsor = invoice.getSponsor();
	    if(id.equals("")){
		receipt.setValue(""+invoice.getInvoiceBalance());
	    }
	}
	UserList managers = new UserList(debug);
	managers.setManager("y");
	if(true){
	    String back = managers.find();
	    if(!back.equals("")){ 
		message += back;
		success = false;
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
	    out.println("<h2>New Receipt</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Receipt </h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"ReceiptServ?\""+
		    " onsubmit=\"return validateForm()\">");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	if(!inv_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"inv_id\" value=\""+inv_id+"\" />");
	}
	out.println("<input type=\"hidden\" name=\"receipt_no\" value=\""+receipt.getReceipt_no()+"\" />");		
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<caption>Receipt Info</caption>");
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">");
	if(sponsor != null){
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+sponsor+"</a>");
	}
	out.println("</td></tr>");
	out.println("<tr><th>Receipt No.</th>");
	out.println("<td class=\"left\">");
	out.println(receipt.getReceipt_no());
	if(!id.equals("")){
	    String checked = "";
	    if(receipt.isVoided()){
		checked = "checked=\"checked\" ";
	    }
	    out.println("&nbsp;&nbsp;");
	    out.println("<input type=\"checkbox\" name=\"voided\" "+checked+" value=\"y\" id=\"voided\" /> <label for=\"voided\">Voided</label>");
	}
	out.println("</td></tr>");
		
	out.println("<tr><th><label for=\"value\">Received Amount $</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value\" size=\"8\" maxlength=\"8\" "+
		    " id=\"value\" value=\""+receipt.getValue()+"\" />");
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("<th><label for=\"received\">Received Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"received\" size=\"10\" maxlength=\"10\" "+
		    " id=\"received\" value=\""+receipt.getReceived()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"check_no\">Check No.</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"check_no\" size=\"15\" maxlength=\"20\" "+
		    " id=\"check_no\" value=\""+receipt.getCheck_no()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Payment Method </th>");
	out.println("<td class=\"left\">");
	String ptype = receipt.getPay_type();
	for(String pstr:Helper.payMethodArr){
	    String checked = "";
	    if(pstr.equals(ptype)) checked = "checked=\"checked\"";
	    out.println("<input type=\"radio\" name=\"pay_type\" value=\""+pstr+"\" "+checked +" id=\""+pstr+"\"/><label for=\""+pstr+"\">"+pstr+"</label>");
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"recu_by\">Received By </label></th>");
	out.println("<td class=\"left\">");	
	out.println("<select name=\"recu_by\" id=\"recu_by\">");
	out.println("<option></option>");
	for(User muser:managers){
	    String selected = "";
	    if(muser.getUserid().equals(receipt.getRecu_by())) selected = "selected=\"selected\" "; 
	    out.println("<option value=\""+muser.getUserid()+"\" "+selected+">"+muser+"</option>");			
	}	
	out.println("</select></td></tr>");	
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

	    out.println("<td valign=\"top\">");
	    out.println("<input type=\"button\" "+
			" onclick=\"javascript:window.open('"+url+
			"ReceiptPrint?id="+id+
			"','Print','menubar=1,toolbar=1,location=0,"+
			"directories=0,status=0,"+
			"scrollbars=2,top=200,left=200,"+
			"resizable=1');\" value=\"Printable\" />");
	    out.println("</td>");
	    out.println("</tr>");
	}
	out.println("</table>");
	out.println("</form>");
	if(invoice != null){
	    if(invoice.hasReceipts()){
		out.println("<table width=\"90%\" border=\"1\">");
		out.println("<caption>Invoice "+inv_id+" Receipts</caption>");
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
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<caption>Related Invoice </caption>");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=\"100%\" >");
	    out.println("<tr><th>Invoice</th>"+
			"<th>Invoice Date</th>"+						
			"<th>Due Date</th>"+
			"<th>Total $</th>"+
			"<th>Invoice Balance</th>"+
			"</tr>");
	    out.println("<tr>");
	    out.println("<td><a href=\""+url+"InvoiceServ?id="+invoice.getId()+"\">"+invoice.getId()+"</a></td>");
	    out.println("<td>"+invoice.getInvoiceDate()+"</td>");			
	    out.println("<td>"+invoice.getDueDate()+"</td>");
	    out.println("<td>$"+invoice.getTotal()+"</td>");
	    out.println("<td>$"+invoice.getInvoiceBalance()+"</td>");
	    out.println("</tr>");
	    out.println("</table>");
	    out.println("</td></tr></table>");
	}
	out.println(Inserts.footer(url));		
	out.println("<script>");
	out.println("  $( \"#received\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
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






















































