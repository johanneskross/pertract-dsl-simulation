FROM openjdk:jre-alpine

ADD pertract.dsl.simulation.service-1.0.0-swarm.jar /opt/wildfly-swarm.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx1024m", "-jar", "/opt/wildfly-swarm.jar"]
