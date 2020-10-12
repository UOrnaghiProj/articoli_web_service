package com.xantrix.webapp.exception;

import java.util.Date;

import lombok.Data;

@Data
public class ErrorResponse {
	
	private Date data;
	private int codice;
	private String Messaggio;

}
