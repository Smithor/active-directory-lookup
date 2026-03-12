[![Build](https://github.com/Smithor/active-directory-lookup/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Smithor/active-directory-lookup/actions/workflows/build.yml) [![](https://jitpack.io/v/Smithor/active-directory-lookup.svg)](https://jitpack.io/#Smithor/active-directory-lookup/master-SNAPSHOT)

# Archive Notice

When I forked this library, this was the best I could find, but it had a good number of problems that needed to be solved. 
Some were easy, like the exceptions, but when it got to the search system, the built in one merely handles everything as a 
string. If you try to do anything meta like grab the oid of a user, it will be absolutely useless to you. It wasn't worth 
fixing, and then I found the Unbound [ldapsdk](https://github.com/pingidentity/ldapsdk), which with a little bit of reading, was basically just as easy. 

The following code accomplishes the same thing as the ActiveDirectoryAuthenticator, but you have much more control.
```java
String user = username+"@"+domain;
try (LDAPConnection connection = new LDAPConnection(domainController, 389, user, password)) {

    WhoAmIExtendedResult result = (WhoAmIExtendedResult) connection.processExtendedOperation(new WhoAmIExtendedRequest());

		Filter filter = Filter.createANDFilter(
				Filter.create("objectClass=user"),
				Filter.createEqualityFilter("sAMAccountName", username)
		);

		System.out.println(result.getAuthorizationID());
		SearchRequest request = new SearchRequest(Util.baseDNFromDomain(domain), SearchScope.SUB, filter, ALL_OPERATIONAL_ATTRIBUTES, "*");
	} catch (LDAPException e) {
		throw new RuntimeException(e);
	}
```

If I do end up needing more, I would just make a wrapper for ldapsdk instead.

Active Directory Lookup
=======================

Active Directory Lookup is an extremely simple Java API to access MS Active Directory for common tasks like user authentication and search. This lightweight library does not depend on any other library (No transitive dependencies) and is merely 17Kb in size. It also provides a minimal [CLI](https://github.com/kdabir/active-directory-lookup/blob/master/src/main/java/io/github/kdabir/adl/cli/ActiveDirectoryCLI.java) for quick operations.


## Quick Start

Assuming we know the values of these variables 

```java
String domain;              // e.g. acme.org
String url;                 // e.g. ldap://somehost.acme.org or ldap://someotherhost.com
String searchBase;          // e.g. dc=acme,dc=org
String username;            // e.g. johndoe
String password;            // e.g. password
```

### Authenticating with Active Directory

```java
authenticator = new ActiveDirectoryAuthenticator(domain, url); // check out other constructors
    
authenticator.authenticate(username, password);
```

### Searching in Active Directory

```java
searcher = new SimpleActiveDirectorySearcher(url, domain, username, password, searchBase);

searcher.searchByUsername("superman");
```
### Building LdapContext

```java
LdapContext ldapContext = ActiveDirectoryAuthenticator
                .getDefaultActiveDirectoryBinder()
                .getLdapContext(url, domain, username, password);
```

## Installation

The built library can be consumed directly from jitpack repo

### Using Gradle

Add this at the top of build.gradle

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```    
Add/merge the dependency in the `dependencies` section    

```groovy
dependencies {
    implementation "com.github.Smithor:active-directory-lookup:master-SNAPSHOT" 
}
```

### Using Maven

Add this to the `pom.xml`

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add this to the `dependencies` section within `pom.xml`

```xml
<dependency>
    <groupId>com.github.Smithor</groupId>
    <artifactId>active-directory-lookup</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

## API Documentation

Browse the [JavaDoc](https://jitpack.io/com/kdabir/active-directory-lookup/1.0.2/javadoc/) for 
details. The key methods to look out for are: 
  
- [`ActiveDirectoryAuthenticator::authenticate(..)`](https://jitpack.io/com/kdabir/active-directory-lookup/1.0.2/javadoc/io/github/kdabir/adl/api/ActiveDirectoryAuthenticator.html#authenticate-java.lang.String-java.lang.String-) 
- [`ActiveDirectoryAuthenticator::isValid(..)`](https://jitpack.io/com/kdabir/active-directory-lookup/1.0.2/javadoc/io/github/kdabir/adl/api/ActiveDirectoryAuthenticator.html#isValid-java.lang.String-java.lang.String-)  
- [`ActiveDirectorySearcher::search(..)`](https://jitpack.io/com/kdabir/active-directory-lookup/1.0.2/javadoc/io/github/kdabir/adl/api/ActiveDirectorySearcher.html#search-io.github.kdabir.adl.api.filters.SearchFilter-)
- [`SimpleActiveDirectorySearcher::searchByUsername(..)`](https://jitpack.io/com/kdabir/active-directory-lookup/1.0.2/javadoc/io/github/kdabir/adl/api/SimpleActiveDirectorySearcher.html#searchByUsername-java.lang.String-)

## Building Locally

The project is built and packaged using Maven.

## References :

* http://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-ldap.html
* http://docs.oracle.com/javase/tutorial/jndi/ops/faq.html
* http://technet.microsoft.com/en-us/library/aa996205(v=exchg.65).aspx#BasicLDAPSyntax


## Finding configuration


`nslookup -type=srv _ldap._tcp.DOMAINNAME`
