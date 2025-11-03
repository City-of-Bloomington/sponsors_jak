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
 *
 */
@WebServlet(urlPatterns = {"/BenefitServ"})
public class BenefitServ extends TopServlet{

    static final long serialVersionUID = 20L;	
    static Logger logger = LogManager.getLogger(BenefitServ.class);
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
    
	String sponship_id="";
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
	String bname1 = "", bname2 = "", bname3 ="", bname4="", bname5="";
	String b1 = "", b2 = "", b3 ="", b4="", b5="";
	String bf1 = "", bf2 = "", bf3 ="", bf4="", bf5="";
	String b_date1 = "", b_date2 = "", b_date3 ="", b_date4="", b_date5="";	
	Sponsorship sponship = new Sponsorship(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals, del_bens = null, ful_bens = null;
	HashMap<String, String> map = new HashMap<String, String>();
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		sponship.setId(value);
		sponship_id = value;
	    }
	    else if (name.equals("bname1")) {
		bname1 = value;
	    }
	    else if (name.equals("bname2")) {
		bname2 = value;
	    }
	    else if (name.equals("bname3")) {
		bname3 = value;
	    }
	    else if (name.equals("bname4")) {
		bname4 = value;
	    }
	    else if (name.equals("bname5")) {
		bname5 = value;
	    }
	    else if (name.equals("b1")) {
		b1 = value;
	    }
	    else if (name.equals("b2")) {
		b2 = value;
	    }
	    else if (name.equals("b3")) {
		b3 = value;
	    }
	    else if (name.equals("b4")) {
		b4 = value;
	    }
	    else if (name.equals("b5")) {
		b5 = value;
	    }
	    else if (name.equals("bf1")) {
		bf1 = value;
	    }
	    else if (name.equals("bf2")) {
		bf2 = value;
	    }
	    else if (name.equals("bf3")) {
		bf3 = value;
	    }
	    else if (name.equals("bf4")) {
		bf4 = value;
	    }
	    else if (name.equals("bf5")) {
		bf5 = value;
	    }
	    else if (name.equals("b_date1")) {
		b_date1 = value;
	    }
	    else if (name.equals("b_date2")) {
		b_date2 = value;
	    }
	    else if (name.equals("b_date3")) {
		b_date3 = value;
	    }
	    else if (name.equals("b_date4")) {
		b_date4 = value;
	    }
	    else if (name.equals("b_date5")) {
		b_date5 = value;
	    }				
	    else if (name.equals("del_bens")) {
		del_bens = vals;
	    }
	    else if (name.equals("ful_bens")) {
		ful_bens = vals; // id of the checked boxes
	    }
	    else if(name.startsWith("ful_date_")){
		if(!value.equals("")){
		    String str = name.substring(9);
		    map.put(str,value);
		}
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(action.equals("Submit") && user.canEdit()){
	    //
	    String back = "";
	    if(del_bens != null){
		back = sponship.deleteBenefits(del_bens);
	    }
	    if(ful_bens != null){
		sponship.setFul_bens(ful_bens);
		back += sponship.updateFlfldBenefits();
	    }
	    if(map.size() > 0){
		back += sponship.updateBenFlfld_dates(map);
	    }
	    back += sponship.addBenefit(b1, bname1, bf1, b_date1);
	    back += sponship.addBenefit(b2, bname2, bf2, b_date2);
	    back += sponship.addBenefit(b3, bname3, bf3, b_date3);
	    back += sponship.addBenefit(b4, bname4, bf4, b_date4);
	    back += sponship.addBenefit(b5, bname5, bf5, b_date5);
	    if(!back.equals("")){
		message += back; // some of benefits may already been added
	    }
	}
	Sponsor sponsor = null;
	Opportunity opport = null;
	DonBenTypeList benefits = null;
	if(true){
	    String back = sponship.doSelect();
	    if(back.equals("")){
		sponsor = sponship.getSponsor();
		opport = sponship.getOpportunity();
		benefits = sponship.getBenefits();
	    }
	    else{
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
	out.println("<h2> Sponsorship Benefits</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"BenefitServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Benefits Info</legend>");
	if(!sponship_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"sponship_id\" value=\""+sponship_id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	    for(int jj=1;jj<6;jj++){
		out.println("<input type=\"hidden\" id=\"b"+jj+"\" name=\"b"+jj+"\" value=\"\" />");
	    }
	}
	out.println("<table border=\"1\" width=\"90%\">"); 
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");          
	out.println("<tr><td class=\"center title\">");
	out.println("Benefits Info</td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");           
	out.println("<tr><th>Opportunity</th>");
	out.println("<td class=\"left\" colspan=\"3\">");
	if(opport != null){
	    out.println("<a href=\""+url+"OpportServ?id="+opport.getId()+"\">"+
			Helper.replaceSpecialChars(opport.getName())+"</a>");
	}
	out.println("</td></tr>");
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\" colspan=\"3\">");
	if(sponsor != null){
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+sponsor.getOrgname()+"</a>");
	}
	out.println("</td></tr>");
	out.println("<tr><th>Sponsorship</th>");
	out.println("<td class=\"left\" colspan=\"3\">");
	out.println("<a href=\""+url+"SponsorshipServ?id="+sponship.getId()+"\">");
	out.println(Helper.replaceSpecialChars(sponship.getDetails()));
	out.println("</a></td></tr>");
	out.println("<tr><th>Season</th>");
	out.println("<td class=\"left\">");
	Season season = opport.getSeason();
	if(season != null){
	    out.println(season);
	}
	out.println("</td>");
	out.println("<th>Year</th><td class=\"left\">"+opport.getYear());
	out.println("</td></tr>");
	out.println("<tr>");
	out.println("</table></td></tr>"); 
	out.println("</table></td></tr>"); 
	// benefit table
	out.println("<table width=\"90%\" border=\"1\">"); 
	int njj = 0;
	if(benefits != null){
	    out.println("<tr><td class=\"center title\">");
	    out.println("Current Benefits</td></tr>");
	    out.println("<tr><td><table width=\"100%\">"); 
	    out.println("<tr><th>#</th><th>Benefit</th><th>Fulfilled</th>"+
			"<th>Fulf. Date</th><th>Notes</th></tr>");
	    int jj=1;
	    for(DonBenType dbt:benefits){
		out.print("<tr><td>"+jj);
		out.println("<input type=\"checkbox\" name=\"del_bens\" value=\""+dbt.getId()+"\" /></td>");
		out.println("<td class=\"left\">"+dbt.getName()+"</td>");
		String checked = dbt.isFulfilled()?"checked=\"checked\"":"";
		out.println("<td class=\"left\"><input type=\"checkbox\" name=\"ful_bens\" value=\""+dbt.getId()+"\" "+checked+" /></td>");
		out.println("<td class=\"left\"><input name=\"ful_date_"+dbt.getId()+"\" id=\"ful_date_"+jj+"\" value=\""+dbt.getFlfld_date()+"\" size=\"10\" /></td>");
		out.println("<td><a href=\""+url+"ActionServ?ben_id="+dbt.getId()+"&amp;sponship_id="+dbt.getSponship_id()+"\">Add Notes</a>");					
		out.println("</td></tr>");
		jj++;
	    }
	    njj = benefits.size();
	    out.println("</table></td></tr>"); 
	}
	out.println("<tr><td class=\"center title\">");
	out.println("Add Benefits (five at a time)</td></tr>");
	out.println("<tr><td><table width=\"100%\">");
	out.println("<tr><th>#</th><th>Benefit</th><th>Fulfilled</th>"+
		    "<th>Fulf. Date</th></tr>");

	for(int jj=1;jj<6;jj++){
	    out.println("<tr><td>"+(njj+jj)+"</td><td class=\"left\">"+
			"<input type=\"text\" name=\"bname"+jj+"\" id=\"bname"+jj+"\" size=\"70\" maxlength=\"150\" /></td>");
	    out.println("<td class=\"left\">"+
			"<input type=\"checkbox\" name=\"bf"+jj+"\" id=\"bf"+jj+"\" /></td>");
	    out.println("<td class=\"left\">"+
			"<input type=\"text\" name=\"b_date"+jj+"\" id=\"b_date"+jj+"\" size=\"10\" maxlength=\"10\" /></td>");		
	}
	out.println("</table></td></tr>"); 
	if(user.canEdit()){
	    out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Submit\" />");
	    out.println("</td></tr>");
	}
	out.println("</table>");  
	out.println("</fieldset>");			
	out.println("</form>");
	out.println(Inserts.footer(url));
	out.println("<script>");
	if(benefits != null && benefits.size() >0){
	    for(int jj=1;jj <= benefits.size();jj++){
		out.println("  $(\"#ful_date_"+jj+"\" ).datepicker(); ");
	    }
	}
	for(int jj=1;jj<6;jj++){
	    out.println("  $(\"#b_date"+jj+"\" ).datepicker(); ");
	    out.println("  ");
	    out.println(" $(\"#bname"+jj+"\").autocomplete({ ");
	    out.println("		source: '"+url+"BenefitService?format=json&type=name', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#b"+jj+"\").val(ui.item.id); ");
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}
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






















































