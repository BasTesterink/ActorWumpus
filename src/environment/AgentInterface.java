package environment;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import triggers.AgentAnnouncement;
import agentcore.AgentInstantiation;
import agentcore.AgentModel;
import agentcore.GridCell;
import agentcore.Percept;
import agentcore.WumpusConstants;
/**
 * This class implements the agents' interface to the environment.
 * 
 * @author Bas Testerink
 */
public class AgentInterface {
	private Wumpus env;
	private WumpusGUI gui;
	private Map<AgentInstantiation, Integer> agents;		// Internal agents
	private boolean[] freespots;							// Agent positions that are free
	
	public AgentInterface(Wumpus env, WumpusGUI gui){
		this.env = env;
		this.gui = gui;
		agents = new HashMap<AgentInstantiation, Integer>();
		freespots = new boolean[]{true,true,true,true};
	}
	
	public int getWorldWidth(){ return env.getWidth(); }
	public int getWorldHeight(){ return env.getHeight(); }
	
	/** Grab or drop gold. */
	public boolean gripper(AgentInstantiation agent, boolean drop){
		if(!agents.containsKey(agent)) return false;
		else {
			int id = agents.get(agent);
			if(drop){
				env.drop(id);
				if(env.getFailedMessage()==null && env.at(env.getX(id), env.getY(id), Wumpus.CHEST))
					gui.updateMood(id, 2);
				else gui.updateMood(id, 0);
			} else {
				env.grab(id);
				if(env.getFailedMessage()==null)
					gui.updateMood(id, 1);
			}
			gui.updateRealWorld();
			return env.getFailedMessage() == null;
		}
		
	}
	
	/** Move an agent up/down/left/right. */
	public boolean move(AgentInstantiation agent, int direction){
		if(!agents.containsKey(agent)) return false;
		else {
			int dX = direction==WumpusConstants.LEFT?(-1):(direction==WumpusConstants.RIGHT?1:0);
			int dY = direction==WumpusConstants.UP?1:(direction==WumpusConstants.DOWN?(-1):0);
			env.move(agents.get(agent), dX, dY);
			try{Thread.sleep(400);}catch(Exception e){}					// Allow the animation to finish
			gui.updateRealWorld();
			return env.getFailedMessage() == null;
		}
	}
	
	/** Register an agent. Returns -1 if there are no free spots left. */
	public synchronized int registerAgent(AgentInstantiation agent){  
		if(agents.size()==WumpusConstants.MAXAGENTAMOUNT) return -1;
		else {
			for(int i = 0; i < WumpusConstants.MAXAGENTAMOUNT; i++)
				if(freespots[i]){
					freespots[i] = false;
					agents.put(agent, i);  
					return i;
				}
			return -1; // Cannot be reached
		}
	}
	
	/** Announce one agent to the other agents in the environment */
	public synchronized void announceAgent(AgentInstantiation agent, int x, int y){ 
		for(AgentInstantiation a : agents.keySet())
			if(a!=agent) 
				a.addExternalEvent(new AgentAnnouncement(agent.getID(), x, y)); 
	}
	 
	/** Give the position of the agent. */
	public void positionUpdate(AgentInstantiation agent, Percept p){
		int id = agents.get(agent); 
		p.setX(env.getX(id));
		p.setY(env.getY(id));
	}
	/**
	 * Obtain the percepts for an agent. 
	 * @param agent Agent that perceives.
	 * @param p Container to put the percepts in.
	 * @return Whether perception succeeded (for now it always does).
	 */
	public boolean perceive(AgentInstantiation agent, Percept p){
		int id = agents.get(agent);
		int x = env.getX(id);
		int y = env.getY(id);  
		
		// Fill the percept container
		p.setX(x);
		p.setY(y);
		p.setAgent0(env.at(x, y, Wumpus.AGENT0));
		p.setAgent1(env.at(x, y, Wumpus.AGENT1));
		p.setAgent2(env.at(x, y, Wumpus.AGENT2));
		p.setAgent3(env.at(x, y, Wumpus.AGENT3));
		p.setChest(env.at(x,y,Wumpus.CHEST));
		p.setGlitter(env.at(x,y,Wumpus.GOLD));
		p.setBreeze(env.at(x,y,Wumpus.BREEZE));
		p.setStench(env.at(x,y,Wumpus.STENCH));
		return true;
	} 
	
	/** Show the beliefs of an agent in the GUI. */
	public void showBelief(GridCell[][] world, AgentModel me, List<AgentModel> others, AgentInstantiation agent){
		int id = agents.get(agent);
		gui.clearBelievedWorld(id);
		gui.addToBelievedWorld(me.getID(), me.getX(), me.getY(), Wumpus.AGENT0<<me.getID());
		for(int x = 0; x < env.getWidth(); x++){
			for(int y = 0; y < env.getHeight(); y++){
				if(world[x][y].isVisited()) gui.addToBelievedWorld(id, x, y, -1); // Remove the unknown symbol
				if(world[x][y].hasAgent0()) gui.addToBelievedWorld(id, x, y, Wumpus.AGENT0);
				if(world[x][y].hasAgent1()) gui.addToBelievedWorld(id, x, y, Wumpus.AGENT1);
				if(world[x][y].hasAgent2()) gui.addToBelievedWorld(id, x, y, Wumpus.AGENT2);
				if(world[x][y].hasAgent3()) gui.addToBelievedWorld(id, x, y, Wumpus.AGENT3);
				if(world[x][y].hasBreeze()) gui.addToBelievedWorld(id, x, y, Wumpus.BREEZE);
				if(world[x][y].hasStench()) gui.addToBelievedWorld(id, x, y, Wumpus.STENCH);
				if(world[x][y].hasChest()) gui.addToBelievedWorld(id, x, y, Wumpus.CHEST);
				if(world[x][y].hasGold()) gui.addToBelievedWorld(id, x, y, Wumpus.GOLD);
				if(world[x][y].hasPit()) gui.addToBelievedWorld(id, x, y, Wumpus.PIT);
				if(world[x][y].hasWumpus()) gui.addToBelievedWorld(id, x, y, Wumpus.WUMPUS);
				if(world[x][y].isSafe()) gui.addToBelievedWorld(id, x, y, Wumpus.SAFE);
			}
		} 
	}
	
}
