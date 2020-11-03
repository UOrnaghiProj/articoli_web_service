package com.xantrix.webapp.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PriceArtWebService", url = "localhost:5071")
public interface PriceClient {

	@GetMapping(value = "/api/prezzi/{codArt}")
	public Double getDefPriceArt(@RequestHeader("Authorization") String AuthHeader,
			@PathVariable("codArt") String codArt);

	@GetMapping(value = "/api/prezzi/{codArt}/{idList}")
	public Double getPriceArt(@RequestHeader("Authorization") String AuthHeader, @PathVariable("codArt") String codArt,
			@PathVariable("idList") String idList);
}
