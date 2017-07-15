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

[comment]: <> (`now4j` is published on both Bintray JCenter and Jitpack.)
`now4j` is published on Bintray JCenter.

##### With Gradle

<!--
Make sure you have either JCenter or Jitpack to your list of repositories in your root `build.gradle`:

```groovy
allprojects {
    repositories {
        //...
        jcenter()                           //To download via JCenter
        maven { url "https://jitpack.io" }  //To download via Jitpack
    }
}
```
-->
Make sure you have JCenter added to the list of repositories in your root `build.gradle`:

```groovy
allprojects {
    repositories {
        //...
        jcenter()
    }
}
```

Then add `now4j` as a gradle dependency in your `build.gradle`

```groovy
compile 'org.rm3l:now4j:1.0.0-rc3'
```

##### With Maven

<!--
Make sure you have either Jcenter or Jitpack to your list of repositories in your `pom.xml`:

```xml
<repositories>
    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
-->
Make sure you have Jcenter added to your list of repositories in your `pom.xml`:

```xml
<repositories>
    <!-- ... -->

    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
</repositories>
```

Then add `now4j` to your `pom.xml` dependencies:

```xml
<dependency>
    <groupId>org.rm3l</groupId>
    <artifactId>now4j</artifactId>
    <version>1.0.0-rc3</version>
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

Visit [https://jitpack.io/com/github/rm3l/now4j/1.0.0-rc3/javadoc](https://jitpack.io/com/github/rm3l/now4j/1.0.0-rc3/javadoc/)
 
## Command Line Interface (CLI)

Download the CLI binary [here](https://github.com/rm3l/now4j/releases/download/1.0.0-rc3/now4j-cli-1.0.0-rc3.jar).

<details><summary>java -jar now4j-cli-1.0.0-rc3.jar --help</summary><p>

```bash
Usage: java -jar now4j-cli-<version>.jar [options] [command] [command options]
  Options:
    --token, --T
      Now API Token. Read from ~/.now.json if not specified here.
    --team, --t
      Now API Team. Read from ~/.now.json if not specified here.
    --debug, -d
      Debug mode
      Default: false
    --help, -h
      Show this help
  Commands:
    deployments      Manage deployments
      Usage: deployments [options]
        Options:
          --deploymentData
            JSON-serialized description of the deployment to add. The keys 
            should represent a file path, with their respective values 
            containing the file contents.
          --deploymentId
            ID of deployment
          --fileId
            ID of file
          -add
            Perform a deployment. Required: --deploymentData
            Default: false
          -getFile, -dl
            Get file content. Required: --deploymentId and --fileId
            Default: false
          -list, -ls
            List deployments. Optional: --deploymentId
            Default: false
          -listFiles, -lsFiles
            List file structure of deployment. Required: --deploymentId
            Default: false
          -remove, -rm, -delete, -del
            Remove a deployment. Required: --deploymentId
            Default: false

    aliases      Manage aliases
      Usage: aliases [options]
        Options:
          --alias
            Hostname or custom url for the alias
          --aliasId
            ID of Alias
          --deploymentId
            ID of Deployment
          -add, -create
            Create a new alias. Required: --deploymentId and --alias
            Default: false
          -list, -ls
            List aliases. Optional: --deploymentId
            Default: false
          -remove, -rm, -delete, -del
            Remove an alias. Required: --aliasId
            Default: false

    domains      Manage domains
      Usage: domains [options]
        Options:
          --domainName
            Name of domain
          --externalDNS
            Indicates whether the domain is an external DNS or not
            Default: false
          --recordData
            JSON-serialized description of the domain record to add.
          --recordId
            ID of Domain Record
          -add
            Add a new domain. Required: --domainName . Optional: --externalDNS
            Default: false
          -addRecord
            Add a new domain record. Required: --domainName and --recordData
            Default: false
          -list, -ls
            List domains
            Default: false
          -listRecords, -lsr
            List domain records. Required: --domainName
            Default: false
          -remove, -rm, -delete, -del
            Remove a domain. Required: --domainName
            Default: false
          -removeRecord, -rmr, -deleteRecord, -delr
            Remove a domain record. Required: --domainName and --recordId
            Default: false

    certs      Manage certificates
      Usage: certs [options]
        Options:
          --certificate, --cert
            CA certificate chain
          --commonName, --cn
            Common Name (CN)
          --domain
            The domain.
            Default: []
          --key
            Private key for the certificate
          --x509, --ca
            X.509 certificate
          -add, -create
            Create a new certificate
            Default: false
          -list, -ls
            List certificates. Required: --cn
            Default: false
          -remove, -rm, -delete, -del
            Remove a certificate. Required: --cn
            Default: false
          -renew
            Renew certificate for domains
            Default: false
          -replace
            Replace certificate for domains
            Default: false

    secrets      Manage secrets
      Usage: secrets [options]
        Options:
          --secretName, --name
            Secret name
          --secretUid, --uid
            UID of Secret
          --secretValue, --value
            Secret value
          -add, -create
            Create a new secret. Required: --name and --value
            Default: false
          -list, -ls
            List secrets
            Default: false
          -remove, -rm, -delete, -del
            Remove a secret. Required: --name or --uid
            Default: false
          -rename
            Rename a secret. Required: --uid and --name
            Default: false
```

</p></details>

## Building from source

You can build `now4j` in the same way as any Gradle project on Git.

However, thanks to the [Gradle Wrapper](https://docs.gradle.org/3.3/userguide/gradle_wrapper.html) (cf. `gradlew` and `gradlew.bat` scripts), 
you do not need to have Gradle installed on your machine.

1. Clone the `now4j` repository on your machine:
```bash
git clone https://github.com/rm3l/now4j && cd now4j
```
2. Switch to the appropriate branch if needed with `git checkout ...`
3. Execute a Gradle build in the directory containing the `build.gradle` file:
```bash
./gradlew build
```
4. You will find the artifacts under `library/build/libs` and `cli/build/libs` directories

## Contributing and Improving Now4J!

Contributions and issue reporting are more than welcome. 
So to help out, do feel free to fork this repo and open up a pull request. 
I'll review and merge your changes as quickly as possible.

You can use [GitHub issues](https://github.com/rm3l/now4j/issues) to report bugs. 
However, please make sure your description is clear enough and has sufficient instructions 
to be able to reproduce the issue.

[comment]: <> (See CONTRIBUTING.md for more on contributing to this Github project.)

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
