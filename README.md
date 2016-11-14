[![Build Status](https://travis-ci.org/vselcuk/htmlanalyzer.svg?branch=master)](https://travis-ci.org/vselcuk/htmlanalyzer)
# Html Analyzer
The aim of the project is simply to analyze the html document for the any given url. The analyze contains the following 
points:

* **Html Document Type:**  
 The analyzer tries to find the document type declaration line and to match the document type by some defined 
 Regular Expressions. HTML5, XMTML and HTML4 types are supported. 

* **Page Title:**  
 The analyzer tries to find the page title.
  
* **Heading level occurrences:**  
 The analyzer tries to count the occurrence of the headings by their levels.
 
* **Hypermedia links:**  
 The analyzer tries to count the internal and external hypermedia links. a[href], link[href], area[href], img[src], 
 script[src] are supported.

* **Login form detection:**  
 The analyzer tries to determine whether a login form exists in the page. There are two logic defined to determine the 
 login form. 
 The page should contain at least a form element:
    * If the form element contains ONLY one password field then it is assumed as a login form.
    * If the following rules apply to the form then it is assumed as a two step login form.
        * The form method is _POST_.
        * The action contains a specific keyword defined in a dictionary. (The dictionary file can be found under 
        [resources](src/main/resources/dictionaries) folder)
        * The form contains ONLY one text field and its name (if name is not defined then id) contains 
        a specific keyword defined in a dictionary. (The dictionary file can be found under 
        [resources](src/main/resources/dictionaries) folder)


## How to run
Run the any of the following commands in the project directory.

* Maven:
```
mvn clean compile exec:java
```

* Docker:

```
# Build the docker image
docker build -t htmlanalyzer .
```
```
# Run the image
docker run -p 4567:4567 htmlanalyzer:latest
```

* Java command:
```
mvn clean package
java -jar ./target/htmlanalyzer-jar-with-dependencies.jar

```

The web server listens on the port _4567_. Please open the the following url in your browser.
```
http://localhost:4567
```

## How to test    
Run the following command in the project directory.    
```
mvn clean test
```
Please see the code covarage [here](/target/site/jacoco-ut).
    
## Improvements
* Html analyzing can be improved by rendering javascipt.
* The document type detection can be improved by implementing more [types](https://en.wikipedia.org/wiki/Document_type_definition).
* Hypermedia detection can be improved by adding detection for embedded object and applet types.
* Internal hypermedia detection can be improved by detecting sub domain.
* Two step login check can be improved by checking two text input fields exists and their name exist in 
the dictionary. (e.g. a form contains two text field like username and email)
* A configuration solution can be introduced in order to store http listen port, dictionary file locations 
logging configuration or http url connection configuration, etc.
* Test coverage can be improved to cover all the source.
    

## Requirements
* Java 8
* jsoup (1.10.1) library (for html parsing)
* sparkjava (2.5.2) library (for simple web application)
* apache commons-io (2.5)
* junit (4.12), mockito (2.0.42-beta), powermock (1.6.6)
* Bootstrap Framework (3.3.7) & Jquery (3.1.1)
