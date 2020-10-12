package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;

public interface ArticoliRepository extends JpaRepository<Articoli, String>{
	
	Articoli findByCodArt(String codArt);
	
	List<Articoli> findByDescrizioneLike(String Descrizione);
	
	/*utilizzo JPQL per una query piu complessa*/
	@Query(value="SELECT a FROM Articoli a JOIN a.barcode b WHERE b.barcode IN (:ean)")
	Articoli SelByEan(@Param("ean") String ean);

}
