package city.sponsor.web;
import java.net.URI;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import city.sponsor.util.*;

public class TopServlet extends HttpServlet {
    static String url = "",url2="", server_path="";
    static boolean debug = false;
    static boolean activeMail = false;
    static String emailStr="@bloomington.in.gov";
    static String cityStateZip = "Bloomington, IN 47402",
	pobox="848", phones="(812)349-3739", sender="";
    static String defaultAttention = "";
    static String endpoint_logout_uri = "";
    // for now 
    static Configuration config = null;
    static Logger logger = LogManager.getLogger(TopServlet.class);
    static ServletContext context = null;
    public void init(ServletConfig conf){
	try{
	    context = conf.getServletContext();
	    url = context.getInitParameter("url");
	    url2 = context.getInitParameter("url2");
	    server_path = context.getInitParameter("server_path");
	    String str = context.getInitParameter("debug");
	    if(str != null && str.equals("true")) debug = true;
	    str = context.getInitParameter("activeMail");
	    if(str != null && str.equals("true")) activeMail = true;	    
	    str = context.getInitParameter("emailStr");
	    if(str != null && !str.isEmpty()) emailStr = "@"+str;
	    str = context.getInitParameter("cityStateZip");
	    if(str != null && !str.isEmpty()) cityStateZip = str;
	    str = context.getInitParameter("pobox");
	    if(str != null && !str.isEmpty()) pobox = str;
	    str = context.getInitParameter("phones");
	    if(str != null && !str.isEmpty()) phones = str;
	    str = context.getInitParameter("attention");
	    if(str != null) defaultAttention = str;
	    str = context.getInitParameter("sender");
	    if(str != null) sender=str;
	    	    str = context.getInitParameter("endpoint_logout_uri");
	    if(str != null)
		endpoint_logout_uri = str;
	    String username = context.getInitParameter("adfs_username");
	    String auth_end_point = context.getInitParameter("auth_end_point");
	    String token_end_point = context.getInitParameter("token_end_point");
	    String callback_uri = context.getInitParameter("callback_uri");
	    String client_id = context.getInitParameter("client_id");
	    String client_secret = context.getInitParameter("client_secret");
	    String scope = context.getInitParameter("scope");
	    String discovery_uri = context.getInitParameter("discovery_uri");
	    config = new
		Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, username);
	    // System.err.println(config.toString());
	}catch(Exception ex){
	    System.err.println(" top init "+ex);
	    logger.error(" "+ex);
	}
    }

}
