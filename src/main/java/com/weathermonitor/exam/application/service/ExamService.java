package com.weathermonitor.exam.application.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class ExamService {
	
	@Autowired
	private TaskScheduler scheduler;
	private HashMap<Integer, Integer> frequencyMap = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	private void parseConfigFile() {
		JSONParser jsonParser = new JSONParser();
        
		
        try (InputStream inputStream = new ClassPathResource("config.json").getInputStream();
        	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            Object obj = jsonParser.parse(reader);
 
            JSONArray cityList = (JSONArray) obj;
            
            cityList.forEach( city -> parseCityObject( (JSONObject) city ) );
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
 
    private void parseCityObject(JSONObject cityObject) 
    {
        int city_id = (int) (long) cityObject.get("city_id");
        int frequency = (int) (long) cityObject.get("frequency");  
        int threshold = (int) (long) cityObject.get("threshold");
        frequencyMap.put(city_id, frequency);
        TemperatureStorage.thresholdMap.put(city_id, threshold);

       
        
    }

	public void scheduleRules() {
		parseConfigFile();
		for(Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
			scheduler.scheduleAtFixedRate(new ScheduleService(entry.getKey()), entry.getValue()*1000);
		}
	}
}
