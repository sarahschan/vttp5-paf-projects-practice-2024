package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class ForexService {

	RestTemplate restTemplate = new RestTemplate();

	public float convert(String from, String to, float amount) {

		String apiUrl = "https://api.frankfurter.app/latest?base=%s&symbols=%s";
		String apiUrlFormatted = String.format(apiUrl, from, to);

		try {
			String currencyData = restTemplate.getForObject(apiUrlFormatted, String.class);

			JsonObject dataObject = Json.createReader(new StringReader(currencyData)).readObject();
			JsonObject rates = dataObject.getJsonObject("rates");

			float exchangeRate = (float) rates.getJsonNumber(to.toUpperCase()).doubleValue();
			float convertedSGD = amount * exchangeRate;

			return convertedSGD;

		} catch (Exception e) {
			return -1000f;
		}

	}
}
