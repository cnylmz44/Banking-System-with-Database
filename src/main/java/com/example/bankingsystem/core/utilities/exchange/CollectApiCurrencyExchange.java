package com.example.bankingsystem.core.utilities.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CollectApiCurrencyExchange {
	private RestTemplate client;
	private HttpHeaders headers;
	private String contentType = "application/json";
	private String authorizationKey = "apikey 2nQdEnX6qpUmaoIT9WPANc:4XK9FD3scr56zboC47Zmp8";
	private String currencyExchangeUrl = "https://api.collectapi.com/economy/exchange?";
	private String goldPricesUrl = "https://api.collectapi.com/economy/goldPrice";
	private HttpEntity<?> requestEntity;

	@Autowired
	public CollectApiCurrencyExchange() {
		// Default Authorization Settings
		client = new RestTemplate();
		headers = new HttpHeaders();
		headers.add("content-type", contentType);
		headers.add("authorization", authorizationKey);
		requestEntity = new HttpEntity<>(headers);
	}

	public int exchangeCurrencies(int amount, String type, String exchangedType) {
		String url = currencyExchangeUrl + "int=" + amount + "&to=" + exchangedType + "&base=" + type;
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		int exchangedAmount;
		try {
			JsonNode node = objectMapper.readTree(responseBody);
			JsonNode resultNode = node.get("result");
			JsonNode dataNode = resultNode.get("data");
			for (JsonNode value : dataNode) {
				exchangedAmount = value.get("calculated").intValue();
				return exchangedAmount;
			}
			return (Integer) null;
		} catch (Exception e) {
			return (Integer) null;
		}
	}

	public int getGramGoldPriceInTRY(String priceType) {
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = client.exchange(goldPricesUrl, HttpMethod.GET, requestEntity, String.class);
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode node = objectMapper.readTree(responseBody);
			JsonNode resultNode = node.get("result");
			for (JsonNode goldNode : resultNode) {
				String name = goldNode.get("name").toString();
				if (name.contains("Gram Altın"))
					return goldNode.get(priceType).intValue();
			}
		} catch (Exception e) {
			return (Integer) null;
		}
		return (Integer) null;
	}

}
