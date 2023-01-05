package com.project1.result.DTO;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalResultantData {
	
	
	HashMap<String, List<ResultantData>> resultantData = new HashMap<String, List<ResultantData>>();

	public HashMap<String,List<ResultantData>> getResultantData() {
		return resultantData;
	}

	public void setResultantData(HashMap<String, List<ResultantData>> resultantData) {
		this.resultantData = resultantData;
	}

}
