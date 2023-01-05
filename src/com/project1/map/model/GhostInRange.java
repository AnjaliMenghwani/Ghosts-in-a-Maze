package com.project1.map.model;

import java.util.ArrayList;

public class GhostInRange {
	
	Boolean inRange;
	ArrayList<Node> closestGhosts;
	
	public GhostInRange() {
		this.inRange = Boolean.FALSE;
		this.closestGhosts = new ArrayList<Node>();
	}
	
	public Boolean getInRange() {
		return inRange;
	}
	public void setInRange(Boolean inRange) {
		this.inRange = inRange;
	}
	public ArrayList<Node> getClosestGhosts() {
		return closestGhosts;
	}
	public void setClosestGhosts(ArrayList<Node> closestGhosts) {
		this.closestGhosts = closestGhosts;
	}
	
}
