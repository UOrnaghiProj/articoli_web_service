package com.xantrix.webapp.service;

import java.util.List;

import javax.transaction.TransactionScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;

@Service
@Transactional(readOnly=true)
@CacheConfig(cacheNames = {"articoli"})
public class ArticoliServiceImpl implements ArticoliService {

	@Autowired
	ArticoliRepository articoliRepository;
	
	@Override
	@Cacheable
	public List<Articoli> SelByDescrizione(String Descrizione) {
		return articoliRepository.findByDescrizioneLike(Descrizione.toUpperCase());
	}

	@Override
	@Cacheable(value = "articolo", key = "#codArt", sync = true)
	public Articoli SelByCodArt(String codArt) {
		return articoliRepository.findByCodArt(codArt);
	}

	@Override
	@Cacheable
	public Articoli SelByBarCode(String BarCode) {
		return articoliRepository.SelByEan(BarCode);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = "articoli", allEntries = true),
			@CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
			})
	public void DelArticolo(Articoli articolo) {
		articoliRepository.delete(articolo);

	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = "articoli", allEntries = true),
			@CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
			})
	public void InsArticolo(Articoli articolo) {
		articoliRepository.save(articolo);
	}

}
