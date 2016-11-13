FROM maven:3.2.3-jdk-8
MAINTAINER Volkan Selcuk <vselcuk@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD pom.xml /usr/src/app/
RUN mvn verify clean --fail-never

ADD . /usr/src/app
RUN mvn package

EXPOSE 4567

CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "/usr/src/app/target/htmlanalyzer-jar-with-dependencies.jar"]