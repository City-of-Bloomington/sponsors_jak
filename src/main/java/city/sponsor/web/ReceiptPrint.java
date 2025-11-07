package city.sponsor.web;

import java.util.*;
import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

//

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 *
 */
@WebServlet(urlPatterns = {"/ReceiptPrint"})
public class ReceiptPrint extends TopServlet{

    static final long serialVersionUID = 62L;	
    static Logger logger = LogManager.getLogger(ReceiptPrint.class);
    /**
     * Generates the Case form and processes view, add, update and delete
     * operations.
     * @param req
     * @param res
     *
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="", inv_id="";
	boolean success = true;
       	String message = "", action = "";
	res.setContentType("text/html");
	PrintWriter out = null;
	String name, value;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Receipt receipt = new Receipt(debug);;
	HttpSession session = null;
	session = req.getSession(false);
	//
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    value = Helper.replaceSpecialChars(value);
	    if (name.equals("id")) {
		id =value;
		receipt.setId(value);
	    }
	    else if (name.equals("action")){ 
		// Get, Save, zoom, edit, delete, New, Refresh
		action = value;  
	    }
	}

	User user = null;
	if(session != null){
	    user = (User)session.getAttribute("user");
	}
	if(user == null){
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return;
	}
	if(!action.startsWith("Pdf")){
	    res.setContentType("text/html");
	    out = res.getWriter();
	}

	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	NumberFormat cf = NumberFormat.getCurrencyInstance();
	if(true){
	    //
	    String back = receipt.doSelect();
	    if(!back.equals("")){
		message = back;
		success = false;
		out.println("<html><head><title>Receipt</title></head>");
		out.println("<body>");
		out.println("<center>");
		out.println("<p>"+message+"</p>");
		out.println("</body></html>");
		return;
	    }
	    Invoice invoice = null;
	    Sponsor sponsor = null;
	    Contact contact = null;
	    PaymentList pays = null;
	    Map<String, String> balances = null;
	    String orgname = "";
	    invoice = receipt.getInvoice();
	    if(invoice != null){
		sponsor = invoice.getSponsor();
		contact = sponsor.getFirstContact();
		orgname = sponsor.getOrgname();
		pays = invoice.getPayments();
		balances = invoice.getInvoicePayBalances();
	    }
	    out.println("<html><head><title>Receipt</title></head>");
	    out.println("<body>");
	    out.println("<center>");
	    out.println("<h3>City Of Bloomington <br />");
	    out.println("Parks and Recreation Department <br />");
	    out.println("Sponsorship Programs</h3>");
	    out.println("<hr width=\"50%\" />");
	    out.println("<h2>RECEIPT</h2>");
	    out.println("<table width=\"90%\">");
	    out.println("<tr><td align=\"center\" width=\"70%\">");
	    out.println("</td><td><img src=\""+url+"images/"+"parks_logo_15.png\" alt=\"City Parks logo\" /></td></tr>");
	    out.println("</table>");
	    String all = "<hr width=\"100%\" size=\"2\"/>";
	    all += "<table width=\"100%\" border=\"0\">\n";
	    all += "<tr><td width=\"100%\"><font size=\"-1\">";
	    all += "<b>"+sponsor.getOrgname()+"<br />";
	    if(contact != null)
		all += "c/o: "+contact.getFullName()+"<br />";
	    else
		all += "<br />";
	    all += " "+sponsor.getAddress()+"<br />";
	    all += " "+sponsor.getCityStateZip()+"<br /><br /><br />";
	    all += "</b></font></td><td></td></tr></table>";
	    all += "<p>&nbsp;</p>";
	    out.println(all);
	    //
	    out.println("<table width='90%'>");
	    String voided = receipt.isVoided()?"voided":"";
	    out.println("<tr><td width='20%'><b>Receipt:</b></td>"+
			"<td width='20%'> "+receipt.getReceipt_no()+" "+voided+"</td>"+
			"<td width='30%'>&nbsp;</td>"+
			"<td align='right'><b>Date: </b></td>"+
			"<td>"+receipt.getReceived()+"</td></tr>");
	    out.println("<tr><td><b>Invoice Ref: </b></td>"+
			"<td>"+receipt.getInv_id()+"</td>"+
			"<td>&nbsp;</td></tr>");
	    out.println("<tr><td><b>Sponsor: </b></td>"+
			"<td colspan=4>"+orgname+"</td></tr>");
	    out.println("</table></td></tr></table>");
			
	    //
	    if(pays != null){
		//
		all = "<table width=\"90%\">"+
		    "<tr><td><table border=\"\"' width=\"100%\">"+
		    "<tr><th><font size=\"-1\">Request Due Date</font></th>"+
		    "<th><font size=\"-1\">Sponsorship</font></th>"+
		    "<th><font size=\"-1\">Amount $</font></th>"+
		    "<th><font size=\"-1\">Sub-Total</font></th></tr>\n";
		int cnt = 0;
		double total = 0;
		Sponsorship sponship;
		for(Payment pay:pays){
		    total = receipt.getValueDbl();
		    sponship = pay.getSponship();
		    double balance = 0;
		    if(balances != null){
			String val = balances.get(pay.getId());
			try{
			    balance = Double.parseDouble(val);
			    total += balance;
			}catch(Exception ex){};
		    }
		    all += "<tr><td><font size=\"-1\">";
		    all += pay.getDueDate();
		    all += "</font></td><td><font size=\"-1\">";
		    all += sponship.getDetails();
		    all += "</font></td><td align=\"right\"><font size=\"-1\">";
		    all += cf.format(pay.getValueDbl());
		    all += "</font></td><td align=\"right\"><font size=\"-1\">";
		    all += cf.format(balance);
		    all += "</font></td><td>";	
		    all += "</tr>";
		}
		total = total - receipt.getValueDbl();
		all += "<tr><td colspan=\"3\" align=\"right\">"+
		    "<font size=\"-1\"><b>Amount Due</b></font></td>"+
		    "<td align=\"right\"><font size=\"-1\"><b>"+cf.format(total)+
		    "</b></font></td></tr>"+
		    "<tr><td colspan=\"3\" align=\"right\">"+
		    "<font size=\"-1\"><b>Amount Paid</b></font></td>"+
		    "<td align=\"right\"><font size=\"-1\"><b>"+cf.format(receipt.getValueDbl())+
		    "</b></font></td></tr>"+					
		    "</table></td></tr>";	
				
		out.println(all);
		out.println("<table width=\"90%\">");
		out.println("<tr><td width=\"30%\">&nbsp;</td><td>&nbsp;</td></tr>");
		out.println("<tr><td><b>Amount Paid:</b></td>"+
			    "<td>"+cf.format(receipt.getValueDbl())+"</td></tr>");
		out.println("<tr><td><b>Method Of Payment: </b></td>");
		out.println("<td>"+receipt.getPay_type()+"</td></tr>");
		String checkNo = receipt.getCheck_no();
		if(!checkNo.equals("")){
		    out.println("<tr><td><b>Check Number: </b></td><td>"+checkNo);
		    out.println("</td></tr>");
		}
		out.println("<tr><td>&nbsp;</td></tr>");	
		out.println("<tr><td><b>Received By: </b></td><td>"+receipt.getRecuByFullName());
		out.println("</td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		out.println("<tr><td>&nbsp;</td><td align='left'>"+
			    "Approved by the State Board of Accounts, 2013"+
			    "</td></tr>");
		out.println("<tr><td>&nbsp;</td><td align='left'>"+
			    "<br /><br />"+
			    "Thank You For Your Payment."+
			    "</td></tr>");
		out.println("</table>");
		out.println("</body></html>");
		out.flush();
		out.close();
		return;
	    }
	}
	//
	out.print("</body></html>");
	out.flush();
	out.close();

    }
    //

}






















































