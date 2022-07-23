package demo;

import com.google.gson.Gson;
import demo.model.Event;
import demo.utilities.GenericLogEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class LogsCheck {
	public static final Logger logger = LoggerFactory.getLogger(LogsCheck.class);
	private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

	public static void main(String...args) {
	    if(!checkInput(args)) {
	    	logger.info("Impossible process the input file");
	    	return;
	    }
	    logger.info("Reading file: " + args[0]);
	    readEvents(args[0]);
	    logger.info("Execution completed");
	
	}
	
    //TODO:the map will contain n/2 element in the worse case, it's better to find 
    //another solution for big files when we need a lot of memory
	protected static void readEvents(String path) {
	    Map<String, GenericLogEvent> map = new HashMap<>();
	    Session session = sessionFactory.openSession();
	    session.beginTransaction();
	    
	    try (Stream<String> lines = Files.lines(Paths.get(path))) {
	    	  lines.forEach(line -> process(map,line,session));
	    }catch (IOException e) {
			logger.error("File not found exception: {}",e.getMessage());
		}
	    
	    session.getTransaction().commit();
	    session.close();
	    sessionFactory.close();
	}
	
	//TODO: it's better use another thread for this logic 
	private static void process(Map<String, GenericLogEvent>map, String line, Session session) {
		GenericLogEvent logEvent = new Gson().fromJson(line, GenericLogEvent.class);
    	GenericLogEvent previus = map.putIfAbsent(logEvent.getId(),logEvent);
    	if(previus!= null) {
    		Event event = getEventFromLogs(previus,logEvent);
    		logger.debug(event.toString());
    		//TODO: manage database exceptions
	    	session.persist(event);
	    	map.remove(previus.getId());
    	}	    				    	
    }
	
	private static Event getEventFromLogs(GenericLogEvent event_1, GenericLogEvent event_2) {
		Event event = new Event();
		event.setId(event_1.getId());
		event.setDuration(calculateTime(event_1.getTimestamp(),event_2.getTimestamp()));
		event.setHost(event_1.getHost());
		event.setType(event_1.getType());
		event.setAlert(event.getDuration()>4);
		return event;
	}
	
	private static long calculateTime(long l1, long l2) {
		return l1 > l2 ?  l1-l2 : l2-l1;
	}
	
	private static boolean checkInput(String[] args) {
		if(args == null || args.length == 0) {
			logger.error("Path is empty");
			return false;
		}
		return checkFile(args[0]);
	}
	
	protected static boolean checkFile(String path) {
		try {
	        Path p = Paths.get(path);
			if(!Files.isReadable(p)) {
				logger.error("Impossible read the file");
				return false;
			}
	    } catch (InvalidPathException | NullPointerException ex) {
	    	logger.error("Invalid Path File");
	        return false;
	    }		
	    return true;
	}

}