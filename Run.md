If you only want to RUN the existing project

You only need to start Tomcat:

C:\Users\nitai\apache-tomcat-10.1.20\bin\startup.bat

Then open:

http://localhost:8080/LaboratoryManagementSystem/

That's it. ✅

When do you need mvn clean package?

Only when you change your Java project code.

then do.

cd C:\Users\nitai\OneDrive\Attachments\Desktop\NITAI\Micro_Project\LaboratoryManagementSystem
mvn clean package

Then deploy the new WAR:

copy target\LaboratoryManagementSystem.war C:\Users\nitai\apache-tomcat-10.1.20\webapps\

and restart Tomcat.

Your routine

No code changes:

C:\Users\nitai\apache-tomcat-10.1.20\bin\startup.bat

Open:

http://localhost:8080/LaboratoryManagementSystem/

Code changes:

cd C:\Users\nitai\OneDrive\Attachments\Desktop\NITAI\Micro_Project\LaboratoryManagementSystem
mvn clean package
copy target\LaboratoryManagementSystem.war C:\Users\nitai\apache-tomcat-10.1.20\webapps\

Then restart Tomcat.

When you're finished

Stop Tomcat:

C:\Users\nitai\apache-tomcat-10.1.20\bin\shutdown.bat

Your Railway MySQL database does not need to be stopped for this.

IMPORTANT:

Just use app → START TOMCAT → OPEN BROWSER

Changed code → BUILD → COPY WAR → RESTART TOMCAT




