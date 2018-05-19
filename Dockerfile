# Ubuntu 16.04
# Oracle Java 1.8.0_101 64 bit
# Maven 3.3.9

FROM frolvlad/alpine-oraclejdk8:full

MAINTAINER Patrick Magauran (https://gitlab.com/RoatingFans)


# update dpkg repositories
RUN apk add --update openssh-client git curl jq zip sed wget ca-certificates tar gettext

# get maven 3.3.9
RUN wget --no-verbose -O /tmp/apache-maven-3.5.3.tar.gz http://apache.osuosl.org/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz

# verify checksum
RUN echo "bbfa43a4ce4ef96732b896d057f8a613aa229801  /tmp/apache-maven-3.5.3.tar.gz" | sha1sum -c

# install maven
RUN tar xzf /tmp/apache-maven-3.5.3.tar.gz -C /usr/share/
RUN ln -s /usr/share/apache-maven-3.5.3 /usr/share/maven
RUN ln -s /usr/share/maven/bin/mvn /usr/local/bin
RUN rm -f /tmp/apache-maven-3.5.3.tar.gz
ENV MAVEN_HOME /opt/maven

CMD [""]