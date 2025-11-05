package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem; // 
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
@WebServlet(urlPatterns = {"/SponFileServ"})
public class SponFileServ extends TopServlet{

    static final long serialVersionUID = 92L;	
    static Logger logger = LogManager.getLogger(SponFileServ.class);
    int maxImageSize = 2000000, maxDocSize=5000000;
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
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
	String newFile = "";
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
	// String pc_path = "c:\\tomcat\\webapps\\sponsors\\files\\";
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
	String new_path = server_path;
	Path path = Paths.get(new_path);
	File myDir = new File(new_path);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
// Create a factory for disk-based file items
	FileCleaningTracker tracker = JakartaFileCleaner.getFileCleaningTracker(context);
	DiskFileItemFactory factory = DiskFileItemFactory.builder()
	    .setPath(path)
	    //.setSizeThreshold(maxDocSize)
	    .setBufferSizeMax(maxMemorySize)
	    .get();
	
	//
	// Set factory constraints
	//
	// if not set will use system temp directory
	// factory.setRepository(fileDirectory); 
	//
	// Create a new file upload handler
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
	String content_type = req.getContentType();
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
			// String mimType = Magic.getMagicMatch(item.get(), false).getMimeType();
			// System.err.println(" type "+mimType);
			//
			// process uploaded item/items
			//
			// String fieldName = item.getFieldName();
			String contentType = item.getContentType();
			// boolean isInMemory = item.isInMemory();
			sizeInBytes = item.getSize();
			String oldName = item.getName();
			String filename = "";
			// 
			logger.debug("file "+oldName);
			if (oldName != null && !oldName.equals("")) {
			    filename = FilenameUtils.getName(oldName);
			    old_name = filename;
			    String extent = filename.lastIndexOf(".") > -1?
				filename.substring(filename.indexOf(".")).toLowerCase():"";
			    if(!extent.equals("")){
				ext = extent;
			    }
			    else{
				// System.err.println(contentType);
				if(contentType.endsWith("jpeg"))
				    ext = ".jpg";
				else if(contentType.endsWith("gif"))
				    ext = ".gif";
				else if(contentType.endsWith("png"))
				    ext = ".png";
				else if(contentType.endsWith("x-download"))
				    ext = ".pdf";
				else if(contentType.endsWith("plain"))
				    ext = ".txt";
				else if(contentType.endsWith("ms-word"))
				    ext = ".doc";
				else if(contentType.contains("html"))
				    ext = ".html";
				else{
				    // ext = ".jpg";
				    message =" This file has no extension, please turn on file extention in your computer ";
				    success = false;
				}
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
			    else if(success){
				//
				// get a new name
				//
				sponFile.setOldName(old_name);
				sponFile.composeName(ext);
				newFile = sponFile.getName();
				if(!newFile.equals("")){
				    saveDirectory = sponFile.getFullPath(server_path,ext, url2);
				    Path uploadedFile = Paths.get(saveDirectory+newFile);
				    item.write(uploadedFile);
				}
				else{
				    message = "Error: no file name assigned ";
				    success = false;
				}
			    }
			}
		    }
		}
	    }
	    else{
		Enumeration<String> values = req.getParameterNames();
		while (values.hasMoreElements()){
		    name = values.nextElement().trim();
		    vals = req.getParameterValues(name);
		    if(vals == null) continue;
		    value = vals[vals.length-1].trim();	
		    if (name.equals("id")){
			id = value;
			sponFile.setId(value);
		    }
		    else if(name.equals("spon_id")){
			spon_id = value;
			sponFile.setSpon_id(value);
		    }
		    else if(name.equals("notes")){
			sponFile.setNotes(value);
		    }
		    else if(name.equals("action")){
			action = value;
		    }	
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message += ex;
	}
	//
    	if(action.equals("Save") && !sizeLimitExceeded){
	    date = Helper.getToday();
	    String back = sponFile.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    //
	    // to prepare a new upload
	    //
	    sponFile = new SponFile(debug);
	    sponFile.setSpon_id(spon_id);
	}
	if(action.equals("Update")){
	    String back = sponFile.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	else if(action.equals("Delete")){
	    String back = sponFile.doDelete();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		id="";
	    }
	}	
	else if(action.equals("download")){
	    String back = sponFile.doSelect();
	    String filename = sponFile.getName();
	    String filePath = sponFile.getPath(server_path, url);
	    filePath += filename;
	    doDownload(req, res, filePath, sponFile);
	    return;
	}
	else if(action.equals("") && !id.equals("")){
	    String back = sponFile.doSelect();
	    spon_id = sponFile.getSpon_id();
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
	out.println("  function validateForm(){		         ");
       	out.println("     return true;				         ");
	out.println("	}	         			             ");
	out.println(" </script>		                         ");
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
	if(id.equals("")){
	    out.println("<tr><td class=\"left\" colspan=\"2\">"+
			"<font color=\"green\">"+
			"To upload a new document to sponsors,<br> "+
			" Download it to your computer"+
			" in a specific folder.<br />"+
			" Sponsors will ReName the file based on "+
			" current date and will use the same extension. <br />"+
			" Click on the Browse button to locate this file"+
			" on your computer. "+
			" After clicking on Save.<br />. A link to the uploaded "+
			" file will be provided with the new name.<br />"+
			" Supported document and image files are of type 'pdf','doc','txt' "+
			" 'html','gif','jpg','png' "+
			" <br /><br />");
	    out.println("</font></td></tr>");
	}
	else{
	    out.println("<tr><th>Date </th><td class=\"left\">"+sponFile.getDate()+"</td></tr>");
	    out.println("<tr><th>File </th><td class=\"left\">Click here <a href=\""+url+"SponFileServ?id="+id+"&amp;action=download\"> "+sponFile.getOldName()+"</a> to download </td></tr>");
	    out.println("<tr><th>New Name</th><td>"+sponFile.getName()+"</td></tr>");
	    out.println("<tr><td colspan=\"2\">If you upload the file after download, it will replace the old one.</td></tr>");
	}
	out.println("<tr><th>File </th><td class=\"left\">"); 
	out.println("<input type=\"file\" name=\"load_file\" "+
		    " size=\"30\"></td></tr>");
	out.println("<tr><th>Notes </th>");
	out.println("<td class=\"left\">"); 
	out.println("<input name=\"notes\" size=\"70\" maxlength=\"90\" value=\""+sponFile.getNotes()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Sponsor</th>");
	out.println("<td class=\"left\">"); 
	out.println("<a href=\""+url+"SponsorServ?id="+spon_id+"\">"+sponsor+
		    "</a></td></tr>");
	out.println("</table></td></tr>");
	//
	if(user.canEdit()){
	    if(id.equals("")){
		out.println("<tr><td class=\"right\">  "+
			    "<input type=\"submit\" name=\"action\" "+
			    "value=\"Save\">"+
			    "</td></tr>");
	    }
	    else{
		out.println("<tr><td><table width=\"50%\"><tr>");
				
		out.println("<td class=\"right\">  "+
			    "<input type=\"submit\" name=\"action\" "+
			    "value=\"Update\">"+
			    "</td>");
		out.println("<td class=\"right\">  "+
			    "<input type=\"submit\" name=\"action\" "+
			    "onclick=\"validateDelete();\" "+
			    "value=\"Delete\">"+
			    "</td>");
		out.println("</table></td></tr>");				
	    }
	}
	out.println("</form>");
	if(sponFiles != null && sponFiles.size() > 0){
	    out.println("<tr><td class=\"center\"><table width=\"80%\">");
	    out.println("<caption>Uploaded Files</caption>");
	    out.println("<tr><th>Date</th><th>File</th><th>Notes</th></tr>");
	    for(SponFile sp:sponFiles){
		out.println("<tr>");
		out.println("<td align=\"left\"><a href=\""+url+"SponFileServ?id="+sp.getId()+"\">"+sp.getDate()+"</a></td>");
		out.println("<td align=\"left\">"+sp.getOldName()+"</td>");
		out.println("<td align=\"left\">"+sp.getNotes()+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table></td></tr>");
	}
	out.println("</table><br>");
	//
	// send what we have so far
	//
	out.println("</div>");
	out.print("</body></html>");
	out.close();

    }

    void doDownload(HttpServletRequest request,
		    HttpServletResponse response,
		    String inputFile,
		    SponFile sponFile){
		
	BufferedInputStream input = null;
	BufferedOutputStream output = null;
	try{
	    //
	    // Decode the file name (might contain spaces and on) and prepare file object.
	    // File file = new File(filePath, URLDecoder.decode(inspFile, "UTF-8"));
	    File file = new File(inputFile);
	    // Check if file actually exists in filesystem.
	    if (file == null || !file.exists()) {
		// Do your thing if the file appears to be non-existing.
		// Throw an exception, or send 404, or show default/warning page, or just ignore it.
		response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
		return;
	    }
	    //
	    // Get content type by filename.
	    String contentType = context.getMimeType(file.getName());
	    //
	    // To add new content types, add new mime-mapping entry in web.xml.
	    if (contentType == null) {
		contentType = "application/octet-stream";
	    }
	    //			
	    // Init servlet response.
	    if(response != null){
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType(contentType);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + (sponFile.getOldName().isEmpty()?sponFile.getName():sponFile.getOldName()) + "\"");
		// Prepare streams.
		//
		// Open streams.
		input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
		output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
		// Write file contents to response.
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int length;
		while ((length = input.read(buffer)) > 0) {
		    output.write(buffer, 0, length);
		}
	    }
	    else{
		System.err.println(" response is null ");
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
        } finally {
	    close(output);
            close(input);
        }
    }	
    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}






















































