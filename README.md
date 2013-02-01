# Java Firebase Token Generator

Library for generating Firebase authentication tokens from Java.

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

See the [Firebase Authentication Docs](https://www.firebase.com/docs/security/authentication.html) for more information about authentication tokens.
