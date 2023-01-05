package com.project1.map.service;

import java.util.List;

import com.project1.map.model.Map;

public interface IMapGenerator {
	
	public List<Map> generateMap();
	
	public Map generate(Map newMap);

}

