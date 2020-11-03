package com.xantrix.webapp.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ArticoliDTO {

	private String codArt;
	private String descrizione;	
	private String um;
	private String codStat;
	private Integer pzCart;
	private double pesoNetto;
	private String idStatoArt;
	private Date dataCreaz;
	private Double prezzo;

	
}
