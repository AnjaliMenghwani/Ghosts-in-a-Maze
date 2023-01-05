package com.project1.agent.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import com.project1.map.model.Coordinates;
import com.project1.map.model.GhostInRange;
import com.project1.map.model.Map;
import com.project1.map.model.Node;
import com.project1.map.utility.CommonNodeMethods;
import com.project1.map.utility.CommonlyUsedMethods;
import com.project1.map.utility.Constants;

public class CommonTraverseMethods {

	public static Random randVar = new Random();
	
	public static void setReachableNodesPathToGoal(Map outputMap) {

		List<Coordinates> allReachableNodes = outputMap.getAllReachableNodes();
		
		HashMap<String, List<Coordinates>> reachableNodesData = new HashMap<>();
		reachableNodesData.put((new Coordinates(0, 0)).toString(), outputMap.getFinalPathToGoal());
		
		for(Coordinates cood: allReachableNodes) {
			List<Coordinates> pathToGoal = new ArrayList<>();
			HashMap<String, Boolean> visitedMap = new HashMap<>();
			pathToGoal = findThePathToGoal(new Node(cood.getX(), cood.getY()), visitedMap, Constants.GOAL_STATE,
					new Map(outputMap), new ArrayList<>());
			reachableNodesData.put(cood.toString(), pathToGoal);
		}
		outputMap.setReachableNodeWisePathToGoal(reachableNodesData);
		
	}
	

	public static Boolean isGhostOnAgentPath(List<Coordinates> agentGoalPath, ArrayList<Node> ghosts) {

		ArrayList<Coordinates> ghostPositions = new ArrayList<>();
		for (Node ghost : ghosts) {
			Coordinates gPos = ghost.getCurPosition();
			ghostPositions.add(gPos);
		}
		Boolean ghostOnGoalPath = CommonlyUsedMethods.containsAny(agentGoalPath, ghostPositions);
		return ghostOnGoalPath;
	}
	
	public static Boolean isGhostOnAgentPathForAgent5(List<Coordinates> agentGoalPath, 
			ArrayList<Node> ghosts, int[][] mapGrid) {

		ArrayList<Coordinates> ghostPositions = new ArrayList<>();
		for (Node ghost : ghosts) {
			Coordinates gPos = ghost.getCurPosition();
			if(mapGrid[gPos.getX()][gPos.getY()] == Constants.UNBLOCKED) {
				ghostPositions.add(gPos);
			}	
		}
		Boolean ghostOnGoalPath = CommonlyUsedMethods.containsAny(agentGoalPath, ghostPositions);
		return ghostOnGoalPath;
	}

	/**
	 * Description: The methods checks whether a path to the destination exists in
	 * the given map or not
	 * 
	 * @param generatedMap
	 * @return True if it does, else false
	 */
	public static Boolean validate(Map generatedMap) {

		Coordinates goalState = new Coordinates(generatedMap.getGridSize() - 1, generatedMap.getGridSize() - 1);

		Node currPos = new Node(0, 0);

		HashMap<String, Boolean> visitedMap = new HashMap<>();
		findThePathToGoal(currPos, visitedMap, goalState, generatedMap, null);

		Set<String> allReachableNodes_Set = visitedMap.keySet();
		List<Coordinates> allReachableNodes = new ArrayList<>();
		for (String reachableNode : allReachableNodes_Set) {
			String[] tempNode = reachableNode.split(" ");
			Coordinates tempCoordinates = new Coordinates(Integer.parseInt(tempNode[0]), Integer.parseInt(tempNode[1]));
			allReachableNodes.add(tempCoordinates);
		}
		generatedMap.setAllReachableNodes(allReachableNodes);
		if (generatedMap.getFinalPathToGoal() != null && !generatedMap.getFinalPathToGoal().isEmpty()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public static List<Coordinates> findThePathToGoal(Node currentNode, HashMap<String, Boolean> visitedMap,
			Coordinates goalState, Map generatedMap, ArrayList<Node> ghosts) {

		int gridSize = generatedMap.getGridSize();
		ArrayList<Coordinates> ghostPositions = new ArrayList<>(CommonNodeMethods.getGhostPosition(ghosts));
		
		PriorityQueue<Node> fringe = new PriorityQueue<>(
				(node1, node2) -> Integer.compare(node1.getPriorityValue(), node2.getPriorityValue()));
		Node newNode = new Node(currentNode.getCurPosition().getX(), currentNode.getCurPosition().getY());
		fringe.add(newNode);

		Boolean goalReached = Boolean.FALSE;

		while (!fringe.isEmpty()) {
			Node topElement = fringe.poll();

			if (goalState.equals(topElement.getCurPosition()) && !goalReached) {
				
				// I need to pass the path to goal to the agent. Map is one thing which will
				// remain constant throughout
				// the code.
				List<Coordinates> finalPath = new ArrayList<>(topElement.getPath());
				generatedMap.setFinalPathToGoal(finalPath);
				goalReached = Boolean.TRUE;
				break;
			}

			int x = topElement.getCurPosition().getX();
			int y = topElement.getCurPosition().getY();

			Boolean ghostPosOnGrid = CommonNodeMethods.compareCurrPosWithGhostPositions(x, y, ghostPositions);

			if (CommonlyUsedMethods.checkForOutOfBounds(x, y, gridSize)
					|| visitedMap.containsKey(String.valueOf(x + " " + y))
					|| (ghosts != null && !ghosts.isEmpty() && ghostPosOnGrid)) {
				continue;
			}
			visitedMap.put(topElement.getCurPosition().getX() + " " + topElement.getCurPosition().getY(), Boolean.TRUE);

			// Add the left Node
			if (y - 1 >= 0 && generatedMap.getGrid()[x][y - 1] == Constants.UNBLOCKED) {
				fringe.add(new Node(x, y - 1, topElement));
			}

			// Add the up Node
			if (x - 1 >= 0 && generatedMap.getGrid()[x - 1][y] == Constants.UNBLOCKED) {
				fringe.add(new Node(x - 1, y, topElement));
			}

			// Add the down Node
			if (x + 1 < gridSize && generatedMap.getGrid()[x + 1][y] == Constants.UNBLOCKED) {
				fringe.add(new Node(x + 1, y, topElement));
			}

			// Add the right Node
			if (y + 1 < gridSize && generatedMap.getGrid()[x][y + 1] == Constants.UNBLOCKED) {
				fringe.add(new Node(x, y + 1, topElement));
			}

		}
		currentNode.setPath(generatedMap.getFinalPathToGoal());

		return generatedMap.getFinalPathToGoal();
	}
	
	public static List<Coordinates> findThePathToGoalForAgent2(Node currentNode, Coordinates goalState, 
			Map generatedMap, ArrayList<Node> ghosts) {

		int gridSize = generatedMap.getGridSize();
		HashMap<String, Boolean> visitedMap = new HashMap<>();
		
		List<Coordinates> finalPath = new ArrayList<>();
		Coordinates currPos = new Coordinates(currentNode.getCurPosition()); 
		finalPath.add(currPos);
		

		ArrayList<Coordinates> ghostPositions = new ArrayList<>(CommonNodeMethods.getGhostPosition(ghosts));
		
		if(ghostPositions.contains(goalState)) {
			return new ArrayList<Coordinates>();
		}
		
		PriorityQueue<Node> fringe = new PriorityQueue<>(
				(node1, node2) -> Integer.compare(node1.getPriorityValue(), node2.getPriorityValue()));
		Node newNode = new Node(currentNode.getCurPosition().getX(), currentNode.getCurPosition().getY());
		fringe.add(newNode);

		Boolean goalReached = Boolean.FALSE;

		while (!fringe.isEmpty()) {
			Node topElement = fringe.poll();
			if (goalState.equals(topElement.getCurPosition()) && !goalReached) {
				// I need to pass the path to goal to the agent. Map is one thing which will
				// remain constant throughout
				// the code.
				finalPath = new ArrayList<>(topElement.getPath());
				generatedMap.setFinalPathToGoal(finalPath);
				goalReached = Boolean.TRUE;
				break;
			}

			int x = topElement.getCurPosition().getX();
			int y = topElement.getCurPosition().getY();

			if (CommonlyUsedMethods.checkForOutOfBounds(x, y, gridSize)
					|| visitedMap.containsKey(String.valueOf(x + " " + y))) {
				continue;
			}
			
			visitedMap.put(topElement.getCurPosition().getX() + " " + topElement.getCurPosition().getY(), Boolean.TRUE);

			// Add the left Node
			if (y - 1 >= 0 && generatedMap.getGrid()[x][y - 1] == Constants.UNBLOCKED 
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x, y - 1, ghostPositions)) {
				fringe.add(new Node(x, y - 1, topElement));
			}

			// Add the up Node
			if (x - 1 >= 0 && generatedMap.getGrid()[x - 1][y] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x - 1, y, ghostPositions)) {
				fringe.add(new Node(x - 1, y, topElement));
			}

			// Add the down Node
			if (x + 1 < gridSize && generatedMap.getGrid()[x + 1][y] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x + 1, y, ghostPositions)) {
				fringe.add(new Node(x + 1, y, topElement));
			}

			// Add the right Node
			if (y + 1 < gridSize && generatedMap.getGrid()[x][y + 1] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x, y + 1, ghostPositions)) {
				fringe.add(new Node(x, y + 1, topElement));
			}
		}
		currentNode.setPath(finalPath);
		if(finalPath.size() == 1) {
			finalPath = new ArrayList<>();
		}

		return finalPath;
	}

	public static void moveAwayFromClosestGhost(Node agent, ArrayList<Node> ghosts, int[][] mapGrid) {
		ArrayList<Node> visibleGhosts = new ArrayList<>();
		visibleGhosts = CommonNodeMethods.findAllVisibleGhosts(ghosts, mapGrid);

		Integer minDisFromGhost = Integer.MAX_VALUE;
		Node closestGhost = new Node();

		ArrayList<Node> possibleAgentNextMoves = new ArrayList<>();
		Integer maxDisFromGhost = Integer.MIN_VALUE;
		Node finalNextMove = new Node();

		if (visibleGhosts != null && !visibleGhosts.isEmpty()) {
			for (Node ghost : visibleGhosts) {
				Integer nodeDistance = CommonlyUsedMethods.getManhattanDistance(agent, ghost);
				if (nodeDistance < minDisFromGhost) {
					minDisFromGhost = nodeDistance;
					closestGhost = ghost;
				}
			}
			possibleAgentNextMoves = CommonNodeMethods.getValidNodeChildren(agent, mapGrid);
			for (Node nextMove : possibleAgentNextMoves) {				
				Integer nodeDistance = CommonlyUsedMethods.getManhattanDistance(nextMove, closestGhost);
				if (nodeDistance > maxDisFromGhost) {
					maxDisFromGhost = nodeDistance;
					finalNextMove = nextMove;
				}
			}
			agent.setCurPosition(finalNextMove.getCurPosition());				
		}
	}
	
	public static void moveAwayFromGhost(Node agent, Node closestGhost, int[][] mapGrid) {
		Integer maxDisFromGhost = Integer.MIN_VALUE;
		Node finalNextMove = new Node();
		ArrayList<Node> possibleAgentNextMoves = CommonNodeMethods.getValidNodeChildren(agent, mapGrid);
		for (Node nextMove : possibleAgentNextMoves) {				
			Integer nodeDistance = CommonlyUsedMethods.getManhattanDistance(nextMove, closestGhost);
			if (nodeDistance > maxDisFromGhost) {
				maxDisFromGhost = nodeDistance;
				finalNextMove = nextMove;
			}
		}
		agent.setCurPosition(finalNextMove.getCurPosition());	
	}
	
	public static Node getClosestGhostNode(Node agent, ArrayList<Node> ghosts, int[][] mapGrid) {
		ArrayList<Node> visibleGhosts = new ArrayList<>();
		visibleGhosts = CommonNodeMethods.findAllVisibleGhosts(ghosts, mapGrid);

		Integer minDisFromGhost = Integer.MAX_VALUE;
		Node closestGhost = new Node();

		if (visibleGhosts != null && !visibleGhosts.isEmpty()) {
			for (Node ghost : visibleGhosts) {
				Integer nodeDistance = CommonlyUsedMethods.getManhattanDistance(agent, ghost);
				if (nodeDistance < minDisFromGhost) {
					minDisFromGhost = nodeDistance;
					closestGhost = ghost;
				}
			}				
		}
		return closestGhost;
	}

	public static Boolean moveAgent2(Node agent, ArrayList<Node> ghosts, Map newMap, List<Coordinates> pathToGoal, 
			List<Coordinates> actualPathTaken, Boolean goalReached, Integer manhattanThreshold) {

		while (!agent.getCurPosition().equals(Constants.GOAL_STATE)) {
			
			if(manhattanThreshold != null && CommonlyUsedMethods.getManhattanDistance(agent, Constants.GOAL_NODE) <= manhattanThreshold) {
				goalReached = Boolean.TRUE;
				break; 
			}
			
			int index = pathToGoal.indexOf(agent.getCurPosition());

			Boolean agentDead = CommonNodeMethods.ghostEatsAgent(ghosts, agent);

			if (agentDead) {
				goalReached = Boolean.FALSE;
				break;
			}
			
			/**
			 * Update this method according to when you want complete or partial visibility of ghosts.
			 */
//			Boolean isGhostOnPath = isGhostOnAgentPath(pathToGoal, ghosts);
			Boolean isGhostOnPath = isGhostOnAgentPathForAgent5(pathToGoal, ghosts, newMap.getGrid());

			if (isGhostOnPath) {
				// Update agent path to goal				
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent2(agent, Constants.GOAL_STATE, newMap, ghosts);

				newMap.setFinalPathToGoal(pathToGoal);				
			}
			// Check if no path exists, then move away from the closest ghost
			if (pathToGoal.size() < index + 1 || index < 0 || pathToGoal.isEmpty()) {

				moveAwayFromClosestGhost(agent, ghosts, newMap.getGrid());
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent2(agent, Constants.GOAL_STATE, newMap, ghosts);
				newMap.setFinalPathToGoal(pathToGoal);					
			}
			else {
				// removing the previous position
				if(pathToGoal.size() > 1) {
					pathToGoal.remove(0);
				}
				// accessing the next move for the agent
				Coordinates nextMove = pathToGoal.get(0);
				agent.setCurPosition(nextMove);
			}
			actualPathTaken.add(agent.getCurPosition());
			
			if(agent.getCurPosition().equals(Constants.GOAL_STATE)) {
				goalReached = Boolean.TRUE;
				break;
			}

			/**
			 * Update Ghost positions as soon as the agent moves
			 */
			ghosts = CommonNodeMethods.updateGhostPositions(ghosts, newMap);
		}

		return goalReached;
	}

	public static Coordinates getNextMoveFromSimulation(Node agent, ArrayList<Node> Ghosts, Map newMap) {

		int x = agent.getCurPosition().getX();
		int y = agent.getCurPosition().getY();
		Coordinates nextMove = new Coordinates();
		ArrayList<Node> ghosts = new ArrayList<>(Ghosts);
		
		// If my Simulation has reached atleast 75% of the grid, then i'll consider that path to be a valid one 
		Double threshold = (0.25 * (2 * newMap.getGridSize()));
		int manhattanThreshold = threshold.intValue();

		/**
		 * DIRECTIONS | INDEX
		 * 
		 * DOWN 	: 0 
		 * UP 		: 1 
		 * RIGHT 	: 2 
		 * LEFT 	: 3 
		 * SAME 	: 4
		 */
		int count[] = new int[5];
		List<Coordinates> moveSet = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			count[i] = 0;
		}
		moveSet.add(new Coordinates(x + 1, y)); // Down : 0
		moveSet.add(new Coordinates(x - 1, y)); // Up : 1
		moveSet.add(new Coordinates(x, y + 1)); // Right : 2
		moveSet.add(new Coordinates(x, y - 1)); // Left : 3
		moveSet.add(new Coordinates(x, y)); 	// Same : 4

		/**
		 * Move Down
		 */
		if (!CommonlyUsedMethods.checkForOutOfBounds(x + 1, y, newMap.getGridSize())
				&& newMap.getGrid()[x + 1][y] != Constants.BLOCKED) {
			for (int i = 0; i < Constants.NO_OF_SIMULATIONS_PER_MOVE_FOR_AGENT_3; i++) {
				Map tempMap = new Map(newMap);
				Node tempAgent = new Node(x + 1, y);
				ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
				Boolean success = Boolean.FALSE;
				List<Coordinates> tempPathToGoal = new ArrayList<>();
				List<Coordinates> tempActualPathTaken = new ArrayList<>();
				success = moveAgent2(tempAgent, tempGhosts, tempMap, tempPathToGoal, tempActualPathTaken,
						Boolean.FALSE, manhattanThreshold);
				if (success) {
					count[0]++;
				}
			}
		}

		/**
		 * Move Up
		 */
		if (!CommonlyUsedMethods.checkForOutOfBounds(x - 1, y, newMap.getGridSize())
				&& newMap.getGrid()[x - 1][y] != Constants.BLOCKED) {
			for (int i = 0; i < Constants.NO_OF_SIMULATIONS_PER_MOVE_FOR_AGENT_3; i++) {
				Map tempMap = new Map(newMap);
				Node tempAgent = new Node(x - 1, y);
				ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
				Boolean success = Boolean.FALSE;
				List<Coordinates> tempPathToGoal = new ArrayList<>();
				List<Coordinates> tempActualPathTaken = new ArrayList<>();
				success = moveAgent2(tempAgent, tempGhosts, tempMap, tempPathToGoal, tempActualPathTaken,
						Boolean.FALSE, manhattanThreshold);
				if (success) {
					count[1]++;
				}
			}
		}

		/**
		 * Move Right
		 */
		if (!CommonlyUsedMethods.checkForOutOfBounds(x, y + 1, newMap.getGridSize())
				&& newMap.getGrid()[x][y + 1] != Constants.BLOCKED) {
			for (int i = 0; i < Constants.NO_OF_SIMULATIONS_PER_MOVE_FOR_AGENT_3; i++) {
				Map tempMap = new Map(newMap);
				Node tempAgent = new Node(x, y + 1);
				ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
				Boolean success = Boolean.FALSE;
				List<Coordinates> tempPathToGoal = new ArrayList<>();
				List<Coordinates> tempActualPathTaken = new ArrayList<>();
				success = moveAgent2(tempAgent, tempGhosts, tempMap, tempPathToGoal, tempActualPathTaken,
						Boolean.FALSE, manhattanThreshold);
				if (success) {
					count[2]++;
				}
			}
		}

		/**
		 * Move Left
		 */
		if (!CommonlyUsedMethods.checkForOutOfBounds(x, y - 1, newMap.getGridSize())
				&& newMap.getGrid()[x][y - 1] != Constants.BLOCKED) {
			for (int i = 0; i < Constants.NO_OF_SIMULATIONS_PER_MOVE_FOR_AGENT_3; i++) {
				Map tempMap = new Map(newMap);
				Node tempAgent = new Node(x, y - 1);
				ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
				Boolean success = Boolean.FALSE;
				List<Coordinates> tempPathToGoal = new ArrayList<>();
				List<Coordinates> tempActualPathTaken = new ArrayList<>();
				success = moveAgent2(tempAgent, tempGhosts, tempMap, tempPathToGoal, tempActualPathTaken,
						Boolean.FALSE, manhattanThreshold);
				if (success) {
					count[3]++;
				}
			}
		}

		/**
		 * Stay at the same place
		 */
		if (!CommonlyUsedMethods.checkForOutOfBounds(x, y, newMap.getGridSize())
				&& newMap.getGrid()[x][y] != Constants.BLOCKED) {
			for (int i = 0; i < Constants.NO_OF_SIMULATIONS_PER_MOVE_FOR_AGENT_3; i++) {
				Map tempMap = new Map(newMap);
				Node tempAgent = new Node(x, y);
				ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
				Boolean success = Boolean.FALSE;
				List<Coordinates> tempPathToGoal = new ArrayList<>();
				List<Coordinates> tempActualPathTaken = new ArrayList<>();
				success = moveAgent2(tempAgent, tempGhosts, tempMap, tempPathToGoal, tempActualPathTaken,
						Boolean.FALSE, manhattanThreshold);
				if (success) {
					count[4]++;
				}
			}
		}

		List<Integer> maxIndices = new ArrayList<>();
		int maxValue = 0;
		for (int i = 0; i < 5; i++) {
			if (count[i] > maxValue) {
				maxValue = count[i];
				maxIndices = new ArrayList<>();
				maxIndices.add(i);
			}
			if (count[i] == maxValue) {
				maxIndices.add(i);
			}
		}
		if (maxIndices.size() == 1) {
			nextMove = new Coordinates(moveSet.get(maxIndices.get(0)));
		} else if (maxIndices.size() > 1) {
			List<Coordinates> nextPossibleMoves = new ArrayList<>();
			for(Integer index: maxIndices) {
				nextPossibleMoves.add(moveSet.get(index));
			}
			nextMove = decideNextMoveAccToAgent2(nextPossibleMoves, agent, new ArrayList<Node>(ghosts), newMap);
		} else {
			moveAwayFromClosestGhost(agent, ghosts, newMap.getGrid());
			nextMove = new Coordinates(agent.getCurPosition().getX(), agent.getCurPosition().getY());
		}
		return nextMove;
	}
	
	public static Coordinates decideNextMoveAccToAgent2(List<Coordinates> nextPossibleMoves, Node agent, 
			ArrayList<Node> ghosts, Map currentMap) {
		
		List<Coordinates> pathToGoal = new ArrayList<>();
		Coordinates currPos = agent.getCurPosition();
		
		//need to add the condition when the current position of the agent isn't in the reachable nodes, thus call A* from that position
		if(currentMap.getReachableNodeWisePathToGoal().containsKey(currPos.toString())) {
			pathToGoal = new ArrayList<>(currentMap.getReachableNodeWisePathToGoal().get(currPos.toString()));
		}
		else {
			ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
			pathToGoal = findThePathToGoalForAgent2(agent, Constants.GOAL_STATE, currentMap,
					tempGhosts);
		}
		
		if(CommonlyUsedMethods.containsAny(nextPossibleMoves, pathToGoal)) {
			for(Coordinates nextMove: nextPossibleMoves) {
				if(nextMove.equals(pathToGoal.get(1))) {
					return nextMove;
				}
			}
		}

		Boolean isGhostOnPath = isGhostOnAgentPath(pathToGoal, ghosts);

		if (isGhostOnPath) {
			pathToGoal = new ArrayList<>();
			ArrayList<Node> tempGhosts = CommonlyUsedMethods.createDeepCopy(ghosts);
			pathToGoal = findThePathToGoalForAgent2(agent, Constants.GOAL_STATE, currentMap,
					tempGhosts);
		}
		// Check if no path exists, then move away from the closest ghost
		if (pathToGoal == null || pathToGoal.isEmpty()) {

			moveAwayFromClosestGhost(agent, ghosts, currentMap.getGrid());
			return agent.getCurPosition();
		}
		
		//Zero'th index contains the current position of the node
		if(pathToGoal.size() > 1) {
			return pathToGoal.get(1);
		}
		return pathToGoal.get(0);
	}
	
	public static Boolean moveAgent4(Node agent, ArrayList<Node> ghosts, Map newMap, List<Coordinates> pathToGoal, 
			List<Coordinates> actualPathTaken, Boolean goalReached) {

		while (!agent.getCurPosition().equals(Constants.GOAL_STATE)) {
			
			int agentRange = (CommonlyUsedMethods.getManhattanDistance(agent, Constants.GOAL_NODE)) / 4;
			
			int index = pathToGoal.indexOf(agent.getCurPosition());

			Boolean agentDead = CommonNodeMethods.ghostEatsAgent(ghosts, agent);

			if (agentDead) {
				goalReached = Boolean.FALSE;
				break;
			}
			GhostInRange isGhostInRange = isGhostInAgentRange(agent, ghosts, newMap, agentRange, Boolean.FALSE);
			Boolean closestGhostsOnPath = isGhostOnAgentPath(pathToGoal, isGhostInRange.getClosestGhosts());

			if (isGhostInRange.getInRange() && closestGhostsOnPath) {

				// Update agent path to goal				
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent4(agent, Constants.GOAL_STATE, newMap, isGhostInRange.getClosestGhosts());

				newMap.setFinalPathToGoal(pathToGoal);				
			}
			
			// Check if no path exists, then move away from the closest ghost
			if (pathToGoal.size() < index + 1 || index < 0 || pathToGoal.isEmpty()) {

				moveAwayFromClosestGhost(agent, isGhostInRange.getClosestGhosts(), newMap.getGrid());
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent4(agent, Constants.GOAL_STATE, newMap, isGhostInRange.getClosestGhosts());
				newMap.setFinalPathToGoal(pathToGoal);				
			}
			else {
				// removing the previous position
				if(pathToGoal.size() > 1) {
					pathToGoal.remove(0);
				}
				// accessing the next move for the agent
				Coordinates nextMove = pathToGoal.get(0);
				agent.setCurPosition(nextMove);
			}
			actualPathTaken.add(agent.getCurPosition());
			
			if(agent.getCurPosition().equals(Constants.GOAL_STATE)) {
				goalReached = Boolean.TRUE;
				break;
			}

			/**
			 * Update Ghost positions as soon as the agent moves
			 */
			ghosts = CommonNodeMethods.updateGhostPositions(ghosts, newMap);
		}

		return goalReached;
	}


	private static GhostInRange isGhostInAgentRange(Node agent, ArrayList<Node> ghosts, Map map, 
			int agentRange, Boolean agent5) {
		
		int gridSize = map.getGridSize();
		GhostInRange output = new GhostInRange();
		Coordinates currPos = agent.getCurPosition();
		int x = currPos.getX();
		int y = currPos.getY();
		int minDistance = Integer.MAX_VALUE;
		List<Coordinates> mapAroundAgent = new ArrayList<>();
		int range = 1;
		
		/**
		 * Check all 8 directions to look for the ghost
		 */
		while(range <= agentRange) {
			if(x + range < gridSize) {
				mapAroundAgent.add(new Coordinates(x + range, y));
			}
			if(x - range >= 0) {
				mapAroundAgent.add(new Coordinates(x - range, y));
			}
			if(y + range < gridSize) {
				mapAroundAgent.add(new Coordinates(x, y + range));
			}
			if(y - range >= 0) {
				mapAroundAgent.add(new Coordinates(x, y - range));
			}
			if(x + range < gridSize && y + range < gridSize) {
				mapAroundAgent.add(new Coordinates(x + range, y + range));
			}
			if(x + range < gridSize && y - range >= 0) {
				mapAroundAgent.add(new Coordinates(x + range, y - range));
			}
			if(x - range >= 0 && y + range < gridSize) {
				mapAroundAgent.add(new Coordinates(x - range, y + range));
			}
			if(x - range >= 0 && y - range >= 0) {
				mapAroundAgent.add(new Coordinates(x - range, y - range));
			}
			range++;
		}
		
		ArrayList<Node> ghostsInRange = new ArrayList<>();
		
		for(Coordinates cood: mapAroundAgent) {
			for(Node ghost: ghosts) {
				Coordinates gPos = ghost.getCurPosition();
				if(gPos.equals(cood)) {
					output.setInRange(Boolean.TRUE);
					int distOfAgent = CommonlyUsedMethods.getManhattanDistance(agent, ghost);
					if(distOfAgent < minDistance) {
						minDistance = distOfAgent;
						
					}
					if(!agent5 || (agent5 && map.getGrid()[gPos.getX()][gPos.getY()] == Constants.UNBLOCKED)) {
						ghostsInRange.add(ghost);
					}	
				}
			}
			output.setClosestGhosts(ghostsInRange);
		}	
		return output;
	}
	
	public static List<Coordinates> findThePathToGoalForAgent4(Node currentNode, Coordinates goalState, 
			Map generatedMap, ArrayList<Node> ghosts) {

		int gridSize = generatedMap.getGridSize();
		HashMap<String, Boolean> visitedMap = new HashMap<>();
		
		List<Coordinates> finalPath = new ArrayList<>();
		Coordinates currPos = new Coordinates(currentNode.getCurPosition()); 
		finalPath.add(currPos);
		

		ArrayList<Coordinates> ghostPositions = new ArrayList<>(CommonNodeMethods.getGhostPosition(ghosts));
		
		if(ghostPositions.contains(goalState)) {
			return new ArrayList<Coordinates>();
		}
		
		PriorityQueue<Node> fringe = new PriorityQueue<>(
				(node1, node2) -> Integer.compare(node1.getPriorityValue(), node2.getPriorityValue()));
		Node newNode = new Node(currentNode.getCurPosition().getX(), currentNode.getCurPosition().getY());
		fringe.add(newNode);

		Boolean goalReached = Boolean.FALSE;

		while (!fringe.isEmpty()) {
			Node topElement = fringe.poll();
			if (goalState.equals(topElement.getCurPosition()) && !goalReached) {
				// Need to pass the path to goal to the agent. Map is one thing which will
				// remain constant throughout
				// the code.
				finalPath = new ArrayList<>(topElement.getPath());
				generatedMap.setFinalPathToGoal(finalPath);
				goalReached = Boolean.TRUE;
				break;
			}

			int x = topElement.getCurPosition().getX();
			int y = topElement.getCurPosition().getY();
			
			/**
			 * We're creating a bubble of unit distance around the agent so as to ensure the agent is 
			 * able to survive for as long as possible.
			 */
			Boolean ghostPosInAgentUnitRange = CommonNodeMethods.CurrPosGhostPositionsWithUnitDistance(x, y, ghostPositions, gridSize);			

			if (CommonlyUsedMethods.checkForOutOfBounds(x, y, gridSize)
					|| visitedMap.containsKey(String.valueOf(x + " " + y))
					|| ghostPosInAgentUnitRange) {
				continue;
			}
			
			visitedMap.put(topElement.getCurPosition().getX() + " " + topElement.getCurPosition().getY(), Boolean.TRUE);

			// Add the left Node
			if (y - 1 >= 0 && generatedMap.getGrid()[x][y - 1] == Constants.UNBLOCKED 
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x, y - 1, ghostPositions)) {
				fringe.add(new Node(x, y - 1, topElement));
			}

			// Add the up Node
			if (x - 1 >= 0 && generatedMap.getGrid()[x - 1][y] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x - 1, y, ghostPositions)) {
				fringe.add(new Node(x - 1, y, topElement));
			}

			// Add the down Node
			if (x + 1 < gridSize && generatedMap.getGrid()[x + 1][y] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x + 1, y, ghostPositions)) {
				fringe.add(new Node(x + 1, y, topElement));
			}

			// Add the right Node
			if (y + 1 < gridSize && generatedMap.getGrid()[x][y + 1] == Constants.UNBLOCKED
					&& !CommonNodeMethods.compareCurrPosWithGhostPositions(x, y + 1, ghostPositions)) {
				fringe.add(new Node(x, y + 1, topElement));
			}
		}
		currentNode.setPath(finalPath);
		if(finalPath.size() == 1) {
			finalPath = new ArrayList<>();
		}

		return finalPath;
	}
	
	public static Boolean moveAgent5(Node agent, ArrayList<Node> ghosts, Map newMap, List<Coordinates> pathToGoal, 
			List<Coordinates> actualPathTaken, Boolean goalReached) {

		while (!agent.getCurPosition().equals(Constants.GOAL_STATE)) {
			
			int agentRange = (CommonlyUsedMethods.getManhattanDistance(agent, Constants.GOAL_NODE)) / 4;
			
			int index = pathToGoal.indexOf(agent.getCurPosition());

			Boolean agentDead = CommonNodeMethods.ghostEatsAgent(ghosts, agent);

			if (agentDead) {
				goalReached = Boolean.FALSE;
				break;
			}
			GhostInRange isGhostInRange = isGhostInAgentRange(agent, ghosts, newMap, agentRange, Boolean.TRUE);
			Boolean closestGhostsOnPath = isGhostOnAgentPath(pathToGoal, isGhostInRange.getClosestGhosts());

			if (isGhostInRange.getInRange() && closestGhostsOnPath) {

				// Update agent path to goal				
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent4(agent, Constants.GOAL_STATE, newMap, isGhostInRange.getClosestGhosts());

				newMap.setFinalPathToGoal(pathToGoal);					
			}
			
			// Check if no path exists, then move away from the closest ghost
			if (pathToGoal.size() < index + 1 || index < 0 || pathToGoal.isEmpty()) {

				moveAwayFromClosestGhost(agent, isGhostInRange.getClosestGhosts(), newMap.getGrid());
				pathToGoal = new ArrayList<>();
				pathToGoal = findThePathToGoalForAgent4(agent, Constants.GOAL_STATE, newMap, isGhostInRange.getClosestGhosts());
				newMap.setFinalPathToGoal(pathToGoal);					
			}
			else {
				// removing the previous position
				if(pathToGoal.size() > 1) {
					pathToGoal.remove(0);
				}
				// accessing the next move for the agent
				Coordinates nextMove = pathToGoal.get(0);
				agent.setCurPosition(nextMove);
			}
			actualPathTaken.add(agent.getCurPosition());
			
			if(agent.getCurPosition().equals(Constants.GOAL_STATE)) {
				goalReached = Boolean.TRUE;
				break;
			}

			/**
			 * Update Ghost positions as soon as the agent moves
			 */
			ghosts = CommonNodeMethods.updateGhostPositions(ghosts, newMap);
		}

		return goalReached;
	}

}
