# Client for PerTract Simulation Service

* License: Eclipse Public License - v 2.0

## Build from source
Either import the project in your IDE or install it in your local Maven repo using

```bash
mvn  clean install
```

## Use client library

```java
ApplicationExecutionArchitecture app; // e.g., use org.fortiss.pmwt.pertract.dsl.extractor
DataWorkloadArchitecture data; // e.g., use org.fortiss.pmwt.pertract.dsl.extractor
ResourceArchitecture resources; // e.g., use org.fortiss.pmwt.pertract.dsl.extractor
int simulationTime = 60; // 1 simulation unit equals 1 second

PerTractSimulationClient client = new PerTractSimulationClient("http://localhost:8080");
SimulationResultsDTO results = client.simulate(app, data, resources, simulationTime);
// do something with your results
```
