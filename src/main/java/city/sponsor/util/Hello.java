package city.sponsor.web;
import java.io.*;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobDataMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// probably not used

public class Hello implements Job{

    static Logger logger = LogManager.getLogger(Hello.class);
    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public Hello() {
    }
    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with
     * the <code>Job</code>.
     * </p>
     * 
     * @throws JobExecutionException
     *             if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
        throws JobExecutionException {

	String to="", from="",subject="", msg="";
	JobDataMap data = context.getJobDetail().getJobDataMap();
        String _to = data.getString("to");
        String _from = data.getString("from");
        String _subject = data.getString("subject");
        String _msg = data.getString("msg");
	String _file = data.getString("file");
	String _path = data.getString("path");
	if(_to != null) to = _to;
	if(_from != null) from = _from;
	if(_subject != null) subject = _subject;
	if(_msg != null) msg = _msg;
	logger.debug(" ***** ");
	logger.debug("to "+to);
	logger.debug("from "+from);
	logger.debug("subj "+subject);
	logger.debug("msg "+msg);		

    }

}
	
