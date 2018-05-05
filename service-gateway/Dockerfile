FROM java:8-jre-alpine
VOLUME /tmp
ADD target/service-gateway-*.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]