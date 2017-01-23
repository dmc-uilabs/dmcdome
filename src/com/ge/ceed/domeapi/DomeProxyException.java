package com.ge.ceed.domeapi;

/**
 * Marker for exceptions from DomeProxy
 * @author dliscomb
 *
 */
public class DomeProxyException extends Exception {

	private static final long serialVersionUID = 135549991135561170L;

	public DomeProxyException() {
	}

	public DomeProxyException(String message) {
		super(message);
	}

	public DomeProxyException(Throwable cause) {
		super(cause);
	}

	public DomeProxyException(String message, Throwable cause) {
		super(message, cause);
	}

}
