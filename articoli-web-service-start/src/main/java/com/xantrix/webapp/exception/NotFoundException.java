package com.xantrix.webapp.exception;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = -8836150399016051845L;
	
	private String messaggio = "Elemento non trovato";

	public NotFoundException() {}
	
	public NotFoundException(String messaggio) {
		super();
		this.messaggio = messaggio;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	
}
