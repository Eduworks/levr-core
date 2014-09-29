levr-core
=========

Base package for Levr product. This project contains the languages and base architecture for LEVR.

LEVR is a web service framework that allows you to write and publish complex web services quickly and in a variety of domain-specific languages.

One of the first languages we wrote to perform this is a functional language called RS2. Examples of this language can be found here: https://github.com/Eduworks/levr-scripts -- and a definition of the language in ANTLR can be found in the project.

* All libraries that derive levr-core are to contain libraries of Resolvers or Crunchers, and be published under the derived moniker.
* Languages are to be defined in ANTLR format, and are contained within levr-core. These languages will define web services as their endpoints.
* The preferred method of publishing web services is through Java Servlets.

levr-core is Open Source under the Apache 2.0 license, the details of which can be found in license.txt.

levr-core is under active development. It is released only as part of other projects, and you will not find JAR releases here. If this is desired, contact one of our developers.

levr-core contains support for the following languages, all of which will be represented as examples in levr-scripts (eventually).

* RS2
* Javascript
* Python

levr-core requires the following to build or use:

* Java 1.6 or above. (1.6.0_26 recommended)
* ANT (for building)
* IVY (for fetching libraries from Maven)
* eduworks-common, found at https://github.com/Eduworks/eduworks-common

The bug tracker for levr-core is being currently maintained on GitHub, at the following url: https://github.com/Eduworks/levr-core/issues

The lead developer for levr-core is Fritz (fritz.ray@eduworks.com).
