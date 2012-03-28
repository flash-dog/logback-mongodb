package logback.mongodb;

import java.net.UnknownHostException;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;

/**
 * @author Christian Trutz
 */
public class MongoDBConnectionSource {

	private volatile DBCollection dbCollection = null;

	private String uri = null;

	private String db = null;

	private String collection = null;

	protected DBCollection getDBCollection() {
		DBCollection dbCollectionHelper = dbCollection;
		if (dbCollectionHelper == null) {
			synchronized (this) {
				dbCollectionHelper = dbCollection;
				if (dbCollectionHelper == null) {
					try {
						final Mongo mongo = new Mongo(new MongoURI(uri));
						dbCollection = mongo.getDB(db)
								.getCollection(collection);
						Runtime.getRuntime().addShutdownHook(
								new Thread(new Runnable() {

									@Override
									public void run() {
										mongo.close();
									}
								}, "mongo shutdown"));
					} catch (MongoException mongoException) {
						mongoException.printStackTrace();
					} catch (UnknownHostException unknownHostException) {
						unknownHostException.printStackTrace();
					}
				}
			}
		}
		return dbCollection;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

}
