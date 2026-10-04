FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY target/sams.war /usr/local/tomcat/webapps/sams.war

EXPOSE 8080

CMD ["catalina.sh", "run"]