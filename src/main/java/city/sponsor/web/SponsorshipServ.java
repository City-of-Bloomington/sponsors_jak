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
@WebServlet(urlPatterns = {"/SponsorshipServ"})
public class SponsorshipServ extends TopServlet{

    static final long serialVersionUID = 82L;	
    static Logger logger = LogManager.getLogger(SponsorshipServ.class);
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
    
	String id="", spon_id="", oppt_id="";
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
	Sponsorship sponship = new Sponsorship(debug);
	Opportunity oppt = null;
	Sponsor sponsor = null;
	DonBenTypeList benefits = null;
	HashMap<String, String> map = new HashMap<String, String>();
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		sponship.setId(value);
		id = value;
	    }
	    else if (name.equals("oppt_id")) {
		sponship.setOppt_id(value);
	    }
	    else if (name.equals("spon_id")) {
		sponship.setSpon_id(value);
	    }
	    else if (name.equals("details")) {
		sponship.setDetails(value);
	    }
	    else if (name.equals("start_date")) {
		sponship.setStart_date(value);
	    }
	    else if (name.equals("cont_start_date")) {
		sponship.setCont_start_date(value);
	    }
	    else if (name.equals("cont_end_date")) {
		sponship.setCont_end_date(value);
	    }			
	    else if (name.equals("pay_type")) {
		sponship.setPay_type(value);
	    }	
	    else if (name.equals("terms")) {
		sponship.setTerms(value);
	    }
	    else if (name.equals("don_type")) {
		sponship.setDon_type(value);
	    }	
	    else if (name.equals("value")) {
		sponship.setValue(value);
	    }
	    else if (name.equals("spon_level")) {
		sponship.setSpon_level(value);
	    }
	    else if (name.equals("del_bens")) {
		sponship.setDel_bens(vals);
	    }
	    else if (name.equals("ful_bens")) {
		sponship.setFul_bens(vals);
	    }
	    else if (name.equals("notes")) {
		sponship.setNotes(value);
	    }						
	    else if(name.startsWith("ful_date_")){
		if(!value.equals("")){
		    String str = name.substring(9);
		    map.put(str, value);
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
	if(!id.equals("") && action.equals("")){
	    action = "zoom";
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    String back = sponship.doSave();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Saved Successfully ";
		id = sponship.getId();
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String back = sponship.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		if(map.size() > 0){
		    back = sponship.updateBenFlfld_dates(map);
		}
		if(!back.equals("")){
		    message += back;
		}
		message += " Updated Successfully ";
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    sponship.doSelect();
	    oppt_id = sponship.getOppt_id();
	    String back = sponship.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += " Deleted Successfully ";
		sponship = new Sponsorship(debug);
		sponship.setOppt_id(oppt_id);
		id = "";
	    }
	}
	else if(action.equals("zoom")){	
	    //
	    String back = sponship.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	else if(action.startsWith("Generate P")){
	    String back = sponship.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		back = sponship.generatePayments();
		if(!back.equals("")){
		    logger.error(back);
		    message += back;
		    success = false;
		}
	    }
	}
	oppt   = sponship.getOpportunity();
	sponsor = sponship.getSponsor();
	oppt_id = sponship.getOppt_id();
	spon_id = sponship.getSpon_id();
	if(!id.equals("")){
	    benefits = sponship.getBenefits();
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
	out.println("  function doTotal(){	                     ");
	out.println("  with(document.myForm){                        ");
	out.println("    v = value.value;       	                 ");
	out.println("    n = terms.value;       	                 ");
	out.println("    if(v != '' && n != ''){                 ");
	out.println("         if(isNaN(v)){          ");
	out.println("            alert(v+' is not a valid number '); ");
	out.println("            value.focus();                      ");
	out.println("            return;                             ");
	out.println("         }                                          ");
		
	out.println("         if(isNaN(n)){          ");
	out.println("            alert(n+' is not a valid number '); ");
	out.println("            terms.focus();                      ");
	out.println("            return;                             ");
	out.println("        }                                          ");		
	out.println("         totalValue.value = v*n;                 ");
	out.println("     }                                          ");
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
	    out.println("<h2>New Sponsorship</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Sponsorship "+id+"</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<p>* indicate required field</p>");
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"SponsorshipServ?\""+
		    " onsubmit=\"return validateForm()\">");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	if(oppt != null){
	    out.println("<input name=\"oppt_id\" type=\"hidden\" value=\""+oppt_id+"\" />");			
	}
	if(sponsor != null){
	    out.println("<input name=\"spon_id\" type=\"hidden\" value=\""+spon_id+"\" />");			
	}	
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Sponsorship Info</caption>");
	if(oppt != null){
	    out.println("<tr><th>Opportunity</th>");
	    out.println("<td class=\"left\">");
	    out.println("<a href=\""+url+"OpportServ?id="+oppt.getId()+"\">"+oppt+"</a></td></tr>");
	}
	else{
	    out.println("<tr><th><label for=\"opptName\">Opportunity</label></th>");
	    out.println("<td class=\"left\">");	    
	    out.println("<input id=\"opptName\" name=\"opptName\" value=\"\" size=\"50\" />");
	    out.println("<label for=\"oppt_id\">ID</label><input id=\"oppt_id\" name=\"oppt_id\" size=\"4\" value=\""+oppt_id+"\" />");			
	}
	out.println("</td></tr>");
	if(sponsor != null){
	    out.println("<tr><th>Sponsor</th>");
	    out.println("<td class=\"left\">");
	    out.println("<a href=\""+url+"SponsorServ?id="+sponsor.getId()+"\">"+sponsor.getOrgname()+"</a></td></tr>");
	}
	else{
	    out.println("<tr><th><label for=\"sponsorName\">Sponsor</label></th>");
	    out.println("<td class=\"left\">");
	    out.println("<input id=\"sponsorName\" name=\"sponsorName\" value=\"\" size=\"50\" />");
	    out.println("<label for=\"spon_id\">ID</label><input id=\"spon_id\" name=\"spon_id\" size=\"4\" value=\""+spon_id+"\" />");			
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"spon_det\">Details</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"details\" size=\"50\" maxlength=\"50\" "+
		    " value=\""+Helper.replaceSpecialChars(sponship.getDetails())+"\" required=\"required\" id=\"spon_det\" />* ");
	out.println("</td></tr>");				
	out.println("<tr><th><label for=\"spon_total\">Total Sponsorship $</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"totalValue\" size=\"8\" maxlength=\"8\" disabled=\"disabled\" id=\"spon_total\" "+
		    " value=\""+sponship.getTotalValue()+"\" />");
	out.println("</td></tr>");	
	out.println("<tr><th><label for=\"value\">Sponsorship Amount $</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"value\" size=\"8\" maxlength=\"8\" "+
		    " id=\"value\" value=\""+sponship.getValue()+"\" onchange=\"doTotal();\"/>");
	out.println("(per term)</td></tr>");
	out.println("<tr>");
	out.println("<th><label for=\"terms\">Number of Sponsorships (terms)</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"terms\" size=\"2\" maxlength=\"2\" id=\"terms\" "+
		    " value=\""+sponship.getTerms()+"\" onchange=\"doTotal();\"/>");
	out.println("</tr>");
	out.println("<tr><th>Payment Option</th>");
	out.println("<td class=\"left\">");
	String ptype = sponship.getPay_type();
	for(String str:Helper.payTypeArr){
	    String checked="";
	    if(str.equals(ptype)) checked="checked=\"checked\"";
	    out.println("<input type=\"radio\" name=\"pay_type\" value=\""+str+"\" "+checked+" id=\""+str+"\" /><label for=\""+str+"\">"+str+"</label>");
	}
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"start_date\">First Payment Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"start_date\" id=\"start_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+sponship.getStart_date()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"cont_start_date\">Contract Start Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"cont_start_date\" id=\"cont_start_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+sponship.getCont_start_date()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"cont_end_date\">Contract End Date</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"cont_end_date\" id=\"cont_end_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+sponship.getCont_end_date()+"\" />");
	out.println("</td></tr>");		
	out.println("<tr><th><label for=\"don_type\">Sponsorship Type</label></th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"don_type\" id=\"don_type\">");
	out.println("<option value=\"\"></option>");
	for(String str: Helper.donTypeArr){
	    String selected="";
	    if(sponship.getDon_type().equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"spon_level\">Sponsorship Level</label></th>");	
	out.println("<td class=\"left\">");
	out.println("<select name=\"spon_level\" id=\"spon_level\">");
	out.println("<option value=\"\"></option>");
	for(int i=0;i< Helper.sponLevelIdArr.length;i++){
	    String str = Helper.sponLevelIdArr[i];
	    String str2 = Helper.sponLevelArr[i];
	    String selected="";
	    if(sponship.getSpon_level().equals(str)){
		selected="selected=\"selected\"";
	    }
	    out.println("<option value=\""+str+"\" "+selected+">"+str2+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"note\">Misc Notes</label></th>");
	out.println("<td class=\"left\">");
	out.println("<textarea name=\"notes\" id=\"note\" cols=\"60\" rows=\"5\">");
	out.println(Helper.replaceSpecialChars(sponship.getNotes()));
	out.println("</textarea></td></tr>");
	out.println("</table>");
	if(!id.equals("") && benefits != null){
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<tr><td class=\"center title\">");
	    out.println("<caption>Benefits</caption>");
	    out.println("<tr><th>Delete</th><th>Benefit</th><th>Fulfilled</th>"+
			"<th>Fulf. Date</th><th>Notes</th>"+
			"</tr>");
	    int jj=1;
	    for(DonBenType dbt:benefits){
		out.print("<tr><td>"+(jj++));
		out.println("<input type=\"checkbox\" name=\"del_bens\" value=\""+dbt.getId()+"\" /></td>");
		out.println("<td class=\"left\">"+dbt.getName()+"</td>");
		String checked = dbt.isFulfilled()? "checked=\"checked\"":"";
		out.println("<td class=\"left\">");
		out.println("<input type=\"checkbox\" name=\"ful_bens\" value=\""+dbt.getId()+"\" "+checked+" /></td>");
		out.println("<td class=\"left\"><input name=\"ful_date_"+dbt.getId()+"\" id=\"ful_date_"+jj+"\" value=\""+dbt.getFlfld_date()+"\" size=\"10\" /></td>");				
		out.println("<td><a href=\""+url+"ActionServ?ben_id="+dbt.getId()+"&amp;sponship_id="+dbt.getSponship_id()+"\">Add Notes</a>");				
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	}
	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Actions </caption>");
	if(id.equals("")){
	    out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Save\" />");
	    out.println("</td></tr>");
	}
	else{ // Save, Update
	    out.println("<tr>");
	    if(user.canEdit()){
		out.println("<td valign=\"top\"><input "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    if(user.canEdit()){
		out.println("<td valign=\"top\"><input "+
			    "type=\"submit\" name=\"action\" value=\"Delete\" />");
		out.println("</td>");
	    }	    
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"BenefitServ?sponship_id="+id+"'\" "+
			"value=\"Add/Edit Benefits\" />");
	    out.println("</td><tr>");
	    out.println("<tr><td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"ActionServ?sponship_id="+id+"'\" "+
			"value=\"Add Notes\" />");
	    out.println("</td>");
	    out.println("<td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"ExpenseServ?sponship_id="+id+"'\" "+
			"value=\"Add Expenses\" />");
	    out.println("</td></tr>");		
	    if(sponship.getDon_type().equals("monetary")){
		out.println("<tr>");
		if(!sponship.hasPayments()){
		    out.println("<td valign=\"top\"><input type=\"submit\" "+
				"name=\"action\" "+
				"value=\"Generate Payments\" />");
		    out.println("</td>");
		}
		else{
		    out.println("<td valign=\"top\"><input type=\"button\" "+
				"onclick=\"document.location='"+url+"PaymentServ?sponship_id="+id+"'\" "+
				"value=\"New Payment\" />");
		    out.println("</td>");
		    out.println("<td valign=\"top\"><input type=\"button\" "+
				"onclick=\"document.location='"+url+"InvoiceServ?spon_id="+spon_id+"'\" "+
				"value=\"New Invoice\" />");
		    out.println("</td>");	
		}
		out.println("</tr>");
	    }
	    
	    out.println("<tr><td valign=\"top\"><input type=\"button\" "+
			"onclick=\"document.location='"+url+"AgreePrint?sponship_id="+id+"'\" "+
			"value=\"Adv. Agreement\" />");
	    out.println("</td>");
	    //			
	    out.println("<td>");
	    out.println("<input type=\"button\" "+
			" onclick=\"validateDelete2();\" "+
			" value=\"Delete\" />");
	    out.println("</td>");

	    out.println("</tr>");
	    out.println("</table>");
	    out.println("</form>");
	}
	if(!id.equals("")){
	    if(sponship.hasPayments()){
		PaymentList pays = sponship.getPayments();
		if(pays != null && pays.size() > 0){
		    Helper.writePayments(out, pays, url);
		}
	    }
	    if(sponship.hasExpenses()){
		ExpenseList rows = sponship.getExpenses();
		if(rows != null && rows.size() > 0){
		    Helper.writeExpenses(out, rows, url);
		}
	    }
	    if(sponship.hasInvoices()){
		InvoiceList rows = sponship.getInvoices();
		if(rows != null && rows.size() > 0){
		    Helper.writeInvoices(out, rows, url);
		}				
	    }
	    ActionList actl = new ActionList(debug, null, null, id);
	    actl.find();
	    if(actl != null && actl.size() > 0){
		Helper.writeActionsVertical(out, actl, url);
	    }						
	}
	if(!oppt_id.equals("")){
	    SponsorshipList donors = new SponsorshipList(debug, oppt_id, null);
	    String back = donors.find();
	    if(donors.size() > 0){
		Helper.writeSponsorships(out, donors, url);				
	    }
	}
	out.println(Inserts.footer(url));
	out.println("<script>");
	out.println("//<![CDATA[  ");		
	out.println("  $( \"#start_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#cont_start_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#cont_end_date\" ).datepicker("+Inserts.jqDateStr(url)+"); ");	
	if(benefits != null && benefits.size() > 0){
	    for(int j=1;j<=benefits.size();j++){
		out.println("  $( \"#ful_date_"+j+"\" ).datepicker(); ");
	    }
	}
	out.println("                                  ");
	if(sponsor == null){
	    out.println(" $(\"#sponsorName\").autocomplete({ ");
	    out.println("		source: '"+url+"SponService?format=json&type=orgname', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#spon_id\").val(ui.item.id); ");
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}
	if(oppt == null){
	    out.println(" $(\"#opptName\").autocomplete({ ");
	    out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	    out.println("		minLength: 2, ");
	    out.println("		select: function( event, ui ) { ");
	    out.println("			if(ui.item){ ");
	    out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	    out.println("			} ");
	    out.println("		}  ");
	    out.println("	}); ");
	}
	out.println(" //]]>   ");
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






















































