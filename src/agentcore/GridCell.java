package agentcore;

import java.util.ArrayList;
import java.util.Iterator; 
import pathplanning.Vertex;
/**
 * Container for storing data about a position in the world. Used for belief deliberation.
 * 
 * @author Bas Testerink
 */

public class GridCell extends Vertex { 
	private boolean hasBreeze, hasStench, hasPit, hasWumpus, hasGold, hasChest, 
					hasAgent0, hasAgent1, hasAgent2, hasAgent3, safe, visited;
	private boolean canHaveWumpus, canHavePit;
	private int x, y;
	
	public GridCell(int x, int y){
		hasBreeze = hasStench = hasPit = hasWumpus = hasGold = hasChest =
				hasAgent0 = hasAgent1 = hasAgent2 = hasAgent3 = safe = visited = false;
		canHaveWumpus = canHavePit = true;
		this.x = x;
		this.y = y;
	}

	/**
	 * Update the neighbor specification for path planning. Not the most efficient
	 * code but the neighbor array is maximally size 4, so I didn't bother spending
	 * effort optimizing it. 
	 * @param topological_neighbors Neighbors according to the wumpus world, which may or may not be safe.
	 */
	public void updateNeighbors(ArrayList<GridCell> topological_neighbors){
		// First remove neighbors that switched to unsafe.
		Iterator<Vertex> it = neighbors.iterator();
		while(it.hasNext()){
			Vertex v = it.next();
			GridCell n = (GridCell)v;
		    if(!n.isSafe()) it.remove();
		}
		
		// Second add neighbors that are now deemed safe.
		for(GridCell n : topological_neighbors)
			if(n.isSafe()&&!neighbors.contains(n))
				neighbors.add(n);
	}
	
	public boolean hasBreeze(){ return hasBreeze; }				public void setBreeze(boolean b){ this.hasBreeze = b; }
	public boolean hasStench(){ return hasStench; }				public void setStench(boolean b){ this.hasStench = b; }
	public boolean hasPit(){ return hasPit; }					public void setPit(boolean b){ this.hasPit = b; }
	public boolean hasWumpus(){ return hasWumpus; }				public void setWumpus(boolean b){ this.hasWumpus = b; }
	public boolean hasGold(){ return hasGold; }					public void setGold(boolean b){ this.hasGold = b; }
	public boolean hasChest(){ return hasChest; }				public void setChest(boolean b){ this.hasChest = b; }
	public boolean hasAgent0(){ return hasAgent0; }				public void setAgent0(boolean b){ this.hasAgent0 = b; }
	public boolean hasAgent1(){ return hasAgent1; }				public void setAgent1(boolean b){ this.hasAgent1 = b; }
	public boolean hasAgent2(){ return hasAgent2; }				public void setAgent2(boolean b){ this.hasAgent2 = b; }
	public boolean hasAgent3(){ return hasAgent3; }				public void setAgent3(boolean b){ this.hasAgent3 = b; }
	public boolean isSafe(){ return safe; }						public void setSafe(boolean b){ this.safe = b; }
	public boolean isVisited(){ return visited; }				public void setVisited(boolean b){ this.visited = b; }
	public boolean canHavePit(){ return canHavePit; }			public void setCanHavePit(boolean b){ this.canHavePit = b; }
	public boolean canHaveWumpus(){ return canHaveWumpus; }		public void setCanHaveWumpus(boolean b){ this.canHaveWumpus = b; }
	public int getX(){ return x;} 
	public int getY(){return y; }
	
	public String toString(){ return "("+x+","+y+")"; }
}
