## What is this repository for? ##

To run spring boot and spring cloud enabled services on the BIP Platform, it must adhere to various service patterns. This repository contains suite of framework libraries, auto configurations, test libraries and parent POM that can be included as dependencies to enable the patterns

## Project Breakdown ##

1. ocp-framework-autoconfigure: Shared auto-configuration for the services to enable the patterns for audit, cache, feign, rest, security, swagger, service, vault etc

1. ocp-reference-libraries: Shared libraries for the services to provide common framework and security interfaces. 

1. ocp-reference-parentpom: Parent POM for spring boot and cloud enabled services. It provides common Maven configuration and dependencies for the suite of projects.

1. ocp-reference-partner: Partner services for reference, showing BGS with sample mock data 

## Core Concepts
* Service Discovery
* [Secrets Management](docs/secrets.md)
* [Configuration Management](docs/config-management.md)

## Contribution guidelines ## 
* If you or your team wants to contribute to this repository, then fork the repository and follow the steps to create a PR for our upstream repo to review and commit the changes
* [Creating a pull request from a fork](https://help.github.com/articles/creating-a-pull-request-from-a-fork/)

## Local Development
Instructions on running the application local can be found [here].(local-dev)
	
