# Java Firebase Token Generator

Library for generating Firebase authentication tokens from Java.

## Installation
The easiest way to install is via Maven. Add this dependency to your project:

```
<dependency>
  <groupId>com.firebase</groupId>
  <artifactId>firebase-token-generator</artifactId>
  <version>1.0.1</version>
</dependency>
```

Otherwise, you can download the source and directly use it in your project. The
token generator has two run time dependencies:

1. commons-codec-1.7.jar
2. json-20090211.jar

Unit tests depend on jUnit 4.11.

## Usage
To generate a token with an arbitrary auth payload:

```
JSONObject arbitraryPayload = new JSONObject();
try {
    arbitraryPayload.put("some", "arbitrary");
    arbitraryPayload.put("data", "here");
} catch (JSONException e) {
    e.printStackTrace();
}   
    
TokenGenerator tokenGenerator = new TokenGenerator("supersecretkey");
String token = tokenGenerator.createToken(arbitraryPayload);
    
System.out.println(token);
```

You can also specify custom options via a second argument to createToken().  For example, to create an admin token, you could use:

```
TokenGenerator tokenGenerator = new TokenGenerator("supersecretkey");
TokenOptions tokenOptions = new TokenOptions();
tokenOptions.setAdmin(true);
String token = tokenGenerator.createToken(null, tokenOptions);
System.out.println(token);
```

See the [Firebase Authentication Docs](https://www.firebase.com/docs/security/authentication.html) for more information about authentication tokens.

License
-------
[MIT](http://firebase.mit-license.org)
