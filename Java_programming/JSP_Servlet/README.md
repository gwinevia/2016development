## 用語説明

* Servlet:Webサーバ上でお仕事をするJavaのプログラム
* JSP:Webサーバ上でお仕事をするJavaのプログラムで,HTMLファイルとJavaのプログラムが合体したもの

どちらもWebサーバ上で動くJavaのプログラム.  
Servletは,ガッツリとJavaのプログラムっぽい書き方であるのに対して,  
JSPは,HTMLファイルの中にJavaのプログラムが埋め込まれたような書き方をする.  

## Tomcat9
Java ServletやJavaServer Pages(JSP)を実行するためのWeb Container  
Tomcatは単独でWebサーバとしても機能することが可能  
オープンソースのソフトウェアであるため,誰でも自由かつ無償で利用・改変・再配布することが出来る

### インストール

* ```$ curl -O http://ftp.riken.jp/net/apache/tomcat/tomcat-9/v9.0.0.M13/bin/apache-tomcat-9.0.0.M13.tar.gz```
* ```$ tar zxvf apache-tomcat-9.0.0.M13.tar.gz ```

### 起動,停止

* 起動  

```
$ /opt/tomcat9/bin/startup.sh 
Using CATALINA_BASE:   /opt/tomcat9
Using CATALINA_HOME:   /opt/tomcat9
Using CATALINA_TMPDIR: /opt/tomcat9/temp
Using JRE_HOME:        /usr/lib/jvm/java-8-oracle
Using CLASSPATH:       /opt/tomcat9/bin/bootstrap.jar:/opt/tomcat9/bin/tomcat-juli.jar
Tomcat started.

```
* 停止  

```
$ /opt/tomcat9/bin/shutdown.sh 
Using CATALINA_BASE:   /opt/tomcat9
Using CATALINA_HOME:   /opt/tomcat9
Using CATALINA_TMPDIR: /opt/tomcat9/temp
Using JRE_HOME:        /usr/lib/jvm/java-8-oracle
Using CLASSPATH:       /opt/tomcat9/bin/bootstrap.jar:/opt/tomcat9/bin/tomcat-juli.jar
```

### バージョン

```
$ /opt/tomcat9/bin/version.sh 
Using CATALINA_BASE:   /opt/tomcat9
Using CATALINA_HOME:   /opt/tomcat9
Using CATALINA_TMPDIR: /opt/tomcat9/temp
Using JRE_HOME:        /usr/lib/jvm/java-8-oracle
Using CLASSPATH:       /opt/tomcat9/bin/bootstrap.jar:/opt/tomcat9/bin/tomcat-juli.jar
Server version: Apache Tomcat/9.0.0.M13
Server built:   Nov 3 2016 20:59:41 UTC
Server number:  9.0.0.0
OS Name:        Linux
OS Version:     4.2.0-42-generic
Architecture:   amd64
JVM Version:    1.8.0_91-b14
JVM Vendor:     Oracle Corporation

```


## 参考
* [「Servlet」と「JSP」の違い](http://wa3.i-3-i.info/diff185java.html)