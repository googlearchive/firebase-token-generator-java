package com.firebase.security.token;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JWT encoder.
 * 
 * @author vikrum
 *
 */
public class JWTEncoder {
	
	private static final String TOKEN_SEP = ".";
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final String HMAC_256 = "HmacSHA256";
	
	/**
	 * Encode and sign a set of claims.
	 * 
	 * @param claims
	 * @param secret
	 * @return
	 */
	public static String encode(JSONObject claims, String secret) {
		
		String encodedHeader = getCommonHeader();
		String encodedClaims = encodeJson(claims);
		
		String secureBits = new StringBuilder(encodedHeader).append(TOKEN_SEP).append(encodedClaims).toString();
		
		String sig = sign(secret, secureBits);
		
		return new StringBuilder(secureBits).append(TOKEN_SEP).append(sig).toString();
	}
	
	private static String sign(String secret, String secureBits) {
		String result = null;
		try {
			Mac sha256_HMAC = Mac.getInstance(HMAC_256);
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(UTF8_CHARSET), HMAC_256);
			sha256_HMAC.init(secret_key);
			result = Hex.encodeHexString(sha256_HMAC.doFinal(secureBits.getBytes(UTF8_CHARSET)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getCommonHeader() {
		JSONObject headerJson = new JSONObject();
		try {
			headerJson.put("typ", "JWT");
			headerJson.put("alg", "HS256");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return encodeJson(headerJson);
	}
	
	private static String encodeJson(JSONObject jsonData) {
		return Base64.encodeBase64String(jsonData.toString().getBytes(UTF8_CHARSET));
	}

}
