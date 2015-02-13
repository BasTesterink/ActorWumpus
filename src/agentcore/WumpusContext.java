package agentcore;

import java.util.ArrayList; 
import java.util.List; 
import pathplanning.Dijkstra;
import pathplanning.Vertex;
import triggers.KnowledgeMessage;
import environment.AgentInterface;
import agentcore.AgentInstantiation;
import agentcore.Context;

/**
 * This class implements belief reasoning and the internal interface to the environment. 
 *
 * @author Bas Testerink
 */

public class WumpusContext extends Context {
	private GridCell[][] world; 								// Believed world
	private List<Vertex> arrayForm; 							// Array form of the gridcells for Dijkstra's algorithm
	private AgentModel me;										// View of oneself
	private boolean pursuingClearWorldGoal, explored; 			// Whether a strategy has been selected to clear the world of gold, whether there is something left to explore
	private List<AgentModel> others;							// List of the other agents (currently not used)
	private AgentInterface agentInterface;						// Environment interface
	private Percept perceptContainer; 							// Made as an attribute so it is not necessary to make a new object for every perceive action
	private List<Vertex> goldSpots, chestSpots, safeSpots;		// Location of gold, chests and safe spots
	private boolean foundWumpus = false;						// Whether the wumpus has been found
	private int wumpusX, wumpusY;								// Location of the wumpus
	private List<Integer> otherAgentIDs;						// Ids of the other agents
	private static int[][] delta = new int[][]{{-1,0},{1,0},{0,-1},{0,1}}; // To loop through neighboring coordinates

	public WumpusContext(AgentInterface agentInterface){
		this.agentInterface = agentInterface;
		world = new GridCell[agentInterface.getWorldWidth()][agentInterface.getWorldHeight()];
		arrayForm = new ArrayList<Vertex>();
		for(int x = 0; x < world.length; x++)
			for(int y = 0; y < world[0].length; y++){
				world[x][y] = new GridCell(x,y);
				arrayForm.add(world[x][y]); // For input of Dijkstra's
			}
		me = new AgentModel();
		pursuingClearWorldGoal = false;
		others = new ArrayList<AgentModel>();
		perceptContainer = new Percept();
		explored = false;
		goldSpots = new ArrayList<Vertex>();
		chestSpots = new ArrayList<Vertex>();
		safeSpots = new ArrayList<Vertex>();
		otherAgentIDs = new ArrayList<Integer>();
	}

	////////////////
	/// Literals ///
	////////////////
	/** Check for a pit. Returns true if there is one for certain or otherwise false (so false is also unknown). */
	private boolean hasPit(int x, int y){
		if(world[x][y].hasPit()) return true; // Already determined.
		if(!world[x][y].canHavePit()) return false;
		return false;
	}
	
	/** Check for the wumpus. Returns true if there is one for certain or otherwise false (so false is also unknown). */
	private boolean hasWumpus(int x, int y){
		if(!inWorld(x, y)) return false;								// Location is ouside of the world
		if(world[x][y].hasWumpus()) return true;						// Already determined the wumpus is here
		if(!world[x][y].canHaveWumpus()) return false;					// Already determined the wumpus cannot be here
		if(foundWumpus && (x!= wumpusX || y!=wumpusY)) return false;	// Found the wumpus elsewhere
		if(foundWumpus && (x== wumpusX && y==wumpusY)) return true;		// Found the wumpus here
		
		// Look at the neighboring cells and see whether (x,y) is the only viable spot for the wumpus.
		ArrayList<GridCell> newIntersection;
		ArrayList<GridCell> intersection = new ArrayList<GridCell>();
		ArrayList<GridCell> candidates = new ArrayList<GridCell>();
		boolean first = true;
		// The wumpus' location is known if either 2 adjacent stenches are smelled, or there is a stench s.t. (x,y) is the only candidate.
		for(int[] d : delta){ //For each adjacent spot if there is a stench then we'll check what the candidate spots for the wumpus are.
			int dX = x+d[0];
			int dY = y+d[1];
			if(inWorld(dX, dY)&&world[dX][dY].hasStench()){
				for(int[] d2 : delta){
					int dX2 = dX+d2[0];
					int dY2 = dY+d2[1];
					if(inWorld(dX2, dY2)&&world[dX2][dY2].canHaveWumpus())
						candidates.add(world[dX2][dY2]);
				}
				if(first){
					intersection.addAll(candidates);
					first = false;
				} else {
					newIntersection = new ArrayList<GridCell>();
					for(GridCell g : intersection)
						if(candidates.contains(g))
							newIntersection.add(g);
					intersection = newIntersection;
				}
				candidates.clear();
			}
		}
		// If the candidate intersection is singular and (x,y) then the wumpus is found.
		if(intersection.size()==1 && intersection.get(0).getX()==x && intersection.get(0).getY()==y){
			foundWumpus = true;
			wumpusX = x;
			wumpusY = y; 
			return true;
		}
			
		return false;
	}
	
	// Some other literals
	public boolean atLocation(int x, int y){ return me.getX()==x && me.getY()==y; }
	public boolean isExplored(){ return explored; }
	public boolean holdsGold(){ return me.holdsGold(); }
	public boolean foundWumpus(){ return foundWumpus; }
	
	///////////////
	/// Actions ///
	///////////////
	/** Perceive the location where the agent is currently standing. */
	public void perceive(AgentInstantiation myInstantiation){
		agentInterface.perceive(myInstantiation, perceptContainer); 
		processPercept();
		agentInterface.showBelief(world, me, others, myInstantiation);
	}

	/** Move up/down/left/right. */
	public boolean move(AgentInstantiation myInstantiation, int direction){
		return agentInterface.move(myInstantiation, direction);
	}

	/** Try to grab gold. */
	public void grab(AgentInstantiation myInstantiation){
		me.setHoldsGold(agentInterface.gripper(myInstantiation,false));
		world[me.getX()][me.getY()].setGold(false);
		Vertex toRemove = null;  
		for(Vertex v : goldSpots){
			GridCell g = (GridCell)v;
			if(g.getX()==me.getX()&&g.getY()==me.getY()) toRemove = v;
		}
		if(toRemove!=null) goldSpots.remove(toRemove);
		agentInterface.showBelief(world, me, others, myInstantiation);
	}
	
	/** Drop gold. */
	public void drop(AgentInstantiation myInstantiation){
		me.setHoldsGold(!agentInterface.gripper(myInstantiation,true));
		if(!world[me.getX()][me.getY()].hasChest()){ 
			world[me.getX()][me.getY()].setGold(true);
			goldSpots.add(world[me.getX()][me.getY()]);
		}
		agentInterface.showBelief(world, me, others, myInstantiation);
	}

	/** Announces the presence of yourself. */
	public void announceMyself(AgentInstantiation myInstantiation){
		agentInterface.announceAgent(myInstantiation, me.getX(), me.getY());
	}

	/** Announce the location of the wumpus. */
	public void announceWumpus(AgentInstantiation myInstantiation){
		for(int i : otherAgentIDs){
			if(i != myInstantiation.getID())
				myInstantiation.getMessenger().sendMessage(myInstantiation.getID(), i, new KnowledgeMessage(wumpusX, wumpusY, true, true, false));
		}
	}
	
	/** Announce a spot that is visited (to let others know it's a safe spot. */ 
	public void announceVisitedSpot(AgentInstantiation myInstantiation){
		for(int i : otherAgentIDs){
			if(i != myInstantiation.getID())
				myInstantiation.getMessenger().sendMessage(myInstantiation.getID(), i, new KnowledgeMessage(me.getX(), me.getY(), false, false, false));
		}
	}

	////////////////////////////////
	/// Aux. methods for actions ///
	////////////////////////////////
	
	private boolean inWorld(int x, int y){ return x>=0 && x<world.length && y>=0 && y<world[0].length;}
	
	/** Update the belief base according to latest percept (through own perception or communication). */
	private void processPercept(){
		int x = perceptContainer.getX();
		int y = perceptContainer.getY(); 
		world[x][y].setVisited(true); 
		world[x][y].setBreeze(perceptContainer.isBreeze());
		world[x][y].setStench(perceptContainer.isStench());
		world[x][y].setGold(perceptContainer.isGlitter());
		if(world[x][y].hasGold() && !goldSpots.contains(world[x][y])) goldSpots.add(world[x][y]);
		world[x][y].setChest(perceptContainer.isChest());
		if(world[x][y].hasChest() && !chestSpots.contains(world[x][y])) chestSpots.add(world[x][y]);
		updateBelievedWorld();
	}
	
	/** Get the current position in the environment and update internal map. */
	public void positionUpdate(AgentInstantiation myInstantiation){
		agentInterface.positionUpdate(myInstantiation, perceptContainer);
		int x = perceptContainer.getX();  
		int y = perceptContainer.getY();
		me.setPosition(x,y); 
		Dijkstra.dijkstra(arrayForm, world[me.getX()][me.getY()]); // Updates the distances from current position to all reachable spots	
		agentInterface.showBelief(world, me, others, myInstantiation);
	}

	/**
	 * Updates the spots that are deemed safe.
	 * Updates the connectivity between spots for path planning. 
	 * Updates the distances from the agent to reachable spots.
	 */
	private void updateBelievedWorld(){
		for(int x = 0; x < world.length; x++)
			for(int y = 0; y < world[0].length; y++){
				if(foundWumpus && !hasWumpus(x, y)) world[x][y].setCanHaveWumpus(false);
				if(world[x][y].isVisited()){ 
					connectToNeighbors(x, y);
					if(!world[x][y].hasBreeze())
						for(int[] d : delta)
							if(inWorld(x+d[0],y+d[1])) world[x+d[0]][y+d[1]].setCanHavePit(false);
					if(!world[x][y].hasStench())
						for(int[] d : delta)
							if(inWorld(x+d[0],y+d[1])) world[x+d[0]][y+d[1]].setCanHaveWumpus(false);
				}
			}
		safeSpots.clear(); // Otherwise visited spots stay here
		for(int x = 0; x < world.length; x++)
			for(int y = 0; y < world[0].length; y++){ 
				world[x][y].setWumpus(hasWumpus(x,y)); 
				world[x][y].setSafe(!world[x][y].isVisited() && !world[x][y].canHavePit() && !world[x][y].canHaveWumpus()); // The visited check is to differentiate between unexplored and explored safe spots
				if(world[x][y].isSafe()){ // If the node is found to be safe
					safeSpots.add(world[x][y]);
					connectToNeighbors(x, y);
				}
				world[x][y].setPit(hasPit(x,y)); 
			}
	} 

	// Update internally the directly (one move) reachable spots from (x,y).
	private void connectToNeighbors(int x, int y){
		if(world[x][y].isSafe() || world[x][y].isVisited()){ // Must be possible to go to this spot
			for(int[] d : delta){ // For each direction
				if(inWorld(x+d[0],y+d[1])){ // If in the world
					GridCell neighbor = world[x+d[0]][y+d[1]]; 
					if(neighbor.isSafe()||neighbor.isVisited()){ // If the neighbor is traversable add it
						if(!neighbor.getNeighbors().contains(world[x][y]))
							neighbor.getNeighbors().add(world[x][y]); 
						if(!world[x][y].getNeighbors().contains(neighbor))
							world[x][y].getNeighbors().add(neighbor);
					}
				}
			}
		}
	}
	
	/** Set the believed wumpus location. Used after a wumpus announcement is received. */
	public void setWumpus(AgentInstantiation myInstantiation, int x, int y){
		foundWumpus = true;
		wumpusX = x;
		wumpusY = y;
		world[x][y].setCanHaveWumpus(true);
		world[x][y].setWumpus(true);
		updateBelievedWorld();	
		positionUpdate(myInstantiation); // Recalculate distances
	}
	
	/** Process information into the believed state. Used after an agent announce a safe spot and/or the wumpus. */
	public void addInfo(AgentInstantiation myInstantiation, int x, int y, boolean canHaveWumpus, boolean canHavePit){
		world[x][y].setCanHaveWumpus(canHaveWumpus);
		world[x][y].setCanHavePit(canHavePit);
		updateBelievedWorld();
		positionUpdate(myInstantiation); // Recalculate distances
	}
	
	////////////
	/// Misc ///
	////////////
	public void addOtherAgent(int i){ otherAgentIDs.add(i); }
	public GridCell getCell(int x, int y){ return world[x][y]; }
	public void setID(int id){ me.setID(id); }
	public int getID(){return me.getID(); }
	public void setExplored(boolean b){ explored = b; }
	public void setPursuingClearWorldGoal(boolean b){ this.pursuingClearWorldGoal = b; }
	public boolean getPursuingClearWorldGoal(){ return pursuingClearWorldGoal; }
	public List<Vertex> getSafeSpots(){ return safeSpots; }
	public List<Vertex> getChestSpots(){ return chestSpots; }
	public List<Vertex> getGoldSpots(){ return goldSpots; }
	public void toJSON(StringBuffer r){ r.append("\"not implemented yet\""); }
}
