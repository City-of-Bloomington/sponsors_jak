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

public class OpportList extends ArrayList<Opportunity>{
	
    boolean debug = false;
    static final long serialVersionUID = 49L;	
    static Logger logger = LogManager.getLogger(OpportList.class);
    String name = "", program_area="", date_from="", date_to="",
	season_id="", year="", spon_id="", instructions="", id="",
	event_id="";
	
    int pageSize = 50, pageNumber = 1;
    String sortBy = ""; 
    PageList pages = null;
    int count = 0;
	
    public OpportList(boolean val){
	debug = val;
	pages = new PageList(val);
	pages.setPageSize(pageSize);		
    }
	
    public void setName(String val){
	if(val != null){
	    name = val;
	    pages.addPair("name",val);
	}
    }
    public void setProgram_area(String val){
	if(val != null){
	    program_area = val;
	    pages.addPair("program_area",val);
	}
    }
    public void setSeason_id(String val){
	if(val != null){
	    season_id = val;
	    pages.addPair("season_id",val);
	}
    }
    public void setYear(String val){
	if(val != null){
	    year = val;
	    pages.addPair("year",val);
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
    public void setInstructions(String val){
	if(val != null){
	    instructions = val;
	    pages.addPair("instructions",val);
	}
    }	
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	    pages.addPair("spon_id",val);
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
    public void setId(String val){
	if(val != null){
	    id = val;
	    pages.addPair("id",val);
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
	String qq ="", qo = "", q = "select o.id ";
	String qc = " select count(*) ";
	String qf =	"from spons_opportunities o ";
	if(!name.equals("")){

	    qw += " o.name like ? "; 
	}
	if(!season_id.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.season = ? "; 
	}
	if(!year.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.year = ? "; 
	}
	if(!program_area.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.program_area = ? "; 
	}
	if(!instructions.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.instructions like ? "; 
	}
	if(!event_id.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.event_id = ? "; 
	}				
	if(!date_from.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.start_date >= str_to_date('"+date_from+"','%m/%d/%Y') "; 
	}
	if(!date_to.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " o.start_date <= str_to_date('"+date_to+"','%m/%d/%Y') "; 
	}
	if(!id.equals("")){
	    if(!qw.equals("")) qw += " and ";			
	    qw += " o.id = ? "; 
	}		
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	if(sortBy.equals(""))
	    sortBy = "start_date DESC";
	qo = " order by o."+sortBy;
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
		if(pageSize > 1){
		    if(pageNumber > 1){
			int offset = pageSize*(pageNumber - 1);
			qo += " limit "+offset+","+pageSize;
		    }
		    else{
			qo += " limit "+pageSize;
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
			Opportunity oppt = new Opportunity(debug,
							   rs.getString(1));
			String str = oppt.doSelect();
			if(str.equals(""))
			    this.add(oppt);
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
	    if(!name.equals("")){
		pstmt.setString(jj++, "%"+name+"%");
	    }
	    if(!season_id.equals("")){
		pstmt.setString(jj++, season_id);
	    }
	    if(!year.equals("")){
		pstmt.setString(jj++, year);
	    }
	    if(!program_area.equals("")){
		pstmt.setString(jj++, program_area);
	    }
	    if(!instructions.equals("")){
		pstmt.setString(jj++, "%"+instructions+"%");
	    }
	    if(!event_id.equals("")){
		pstmt.setString(jj++, event_id);
	    }
	    if(!id.equals("")){
		pstmt.setString(jj++, id);
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






















































