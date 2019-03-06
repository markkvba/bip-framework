## What is this repository for? ##

To run spring boot and spring cloud enabled services on the BIP Platform, it must adhere to various service patterns. This repository contains suite of framework libraries, auto configurations, test libraries and parent POM that must be included as dependencies to enable the patterns

## Project Breakdown ##

1. ocp-framework-autoconfigure: Shared auto-configuration for the services to enable the patterns for audit, cache, feign, rest, security, swagger, service, vault etc

1. ocp-framework-libraries: Shared libraries for the services to provide common framework and security interfaces. 

1. ocp-framework-parentpom: Parent POM for spring boot and cloud enabled services. It provides common Maven configuration and dependencies for the suite of projects.

1. ocp-framework-test-lib: Test library framework to support functional testing for the services

## How to include these dependency libraries in your project ##

There are 2 options to ensure that these libraries are downloaded on your local workstation for your service project to build.

** OPTION 1 **
1. Clone this repository
   
    git clone https://github.com/department-of-veterans-affairs/ocp-framework.git
    
1. Navigate to the directory and run command
   
    mvn clean install

** OPTION 2 **
Add the below section in the reactor (root) pom.xml of your service project. See example: https://github.com/department-of-veterans-affairs/ocp-reference-spring-boot/blob/master/pom.xml

pom.xml

	<distributionManagement>
	    <repository>
	        <id>github</id>
	        <name>GitHub Repository</name>
	        <url>https://raw.github.com/department-of-veterans-affairs/ocp-framework/mvn-repo</url>
	    </repository>
	</distributionManagement>

Update your local ~/.m2/settings.xml 

	<settings>
	  <servers>
	    <server>
	      <id>github</id>
	      <username>{{GitHub User Name}}</username>
	      <password>{{Personal Access Token}}</password>
	      <configuration>
        	<httpHeaders>
	          	<property>
	            	<name>Authorization</name>
	            	<!-- Base64-encoded username:access_token -->
	            	<!-- Example site to generate https://codebeautify.org/base64-encode -->
	            	<value>Basic {{base64 encoded content}}</value>
	          	</property>
        	</httpHeaders>
      </configuration>
	    </server>
	  </servers>
	</settings>

## How to deploy and host a maven repository on GitHub ##

source : http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github

~/.m2/settings.xml
	
	Same format as mentioned in the previous section 

ocp-framework-parentpom/pom.xml

	<properties>
	<!-- github server corresponds to entry in ~/.m2/settings.xml -->
	    	<github.global.server>github</github.global.server>
	</properties>

	<plugins>
	    <plugin>
	        <artifactId>maven-deploy-plugin</artifactId>
	        <configuration>
	               <altDeploymentRepository>internal.repo::default::file://${project.build.directory}/mvn-repo</altDeploymentRepository>
	        </configuration>
	    </plugin>
	    <plugin>
	        <groupId>com.github.github</groupId>
	        <artifactId>site-maven-plugin</artifactId>
	        <version>0.12</version>
	        <configuration>
	            <message>Maven artifacts for ${project.version}</message>  <!-- git commit message -->
	            <noJekyll>true</noJekyll>                                  <!-- disable webpage processing -->
	            <outputDirectory>${project.build.directory}/mvn-repo</outputDirectory> <!-- matches distribution management repository url above -->
	            <branch>refs/heads/mvn-repo</branch>                       <!-- remote branch name -->
	            <includes><include>**/*</include></includes>
	            <repositoryName>ocp-framework</repositoryName>      <!-- github repo name -->
	            <repositoryOwner>department-of-veterans-affairs</repositoryOwner>    <!-- github username  -->
	            <merge>true</merge>
	        </configuration>
	        <executions>
	          <!-- run site-maven-plugin's 'site' target as part of the build's normal 'deploy' phase -->
	          <execution>
	            <goals>
	              <goal>site</goal>
	            </goals>
	            <phase>deploy</phase>
	          </execution>
	        </executions>
		</plugin>
	</plugins>

Run command to deploy and upload artifacts to ocp-framework
	
	mvn clean deploy