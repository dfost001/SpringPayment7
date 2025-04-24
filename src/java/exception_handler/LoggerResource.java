package exception_handler;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
//import java.util.logging.XMLFormatter;
import java.util.logging.StreamHandler;

/*
 * FileHandler will create the file, directory must exist
*/
public class LoggerResource {
	
	private static FileHandler fh = null;
	private static StreamHandler strm = null;
	private static final String PATTERN = "%h/myLogs/Spring5/spring.log";
	private static final Integer BYTE_LIMIT = 10000;
	private static final Integer FILE_COUNT = 1;
	private static final Boolean APPEND = true;
	
	private static void initFileHandler() throws SecurityException, IOException{			  	   
		
		    fh = new FileHandler(PATTERN,BYTE_LIMIT,FILE_COUNT,APPEND);
		    fh.setFormatter(new SimpleFormatter());		
	}
	
	@SuppressWarnings("rawtypes")
	public static Logger produceLogger(String cl) {

		
		Logger logger = Logger.getLogger(cl);
		String exception = "Error creating FileHandler ";

		try {
			if (fh == null)
				initFileHandler();
		} catch (SecurityException e) {

			strm = new StreamHandler(System.out, new SimpleFormatter());
			exception += "Security Exception " + e.getMessage();

		} catch (IOException io) {
			strm = new StreamHandler(System.out, new SimpleFormatter());
			exception += "IOException " + io.getMessage();

		}
		if (fh != null)
			logger.addHandler(fh);
		else {
			logger.addHandler(strm);
			System.out.println(exception);
		}
		return logger;
	}
        
          public static Logger createFileHandler(String pathToFile, Class<?> clz) {
              
              Logger logger = Logger.getLogger(clz.getCanonicalName());
              
              FileHandler myHandler = null;
              
              StreamHandler strmHandler = null;
              
              String ehr = null;
              
              try {
                  myHandler = new FileHandler(pathToFile,BYTE_LIMIT,FILE_COUNT,APPEND);
		  myHandler.setFormatter(new SimpleFormatter());
              } catch (IOException io) {
                  ehr = "LoggerResource#createFileHandler: Error creating log file: " + io.getMessage();
                  strmHandler = new StreamHandler(System.out, new SimpleFormatter());
              }
              
              if (myHandler != null)
			logger.addHandler(myHandler);
	      else {
			logger.addHandler(strmHandler);
			System.out.println(ehr);
		}
	     return logger;
       
          }
        
        public static void flush(Logger logger) {
            
            Handler[] handlers = logger.getHandlers();
            
            boolean isNull = handlers == null;
            
            if(isNull)
                System.out.println("LoggerResource#flush: handlers obtained=null");
            else {
                for(Handler handler : handlers)
                    handler.flush();
                System.out.println("LoggerResource#flush: handlers obtained=" + handlers.length);
            }
        }

}
