This page documents the purpose and capabilities of **OpenShift Container Platform Framework Libraries** for the services.

## What is this library project for? ##

This project contains interfaces, annotations and classes consumed by the application services for various functionality:
* Marker interfaces for transfer objects to support common identification and behavior
* Rest Provider Message classes, RestTemplate
* Audit and Performance Logging aspects
* Utility ops for logging and handling exceptions
* Root classes for checked and runtime exceptions
* WebService client config
* Security JWT base classes, properties and exceptions
* Service Domain Message classes, timer and validation aspects

## BIP Framework principles
BIP Framework aims to:
* free developers from many of the complexities of dealing with the underlying platform,
* enable centralized application configuration,
* enable developers to focus more on business requirements and less on boilerplate code,
* encourage developers to use good coding practices and patterns that are effective and efficient,
* encourage developers to write code that presents a common "look and feel" across projects,
* enable developers to produce reliable code that takes less time to develop and test.

## How to add the Framework dependency
Add the dependency in the application projects POM file.

    <dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-libraries</artifactId>
        <version><!-- add the appropriate version --></version>
    </dependency>

## Framework usage in service applications

For more information about developing applications on the BIP Framework, see [Developing with BIP Framework](https://github.com/department-of-veterans-affairs/ocp-reference-spring-boot/tree/master/docs/developeing-with-bip-framework.md).

## Class Diagrams

#### Aspects Join Points and Pointcuts Packages
    gov.va.bip.framework.aspect
    gov.va.bip.framework.rest.provider.aspect
    gov.va.bip.framework.service.aspect
<img src = "/images/cd-aspect-packages.jpg">

#### Auditing Package
    gov.va.bip.framework.audit
    gov.va.bip.framework.audit.annotation
    gov.va.bip.framework.audit.http
    gov.va.bip.framework.audit.model
<img src = "/images/cd-audit-package.jpg">

#### Auditing Package
    gov.va.bip.framework.cache
    gov.va.bip.framework.cache.interceptor
<img src = "/images/cd-cache-package.jpg">

#### Client Packages
**REST**

    gov.va.bip.framework.client.rest.template
<img src = "/images/cd-client-rest-package.jpg">

**SOAP**

    gov.va.bip.framework.client.ws
    gov.va.bip.framework.client.ws.interceptor
    gov.va.bip.framework.client.ws.interceptor.transport
    gov.va.bip.framework.client.ws.remote
<img src = "/images/cd-client-ws-package.jpg">

#### Config Packages
    gov.va.bip.framework.config
<img src = "/images/cd-config-package.jpg">

##### Exception Packages
    gov.va.bip.framework.exception
    gov.va.bip.framework.exception.interceptor
    gov.va.bip.framework.rest.exception
    gov.va.bip.framework.security.jwt
    gov.va.bip.framework.service
<img src = "/images/cd-exception-package.jpg">

#### Logging Packages
    gov.va.bip.framework.log
<img src = "/images/cd-log-package.jpg">

#### Messages Packages
    gov.va.bip.framework.messages
<img src = "/images/cd-messages-package.jpg">

#### Rest Packages
    gov.va.bip.framework.rest.exception
    gov.va.bip.framework.rest.provider
    gov.va.bip.framework.rest.aspect
<img src = "/images/cd-rest-package.jpg">

#### Security Packages
    gov.va.bip.framework.security
    gov.va.bip.framework.security.model
    gov.va.bip.framework.security.util
<img src = "/images/cd-security-package.jpg">

    gov.va.bip.framework.security.jwt
    gov.va.bip.framework.security.jwt.correlation
<img src = "/images/cd-security-jwt-package.jpg">

#### Service Packages
    gov.va.bip.framework.service
    gov.va.bip.framework.service.spect
<img src = "/images/cd-service-package.jpg">

#### Swagger Packages
    gov.va.bip.framework.swagger
<img src = "/images/cd-swagger-package.jpg">

#### Transfer Packages
    gov.va.bip.framework.transfer
    gov.va.bip.framework.transfer.jaxb.adapters
    gov.va.bip.framework.transfer.transform
<img src = "/images/cd-transfer-package.jpg">

#### Validation Packages
    gov.va.bip.framework.validation
<img src = "/images/cd-validator-package.jpg">



