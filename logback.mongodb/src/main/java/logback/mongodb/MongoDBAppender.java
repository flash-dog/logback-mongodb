package logback.mongodb;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Trutz
 */
public class MongoDBAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public static final String UNKNOW_HOST = "unknow host";
    private MongoDBConnectionSource connectionSource = null;
    private boolean startJvmMonitor = false;
    private boolean logToLocal = false;

    private JvmMonitor jvmMonitor = null;
//    private Logger logger = LoggerFactory.getLogger(MongoDBAppender.class);
	@Override
	protected void append(ILoggingEvent eventObject) {
		BasicDBObject logEntry = new BasicDBObject();
		logEntry.append("message", eventObject.getFormattedMessage());
		logEntry.append("logger", eventObject.getLoggerName());
		logEntry.append("thread", eventObject.getThreadName());
		logEntry.append("timestamp", new Date(eventObject.getTimeStamp()));
		logEntry.append("level", eventObject.getLevel().toString());
        logEntry.append("pid", getPid());
        logEntry.append("ip", getIp());
        Map<String, String> data = eventObject.getMDCPropertyMap();
        for(String key:data.keySet()){
            logEntry.append(key, data.get(key));
        }
		connectionSource.getDBCollection().insert(logEntry);
        if(logToLocal){
            System.out.println(eventObject.getFormattedMessage());
        }
	}


    private String getIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return UNKNOW_HOST;
        }
    }
    private String getPid(){
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    @Override
    public void start() {
        if(startJvmMonitor){
            jvmMonitor = JvmMonitor.getInstance(connectionSource, logToLocal);
        }
        super.start();
    }


    @Override
    public void stop() {
        if (!isStarted())
            return;
        if(jvmMonitor != null)
            jvmMonitor.stop();

        // mark this appender as stopped so that Worker can also stop if it is invoking aii.appendLoopOnAppenders
        // and sub-appenders consume the interruption
        super.stop();

    }

	public void setConnectionSource(MongoDBConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

    public void setStartJvmMonitor(boolean startJvmMonitor) {
        this.startJvmMonitor = startJvmMonitor;
    }

    public void setLogToLocal(boolean logToLocal) {
        this.logToLocal = logToLocal;
    }
}
