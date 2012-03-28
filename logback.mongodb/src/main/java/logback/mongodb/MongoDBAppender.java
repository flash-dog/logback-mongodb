package logback.mongodb;

import java.util.Date;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import com.mongodb.BasicDBObject;

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
		connectionSource.getDBCollection().insert(logEntry);
	}

	public void setConnectionSource(MongoDBConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

}
