package com.xantrix.webapp.UnitTest.ControllerTest;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xantrix.webapp.Application;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

 

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectArtTest
{
	private MockMvc mockMvc;

	private String authServerUrl = "http://localhost:9100/auth";
	private String tokenJWT = "";
	private String userId = "Umberto";
	private String password = "password";
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() throws JsonMappingException, JsonProcessingException, JSONException
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		this.getTokenFromAuthServer();

	}
	
	private void getTokenFromAuthServer() throws JSONException, JsonMappingException, JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		JSONObject userJson = new JSONObject();
		userJson.put("username", userId);
		userJson.put("password", password);
		
		HttpEntity<String> request = new HttpEntity<String>(userJson.toString(), headers);
		
		String jsonToken = restTemplate.postForObject(authServerUrl, request, String.class);
		JsonNode root = objectMapper.readTree(jsonToken);
		String token = root.path("token").asText();
		
		this.tokenJWT = "Bearer " + token;
	}
	
	private String ApiBaseUrl = "/api/articoli";
	
	String JsonData =  
			"{\n" + 
			"    \"codArt\": \"002000301\",\n" + 
			"    \"descrizione\": \"ACQUA ULIVETO 15 LT\",\n" + 
			"    \"um\": \"PZ\",\n" + 
			"    \"codStat\": \"\",\n" + 
			"    \"pzCart\": 6,\n" + 
			"    \"pesoNetto\": 1.5,\n" + 
			"    \"idStatoArt\": \"1\",\n" + 
			"    \"dataCreaz\": \"2010-06-14\",\n" + 
			"    \"prezzo\": 1.07,\r\n" +
			"    \"barcode\": [\n" + 
			"        {\n" + 
			"            \"barcode\": \"8008490000021\",\n" + 
			"            \"idTipoArt\": \"CP\"\n" + 
			"        }\n" + 
			"    ],\n" + 
			"    \"famAssort\": {\n" + 
			"        \"id\": 1,\n" + 
			"        \"descrizione\": \"DROGHERIA ALIMENTARE\"\n" + 
			"    },\n" + 
			"    \"ingredienti\": null,\n" + 
			"    \"iva\": {\n" + 
			"        \"idIva\": 22,\n" + 
			"        \"descrizione\": \"IVA RIVENDITA 22%\",\n" + 
			"        \"aliquota\": 22\n" + 
			"    }\n" + 
			"}";
	
	/*
	 * 
	 */
//	@Test
//	public void A_listArtByEan() throws Exception
//	{
//		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/ean/8008490000021").header("Authorization", tokenJWT)
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//				 //articoli
//				.andExpect(jsonPath("$.codArt").exists())
//				.andExpect(jsonPath("$.codArt").value("002000301"))
//				.andExpect(jsonPath("$.descrizione").exists())
//				.andExpect(jsonPath("$.descrizione").value("ACQUA ULIVETO 15 LT"))
//				.andExpect(jsonPath("$.um").exists())
//				.andExpect(jsonPath("$.um").value("PZ"))
//				.andExpect(jsonPath("$.codStat").exists())
//				.andExpect(jsonPath("$.codStat").value(""))
//				.andExpect(jsonPath("$.pzCart").exists())
//				.andExpect(jsonPath("$.pzCart").value("6"))
//				.andExpect(jsonPath("$.pesoNetto").exists())
//				.andExpect(jsonPath("$.pesoNetto").value("1.5"))
//				.andExpect(jsonPath("$.idStatoArt").exists())
//				.andExpect(jsonPath("$.idStatoArt").value("1"))
//				.andExpect(jsonPath("$.dataCreaz").exists())
//				.andExpect(jsonPath("$.dataCreaz").value("2010-06-14"))
//				.andExpect(jsonPath("$.prezzo").value("0.9630000000000001"))
//				 //barcode
//				.andExpect(jsonPath("$.barcode[0].barcode").exists())
//				.andExpect(jsonPath("$.barcode[0].barcode").value("8008490000021")) 
//				.andExpect(jsonPath("$.barcode[0].idTipoArt").exists())
//				.andExpect(jsonPath("$.barcode[0].idTipoArt").value("CP")) 
//				 //famAssort
//				.andExpect(jsonPath("$.famAssort.id").exists())
//				.andExpect(jsonPath("$.famAssort.id").value("1")) 
//				.andExpect(jsonPath("$.famAssort.descrizione").exists())
//				.andExpect(jsonPath("$.famAssort.descrizione").value("DROGHERIA ALIMENTARE")) 
//				 //ingredienti
//				.andExpect(jsonPath("$.ingredienti").isEmpty())
//				 //Iva
//				.andExpect(jsonPath("$.iva.idIva").exists())
//				.andExpect(jsonPath("$.iva.idIva").value("22")) 
//				.andExpect(jsonPath("$.iva.descrizione").exists())
//				.andExpect(jsonPath("$.iva.descrizione").value("IVA RIVENDITA 22%"))
//				.andExpect(jsonPath("$.iva.aliquota").exists())
//				.andExpect(jsonPath("$.iva.aliquota").value("22"))	
//				
//				.andDo(print());
//	}
	
	private String Barcode = "8008490002138";
	
	@Test
	public void B_ErrlistArtByEan() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/ean/" + Barcode).header("Authorization", tokenJWT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codice").value(404))
				.andExpect(jsonPath("$.messaggio").value("Il barcode " + Barcode + " non è stato trovato!"))
				.andDo(print());
	}
	
//	@Test
//	public void C_listArtByCodArt() throws Exception
//	{
//		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/codice/002000301").header("Authorization", tokenJWT)
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//				.andExpect(content().json(JsonData)) 
//				.andReturn();
//	}
	
	private String CodArt = "002000301b";
	
	@Test
	public void D_ErrlistArtByCodArt() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/codice/" + CodArt).header("Authorization", tokenJWT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codice").value(404))
				.andExpect(jsonPath("$.messaggio").value("L'articolo con codice " + CodArt + " non è stato trovato!"))
				.andDo(print());
	}
	
	private String JsonData2 = "[" + JsonData + "]";

//	@Test
//	public void E_listArtByDesc() throws Exception
//	{
//		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/descrizione/ACQUA ULIVETO 15 LT").header("Authorization", tokenJWT)
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$", hasSize(1)))
//				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//				//.andExpect(content().json(JsonData2)) 
//				.andReturn();
//	}
	
	@Test
	public void G_TestErrlistArtByDesc() throws Exception
	{
		String Filter = "123ABC";
		
		mockMvc.perform(MockMvcRequestBuilders.get(ApiBaseUrl + "/cerca/descrizione/" + Filter).header("Authorization", tokenJWT)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codice").value(404))
				.andExpect(jsonPath("$.messaggio").value("Non è stato trovato alcun articolo avente descrizione " + Filter))
				.andReturn();
	}
}
