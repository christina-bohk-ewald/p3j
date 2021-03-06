## Development Guidelines

### General technical overview

The paper "Probabilistic Population Projection with James II" by Bohk et. al provides a general (albeit slightly outdated) technical overview of the tool.
It can be freely accessed here: http://www.informs-sim.org/wsc09papers/193.pdf

The methodology is detailed in: C. Bohk, Ein probabilistisches Bevölkerungsprognosemodell. Wiesbaden: VS Verlag für Sozialwissenschaften, 2012.
Available: http://dx.doi.org/10.1007/978-3-531-19267-3

### Building the project

We use Maven 3 to build the project. See http://maven.apache.org if you are not familiar with this tool.

If you want to start the P3J during development from an IDE (e.g., Eclipse), please make sure to run the main class (p3j.gui.P3J) with the working directory bein set to the binary directory (i.e. `p3j/target/classes`).

### Coding Conventions

We use SonarQube (http://www.sonarsource.org/) with a slightly adapted 'Sonar way' rule set (the default) to check the P3J code for inconsistencies, bugs, etc.:
just set the Checkstyle rule 'Package name' so that it enforces a top package called p3j, via the regular expression `^p3j+(\.[a-z0-9]*)*$` (in the SonarQube configuration).
If this is configured correctly, there should be no violations to the rules - please check before sending us pull requests! 
SonarQube is free and simple to install.

### Making a Release:

To make a release, do the following:
1. Update version number in pom.xml (remove -SNAPSHOT)
2. Execute "mvn clean site package" (in *this* order, so that the API documentation is included in the release)
3. If the build is successful, commit the changes and add an appropriate tag ('p3j-x.y.z')
4. Update version number to the new version in the splash screen (see p3j.gui.P3J) and also update it with an additional '-SNAPSHOT' suffix in the pom.xml
5. Commit the change, push changes and upload new files to 'Downloads'