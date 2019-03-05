## What is this project for? ##

OCP Framework Autoconfigure Project is a suite of POM files that provides application services with starter dependencies for the BIP platform. 

## Overview of the packages ##
**gov.va.ocp.framework.audit.autoconfigure**: OCP Audit AutoConfiguration that provides bean and enables Async method execution 

**gov.va.ocp.framework.cache.autoconfigure**: Redis cache auto-configuration

**gov.va.ocp.framework.feign.autoconfigure**: Feign cache auto-configuration

**gov.va.ocp.framework.hystrix.autoconfigure**: Hystrix auto-configuration to set RequestAttributes to be passed from ThreadLocal to Hystrix threads

**gov.va.ocp.framework.modelvalidator.autoconfigure**: auto-configuration for model validator

**gov.va.ocp.framework.rest.autoconfigure**: auto-configuration for rest template

**gov.va.ocp.framework.security.autoconfigure**: auto-configuration for security framework using JWT token

**gov.va.ocp.framework.service.autoconfigure**: auto-configuration for service configurations for beans, exceptions and aspect

**gov.va.ocp.framework.swagger.autoconfigure**: swagger starter and autoconfiguration to generate and configure swagger documentation

**gov.va.ocp.framework.vault.bootstrap.autoconfigure**: vault starter and bootstrap auto-configuration to bootstrap the Vault PropertySource as the first source loaded. 
                                                        This is important so that we can use the Vault generated Consul ACL token to authenticate with Consul for both 
                                                        Service Discovery and a K/V configuration source
     
## How to add dependencies in your maven pom.xml? ##
    <dependency>
        <groupId>gov.va.ocp.reference</groupId>
        <artifactId>ocp-framework-autoconfigure</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>


