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


## Apache(Web server)と連携する

* Apache2のインストール：```$ sudo apt-get install apache2``` 
* Apache2の基本設定  

```
$ vi /etc/apache2/conf-enabled/security.conf

# 26行目：変更
ServerTokens Prod

# 37行目：変更
ServerSignature Off

$ vi /etc/apache2/mods-enabled/dir.conf

# 2行目：ディレクトリ名のみでアクセスできるファイル名を設定
DirectoryIndex index.html index.htm

$ vi /etc/apache2/apache2.conf

# 70行目：サーバー名追記
ServerName (サーバー名)

$ vi /etc/apache2/sites-enabled/000-default.conf

$ /etc/init.d/apache2 restart 
 * Restarting web server apache2
   ...done.
```

* 有効化：```sudo a2enmod proxy_ajp```

* 
```
$ vi /etc/apache2/conf-available/proxy_ajp.conf

# 新規作成
ProxyPass /tomcat8/ ajp://localhost:8009/
```


* 
```
$ sudo a2enconf proxy_ajp 
Enabling conf proxy_ajp.
To activate the new configuration, you need to run:
service apache2 reload

$ /etc/init.d/apache2 restart 
```

## 参考
* [「Servlet」と「JSP」の違い](http://wa3.i-3-i.info/diff185java.html)
* [Tomcat 8 : JAVAアプリケーションサーバー](https://www.server-world.info/query?os=Ubuntu_14.04&p=tomcat8)
* [Ubuntu 14.04 LTSにTomcatを導入する](http://qiita.com/satoichiki/items/465214c76d120c945ed8)