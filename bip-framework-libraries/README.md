This page documents the purpose and capabilities of **OpenShift Container Platform Framework Libraries** for the services.

## What is this library project for? ##

This project contains interfaces, annotations and classes consumed by the application services for various functionality:
* Marker interfaces for transfer objects to support ommon identification and behavior
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

## Framework usage in service applications

#### Model objects and layer separation
BIP Framework requires distinct separation of model objects between the **Provider**, **Domain**, and **Partner** layers. For more information, see [Design: Layer and Model Separation](https://github.com/department-of-veterans-affairs/ocp-reference-spring-boot/tree/master/docs/design-layer-separation.md).

#### Auditing


#### Application security
Security is applied at the REST interface by the security layer. See [Application Security Management](https://github.com/department-of-veterans-affairs/ocp-reference-spring-boot/tree/master/docs/application-security-management.md).

#### 

## How to add dependency in your project?

    <dependency>
        <groupId>gov.va.bip.framework</groupId>
        <artifactId>bip-framework-libraries</artifactId>
        <version><!-- add the appropriate version --></version>
    </dependency>

## Class Diagrams

##### Auditing Package
gov.va.bip.framework.audit

<img src = "/images/cd-audit-package.jpg">

##### Aspects Join Points and Pointcuts Packages
   gov.va.bip.framework.aspect <br/>
   gov.va.bip.framework.rest.provider.aspect<br/>
   gov.va.bip.framework.service.aspect<br/>
   
<img src = "/images/cd-aspect-packages.jpg">

##### Exception Packages
   gov.va.bip.framework.exception <br/>
   gov.va.bip.framework.rest.exception<br/>
   gov.va.bip.framework.service<br/>
   
<img src = "/images/bip-framework-exception-class-diagram.jpg">

gov.va.bip.framework.log

<img src = "/images/bip-framework-log-class-diagram.jpg">

gov.va.bip.framework.security
gov.va.bip.framework.security.model
gov.va.bip.framework.security.util

<img src = "/images/bip-framework-security-class-diagram.jpg">

gov.va.bip.framework.security.jwt
gov.va.bip.framework.security.jwt.correlation

<img src = "/images/bip-framework-jwt-class-diagram.jpg">

gov.va.bip.framework.transfer
gov.va.bip.framework.transfer.jaxb.adapters
gov.va.bip.framework.transfer.transform

<img src = "/images/bip-framework-transfer-class-diagram.jpg">

gov.va.bip.framework.validation

<img src = "/images/bip-framework-validation-class-diagram.jpg">

gov.va.bip.framework.client.ws
gov.va.bip.framework.client.ws.interceptor
gov.va.bip.framework.client.ws.interceptor.transport

<img src = "/images/bip-framework-ws-class-diagram.jpg">




