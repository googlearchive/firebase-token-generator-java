package com.firebase.security.token;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/* Added some basic test; token generator needs to be refactored to accept
 * configurable timestamp source to allow for more thorough testing.
 * 
 * Results:
 * 

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.firebase.security.token.BasicTokenGeneratorTest
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.465 sec

Results :

Tests run: 7, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.631s
[INFO] Finished at: Mon Jul 15 17:27:52 PDT 2013
[INFO] Final Memory: 10M/152M
[INFO] ------------------------------------------------------------------------

 */
public class BasicTokenGeneratorTest {

    private final String FIREBASE_SUPER_SECRET_KEY = "moozooherpderp";

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void checkIfBasicLength() {
        Map<String, Object> payload = new HashMap<String, Object>();

        TokenGenerator tokenGenerator = new TokenGenerator("x");
        String token = tokenGenerator.createToken(payload);
    }

    @Test
    public void checkBasicStructureHasCorrectNumberOfFragments() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);

        String[] tokenFragments = token.split("\\.");

        assertTrue("Token has the proper number of fragments: jwt metadata, payload, and signature", tokenFragments.length == 3);
    }

    @Test
    public void checkIfResultProperlyDoesNotHavePadding() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);

        assertTrue(token.indexOf('=') < 0);
    }

    @Test
    public void checkIfResultIsUrlSafePlusSign() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);

        assertTrue(token.indexOf('+') < 0);
    }

    @Test
    public void checkIfResultIsUrlSafePlusSlash() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);

        assertTrue(token.indexOf('/') < 0);
    }

    @Test
    public void checkIfResultHasWhiteSpace() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("a", "apple");
        payload.put("b", "banana");
        payload.put("c", "carrot");
        payload.put("number", Double.MAX_VALUE);
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");
        payload.put("herp1", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.?");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(token);
        boolean hasWhiteSpace = matcher.find();

        assertFalse("Token has white space", hasWhiteSpace);
    }

    @Test
    public void basicInspectionTest() {
        String customData = "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|";
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "1");
        payload.put("abc", customData);

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        TokenOptions tokenOptions = new TokenOptions(new Date(), new Date(), true, true);

        String token = tokenGenerator.createToken(payload, tokenOptions);

        String[] tokenFragments = token.split("\\.");

        String header = tokenFragments[0];
        String claims = tokenFragments[1];

        try {
            header = new String(Base64.decodeBase64(header), "UTF-8");
            claims = new String(Base64.decodeBase64(claims), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }

        try {
            JSONObject jsonHeader = new JSONObject(header);
            assertEquals("Got alg", "HS256", jsonHeader.get("alg"));
            assertEquals("Got typ", "JWT", jsonHeader.get("typ"));
        } catch (JSONException e) {
            fail(e.getMessage());
        }

        try {
            JSONObject jsonClaims = new JSONObject(claims);
            assertEquals("Got version", 0, jsonClaims.get("v"));

            JSONObject jsonData = jsonClaims.getJSONObject("d");
            assertEquals("Got data", customData, jsonData.get("abc"));
            assertNotNull("Got some exp", jsonClaims.getLong("exp"));
            assertNotNull("Got some iat", jsonClaims.getLong("iat"));
            assertNotNull("Got some nbf", jsonClaims.getLong("nbf"));
            assertTrue("Admin", jsonClaims.getBoolean("admin"));
            assertTrue("Debug", jsonClaims.getBoolean("debug"));
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void requireUidInPayload() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("abc", "0123456789~!@#$%^&*()_+-=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./;'[]\\<>?\"{}|");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void requireUidStringInPayload() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", 1);

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test
    public void allowMaxLengthUid() {
        Map<String, Object> payload = new HashMap<String, Object>();
        //                          10        20        30        40        50        60        70        80        90       100       110       120       130       140       150       160       170       180       190       200       210       220       230       240       250   256
        payload.put("uid", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void disallowUidTooLong() {
        Map<String, Object> payload = new HashMap<String, Object>();
        //                          10        20        30        40        50        60        70        80        90       100       110       120       130       140       150       160       170       180       190       200       210       220       230       240       250    257
        payload.put("uid", "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test
    public void allowEmptyStringUid() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void disallowTokensTooLong() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", "blah");
        payload.put("longVar", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345612345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234561234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload);
    }

    @Test
    public void allowNoUidWithAdmin() {
        TokenOptions tokenOptions = new TokenOptions();
        tokenOptions.setAdmin(true);

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(null, tokenOptions);
        Map<String, Object> payload1 = new HashMap<String, Object>();
        String token1 = tokenGenerator.createToken(payload1, tokenOptions);
        Map<String, Object> payload2 = new HashMap<String, Object>();
        payload2.put("foo", "bar");
        String token2 = tokenGenerator.createToken(payload2, tokenOptions);
        Map<String, Object> payload3 = new HashMap<String, Object>();
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void disallowInvalidUidWithAdmin1() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", 1);

        TokenOptions tokenOptions = new TokenOptions();
        tokenOptions.setAdmin(true);

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload, tokenOptions);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void disallowInvalidUidWithAdmin2() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("uid", null);

        TokenOptions tokenOptions = new TokenOptions();
        tokenOptions.setAdmin(true);

        TokenGenerator tokenGenerator = new TokenGenerator(FIREBASE_SUPER_SECRET_KEY);
        String token = tokenGenerator.createToken(payload, tokenOptions);
    }

}
