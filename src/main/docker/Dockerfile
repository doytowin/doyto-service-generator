FROM openjdk:8u111-jre-alpine
VOLUME /tmp
VOLUME /target
ADD generator##1.0.2.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]

