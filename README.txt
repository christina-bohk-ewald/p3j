Development Guidelines
======================

General technical overview:

The paper "Probabilistic Population Projection with James II" by Bohk et. al provides a general (albeit slightly outdated) technical overview of the tool.
It can be freely accessed here: http://www.informs-sim.org/wsc09papers/193.pdf

Building the project:

We use Maven 3 to build the project. See http://maven.apache.org/ if you are not familiar with this tool.

Coding Convetions:

We use Sonar (http://www.sonarsource.org/) with a slightly adapted 'Sonar way' rule set (the default) to check the P3J code for inconsistencies, bugs, etc.:
just set the Checkstyle rule 'Package name' so that it enforces a top package called p3j, via the regular expression '^p3j+(\.[a-z0-9]*)*$' (in the Sonar configuration).
If this is configured correctly, there should be no violations to the rules - please check before sending us pull requests! 
Sonar is free and simple to install.

Making a Release:

To make a release, do the following:
1. Update version number in pom.xml and in splash screen (see p3j.gui.P3J)
2. Execute "mvn clean site package" (in *this* order, so that the API documentation is included in the relasse)