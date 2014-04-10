Geolocation-based Searching of Legacy PDF Documents
===============================
Author: SeeMai Chan
Technologies: Java, Javascript, HTML, CSS
Summary: Searching of PDF documents based on geolocation
Source: https://github.com/aimoaio/GeolocationSearching

Abstract
-----------

Much data online is available as PDF (Portable Document Format) files, including public transport and government data. PDF is a file format commonly found on the internet, it retains intended print layout, keeps all formatting and can be opened on almost any device. PDF files can be loaded on Smartphones for display but searching on these can be slow and difficult. In the case of transport and vehicle documents provided by the government, they also contain a lot of locational information. Under the assumption that most users who view these documents are looking for information regarding their current location, the project's aim is to develop an application that automates the PDF searching process based upon terms related to the user's location.

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7. 

For users, an internet connection is required to run. For developers, an internet connection is required for testing.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.md#configure-maven) before testing the application.


Importing the application into Eclipse
-------------------------

File --> Import --> Project from Git --> Clone URI

Enter: https://github.com/aimoaio/GeolocationSearching

Deploying this application to OpenShift
-------------------------
2. Create an account on OpenShift: https://www.openshift.com/
3. Create a namespace and an application name.
4. Obtain the repository URL for the application (starts with ssh:)
5. Push this repository to the OpenShift repository using Git.

Dependency & Configuration for build
-------------------------

The application depends on the PDFBox dependency, version 1.8.3. This can be found on: http://pdfbox.apache.org/

This dependency must be added to the pom.xml configuration file.

Additionally the following line should be added to the pre_build scripts file inside action_hooks of the OpenShift folder

Location of script file: git repo name/.openshift/action_hooks/pre_build

mvn install:install-file -Dfile=../../app-root/repo/pdfbox-app-1.8.3.jar -DgroupId=org.apache.pdfbox -DartifactId=pdfbox -Dversion=1.8.3 -Dpackaging=jar


Accessing the demo application 
---------------------

The application demo can be seen at: http://geo-geolocation.rhcloud.com/

