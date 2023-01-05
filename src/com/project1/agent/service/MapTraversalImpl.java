package com.project1.agent.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project1.agent.utility.Agent;
import com.project1.agent.utility.AgentTraversalThread;
import com.project1.agent.utility.CommonTraverseMethods;
import com.project1.map.model.Coordinates;
import com.project1.map.model.Map;
import com.project1.map.model.Node;
import com.project1.map.utility.CommonNodeMethods;
import com.project1.map.utility.CommonlyUsedMethods;
import com.project1.map.utility.Constants;
import com.project1.result.DTO.ResultantData;

public class MapTraversalImpl implements IMapTraversal {

	public static Random randVar = new Random();

	@Override
	public HashMap<Map, Boolean> Agent1(List<Map> validatedMaps) {

		HashMap<Map, Boolean> successRate = new HashMap<>();

		moveAgent(validatedMaps, Agent.AGENT1);

		return successRate;
	}

	@Override
	public HashMap<Map, Boolean> Agent2(List<Map> validatedMaps) {

		HashMap<Map, Boolean> successRate = new HashMap<>();

		moveAgent(validatedMaps, Agent.AGENT2);

		return successRate;
	}

	@Override
	public HashMap<Map, Boolean> Agent3(List<Map> validatedMaps) {

		HashMap<Map, Boolean> successRate = new HashMap<>();

		moveAgent(validatedMaps, Agent.AGENT3);

		return successRate;
	}

	@Override
	public HashMap<Map, Boolean> Agent4(List<Map> validatedMaps) {

		HashMap<Map, Boolean> successRate = new HashMap<>();

		moveAgent(validatedMaps, Agent.AGENT4);

		return successRate;
	}

	@Override
	public HashMap<Map, Boolean> Agent5(List<Map> validatedMaps) {

		HashMap<Map, Boolean> successRate = new HashMap<>();

		moveAgent(validatedMaps, Agent.AGENT5);

		return successRate;
	}

	@Override
	public HashMap<Map, Boolean> moveAgent(List<Map> validatedMaps, Agent agent) {

		HashMap<Map, Boolean> successRate = new HashMap<>();
		ExecutorService pool = Executors.newFixedThreadPool(20);
		HashMap<String, List<ResultantData>> allResultantData = new HashMap<>();

		int iter = 1;

		File file = null;

		int mapCounter = 0;
		for (Map eachMap : validatedMaps) {

			mapCounter++;
			List<ResultantData> resultantDataListForMap = new ArrayList<>();
			for (iter = 0; iter < 10; iter++)
//			for(iter = 1; iter < 202 ; iter += 10)
			{
				int noOfGhosts = (int) Math.pow(2, iter);
				// int noOfGhosts = iter;

				ResultantData resultantData = new ResultantData();

				resultantData.setNumberOfGhosts(noOfGhosts);

				Callable<Boolean> callable = new AgentTraversalThread(eachMap, resultantData, noOfGhosts, agent);
				Future<Boolean> success = (Future<Boolean>) pool.submit(callable);

				try {
					resultantData.setSuccess(success.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				resultantDataListForMap.add(resultantData);

				System.out.println("resultant data successs: " + noOfGhosts + " " + resultantData.isSuccess());
//				if(resultantData.isSuccess())
//				{
//					totalSuccess++;
//				}
			}

			allResultantData.put(eachMap.getMapId(), resultantDataListForMap);

			if (mapCounter > 30) {
				break;
			}

		}

		pool.shutdown();

		switch (agent) {
		case AGENT1: {
			file = new File(Constants.AGENT1_RESULTANT_DATA_FILE_PATH);
		}
			break;
		case AGENT2: {
			file = new File(Constants.AGENT2_RESULTANT_DATA_FILE_PATH);

		}
			break;
		case AGENT3: {
			file = new File(Constants.AGENT3_RESULTANT_DATA_FILE_PATH);

		}
			break;
		case AGENT4: {
			file = new File(Constants.AGENT4_RESULTANT_DATA_FILE_PATH);

		}
			break;
		case AGENT5: {
			file = new File(Constants.AGENT5_RESULTANT_DATA_FILE_PATH);

		}
			break;
		}

		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(file, allResultantData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return successRate;
	}

	/**
	 * The agent moves simply according to the path calculated through A* algorithm.
	 * It does not look at the positions of the ghost at all.
	 * @param validatedMap
	 * @param noOfGhosts
	 * @return
	 */
	public static Boolean moveAgent1(Map validatedMap, int noOfGhosts) {
		int gridSize = validatedMap.getGridSize();
		List<Coordinates> pathToGoal = new ArrayList<>(validatedMap.getFinalPathToGoal());
		Coordinates goalState = new Coordinates(gridSize - 1, gridSize - 1);

		Node agent1 = new Node(0, 0);
		ArrayList<Node> ghosts = new ArrayList<>();
		List<Coordinates> allReachableNodes = new ArrayList<>(validatedMap.getAllReachableNodes());

		/**
		 * This ensures that the initial positions of the ghosts are at reachable
		 * positions from the agent
		 */
		noOfGhosts = CommonNodeMethods.initializeGhosts(ghosts, allReachableNodes, noOfGhosts);

		for (Coordinates path : pathToGoal) {

			Boolean agentDead = CommonNodeMethods.ghostEatsAgent(ghosts, agent1);

			if (agentDead) {
				return Boolean.FALSE;
			}

			agent1.setCurPosition(new Coordinates(path.getX(), path.getY()));

			/**
			 * Update Ghost positions as soon as the ghosts move
			 */
			ghosts = CommonNodeMethods.updateGhostPositions(ghosts, validatedMap);

			if (agent1.getCurPosition().equals(goalState)) {
				return Boolean.TRUE;
			}

		}

		return Boolean.FALSE;
	}

	/**
	 * Every time the agent finds a ghost on the path, the agent recalculates the path and moves accordingly.
	 * @param validatedMap
	 * @param noOfGhosts
	 * @return
	 */
	public static Boolean moveAgent2(Map validatedMap, int noOfGhosts) {

		List<Coordinates> pathToGoal = new ArrayList<>(validatedMap.getFinalPathToGoal());
		List<Coordinates> actualPathTaken = new ArrayList<>();

		Map newMap = new Map(validatedMap);

		Node agent2 = new Node(0, 0);
		agent2.setPath(pathToGoal);
		actualPathTaken.add(agent2.getCurPosition());

		ArrayList<Node> ghosts = new ArrayList<>();
		List<Coordinates> allReachableNodes = new ArrayList<>(newMap.getAllReachableNodes());

		noOfGhosts = CommonNodeMethods.initializeGhosts(ghosts, allReachableNodes, noOfGhosts);

		Boolean goalReached = Boolean.FALSE;
		CommonTraverseMethods.moveAgent2(agent2, ghosts, newMap, pathToGoal, actualPathTaken, goalReached, null);

		if (agent2.getCurPosition().equals(Constants.GOAL_STATE)) {
			goalReached = Boolean.TRUE;
		}

		agent2.setActualPathTaken(actualPathTaken);

		return goalReached;
	}

	/**
	 * The agent simulates the map 10 times for each move using the logic of Agent 2.
	 * Ties are broken according to the pre-calculated paths from the current position to goal if available
	 * @param validatedMap
	 * @param noOfGhosts
	 * @return
	 */
	public static Boolean moveAgent3(Map validatedMap, int noOfGhosts) {

		int gridSize = validatedMap.getGridSize();

		List<Coordinates> actualPathTaken = new ArrayList<>();
		actualPathTaken.add(new Coordinates(0, 0));

		Map newMap = new Map(validatedMap);

		HashMap<String, List<Coordinates>> nodeWisePath = new HashMap<>();
		for (Entry<String, List<Coordinates>> pair : validatedMap.getReachableNodeWisePathToGoal().entrySet()) {
			nodeWisePath.put(pair.getKey(), new ArrayList<Coordinates>(pair.getValue()));
		}
		newMap.setReachableNodeWisePathToGoal(nodeWisePath);

		Coordinates goalState = new Coordinates(gridSize - 1, gridSize - 1);

		Node agent3 = new Node(0, 0);

		ArrayList<Node> ghosts = new ArrayList<>();
		List<Coordinates> allReachableNodes = new ArrayList<>(newMap.getAllReachableNodes());

		noOfGhosts = CommonNodeMethods.initializeGhosts(ghosts, allReachableNodes, noOfGhosts);

		Boolean goalReached = Boolean.FALSE;

		while (!agent3.getCurPosition().equals(goalState)) {

			Coordinates nextMove = new Coordinates();

			Boolean agentDead = CommonNodeMethods.ghostEatsAgent(ghosts, agent3);

			if (agentDead) {
				goalReached = Boolean.FALSE;
				break;
			}

			ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
			nextMove = new Coordinates(CommonTraverseMethods.getNextMoveFromSimulation(agent3, tempGhosts, newMap));

			agent3.setCurPosition(nextMove);

			actualPathTaken.add(nextMove);

			/**
			 * // * Update Ghost positions as soon as the agent moves //
			 */
			ghosts = CommonNodeMethods.updateGhostPositions(ghosts, newMap);

		}

		if (agent3.getCurPosition().equals(goalState)) {
			goalReached = Boolean.TRUE;
			agent3.setActualPathTaken(actualPathTaken);
		}

		agent3.setActualPathTaken(actualPathTaken);
		return goalReached;

	}

	/**
	 * Instead of recalculating the path every time I find a ghost on the path, We
	 * set up a radius of 1/4 the distance to the Goal from the agent's current
	 * position. 
	 * @param validatedMap
	 * @param noOfGhosts
	 * @return
	 */
	public static boolean moveAgent4(Map validatedMap, int noOfGhosts) {

		Map newMap = new Map(validatedMap);
		List<Coordinates> pathToGoal = new ArrayList<>(validatedMap.getFinalPathToGoal());
		List<Coordinates> actualPathTaken = new ArrayList<>();

		Node agent4 = new Node(0, 0);
		agent4.setPath(pathToGoal);
		actualPathTaken.add(agent4.getCurPosition());

		ArrayList<Node> ghosts = new ArrayList<>();
		List<Coordinates> allReachableNodes = new ArrayList<>(newMap.getAllReachableNodes());

		noOfGhosts = CommonNodeMethods.initializeGhosts(ghosts, allReachableNodes, noOfGhosts);

		Boolean goalReached = Boolean.FALSE;
		CommonTraverseMethods.moveAgent4(agent4, ghosts, newMap, pathToGoal, actualPathTaken, goalReached);

		if (agent4.getCurPosition().equals(Constants.GOAL_STATE)) {
			goalReached = Boolean.TRUE;
		}

		agent4.setActualPathTaken(actualPathTaken);

		return goalReached;
	}

	/**
	 * This is Agent 4 with partial visibility of ghosts i.e. the agent cannot see
	 * the ghosts within the walls.
	 * @param validatedMap
	 * @param noOfGhosts
	 * @return
	 */
	public static boolean moveAgent5(Map validatedMap, int noOfGhosts) {

		/**
		 * Instead of recalculating the path every time I find a ghost on the path, We
		 * set up a radius of 1/4 the distance to the Goal from the agent's current
		 * position.
		 */

		Map newMap = new Map(validatedMap);
		List<Coordinates> pathToGoal = new ArrayList<>(validatedMap.getFinalPathToGoal());
		List<Coordinates> actualPathTaken = new ArrayList<>();

		Node agent5 = new Node(0, 0);
		agent5.setPath(pathToGoal);
		actualPathTaken.add(agent5.getCurPosition());

		ArrayList<Node> ghosts = new ArrayList<>();
		List<Coordinates> allReachableNodes = new ArrayList<>(newMap.getAllReachableNodes());

		noOfGhosts = CommonNodeMethods.initializeGhosts(ghosts, allReachableNodes, noOfGhosts);

		Boolean goalReached = Boolean.FALSE;
		CommonTraverseMethods.moveAgent5(agent5, ghosts, newMap, pathToGoal, actualPathTaken, goalReached);

		if (agent5.getCurPosition().equals(Constants.GOAL_STATE)) {
			goalReached = Boolean.TRUE;
		}

		agent5.setActualPathTaken(actualPathTaken);

		return goalReached;
	}

}
