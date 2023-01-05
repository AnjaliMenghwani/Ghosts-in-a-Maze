package com.project1.agent.service;

import java.util.HashMap;
import java.util.List;

import com.project1.agent.utility.Agent;
import com.project1.map.model.Map;

public interface IMapTraversal {
	
	public HashMap<Map, Boolean> Agent1(List<Map> generatedMaps);
	
	public HashMap<Map, Boolean> Agent2(List<Map> generatedMaps);
	
	public HashMap<Map, Boolean> Agent3(List<Map> generatedMaps);
	
	public HashMap<Map, Boolean> Agent4(List<Map> generatedMaps);
	
	public HashMap<Map, Boolean> Agent5(List<Map> validatedMaps);

	public HashMap<Map, Boolean> moveAgent(List<Map> validatedMaps, Agent agent);

}
