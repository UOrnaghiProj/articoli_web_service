package com.xantrix.webapp.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ArticoliService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @author umberto
 *
 */

@RestController
@RequestMapping("/api/articoli")
@Api(value="alphashop", tags="Controller Operazioni di gestione dati articoli")
public class ArticoliController
{
	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);

	@Autowired
	private ArticoliService articoliService;
	
	@Autowired
	private ResourceBundleMessageSource errMessage;

	
	@ApiOperation(
		      value = "Ricerca l'articolo per BARCODE", 
		      notes = "Restituisce i dati dell'articolo in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "L'articolo cercato è stato trovato!"),
	    @ApiResponse(code = 404, message = "L'articolo cercato NON è stato trovato!"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- Ricerca Per Barcode ------------------------------------
	@RequestMapping(value = "/cerca/ean/{barcode}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Articoli> listArtByEan(@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode)
		throws NotFoundException	 
	{
		logger.info("****** Otteniamo l'articolo con barcode " + Barcode + " *******");

		Articoli articolo = articoliService.SelByBarCode(Barcode);
		
		if (articolo == null)
		{
			String ErrMsg = String.format("Il barcode %s non è stato trovato!", Barcode);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		 
		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
	}
	
	@ApiOperation(
		      value = "Ricerca l'articolo per CODICE", 
		      notes = "Restituisce i dati dell'articolo in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{ 	@ApiResponse(code = 200, message = "L'articolo cercato è stato trovato!"),
		@ApiResponse(code = 404, message = "L'articolo cercato NON è stato trovato!"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- Ricerca Per Codice ------------------------------------
	@RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Articoli> listArtByCodArt(@PathVariable("codart") String CodArt)  
			throws NotFoundException
	{
		logger.info("****** Otteniamo l'articolo con codice " + CodArt + " *******");

		Articoli articolo = articoliService.SelByCodArt(CodArt);

		if (articolo == null)
		{
			String ErrMsg = String.format("L'articolo con codice %s non è stato trovato!", CodArt);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
	}
	
	@ApiOperation(
		      value = "Ricerca l'articolo per DESCRIZIONE o parte di essa", 
		      notes = "Restituisce un collezione di articoli in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "L'articolo/i sono stati trovati"),
		@ApiResponse(code = 404, message = "Non è stato trovato alcun articolo"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- Ricerca Per Descrizione ------------------------------------
	@RequestMapping(value = "/cerca/descrizione/{filter}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Articoli>> listArtByDesc(@PathVariable("filter") String Filter)
			throws NotFoundException
	{
		logger.info("****** Otteniamo gli articoli con Descrizione: " + Filter + " *******");

		List<Articoli> articoli = articoliService.SelByDescrizione(Filter + "%");

		if (articoli.size() == 0)
		{
			String ErrMsg = String.format("Non è stato trovato alcun articolo avente descrizione %s", Filter);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
			
		}

		return new ResponseEntity<List<Articoli>>(articoli, HttpStatus.OK);
	}
	
	@ApiOperation(
		      value = "Inserimento dati NUOVO articolo", 
		      notes = "Può essere usato solo per l'inserimento dati di un nuovo articolo",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo salvati con successo"),
		@ApiResponse(code = 400, message = "Uno o più dati articolo non validi"),
		@ApiResponse(code = 406, message = "Inserimento dati articolo esistente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- INSERIMENTO ARTICOLO ------------------------------------
	@RequestMapping(value = "/inserisci", method = RequestMethod.POST)
	public ResponseEntity<?> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult) 
			throws BindingException, DuplicateException
	{
		logger.info("Salviamo l'articolo con codice " + articolo.getCodArt());
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}
		 
		//Disabilitare se si vuole gestire anche la modifica 
		Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt != null)
		{
			String MsgErr = String.format("Articolo %s presente in anagrafica! "
					+ "Impossibile utilizzare il metodo POST", articolo.getCodArt());
			
			logger.warn(MsgErr);
			
			throw new DuplicateException(MsgErr);
		}
		
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		
		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		articoliService.InsArticolo(articolo);
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Inserimento Articolo " + articolo.getCodArt() + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
	}
	
	@ApiOperation(
		      value = "MODIFICA dati articolo in anagrfica", 
		      notes = "Può essere usato solo per la modifica dati di un articolo presente in anagrafica",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo salvati con successo"),
		@ApiResponse(code = 400, message = "Uno o più dati articolo non validi"),
		@ApiResponse(code = 404, message = "Articolo non presente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- MODIFICA ARTICOLO ------------------------------------
	@RequestMapping(value = "/modifica", method = RequestMethod.PUT)
	public ResponseEntity<?> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
				UriComponentsBuilder ucBuilder) throws BindingException,NotFoundException  
	{
		logger.info("Modifichiamo l'articolo con codice " + articolo.getCodArt());
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}
		
		Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt == null)
		{
			String MsgErr = String.format("Articolo %s non presente in anagrafica! "
					+ "Impossibile utilizzare il metodo PUT", articolo.getCodArt());
			
			logger.warn(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}
		
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		
		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		articoliService.InsArticolo(articolo);
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Modifica Articolo " + articolo.getCodArt() + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
	}
	
	@ApiOperation(
		      value = "ELIMINAZIONE dati articolo in anagrfica", 
		      notes = "Si esegue una eliminazione a cascata dei barcode e degli ingredienti",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo eliminati con successo"),
		@ApiResponse(code = 404, message = "Articolo non presente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	// ------------------- ELIMINAZIONE ARTICOLO ------------------------------------
	@RequestMapping(value = "/elimina/{codart}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteArt(@PathVariable("codart") String CodArt)
			throws  NotFoundException 
	{
		logger.info("Eliminiamo l'articolo con codice " + CodArt);

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		Articoli articolo = articoliService.SelByCodArt(CodArt);

		if (articolo == null)
		{
			String MsgErr = String.format("Articolo %s non presente in anagrafica!",CodArt);
			
			logger.warn(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}

		articoliService.DelArticolo(articolo);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Articolo " + CodArt + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}

	
}