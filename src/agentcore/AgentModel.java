package agentcore;
/**
 * A simple container for storing data about other agents.
 * 
 * @author Bas Testerink
 */
public class AgentModel { 
	private int x, y, id; // Location and id (WumpusConstants.AGENT0/1/2/3)
	private boolean holdsGold; // Whether gold is being held
	
	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getID(){ return id; }
	public boolean holdsGold(){ return holdsGold; }
	public void setID(int id){ this.id = id; }
	public void setPosition(int x, int y){ this.x = x; this.y = y; }
	public void setHoldsGold(boolean b){ holdsGold = b; }
	public void goldGrabbed(){ holdsGold = true; }
	public void goldReleased(){ holdsGold = false; }
}
