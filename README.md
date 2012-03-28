logback.mongodb
===============

This project contains a "Logback":http://logback.qos.ch/ "appender":http://logback.qos.ch/manual/appenders.html for "mongoDB":http://www.mongodb.org/.

Configuration
-------------

If you add 

&lt;appender name="MONGO" class="logback.mongodb.MongoDBAppender"&gt;<br/>
&nbsp;&nbsp;&lt;connectionSource class="logback.mongodb.MongoDBConnectionSource"&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;uri&gt;mongodb://localhost&lt;/uri&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;db&gt;logdb&lt;/db&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;collection&gt;logs&lt;/collection&gt;
&nbsp;&nbsp;&lt;/connectionSource&gt;<br/>
&lt;/appender&gt;<br/>

p. into your @logback.xml@ or @logback-test.xml@ file, Logback will connect to @mongodb://localhost@ server and append your logs into @logdb@ mongo database using the @logs@ collection. Please see also "mongoDB connection documentation":http://www.mongodb.org/display/DOCS/Connections for more and detailed infos on mongo URIs.

Building
--------
mvn clean install

To use the Logback.MongoDB Configure the appender as above, however you may find it easier to configure using the port 127.0.0.1 instead of using localhost.

logback.xml
-----------
&lt;appender name="MONGO" class="logback.mongodb.MongoDBAppender"&gt;<br/>
&nbsp;&nbsp;&lt;connectionSource class="logback.mongodb.MongoDBConnectionSource"&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;uri&gt;mongodb://127.0.0.1&lt;/uri&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;db&gt;logdb&lt;/db&gt;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&lt;collection&gt;logs&lt;/collection&gt;<br/>
&nbsp;&nbsp;&lt;/connectionSource&gt;<br/>
&lt;/appender&gt;<br/>

...

&lt;root level="trace"&gt;<br/>
&nbsp;&nbsp;&lt;appender-ref ref="console"/&gt;<br/>
&nbsp;&nbsp;&lt;appender-ref ref="MONGO"/&gt;<br/>
&lt;/root&gt;<br/>


Maven Dependency
----------------
Add to your project's pom.xml<br/>
&lt;dependency&gt;<br/>
&nbsp;&nbsp;&lt;groupId&gt;logback.mongodb&lt;/groupId&gt;<br/>
&nbsp;&nbsp;&lt;artifactId&gt;logback.mongodb&lt;/artifactId&gt;<br/>
&nbsp;&nbsp;&lt;version&gt;1.0.1-SNAPSHOT&lt;/version&gt;<br/>
&lt;/dependency&gt;<br/>

Run the application or test that logs.

Checking the logs
-----------------
Query the logs from Mongo commandline:<br/>
$ mongo<br/>
&gt; use logdb<br/>
&gt; db.logs.count()<br/>
&gt; db.logs.find()<br/>


