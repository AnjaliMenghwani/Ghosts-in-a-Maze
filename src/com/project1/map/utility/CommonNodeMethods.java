package com.project1.map.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.project1.map.model.Coordinates;
import com.project1.map.model.Map;
import com.project1.map.model.Node;

public class CommonNodeMethods {
	
	public static Random randVar = new Random();
	
	public static Boolean ghostEatsAgent(List<Node> ghosts, Node agent) {
		for(Node ghost: ghosts) {
			if(ghost.getCurPosition().equals(agent.getCurPosition())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public static ArrayList<Node> updateGhostPositions(ArrayList<Node> ghosts, Map mapData) {
		int[][] map = mapData.getGrid();
		
		for(Node ghost: ghosts) {
			
			ArrayList<Node> ghostNextPossiblePositions = getValidGhostChildren(ghost);
			int randomIndx = randVar.nextInt(ghostNextPossiblePositions.size());
			Node ghostNextPosition = ghostNextPossiblePositions.get(randomIndx);
			
			/**
			 * if the probability is less than 0.5 then the ghosts do not enter the new position which is the wall
			 */
			if(map[ghostNextPosition.getCurPosition().getX()][ghostNextPosition.getCurPosition().getY()] == Constants.BLOCKED) {
				double probOfGhostInWall = randVar.nextDouble();
				if(probOfGhostInWall > Constants.PROB_OF_GHOST_IN_WALL) {
					ghost.setCurPosition(ghostNextPosition.getCurPosition());
				}
			}
			else {
				ghost.setCurPosition(ghostNextPosition.getCurPosition());
			}
			
		}
		
		return ghosts;
	}
	
	public static ArrayList<Node> getValidGhostChildren(Node ghost) {
		ArrayList<Node> ghostChildren = new ArrayList<>();
		Coordinates currPos = new Coordinates(ghost.getCurPosition().getX(), ghost.getCurPosition().getY());
		int x = currPos.getX();
		int y = currPos.getY();
		
		if(x - 1 >= 0) {
			ghostChildren.add(new Node(x-1, y));
		}
		if(x + 1 < Constants.GRIDSIZE) {
			ghostChildren.add(new Node(x+1, y));
		}
		if(y - 1 >= 0) {
			ghostChildren.add(new Node(x, y-1));
		}
		if(y + 1 < Constants.GRIDSIZE) {
			ghostChildren.add(new Node(x, y+1));
		}
		
		return ghostChildren;
	}
	
	public static int initializeGhosts(ArrayList<Node> ghosts, List<Coordinates> allReachableNodes, int totalNoOfGhosts) {
		
		if(totalNoOfGhosts == 0) {
			return 0;
		}
		int noOfGhosts = 0;
		while(noOfGhosts  < totalNoOfGhosts) {
			int randomIndx = randVar.nextInt(allReachableNodes.size());
			Coordinates ghostPosition = allReachableNodes.get(randomIndx) != new Coordinates(0, 0) ? 
					allReachableNodes.get(randomIndx) : allReachableNodes.get(randVar.nextInt(allReachableNodes.size()));
			Node ghost = new Node(ghostPosition.getX(), ghostPosition.getY());
			ghosts.add(ghost);
			noOfGhosts++;
		}	
		return noOfGhosts;
	}
	
	public static ArrayList<Coordinates> getGhostPosition(ArrayList<Node> ghosts) {
		
		ArrayList<Coordinates> ghostPositions = new ArrayList<>();
		if(ghosts != null && !ghosts.isEmpty()) {
			for(Node ghost: ghosts) {
				Coordinates gPos = new Coordinates(ghost.getCurPosition().getX(), ghost.getCurPosition().getY());
				ghostPositions.add(gPos);
			}
			return ghostPositions;
		}
		return new ArrayList<>();		
	}

	public static ArrayList<Node> findAllVisibleGhosts(ArrayList<Node> ghosts, int[][] mapGrid) {
		ArrayList<Node> visibleGhosts = new ArrayList<>();
		for(Node ghost: ghosts) {
			Coordinates gPos = ghost.getCurPosition();
			if(mapGrid[gPos.getX()][gPos.getY()] == Constants.UNBLOCKED) {
				visibleGhosts.add(ghost);
			}
		}
		return visibleGhosts;
	}
	
	public static ArrayList<Node> getValidNodeChildren(Node agent, int[][] mapGrid) {
		ArrayList<Node> nodeChildren = new ArrayList<>();
		Coordinates currPos = new Coordinates(agent.getCurPosition().getX(), agent.getCurPosition().getY());
		int x = currPos.getX();
		int y = currPos.getY();
		
		//This adds my current position as a possible move to be taken
		nodeChildren.add(new Node(x,y));
		
		if(x - 1 >= 0 && mapGrid[x-1][y] == Constants.UNBLOCKED) {
			nodeChildren.add(new Node(x-1, y));
		}
		if(x + 1 < Constants.GRIDSIZE && mapGrid[x + 1][y] == Constants.UNBLOCKED) {
			nodeChildren.add(new Node(x+1, y));
		}
		if(y - 1 >= 0 && mapGrid[x][y - 1] == Constants.UNBLOCKED) {
			nodeChildren.add(new Node(x, y-1));
		}
		if(y + 1 < Constants.GRIDSIZE && mapGrid[x][y + 1] == Constants.UNBLOCKED) {
			nodeChildren.add(new Node(x, y+1));
		}
		
		return nodeChildren;
	}
	
	public static Boolean compareCurrPosWithGhostPositions(int x, int y, ArrayList<Coordinates> ghostPositions) {
		if(ghostPositions.isEmpty()) {
			return Boolean.FALSE;
		}
		
		for(Coordinates gPos: ghostPositions) {
			if(gPos.getX() == x && gPos.getY() == y) {
				return Boolean.TRUE;
			}
		}	
		return Boolean.FALSE;
	}
	
	public static Boolean CurrPosGhostPositionsWithUnitDistance(int x, int y, ArrayList<Coordinates> ghostPositions, int gridSize) {
		if(ghostPositions.isEmpty()) {
			return Boolean.FALSE;
		}
		
		List<Coordinates> coods = new ArrayList<>();
		if(CommonlyUsedMethods.checkForOutOfBounds(x + 1, y, gridSize)) {
			coods.add(new Coordinates(x + 1, y));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x - 1, y, gridSize)) {
			coods.add(new Coordinates(x - 1, y));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x, y + 1, gridSize)) {
			coods.add(new Coordinates(x, y + 1));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x, y - 1, gridSize)) {
			coods.add(new Coordinates(x, y - 1));
		}
		
		if(CommonlyUsedMethods.checkForOutOfBounds(x + 1, y + 1, gridSize)) {
			coods.add(new Coordinates(x + 1, y + 1));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x + 1, y - 1, gridSize)) {
			coods.add(new Coordinates(x + 1, y - 1));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x - 1, y + 1, gridSize)) {
			coods.add(new Coordinates(x - 1, y + 1));
		}
		if(CommonlyUsedMethods.checkForOutOfBounds(x - 1, y - 1, gridSize)) {
			coods.add(new Coordinates(x - 1, y - 1));
		}
		
		for(Coordinates gPos: ghostPositions) {
			for(Coordinates cood: coods) {
				if(gPos.getX() == cood.getX() && gPos.getY() == cood.getY()) {
					return Boolean.TRUE;
				}
			}
			
		}	
		return Boolean.FALSE;
	}

}
