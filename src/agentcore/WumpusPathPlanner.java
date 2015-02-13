package agentcore;

import java.util.ArrayList;
import java.util.List;
import pathplanning.Vertex;

/**
 * This class contains some auxiliary code for path planning.
 * 
 * @author Bas Testerink
 */

public class WumpusPathPlanner { 
	/** Returns the moves to get to a goal. */
	public static ArrayList<Integer> planPath(Vertex goal){
		ArrayList<Integer> plan = new ArrayList<Integer>();
		if(goal.getDistance() > 0 && goal.getDistance() < Integer.MAX_VALUE){
			Vertex to = goal; 
			Vertex from = nearestSpot(goal.getNeighbors()); 
			while(to != null){
				GridCell fromG = (GridCell)from;
				GridCell toG = (GridCell)to; 
				if(fromG.getX() > toG.getX()) plan.add(WumpusConstants.LEFT);
				else if(fromG.getX() < toG.getX()) plan.add(WumpusConstants.RIGHT);
				else if(fromG.getY() > toG.getY()) plan.add(WumpusConstants.DOWN);
				else if(fromG.getY() < toG.getY()) plan.add(WumpusConstants.UP);
				
				if(from.getDistance() == 0) to = null;
				else {
					to = from;
					from = nearestSpot(from.getNeighbors());
				}
			}
		}
		
		return plan;
	}
	
	/** Returns the nearest vertex of a given list. */
	public static Vertex nearestSpot(List<Vertex> spots){
		Vertex min = null;
		for(int i = 0; i < spots.size(); i++)
			if(spots.get(i).getDistance()!=Integer.MAX_VALUE &&
			   (min==null || min.getDistance() > spots.get(i).getDistance()))
				min = spots.get(i);
		return min;
	}
}
