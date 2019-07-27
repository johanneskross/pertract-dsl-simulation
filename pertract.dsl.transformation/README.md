# Transforming PerTract-DSL instances #

* License: Eclipse Public License - v 2.0

This repository contains three Maven projects - the build order is as follows:
* __pertract.dsl.transformation.palladio.dependencies__: installs non-Maven artifacts into your local Maven repository
* __pertract.dsl.transformation.palladio__: transforms DSL-instances to performance models
* __pertract.dsl.transformation.palladio.simulator__: simulates performance models

## Requirements / Dependencies
_Requirements: Java Oracle JDK or OpenJDK, Apache Maven, Docker (optional)_

You need to have installed the __PerTract DSL__ git repository / Maven project into your workspace or local maven repository 

## Build from source
```bash
mvn -f pertract.dsl.transformation.palladio.dependencies/pom.xml clean install
mvn -f pertract.dsl.transformation.palladio/pom.xml clean install
mvn -f pertract.dsl.transformation.palladio.simulator/pom.xml clean install
```

### Run programmatically
You can include _pertract.dsl.transformation.palladio_ into your IDE and use the interfaces to transform PerTract-DSL-instances to Palladio component models. 