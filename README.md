## What is this repository for? ##

To run spring boot and spring cloud enabled services on the BIP Platform, it must adhere to various service patterns. This repository contains suite of framework libraries, auto configurations, test libraries and parent POM that can be included as dependencies to enable the patterns

## Project Breakdown ##

1. ocp-framework-autoconfigure: Shared auto-configuration for the services to enable the patterns for audit, cache, feign, rest, security, swagger, service, vault etc

1. ocp-framework-libraries: Shared libraries for the services to provide common framework and security interfaces. 

1. ocp-framework-parentpom: Parent POM for spring boot and cloud enabled services. It provides common Maven configuration and dependencies for the suite of projects.

1. ocp-framework-test-lib: Test library framework to support functional testing for the services
	
