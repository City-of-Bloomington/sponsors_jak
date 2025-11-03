package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.text.*;
import java.net.*;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font.FontFamily;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * printable invoice
 *
 */
@WebServlet(urlPatterns = {"/InvoicePrint"})
public class InvoicePrint extends TopServlet{

    static final long serialVersionUID = 10L;
    public final static Locale local = new Locale("en","US");    
    static NumberFormat cf = NumberFormat.getCurrencyInstance(local);		
    static Logger logger = LogManager.getLogger(InvoicePrint.class);
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
		
	// res.setContentType("text/html");
	//PrintWriter out = res.getWriter();
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
	Map<String, String> balances = null;
	Invoice invoice =  new Invoice(debug);
	Contact contact = null;
	Opportunity oppt = null;
	Event event = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals, pay_ids = null, phoneArr = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		invoice.setId(value);
		id = value;
	    }
	}
	if(true){
	    String back = invoice.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    pays = invoice.getPayments();
	    balances = invoice.getInvoicePayBalances();
	}
	sponsor = invoice.getSponsor();
	spon_id = invoice.getSpon_id();
	if(sponsor != null){
	    contact = sponsor.getFirstContact();
	    if(contact == null){
		contact = new Contact(debug);
	    }
	}
	else{
	    sponsor = new Sponsor(debug); // to avoid null;
	    contact = new Contact(debug);
	}
	if(!phones.equals("")){
	    if(phones.indexOf(",") > -1){
		phoneArr = phones.split(",");
	    }
	    else{
		phoneArr = new String[1];
		phoneArr[0] = phones;
	    }
	}
	//
	// delete startNew
	//
	Sponsorship sponship = null;
	String start_date = "", terms = "", end_date="";
	if(pays != null && pays.size() > 0){
	    Payment pay = pays.get(0);
	    sponship = pay.getSponship();
	    oppt = sponship.getOppt();
	    event = oppt.getEvent();
	    start_date = sponship.getCont_start_date();
	    if(start_date.equals(""))
		start_date = sponship.getStart_date();
	    end_date = sponship.getCont_end_date();
	    terms = sponship.getTerms();
	}
	String attention = defaultAttention;
	if(invoice.hasAttention()){
	    attention = invoice.getAttention();
	}
	Rectangle pageSize = new Rectangle(612, 792); // 8.5" X 11"
        Document document = new Document(pageSize, 36, 36, 18, 18);// 18,18,54,35
	ServletOutputStream out = null;
	String [] remit ={"Remittance Address: ","City of Bloomington","PO Box 100","Bloomington, IN 47402"};
	User staff = invoice.getStaff();
	if(staff == null){
	    staff = user;
	}
	try{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter writer = PdfWriter.getInstance(document, baos);
	    String spacer="  ";
	    document.open();
	    //
	    // header
	    Image image = Image.getInstance(url+"images/"+"parks_logo_15.png");
	    Font fnts = FontFactory.getFont("Times-Roman", 10);
	    Font fntbs = FontFactory.getFont("Times-Roman", 10, Font.BOLD);
	    Font fnt = FontFactory.getFont("Times-Roman", 12);
	    Font fntb = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
	    Font fnt2 = FontFactory.getFont("Times-Roman", 14);
	    Font fntb2 = FontFactory.getFont("Times-Roman", 14, Font.BOLD);
	    Font fnt4b = FontFactory.getFont("Times-Roman", 18, Font.BOLD);
	    Font fntbu =
		new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE);
	    // logo table and image
	    // float[] widthLogo = {30f,70f}; // percentages
	    PdfPTable logoTable = new PdfPTable(1);
	    logoTable.setWidthPercentage(100);
	    logoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    logoTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    Phrase phrase = new Phrase();
	    Chunk ch = null;
	    // 
	    PdfPCell cell = new PdfPCell(image);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    logoTable.addCell(cell);	    
	    //
	    document.add(logoTable);
	    
	    
	    float[] widths = {33f, 34f, 33f}; // percentages
	    PdfPTable headTable = new PdfPTable(widths);
	    headTable.setWidthPercentage(100);
	    headTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    headTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    float[] width ={33f};
	    float[] width2 ={25f,60f,15f};
	    PdfPTable leftTable = new PdfPTable(width);
	    leftTable.setWidthPercentage(33);
	    leftTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    leftTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	    // space
	    Phrase spacePhrase = new Phrase();
	    ch = new Chunk(spacer, fnt4b);
	    spacePhrase.add(ch); // for user later
	    //
	    //
	    for(String str:remit){
		phrase = new Phrase();
		ch = new Chunk(str, fntb);
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		leftTable.addCell(cell);
	    }
	    phrase = new Phrase();
	    ch = new Chunk("Attention: "+attention, fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);

	    // add left table to header table
	    headTable.addCell(leftTable);			
	    //
	    // Middle table
	    phrase = new Phrase();
	    ch = new Chunk("INVOICE",fnt4b);
	    phrase.add(ch);

	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_TOP);	    
	    //
	    headTable.addCell(cell);
	    //
	    PdfPTable rightTable = new PdfPTable(width);
	    rightTable.setWidthPercentage(3);
	    rightTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    rightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);			
	    phrase = new Phrase();
	    ch = new Chunk("Invoice Number: ",fntb);
	    phrase.add(ch);
	    ch = new Chunk("18-"+invoice.getId(),fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    rightTable.addCell(cell);
	    phrase = new Phrase();
	    ch = new Chunk("Date: ",fntb);
	    phrase.add(ch);
	    ch = new Chunk(invoice.getInvoiceDate(),fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    rightTable.addCell(cell);

	    phrase = new Phrase();
	    ch = new Chunk("Contract Start Date: ",fntb);
	    phrase.add(ch);
	    ch = new Chunk(start_date, fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    rightTable.addCell(cell);

	    if(!end_date.isEmpty()){
		phrase = new Phrase();
		ch = new Chunk("Contract End Date: ",fntb);
		phrase.add(ch);
		ch = new Chunk(end_date, fnt);
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightTable.addCell(cell);
	    }
	    if(!terms.isEmpty()){
		phrase = new Phrase();
		ch = new Chunk("Contract Terms: ",fntb);
		phrase.add(ch);
		ch = new Chunk(terms, fnt);
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		rightTable.addCell(cell);
	    }
	    phrase = new Phrase();
	    ch = new Chunk("Payment due date: ",fntb);
	    phrase.add(ch);
	    ch = new Chunk(invoice.getDueDate(), fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    rightTable.addCell(cell);
	    headTable.addCell(rightTable);
	    //
	    document.add(headTable);
	    //
	    // line separater
	    //
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.BOTTOM);
	    cell.setBorderColor(BaseColor.BLACK);
	    cell.setBorderWidth(1f);

	    PdfPTable lineTable = new PdfPTable(1);                
	    lineTable.addCell(cell);
	    lineTable.setWidthPercentage(100f);
	    lineTable.setHorizontalAlignment(Element.ALIGN_CENTER);
	    document.add(lineTable);
	    //			  
	    // sponsor contact
	    //
	    //
	    phrase = new Phrase();
	    ch = new Chunk("\n"+sponsor.getOrgname()+"\n",fntb);
	    phrase.add(ch);
	    float indent = 30f;
	    Paragraph ph = new Paragraph(phrase);
	    ph.setIndentationLeft(indent);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("c/o: "+contact.getFullName()+"\n",fntb);
	    phrase.add(ch);
	    ph.add(phrase);
	    //
	    phrase = new Phrase();
	    ch = new Chunk(sponsor.getAddress()+"\n",fntb);
	    phrase.add(ch);
	    ph.add(phrase);
	    //
			
	    phrase = new Phrase();
	    ch = new Chunk(sponsor.getCityStateZip()+"\n\n",fntb);
	    phrase.add(ch);
	    ph.add(phrase);
	    document.add(ph);
	    //
	    //
	    PdfPTable contTable = new PdfPTable(width2); // 3 columns
	    contTable.setWidthPercentage(100f);
	    contTable.getDefaultCell().setPadding(5);
	    // leftTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    contTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    phrase = new Phrase();
	    ch = new Chunk("Payment Details",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorderWidth(2f);
	    cell.setPadding(5);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);			
	    contTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("Description",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorderWidth(2f);
	    cell.setPadding(5);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
	    contTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("Total",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorderWidth(2f);
	    cell.setPadding(5);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
	    contTable.addCell(cell);
	    //
	    if(pays != null){
		int cnt = 0;
		double total = 0;
		int paidCount = 0;
		String old_sponship_id="";
		for(Payment pay:pays){
		    total += pay.getBalance();
		    sponship = pay.getSponship();
		    String sponship_id = sponship.getId();
		    if(sponship_id.equals(old_sponship_id)){
			paidCount++;
		    }
		    else{
			paidCount = sponship.getPaidCount();
			old_sponship_id = sponship_id;
		    }
		    oppt = sponship.getOppt();
		    event = oppt.getEvent();
		    double balance = 0;
		    if(balances != null){
			String val = balances.get(pay.getId());
			try{
			    balance = Double.parseDouble(val);
			}catch(Exception ex){};
		    }
		    terms = sponship.getTerms();
		    if(terms.equals("")){
			terms = "1";
		    }
		    phrase = new Phrase();
		    //
		    // removed according to Julie request
		    // ch = new Chunk((paidCount+1)+" of "+terms,fnt);
		    //
		    ch = new Chunk("",fnt);										
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    // cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    contTable.addCell(cell);
		    //
		    String str2 = event == null ? oppt.getName():event.toString();
		    str2 += ", ";
		    str2 += sponship.getDetails();
		    phrase = new Phrase();
		    ch = new Chunk(str2, fnt);
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    // cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    contTable.addCell(cell);
		    //
		    phrase = new Phrase();
		    ch = new Chunk(cf.format(balance), fnt);
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    // cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    contTable.addCell(cell);
		    cnt++;
		}
		if(cnt < 5){
		    for(int jj=cnt;jj<5;jj++){ // additional rows
			for(int i=0;i<3;i++){ // each row
			    cell = new PdfPCell(spacePhrase);
			    // cell.setBorder(Rectangle.NO_BORDER);
			    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			    contTable.addCell(cell);
			}
		    }
		}
		//
		// last row
		phrase = new Phrase();
		ch = new Chunk("TOTAL DUE: ", fntb);
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setColspan(2);
		cell.setPadding(5);
		contTable.addCell(cell);					
		//
		phrase = new Phrase();
		ch = new Chunk(cf.format(total), fntb);
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorderWidth(2f);
		cell.setPadding(5);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		contTable.addCell(cell);
	    }
	    document.add(contTable);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("For questions please call: ", fnt);
	    phrase.add(ch);
	    ch = new Chunk(staff.getFullName()+" at "+staff.getPhone(),fntb);
	    phrase.add(ch);
	    Paragraph ph2 = new Paragraph(phrase);
	    document.add(ph2);
	    //

	    phrase = new Phrase();
	    ch = new Chunk("Please make check payable to: ", fnt);
	    phrase.add(ch);
	    ch = new Chunk("The City of Bloomington Parks and Recreation Department\n",fntb);
	    phrase.add(ch);
	    ch = new Chunk("To pay with Visa, Master Card, or Discover, call 812-349-3700 and reference your invoice number.\n\n",fnt);
	    phrase.add(ch);						
	    ph = new Paragraph(phrase);
	    document.add(ph);

	    //
	    phrase = new Phrase();
	    ch = new Chunk("\n\nThank You\n\n",fntb2);
	    phrase.add(ch);
	    ph = new Paragraph(phrase);
	    document.add(ph);			

	    PdfPTable footTable = new PdfPTable(1); 
	    footTable.setWidthPercentage(100);
	    footTable.getDefaultCell().setBackgroundColor(BaseColor.GRAY);
	    footTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	    phrase = new Phrase();
	    ch = new Chunk("Office Use Only\n", fntbu);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);
	    ch = new Chunk("Deposit in revenue line(s):", fnts);
	    phrase = new Phrase();			
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);			
	    String str2 = invoice.getDeposit_notes();
	    if(str2.equals("")){
		str2 = "---------------------------------------------------------------------------------------------------------------------------------------------------------\n";
		str2 += "---------------------------------------------------------------------------------------------------------------------------------------------------------\n";		
	    }
	    ch = new Chunk(str2, fnts);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);
	    //
	    cell = new PdfPCell(spacePhrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);			
	    ch = new Chunk("Notify the following Parks and Recreation Staff when when payment has been received and deposit made: ", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);
	    //
	    str2 = staff.getFullName()+" ("+staff.getPhone()+", "+staff.getUserid()+"@bloomington.in.gov)";
	    ch = new Chunk(str2, fnts);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);
	    //
	    cell = new PdfPCell(spacePhrase);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    cell.setBorder(Rectangle.NO_BORDER);
	    footTable.addCell(cell);				
	    document.add(footTable);
			
	    document.close();
	    writer.close();
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
	    out = res.getOutputStream();
	    if(out != null){
		baos.writeTo(out);
	    }
	}catch(Exception ex){
	    logger.error(ex);
	}
    }

}






















































