# A REST Service to (transform and) simulate PerTract-DSL instances

* License: Eclipse Public License - v 2.0


We provide a WildFly Swarm jar as well as a docker image that includes the wildfly swarm jar. Swagger UI is integrated to enable you to explore the interface in your browser.
 
 ## Requirements / Dependencies
_Requirements: Java Oracle JDK or OpenJDK, Apache Maven, Docker (optional)_

You need to have installed the pertract.dsl as well as pertractl.dsl.transformation.* projects into your workspace or local maven repository


## Build from source
```bash
mvn clean package docker:build
```
_the last command line argument docker:build requires that Docker is running on your machine. If it is not the case, remove it and just use the Wildfly Swarm jar_
 
The Wildfly Swarm jar will be located in the _pertract.dsl.simulation.service/target_ and the docker image will be locally installed. You can proceed at the section _Run REST interface_.

## Run REST interface
We provide to ways to run the REST interface:
* Wildfly Swarm (you can download the jar artifact from this repository)
   ```
   java -Xmx1024m -jar pertract.dsl.simulation.service-1.0.0-swarm.jar
   ```
* Docker
   ```
   docker run -p 8080:8080 pertract/simulation
   ```

## Use REST interface
Swagger UI will be available at http://localhost:8080 and visualize you the interface.
