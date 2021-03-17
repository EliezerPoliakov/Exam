package com.weathermonitor.exam.application.service;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class ScheduleService implements Runnable{
	    private int city_id;
	    

		static RestTemplate restTemplate = new RestTemplate();
		
		static final String url = "https://api.openweathermap.org/data/2.5/weather?id=";
		static final String appid = "&units=metric&appid=e735b6b632e6c008be941b8dbdb346d4";
	    
	    public ScheduleService(int city_id){
	        this.city_id = city_id;
	    }
	    
	    @Override
	    public void run() {
	    	String fullUrl = url + city_id + appid;
	    	String data = restTemplate.exchange(fullUrl, HttpMethod.GET, null, String.class).getBody();
	     	System.out.println(parseData(data));
	    }
	    
	    
	    private String parseData(Object obj) {
			JSONParser jsonParser = new JSONParser();
	        
	        JSONObject json = null;
			try {
				json = (JSONObject) jsonParser.parse(obj.toString());
			} catch (ParseException e) {
							e.printStackTrace();
			}
			long timestamp = (long) json.get("dt");
			String city_name = (String) json.get("name");
			double temperature = Double.parseDouble((((JSONObject)(json.get("main"))).get("temp")).toString());  
			double windSpeed =  Double.parseDouble((((JSONObject)(json.get("wind"))).get("speed").toString()));
			
			double prevTemp = TemperatureStorage.temperatureMap.getOrDefault(city_id, temperature);
			TemperatureStorage.temperatureMap.put(city_id, temperature);
			
			String res = "timestamp = " + timestamp + ", city: " + city_name + ",  temperature = " + temperature + ", wind speed = " + windSpeed; 
			if (Math.abs(temperature - prevTemp) / prevTemp  * 100 > TemperatureStorage.thresholdMap.get(city_id))
				res += "\n Warning: weather changed massively.";
			return res;
			
	    }
	 
	   
}
