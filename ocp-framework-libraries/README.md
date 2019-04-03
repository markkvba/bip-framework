This page documents the purpose and capabilities of **OpenShift Container Platform Framework Libraries** for the services.

## What is this library project for? ##

This project contains interfaces, annotations and classes consumed by the application services for various functionality as listed below
* Rest Provider Message classes, RestTemplate
* Audit and Performance Logging aspects
* Utility ops for logging and handling exceptions
* Root classes for checked and runtime exceptions
* WebService client config
* Security JWT base classes, properties and exceptions
* Service Domain Message classes, timer and validation aspects

## How to add dependency in your project?

    <dependency>
        <groupId>gov.va.ocp.framework</groupId>
        <artifactId>ocp-framework-libraries</artifactId>
        <version><!-- add the appropriate version --></version>
    </dependency>

## Class Diagrams

##### Package for Audting
gov.va.ocp.framework.audit

<img src = "/images/cd-audit-package.jpg">

##### Packages for Aspects Join Points and Pointcuts
   gov.va.ocp.framework.aspect
   gov.va.ocp.framework.rest.provider.aspect
   gov.va.ocp.framework.service.aspect
   
<img src = "/images/cd-aspect-packages.jpg">
