package logback.mongodb;



import com.mongodb.BasicDBObject;
import com.sun.management.OperatingSystemMXBean;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.ManagementFactory;

import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: Hill.Hu
 */
public class JvmMonitor {
	private static JvmMonitor uniqueInstance = null;

//    private Logger logger = LoggerFactory.getLogger(JvmMonitor.class);
    private long lastProcessCpuTime = 0;
    private long lastUptime = 0;
    public static final int DEFAULT_REFRESH_SECONDS = 60;
    private MongoDBConnectionSource connectionSource;
    private boolean logToLocal = false;
    ScheduledExecutorService executorService = null;
    public synchronized static  JvmMonitor getInstance(MongoDBConnectionSource connectionSource, int periodSeconds, boolean logToLocal){
        if(uniqueInstance==null)
    		uniqueInstance=new JvmMonitor(connectionSource, periodSeconds, logToLocal);
    	return uniqueInstance;
    }
    
    public synchronized static  JvmMonitor getInstance(MongoDBConnectionSource connectionSource, boolean logToLocal ){
    	if(uniqueInstance==null)
    		uniqueInstance=new JvmMonitor(connectionSource, logToLocal);
    	return uniqueInstance;
    }
    
    private JvmMonitor(MongoDBConnectionSource connectionSource, boolean logToLocal ) {
        this(connectionSource, DEFAULT_REFRESH_SECONDS, logToLocal);
    }

    private JvmMonitor(MongoDBConnectionSource connectionSource, int periodSeconds, boolean logToLocal ) {
        this.connectionSource = connectionSource;
        this.logToLocal = logToLocal;
//        logger.info("jvm monitor start  ...");
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                record();
            }

        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }

    public void stop(){
        executorService.shutdown();
    }

    public void record() {
        Date now = new Date();
        String message = "memoryUsed=" + getMemoryUsed() + "k "
                + " cpuUsed=" + getCpu() + " threadCount=" + getThreadCount();
        BasicDBObject logEntry = new BasicDBObject();
        logEntry.append("message", message);
        logEntry.append("timestamp", now);
        logEntry.append("level", "info");
        logEntry.append("pid", getPid());
        logEntry.append("ip", getIp());
        logEntry.append("className", "org.log4mongo.contrib.JvmMonitor");
        connectionSource.getDBCollection().insert(logEntry);
        if(logToLocal){
            System.out.println(String.format("%s %s", now, message));
        }

    }
    public static final String UNKNOW_HOST = "unknow host";
    private String getIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return UNKNOW_HOST;
        }
    }
    private String getPid(){
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
    }


    protected int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    protected long getMemoryUsed() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024);
    }

    protected double getCpu() {
        OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        RuntimeMXBean runbean = java.lang.management.ManagementFactory
                .getRuntimeMXBean();
        long uptime = runbean.getUptime();
        long processCpuTime = osbean.getProcessCpuTime();
        //cpu count
        int processors = osbean.getAvailableProcessors();
        //uptime in milliseconds ,and    processCpuTime in nao seconds
        double cpu = (processCpuTime - lastProcessCpuTime) / ((uptime - lastUptime) * 10000f * processors);
        lastProcessCpuTime = processCpuTime;
        lastUptime = uptime;
        return (int) cpu;  //
    }

}
