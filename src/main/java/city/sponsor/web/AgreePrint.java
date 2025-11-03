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
 */
@WebServlet(urlPatterns = {"/AgreePrint"})
public class AgreePrint extends TopServlet{

    static final long serialVersionUID = 16L;	
    static Logger logger = LogManager.getLogger(AgreePrint.class);
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
	String message="", action="", start_date="", end_date="";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
	NumberFormat cf = NumberFormat.getCurrencyInstance();		

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
	Sponsor sponsor = null;
	PaymentList pays = null;
	Map<String, String> balances = null;
	Invoice invoice =  new Invoice(debug);
	Contact contact = null;
	ContactList contacts = null;
	Sponsorship sponship = new Sponsorship(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals, pay_ids = null, phoneArr = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		sponship.setId(value);
	    }	
	}
	if(true){
			
	    String back = sponship.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	sponsor = sponship.getSponsor();
	spon_id = sponsor.getId();
	Opportunity opport = sponship.getOppt();
	if(sponsor != null){
	    contact = sponsor.getFirstContact();
	    contacts = sponsor.getContacts();
	}
	else{
	    sponsor = new Sponsor(debug); // to avoid null;
	    contact = new Contact(debug);
	}
	//
	// delete startNew
	//
	String terms = "";
	String program = "", contactNames="", contactPhones="";
	if(opport != null){
	    Event event = opport.getEvent();
	    if(!event.getName().equals("Unknown")){
		program += event;
	    }
	    else{
		program += opport.getName();
	    }
	    Type season = opport.getSeason();
	    if(season != null){
		program += " "+season;
	    }
	    String year = opport.getYear();
	    if(!year.equals("")){
		program += " "+year;
	    }
	    if(contacts != null){
		for(Contact cont:contacts){
		    if(!contactNames.equals("")) contactNames +=", "; 
		    contactNames = cont.getFullName();
		    String str = cont.getPhone();
		    if(str != null && !str.equals("")){
			contactPhones += ", ";
			contactPhones += str;
		    }										
		}
	    }
	}
	if(true){
	    String start_date2 = sponship.getStart_date2();
	    start_date = opport.getStart_date();
	    end_date = opport.getEnd_date();
	    terms = sponship.getTerms();
	    DonBenTypeList benefits = null;						
	    String phone = sponsor.getPhonesAbbr();
	    if(phone.equals("")){
		phone = contactPhones;
	    }
	    out.println("<html><head><title>Agreement</title></head>");
	    out.println("<body>");
	    out.println("<center>");
	    String all = "";
	    all += "<p align=\"center\">"; 
	    all +="<img src=\""+url+"images/parks_logo_min.jpg\" alt=\"city logo\" /><br /><br />";
	    all += "PARTNERSHIP/ADVERTISING AGREEMENT";
	    all += "</p>";
	    all += "<p align=\"left\">";
	    all += "This Agreement is entered into on <u>"+start_date2+"</u>, between the Bloomington Parks and Recreation Department (\"Parks\") and <u>"+sponsor.getOrgname()+"</u>(\"Business\").</p>";
	    all += "<table width=\"100%\">";
	    all += "<tr><td width=\"4%\" valign=\"top\"><b>1.</b></td>";
	    all += "<td><b>Purpose of Agreement.</b> ";
	    all += "This agreement sets forth the terms under which Parks will provide promotional opportunities to Business at the following Parks facility and/or for the following Parks program: <u>"+program+"</u>";
	    all += "</td></tr>";
	    //
	    all += "<tr><td valign=\"top\"><b>2.</b></td>";
	    all += "<td><b> Identification of Business.</b><br /> ";
	    String str = sponsor.getOrgname();
	    all += "<b>Name:</b> <u>"+str+Helper.fillUpTo(5,str,70,"_")+"</u><br />";
	    str = sponsor.getAddress();
	    all += "<b>Address:</b> <u>"+str+Helper.fillUpTo(8,str,70,"_")+"</u><br />";
	    str = sponsor.getCityStateZip();
	    all += "<b>City/State/Zip:</b><u> "+str+Helper.fillUpTo(15,str,70,"_")+"</u><br />";
	    str = contactNames;
	    all += "<b>Contact name(s):</b><u> "+str+Helper.fillUpTo(15,str,70,"_")+"</u><br />";
	    str = phone;
	    all += "<b>Phone:</b> <u>"+str+ Helper.fillUpTo(6,str,70,"_")+"</u><br />";
	    str = sponsor.getEmail();
	    all += "<b>Email:</b> <u>"+str+Helper.fillUpTo(6,str,70,"_")+"</u><br />";
	    str = sponsor.getWebsite();
	    all += "<b>Web Address:</b> <u>"+str+Helper.fillUpTo(12,str,70,"_")+"</u><br />";
	    all += "<br /></td></tr>";
	    //
	    all += "<tr><td valign=\"top\"><b>3.</b></td>";
	    all += "<td><b> Terms of Agreement and Payment.</b><br /> ";
	    all += "This agreement shall be in full force and effect from <u>";
	    all += start_date+"</u> to <u>"+end_date+"</u><br />";
	    str = sponship.getTotalValue();
	    double cost = 0.;
	    try{
		cost = Double.parseDouble(str);
	    }catch(Exception ex){};
	    str = ""+cf.format(cost);
	    all += "<b>Cost:</b> <u>"+str+Helper.fillUpTo(4,str,70,"_")+"</u><br />";
	    str = sponship.getTerms();
	    all += "<b>Payment Terms:</b> <u>"+str+Helper.fillUpTo(14,str,70,"_")+"</u><br />";
	    all += "<b>Special remittance instructions: </b>";
	    all += "<br /></td></tr>";
	    //
	    benefits = sponship.getBenefits();						
	    all += "<tr><td><b>4.</b></td><td><b>Partner Benefits</b>";
	    all += "</td></tr>";
	    if(benefits != null && benefits.size() > 0){
		all += "<tr><td></td><td><table border=\"0\">";
		all += "<tr><td><ol>";
		for(DonBenType dbt:benefits){
		    all += "<li>"+dbt.getName()+"</li>";
		}
		all += "</ol></td></tr></table></td></tr>";
	    }
	    //
	    all += "<tr><td valign=\"top\"><b>5.</b></td>";
	    all += "<td><b>General provisions.</b><br />";
	    all += "a. Parks reserves the right to approve all advertising displayed under this Agreement to ensure conformance with its policies and with community and business standards. Advertisements for alcoholic beverage products, firearms, or tobacco products will not be accepted. Parks may restrict or refuse contracts with advertisers when it deems that such advertisement would be out of place or would detract from the ambiance of the Parks facility, program, or venue under consideration for the placement of said advertisement.<br />";
	    all += "b. This agreement may only be cancelled by mutual written agreement of the parties.<br /> ";
			
	    all += "</td></tr></table>";
	    //
	    all += "<table width=\"100%\" border=\"0\">";
	    all += "<tr><td align=\"center\"><b>BUSINESS</b></td>";
	    all += "<td align=\"left\"><b>BLOOMINGTON PARKS AND RECREATION</b></td></tr>";
	    all += "<tr><td>&nbsp;</td></tr>";
	    all += "<tr><td>By:_______________________________</td><td>By___________________________________</td></tr>";
	    all += "<tr><td>&nbsp;</td><td>Paula McDevitt, Director</td></tr>";
	    all += "<tr><td>___________________________________</td></tr>";
	    all += "<tr><td>Printed name and title</td></tr>";
	    all += "</table>";
	    all += "</center>";
	    //
	    // first page
	    //
	    out.println(all);			
			
	}
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































