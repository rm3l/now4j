# Now4J : Java Client for the [△ Now](https://zeit.co/now) API

[![](https://jitpack.io/v/rm3l/now4j.svg)](https://jitpack.io/#rm3l/now4j)
[![Bintray](https://img.shields.io/bintray/v/rm3l/maven/org.rm3l:now4j.svg)](https://bintray.com/rm3l/maven/org.rm3l%3Anow4j) 
[![Travis branch](https://img.shields.io/travis/rm3l/now4j/master.svg)](https://travis-ci.org/rm3l/now4j) 
[![Coverage Status](https://coveralls.io/repos/github/rm3l/now4j/badge.svg?branch=master)](https://coveralls.io/github/rm3l/now4j?branch=master)  
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://github.com/rm3l/now4j/blob/master/LICENSE) 

[![GitHub watchers](https://img.shields.io/github/watchers/rm3l/now4j.svg?style=social&label=Watch)](https://github.com/rm3l/now4j) 
[![GitHub stars](https://img.shields.io/github/stars/rm3l/now4j.svg?style=social&label=Star)](https://github.com/rm3l/now4j) 
[![GitHub forks](https://img.shields.io/github/forks/rm3l/now4j.svg?style=social&label=Fork)](https://github.com/rm3l/now4j)

## What is it?

Zeit's [Now](https://zeit.co/now) is a service allowing to deploy an application 
(Node.js or Docker-powered) from your local machine to a remote cloud service in moments.

Now4J is a Java Client library for accessing the [△ Now API](https://zeit.co/api).

This Java Client supports the following core features of the Now API:
- deployments
- domains
- files
- aliases
- certificates
- secrets

A standalone Command Line Interface (CLI) app is also available for you to manipulate your Now resources.

## Library

### Installation

#### With Gradle

Add it as a gradle dependency in your `build.gradle`

```groovy
compile 'org.rm3l:now4j:1.0.0-rc2'
```

#### With Maven

Add the following dependency to your `pom.xml` dependencies

```xml
<dependency>
    <groupId>org.rm3l</groupId>
    <artifactId>now4j</artifactId>
    <version>1.0.0-rc2</version>
</dependency>
```

### Usage

Grab your Zeit Account token [here](https://zeit.co/account#api-tokens).

Initialize a `NowClient` instance, by calling any of the static `NowCient.create(...)` methods:

```java
final NowClient nowClient = NowCient.create(<myToken>, <myTeam>);
```

Please note that if you want to read your `token` and `team` from `~/.now.json`, you can simply call:

```java
final NowClient nowClient = NowCient.create();
```

And finally, you can use its methods to retrieve data, e.g,:

```java
nowClient.getDeployments();
```

Note that all the methods in `NowClient` are overloaded and come under two forms:
- a synchronous method, e.g: `NowClient#getDeployments()`;
- an asynchronous method of the same name, but taking a `ClientCallback` instance which will 
 be notified of the response. For example: `NowClient#getDeployments(ClientCallback)`
 
### Javadoc

TODO
 
## Command Line Interface (CLI)

TODO

## Contributing

TODO

## LICENSE

    The MIT License (MIT)
    
    Copyright (c) 2017 Armel Soro
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
