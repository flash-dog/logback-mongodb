package logback.mongodb;

import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

/**
 * @author Christian Trutz
 */
public class MongoDBAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private MongoDBConnectionSource connectionSource = null;

	@Override
	protected void append(ILoggingEvent eventObject) {
		BasicDBObject logEntry = new BasicDBObject();
		logEntry.append("message", eventObject.getFormattedMessage());
		logEntry.append("logger", eventObject.getLoggerName());
		logEntry.append("thread", eventObject.getThreadName());
		logEntry.append("timestamp", new Date(eventObject.getTimeStamp()));
		logEntry.append("level", eventObject.getLevel().toString());
        Map<String, String> data = eventObject.getMDCPropertyMap();
        for(String key:data.keySet()){
            logEntry.append(key, data.get(key));
        }
		connectionSource.getDBCollection().insert(logEntry);
//        System.out.println(result.getError());

//        connectionSource.getDBCollection()
	}

	public void setConnectionSource(MongoDBConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

}
