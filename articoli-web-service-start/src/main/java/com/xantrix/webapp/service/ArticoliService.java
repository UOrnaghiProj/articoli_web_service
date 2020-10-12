package com.xantrix.webapp.service;

import java.util.List;

import com.xantrix.webapp.entities.Articoli;

public interface ArticoliService {

	public List<Articoli> SelByDescrizione(String Descrizione);
	public Articoli SelByCodArt(String codArt);
	public Articoli SelByBarCode(String BarCode);
	public void DelArticolo(Articoli articolo);
	public void InsArticolo(Articoli articolo);
	
}
