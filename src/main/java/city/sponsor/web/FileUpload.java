package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.jakarta.servlet6.*;
import org.apache.commons.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;


/**
 * Generates the interface to handle image viewing and uploading.
 * Uses third party package to handle multi part forms.
 * A new version uses Apache commons multipart package, check ImageProc.java
 * class
 *
 */
@WebServlet(urlPatterns = {"/FileUpload"})
public class FileUpload extends TopServlet{

    static final long serialVersionUID = 37L;	
    static Logger logger = LogManager.getLogger(FileUpload.class);
    private static int maxImageSize = 2000000, maxDocSize=5000000;
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    String [] inspIdArr = null;
    String [] inspArr = null;
    String [] typeIdArr = null;
    String [] typeArr = null;
    String currentDay = "";
    int seq = 100;
    public static String[] allmonths = {"\n","JAN","FEB","MAR",
	"APR","MAY","JUN",
	"JUL","AUG","SEP",
	"OCT","NOV","DEC"};

    static final String MONTH_SELECT = "<option>JAN\n" + 
	"<option>FEB\n" + 
	"<option>MAR\n" + 
	"<option>APR\n" + 
	"<option>MAY\n" + 
	"<option>JUN\n" + 
	"<option>JUL\n" + 
	"<option>AUG\n" + 
	"<option>SEP\n" + 
	"<option>OCT\n" + 
	"<option>NOV\n" + 
	"<option>DEC\n" + 
	"</select>";

    /**
     * Generates the main upload or view image form.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	boolean connectDbOk = false, success = true,
	    sizeLimitExceeded = false;
	String saveDirectory ="",file_path="";
	String newFile = "",  cur_path="";
	String action="", date="", load_file="";
	String id="", spon_id = "", notes="", action2="";
	
	String message = "";
	int maxMemorySize = 5000000; // 5 MB , int of bytes
	int maxRequestSize = 5000000; // 5 MB
	String [] vals;
	User user = null;
	HttpSession session = null;
	long sizeInBytes = 0;
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 

	// we need this path for save purpose
	String pc_path = "C:\\webapps\\sponsors\\files\\";
	if(url.indexOf("local") > -1)
	    cur_path = pc_path;
	else
	    cur_path = server_path+"files/";

	
	session = req.getSession(false);

	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		System.err.println(" no user found in session");
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    System.err.println("no session or session timed out");
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	// 
	// we have to make sure that this directory exits
	// if not we create it
	//
	SponFile sponFile = new SponFile(debug);
	String new_path = cur_path;
	Path path = Paths.get(new_path);
	File myDir = new File(new_path);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	FileCleaningTracker tracker = JakartaFileCleaner.getFileCleaningTracker(context);
	DiskFileItemFactory factory = DiskFileItemFactory.builder()
	    .setPath(path)
	    //.setSizeThreshold(maxDocSize)
	    .setBufferSizeMax(maxMemorySize)
	    .get();
	JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);

	// Set overall request size constraint
	upload.setFileSizeMax(maxRequestSize);
	// ServletFileUpload upload = new ServletFileUpload();
	//
	// Set overall request size constraint
	upload.setSizeMax(maxRequestSize);
	//
	String ext = "", old_name="";

	List<DiskFileItem> items = null;
	try{
	    if(JakartaServletDiskFileUpload.isMultipartContent(req)){	    
		items = upload.parseRequest(req);
		Iterator<DiskFileItem> iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = iter.next();
		    if (item.isFormField()) {
			//
			// process form fields
			//
			name = item.getFieldName();
			value = item.getString();
			if (name.equals("id")){  
			    id = value;
			    sponFile.setId(value);
			}
			else if (name.equals("notes")) {
			    notes=value;
			    sponFile.setNotes(value);
			}
			else if (name.equals("spon_id")){ 
			    spon_id = value;
			    sponFile.setSpon_id(value);
			}
			else if (name.equals("load_file")) {
			    load_file =value.replace('+',' ');
			}
			else if(name.equals("action")){
			    action = value;
			}
		    }
		    else {
			//
			// process uploaded item/items
			//
			// String fieldName = item.getFieldName();
			String contentType = item.getContentType();
			// boolean isInMemory = item.isInMemory();
			sizeInBytes = item.getSize();
			String oldName = item.getName();
			String filename = "";
			if (oldName != null) {
			    filename = FilenameUtils.getName(oldName);
			    logger.error(" file "+oldName);
			    String extent = 
				filename.substring(filename.indexOf(".")).toLowerCase();
			    if(extent.equals("")){
				ext = extent;
			    }
			    else{
				if(contentType.endsWith("jpeg"))
				    ext = ".jpg";
				else if(contentType.endsWith("gif"))
				    ext = ".gif";
				else if(contentType.endsWith("png"))
				    ext = ".png";
				else if(contentType.endsWith("application"))
				    ext = ".pdf";
				else if(contentType.endsWith("plain"))
				    ext = ".txt";
				else if(contentType.endsWith("msword"))
				    ext = ".doc";
				else if(contentType.contains("html"))
				    ext = ".html";
				else
				    ext = ".jpg";
			    }
			    //
			    // create the file on the hard drive and save it
			    //
			    if(sizeInBytes > maxDocSize) 
				sizeLimitExceeded = true;
			    if(sizeLimitExceeded){
				message = " File Uploaded exceeds size limits "+
				    sizeInBytes;
				success = false;
			    }
			    else{
				//
				// get a new name
				//
				newFile = sponFile.getFullPath(cur_path, ext, url);
				Path uploadedFile = Paths.get(cur_path+newFile);
				item.write(uploadedFile);
			    }
			}
		    }
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message = "Error communicating with Database "+ex;
	}
	//
    	if(action.equals("Save") && !sizeLimitExceeded){
	    date = Helper.getToday(); 
	}
	Sponsor sponsor = null;
	SponFileList sponFiles = null;
	if(!spon_id.equals("")){
	    sponsor = new Sponsor(debug, spon_id);
	    String back = sponsor.doSelect();
	    if(!back.equals("")){
		message += back;
	    }
	    sponFiles = sponsor.getSponFiles();
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
    	//
	out.println("<script language=javascript>");
	out.println("  function validateForm(){		                 ");
       	out.println("     return true;				         ");
	out.println("	}	         			             ");
	out.println(" </script>		                            ");
    	out.println(" <center><h3>File Upload</h3>");
	//
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color=\"red\">"+message+"</font></h3>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    "ENCTYPE=\"multipart/form-data\" >");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	}
	if(!spon_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"spon_id\" value=\""+spon_id+"\" />");
	}	
	//
	out.println("<table border=\"1\" width=\"80%\">");
	out.println("<tr><td>");
	//
	// 1st block
	//
	out.println("<table width=100%>");
	out.println("<tr><td class=\"left\" colspan=\"2\">"+
		    "<font color=\"green\">"+
		    "To upload a new document to sponsors,<br> "+
		    " Download it to your computer"+
		    " in a specific folder.<br />"+
		    " Sponsors will ReName the file based on "+
		    " current date and will use the same extension. <br />"+
		    " Click on the Browse button to locate this file"+
		    " on your computer. "+
		    " After clicking on Save, a link to the uploaded "+
		    " file will be provided with the new name.<br />"+
		    " Supported document and image files are of type 'pdf','doc','txt' "+
		    " 'html','gif','jpg','png' "+
		    " <br>");
	out.println("</font></td></tr>");
	out.println("<tr><th>File </th><td class=\"left\">"); 
	out.println("<input type=\"file\" name=\"load_file\" "+
		    " size=\"30\"></td></tr>");
	out.println("<tr><th>Notes </th>");
	out.println("<td class=\"left\">"); 
	out.println("<input name=\"notes\" size=\"70\" maxlength=\"90\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">"); 
	out.println("<a href=\""+url+"SponsorServ?id="+id+"\">"+sponsor+
		    "</a></td></tr>");
	//
	if(user.canEdit()){
	    out.println("<tr><td algn=\"right\">  "+
			"<input type=\"submit\" name=\"action\" "+
			"value=\"Save\">"+
			"</td></tr>");
	}
	out.println("</form>");
	if(sponFiles != null && sponFiles.size() > 0){

			
	}
	out.println("</table></td></tr>");
	out.println("</table><br>");
	//
	// send what we have so far
	//
	out.println("</div>");
	out.print("</body></html>");
	out.close();

    }

}






















































