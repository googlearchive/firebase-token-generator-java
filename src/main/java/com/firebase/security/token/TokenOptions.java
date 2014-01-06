package com.firebase.security.token;

import java.util.Date;

/**
 * Token options.
 * 
 * @author vikrum
 *
 */
public class TokenOptions {
	
	private Date expires;
	private Date notBefore;
	private boolean admin;
	private boolean debug;
	
	/**
	 * Default constructor.
	 */
	public TokenOptions() {
		expires = null;
		notBefore = null;
		admin = false;
		debug = false;
	}

	/**
	 * Parametrized constructor.
	 * 
	 * @param expires The date/time at which the token should no longer be considered valid. (default is never).
	 * @param notBefore The date/time before which the token should not be considered valid. (default is now).
	 * @param admin Set to true to bypass all security rules (you can use this for trusted server code).
	 * @param debug Set to true to enable debug mode (so you can see the results of Rules API operations).
	 */
	public TokenOptions(Date expires, Date notBefore, boolean admin, boolean debug) {
		super();
		this.expires = expires;
		this.notBefore = notBefore;
		this.admin = admin;
		this.debug = debug;
	}

	/**
	 * @return the expires
	 */
	public Date getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(Date expires) {
		this.expires = expires;
	}

	/**
	 * @return the notBefore
	 */
	public Date getNotBefore() {
		return notBefore;
	}

	/**
	 * @param notBefore the notBefore to set
	 */
	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
}
