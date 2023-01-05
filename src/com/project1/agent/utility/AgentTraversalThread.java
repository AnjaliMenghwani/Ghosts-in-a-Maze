package com.project1.agent.utility;

import java.util.concurrent.Callable;

import com.project1.agent.service.MapTraversalImpl;
import com.project1.map.model.Map;
import com.project1.result.DTO.ResultantData;

public class AgentTraversalThread implements Callable<Boolean> {

	
	private Map map = new Map();
	ResultantData resultantData = new ResultantData();
	int noOfGhosts = 0;
	Agent agent = null;	

	public AgentTraversalThread(Map map, ResultantData resultantData, int noOfGhosts, Agent agent)
	{
		this.map = map;
		this.resultantData = resultantData;
		this.noOfGhosts = noOfGhosts;
		this.agent = agent;
	}
	
	
	private boolean moveAgent() {
		
		Boolean success = true;
		
		switch (agent) {
		
		case AGENT1:{
			success  = MapTraversalImpl.moveAgent1(map, noOfGhosts);
		}
		break;
		case AGENT2:{
			success = MapTraversalImpl.moveAgent2(map, noOfGhosts);
		}
		break;
		case AGENT3:{
			success = MapTraversalImpl.moveAgent3(map, noOfGhosts);
		}
		break;
		case AGENT4:{
			success = MapTraversalImpl.moveAgent4(map, noOfGhosts);
		}
		break;
		case AGENT5:{
			success = MapTraversalImpl.moveAgent5(map, noOfGhosts);
		}
		break;
		}
		if(success) {			
			System.out.println("success");
		}
		

		return success;
	
	}

	@Override
	public Boolean call() throws Exception {boolean success = true;
	
		return success = moveAgent();
	
	}

}
