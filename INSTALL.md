
Installation
============

Quick installation of LinSign.

------------------------------------------------------------------------------

Prerequisite
------------

### Software

* Operating System: GNU/Linux, e.g. Debian, CentOS
* Application Server: Tomcat 7
* Java environment: OpenJDK JDK 7
* Signature library: SD-DSS 4.5.0

### Services

* Timestamping Service: Timestamping URL, certificates
* Trusted Time: NTP server FQDN


Installation
------------

### Prerequisite

* Operating System: GNU/Linux, e.g. Debian, CentOS
* Resources: RAM 4 Gio, Disk 20 Gio
* Componants: Maven 2, Tomcat 7, Apache HTTP Server 2

Maven installation:
```shell
$ sudo apt-get install maven2
```

Tomcat installation:
```shell
$ sudo apt-get install tomcat7
```

Apache HTTP Server installation:
```shell
$ sudo apt-get install apache2
```

### Get source

Get LinSign source code from GitHub:
```shell
$ mkdir -p /opt/linsign
$ cd /opt/linsign/
$ git clone https://github.com/linagora/linsign.git
```

### Build and deploy

Build all LinSign modules:
```shell
$ cd linsign-applet/
$ mvn clean install
$ cd ../linsign-application/
$ mvn clean install
$ cd ../linsign-dss/
$ mvn clean install
```

If you want to import these modules in an Eclipse project:
```shell
$ mvn eclipse:eclipse
```

Then deploy it:
```shell
$ cp /opt/linsign/linsign-dss/dss-demo-webapp/target/file.war /var/lib/tomcat7/webapps/linsign-webapp.war
$ sudo systemctl restart tomcat7.service
```

### Configuration

Create configuration folders, and give rights to user `tomcat7`:
```shell
$ sudo mkdir -p /etc/linsign/policy
$ sudo mkdir -p /var/log/linsign
$ sudo mkdir -p /var/www/linsign/{cert,crl}
$ sudo chown -r tomcat7:tomcat7 /etc/linsign
$ sudo chown -r tomcat7:tomcat7 /var/log/linsign
$ sudo chown -r tomcat7:tomcat7 /var/www/linsign
```

Then copy your CA certificates and CRLs into these folders.

### Access

Application URL: `http://localhost:8080/linsign-webapp/home`

Go to Signature tab.

If need: in Java configuration panel, Security tab, update the site list with exception,
and append the URL address: `http://localhost:8080/linsign-webapp/signature`

Then, add an administrator user, in the file `/var/lib/tomcat7/conf/tomcat-users.xml`,
with the following row:
```xml
<user username="admin" password="admin" roles="admin"/>
```


Annexes
-------

### Annex A – Timestamping Services

#### Free timestamping services

Time Stamping Authorithy  
`http://tsp.iaik.at/tsp/`

Free Time Stamp Authority  
`https://www.freetsa.org/`

Safe Creative Timestamp Server  
`https://tsa.safecreative.org/`


#### Timestamping applications

SignServer - Open Source server side Digital Signatures - Features  
`https://www.signserver.org/features.html`


------------------------------------------------------------------------------
