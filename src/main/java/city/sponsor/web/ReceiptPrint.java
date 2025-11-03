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
	    out.println("<table width=\"90%\">");
	    out.println("<tr><td align=\"center\" width=\"70%\">");
	    out.println("<h3>City Of Bloomington <br />");
	    out.println("Parks and Recreation Department <br />");
	    out.println("Sponsorship Programs</h3>");
	    out.println("<hr width=\"50%\" />");
	    out.println("<h2>RECEIPT</h2>");
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
	/**
	   else if(action.startsWith("Pdf")){
	   Document document = new Document();
	   ServletOutputStream out2 = null;
	   try {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   PdfWriter.getInstance(document, baos);
		
	   Font fnt = new Font(Font.TIMES_ROMAN, Font.DEFAULTSIZE, 
	   Font.NORMAL);
	   Font fntb = new Font(Font.TIMES_ROMAN, Font.DEFAULTSIZE, 
	   Font.BOLD);
	   //
	   document.open();
	   Paragraph p = new Paragraph();
	   p.setAlignment(Element.ALIGN_CENTER);
	   Chunk ch = new Chunk("City of Bloomington\n"+
	   "Public Works Department\n"+
	   "Trash & Yard Waste Sticker Program\n\n"+
	   "RECEIPT\n\n",
	   FontFactory.getFont(FontFactory.TIMES_ROMAN, 
	   14,
	   Font.BOLD));
	   p.add(ch);
	   document.add(p);
	   //
	   PdfPTable table = new PdfPTable(3);
	   table.setWidthPercentage(90);
	   table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	   table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	   //
	   // 1 st row
	   Phrase phrase = new Phrase();
	   ch = new Chunk("Receipt: ",fntb);
	   Chunk ch2 = new Chunk(""+rid+voidFlag, fnt);
	   phrase.add(ch);
	   phrase.add(ch2);
	   table.addCell(phrase);
	   table.addCell(" ");
	   ch = new Chunk("Date: ",fntb);
	   ch2 = new Chunk(paidDate,fnt);
	   phrase = new Phrase();
	   phrase.add(ch);
	   phrase.add(ch2);
	   table.addCell(phrase);
	   //
	   // 2nd row
	   ch = new Chunk("Invoice Ref: ",fntb);
	   ch2 = new Chunk(""+inid+voidFlag2,fnt);
	   phrase = new Phrase();
	   phrase.add(ch);
	   phrase.add(ch2);
	   table.addCell(phrase);
	   phrase = new Phrase(" ");
	   PdfPCell cell = new PdfPCell(phrase);
	   cell.setColspan(2);
	   cell.setBorder(Rectangle.NO_BORDER);
	   table.addCell(cell);
	   //
	   String billedTo = vname;
	   if(vname.startsWith("Public")) billedTo = paidBy;
	   // 2nd row
	   ch = new Chunk("Customer: ",fntb);
	   ch2 = new Chunk(""+billedTo,fnt);
	   phrase = new Phrase();
	   phrase.add(ch);
	   phrase.add(ch2);
	   cell = new PdfPCell(phrase);
	   cell.setColspan(3);
	   cell.setBorder(Rectangle.NO_BORDER);
	   table.addCell(cell);
	   document.add(table);
	   document.add(Chunk.NEWLINE);
	   document.add(Chunk.NEWLINE);
	   fnt = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	   fntb = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
	   double amountPaid2 = Double.valueOf(amountPaid).doubleValue();
	   //
	   // Sales table
	   //
	   if(sales != null){
	   table = new PdfPTable(6);
	   table.getDefaultCell().setBorder(1);
	   table.setWidthPercentage(95);
	   //
	   // table header
	   //
	   phrase = new Phrase("Item",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   phrase = new Phrase("Sticker Numbers",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   phrase = new Phrase("Sheets",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   phrase = new Phrase("Quantity of Stickers",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   phrase = new Phrase("Sticker Price",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   phrase = new Phrase("Sub-Total",fntb);
	   cell = new  PdfPCell(phrase);
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   table.addCell(cell);
	   int cnt = 0;
	   double total = 0, trate=0, yrate=0, rate=0, cost=0,
	   factor=1.;
	   int startSeq=0, endSeq=0;
	   double sheetCount=0;
	   String prefix="T";
	   try{
	   trate = Double.valueOf(trashRate).doubleValue();
	   yrate = Double.valueOf(yardRate).doubleValue();

	   }catch(Exception ex){
	   System.err.println(ex);
	   }
	   for(Iterator it=sales.iterator();it.hasNext();){
	   startSeq=0; endSeq=0; sheetCount=0; cost=0;
	   String type="",halfPrice="", forFree="";
	   Sale sale = (Sale)it.next();
	   type = sale.getType();
	   halfPrice = sale.getHalfPrice();
	   if(halfPrice != null && !halfPrice.equals("")){
	   factor = 0.5;
	   }
	   forFree = sale.getForFree();
	   if(forFree != null && !forFree.equals("")){
	   factor = 0.0;
	   }
	   startSeq = sale.getStartSeq().intValue();
	   endSeq = sale.getEndSeq().intValue();
	   sheetCount = sale.getSheetCount().doubleValue();
	   if(startSeq > 0 && endSeq > 0){
	   if(type.equals("Trash")){
	   table.addCell(new 
	   Phrase("Trash Stickers",fntb));
	   rate = trate*factor;
	   prefix = "T";
	   }
	   else{
	   table.addCell(new Phrase("Yard Waste",fntb));
	   rate = yrate*factor;
	   prefix = "Y";
	   }
	   table.addCell(new 
	   Phrase(prefix+startSeq+" - "+endSeq,fnt));
	   cell = new PdfPCell(new Phrase(""+sheetCount,fnt));
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
	   cnt = endSeq-startSeq+1;
	   p = new Paragraph(""+cnt,fnt);
	   cell = new PdfPCell(p);
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
			    
	   cell = new PdfPCell(new Paragraph("$"+Helper.formatNumber(rate),fnt));
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
			    
	   cost = cnt * rate;
	   total = total+cost;
	   p = new Paragraph(cf.format(cost),fnt);		
	   cell = new PdfPCell(p);
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
	   }
	   }
	   phrase = new Phrase(" ");
	   cell = new PdfPCell(phrase);
	   cell.setColspan(3);
	   table.addCell(cell);
	   cell = new PdfPCell(new Phrase("Total ",fntb));
	   cell.setColspan(2);
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
	   cell = new PdfPCell(new Phrase(
	   cf.format(total),fntb));
	   cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	   table.addCell(cell);
	   document.add(table);
	   document.add(Chunk.NEWLINE);
	   document.add(Chunk.NEWLINE);
	   }
	   fnt = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
	   fntb = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
	   //
	   table = new PdfPTable(2);
	   table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	   table.setWidthPercentage(45);
	   table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	   table.setHorizontalAlignment(Element.ALIGN_LEFT);
	   table.addCell(new Phrase("Amount Paid: ",fntb));
	   table.addCell(new Phrase(cf.format(amountPaid2),fnt));
	   table.addCell(new Phrase("Method Of Payment: ",fntb));

	   table.addCell(new Phrase(paidMethod,fnt));
	   if(!checkNo.equals("")){
	   table.addCell(new Phrase("Check Number: ",fntb));
	   table.addCell(new Phrase(checkNo,fnt));
	   }
	   table.addCell(new Phrase("Received By: ",fntb));
	   table.addCell(new Phrase(receivedBy,fnt));
	   //
	   document.add(table);
	   document.add(Chunk.NEWLINE);
	   document.add(Chunk.NEWLINE);
	   p = new Paragraph("Approved by the State Board of Accounts, "+
	   "2007\n"+
	   "Thank You for Your Payment");
	   p.setAlignment(Element.ALIGN_CENTER);
	   //
	   document.add(p);
	   document.add(Chunk.NEWLINE);
	   //
	   document.close();
	   res.setHeader("Expires", "0");
	   res.setHeader("Cache-Control", 
	   "must-revalidate, post-check=0, pre-check=0");
	   res.setHeader("Pragma", "public");
	   //
	   // setting the content type
	   res.setContentType("application/pdf");
	   //
	   // the contentlength is needed for MSIE!!!
	   res.setContentLength(baos.size());
	   //
	   // write ByteArrayOutputStream to the ServletOutputStream
	   out2 = res.getOutputStream();
	   baos.writeTo(out2);
	   }
	   catch(Exception ex){
	   System.err.println(ex);
	   success = false;
	   message = "Error "+ ex;
	   }
	   finally{
	   if(out2 != null){ 
	   out2.flush();
	   out2.close();
	   }
	   }
	   return;
	   }
	*/
	//
	out.print("</body></html>");
	out.flush();
	out.close();

    }
    //

}






















































