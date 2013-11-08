package com.firebase.security.token;

import java.util.Date;
import java.lang.IllegalArgumentException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Firebase JWT token generator.
 * 
 * @author vikrum
 *
 */
public class TokenGenerator {
	
	private static final int TOKEN_VERSION = 0;
	
	private String firebaseSecret;

	/**
	 * Default constructor given a Firebase secret.
	 * 
	 * @param firebaseSecret
	 */
	public TokenGenerator(String firebaseSecret) {
		super();
		this.firebaseSecret = firebaseSecret;
	}
	
	/**
	 * Create a token for the given object.
	 * 
	 * @param data
	 * @return
	 */
	public String createToken(JSONObject data) {
		return createToken(data, new TokenOptions());
	}
	
	/**
	 * Create a token for the given object and options.
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	public String createToken(JSONObject data, TokenOptions options) {
		if ((data == null || data.length() == 0) && (options == null || (!options.isAdmin() && !options.isDebug()))) {
			throw new IllegalArgumentException("TokenGenerator.createToken: data is empty and no options are set.  This token will have no effect on Firebase.");
		}

		JSONObject claims = new JSONObject();
		
		try {
			claims.put("v", TOKEN_VERSION);
			claims.put("iat", new Date().getTime() / 1000);
			
			if(data != null && data.length() > 0) {
				claims.put("d", data);
			}
			
			// Handle options
			if(options != null) {
				if(options.getExpires() != null) {
					claims.put("exp", options.getExpires().getTime() / 1000);
				}
				
				if(options.getNotBefore() != null) {
					claims.put("nbf", options.getNotBefore().getTime() / 1000);
				}
				
				// Only add these claims if they're true to avoid sending them over the wire when false.
				if(options.isAdmin()) {
					claims.put("admin", options.isAdmin());
				}
				
				if(options.isDebug()) {
					claims.put("debug", options.isDebug());	
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return computeToken(claims);
	}

	private String computeToken(JSONObject claims) {
		return JWTEncoder.encode(claims, firebaseSecret);
	}
}
