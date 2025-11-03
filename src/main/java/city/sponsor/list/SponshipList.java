package city.sponsor.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import city.sponsor.model.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; 
/**
 *
 *
 */

public class SponshipList extends ArrayList<Sponsorship>{

    static final long serialVersionUID = 76L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(SponshipList.class);
    String details = "", pay_type="", spon_level="",
	date_from="", date_to="", don_type="", year="",
	value_from="", value_to="", event_id="",
	terms_from="", terms_to="", id="", season_id="", program_area="",
	spon_id="", oppt_id="";

    int pageSize = 50, pageNumber = 1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
    boolean all = false;
	
    public SponshipList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);		
    }
    public SponshipList(boolean val, boolean all){
	debug = val;
	this.all = all;
	pages = new PageList(val);
	pages.setPageSize(pageSize);		
    }	
    public void setId(String val){
	if(val != null){
	    id = val;
	    pages.addPair("id",val);
	}
    }
    public void setSeason_id(String val){
	if(val != null){
	    season_id = val;
	    pages.addPair("season_id",val);
	}
    }
    public void setProgram_area(String val){
	if(val != null){
	    program_area = val;
	    pages.addPair("program_area",val);
	}
    }				
    public void setYear(String val){
	if(val != null){
	    year = val;
	    pages.addPair("year",val);
	}
    }		
    public void setDetails(String val){
	if(val != null){
	    details = val;
	    pages.addPair("details",val);
	}
    }
    public void setPay_type(String val){
	if(val != null){
	    pay_type = val;
	    pages.addPair("pay_type",val);
	}
    }
    public void setDon_type(String val){
	if(val != null){
	    don_type = val;
	    pages.addPair("don_type",val);
	}
    }	
    public void setSpon_level(String val){
	if(val != null){
	    spon_level = val;
	    pages.addPair("spon_level",val);
	}
    }
    public void setValue_from(String val){
	if(val != null){
	    value_from = val;
	    pages.addPair("value_from",val);
	}
    }
    public void setValue_to(String val){
	if(val != null){
	    value_to = val;
	    pages.addPair("value_to",val);
	}
    }	
    public void setTerms_from(String val){
	if(val != null){
	    terms_from = val;
	    pages.addPair("terms_from",val);
	}
    }
    public void setTerms_to(String val){
	if(val != null){
	    terms_to = val;
	    pages.addPair("terms_to",val);
	}
    }
    public void setEvent_id(String val){
	if(val != null){
	    event_id = val;
	    pages.addPair("event_id",val);
	}
    }		
    public void setDate_from(String val){
	if(val != null){
	    date_from = val;
	    pages.addPair("date_from",val);
	}
    }	
    public void setDate_to(String val){
	if(val != null){
	    date_to = val;
	    pages.addPair("date_to",val);
	}
    }
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	    pages.addPair("spon_id",val);
	}
    }
    public void setOppt_id(String val){
	if(val != null){
	    oppt_id = val;
	    pages.addPair("oppt_id",val);
	}
    }
    public void setSortBy(String val){
	if(val != null){
	    sortBy = val;
	    pages.addPair("sortBy",val);
	}
    }
    public void setPageSize(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageSize = Integer.parseInt(val);
	    }
	    catch(Exception ex){}
	    pages.setPageSize(val);
	}
    }
    public void setPageNumber(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageNumber = Integer.parseInt(val);
	    }
	    catch(Exception ex){}		
	    pages.setPageNumber(val);
	}
    }
    public int getCount(){
	return count;
    }	
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq ="", qo = "", q = "select s.id ";
	String qc = " select count(*) ";
	String qf =	"from spons_sponsorships s,spons_opportunities op ";
	boolean payTable = false;// ", spons_payments pp  
	qw += " s.oppt_id=op.id ";
	if(!details.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " s.details like ? "; 
	}
	if(!spon_level.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " s.spon_level = ? "; 
	}
	if(!pay_type.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " s.pay_type = ? "; 
	}	
	if(!spon_id.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " s.spon_id = ? "; 
	}
	if(!oppt_id.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " s.oppt_id = ? "; 
	}	
	if(!date_from.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.start_date >= str_to_date('"+date_from+"','%m/%d/%Y') "; 
	}
	if(!date_to.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.start_date <= str_to_date('"+date_to+"','%m/%d/%Y') "; 
	}
	if(!value_from.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.value >= ? "; 
	}
	if(!value_to.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.value <= ? "; 
	}
	if(!terms_from.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.terms >= ? "; 
	}
	if(!terms_to.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.terms <= ? "; 
	}
	if(!don_type.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.don_type = ? "; 
	}
	if(!id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.id = ? "; 
	}
	if(!event_id.equals("")){
	    if(!qw.equals("")) qw += " and ";						
	    qw += " op.event_id = ? ";
	}
	if(!year.equals("")){
	    qw += " (year(s.start_date) = ? or op.year = ?)"; 
	}
	if(!season_id.equals("")){
	    if(!qw.equals("")) qw += " and ";						
	    qw += " op.season = ? ";
	}
	if(!program_area.equals("")){
	    if(!qw.equals("")) qw += " and ";						
	    qw += " op.program_area = ? ";
	}				
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	if(sortBy.equals(""))
	    sortBy = "details";
	qo = " order by s."+sortBy;
	qq = qc + qf + qw;	
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    back = setPStatement(pstmt);
	    if(back.equals("")){
		rs = pstmt.executeQuery();
		if(rs.next()){
		    count = rs.getInt(1);
		}
	    }
	    if(count > 0){
		if(!all){					
		    if(pageSize > 1){
			if(pageNumber > 1){
			    int offset = pageSize*(pageNumber - 1);
			    qo += " limit "+offset+","+pageSize;
			}
			else{
			    qo += " limit "+pageSize;
			}
		    }
		}
		qq = q+qf+qw+qo;
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		back = setPStatement(pstmt);
		if(back.equals("")){
		    rs = pstmt.executeQuery();
		    while(rs.next()){
			Sponsorship sp = new Sponsorship(debug,
							 rs.getString(1));
			String str = sp.doSelect();
			if(str.equals(""))
			    this.add(sp);
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex+" : "+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    private String setPStatement(PreparedStatement pstmt){
	String back = "";
	try{
	    int jj  = 1;
	    if(!details.equals("")){
		pstmt.setString(jj++, "%"+details+"%");
	    }
	    if(!spon_level.equals("")){
		pstmt.setString(jj++, spon_level);
	    }
	    if(!pay_type.equals("")){
		pstmt.setString(jj++, pay_type);
	    }
	    if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }			
	    if(!value_from.equals("")){
		pstmt.setString(jj++, value_from);
	    }
	    if(!value_to.equals("")){
		pstmt.setString(jj++, value_to);
	    }
	    if(!terms_from.equals("")){
		pstmt.setString(jj++, terms_from);
	    }
	    if(!terms_to.equals("")){
		pstmt.setString(jj++, terms_to);
	    }
	    if(!don_type.equals("")){
		pstmt.setString(jj++, don_type);
	    }
	    if(!id.equals("")){
		pstmt.setString(jj++, id);
	    }
	    if(!event_id.equals("")){
		pstmt.setString(jj++, event_id);
	    }
	    if(!year.equals("")){
		pstmt.setString(jj++, year);
		pstmt.setString(jj++, year);								
	    }
	    if(!season_id.equals("")){
		pstmt.setString(jj++, season_id);
	    }
	    if(!program_area.equals("")){
		pstmt.setString(jj++, program_area);
	    }						
	}catch(Exception ex){
	    back += ex;
	    logger.error(back);
	}
	return back;
    }
    public PageList buildPages(String url){
	if(pages != null && !url.equals("")){
	    pages.setCount(count);
	    pages.setUrl(url);
	    pages.build();
	}
	return pages;
    }
				
}






















































