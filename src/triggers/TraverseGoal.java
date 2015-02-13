package triggers;
 
import agentcore.Context; 
import agentcore.Trigger;
import agentcore.WumpusContext;

/**
 * This goal is to go somewhere specific and perform an action (grab/drop/perceive). 
 * 
 * @author Bas Testerink
 */
public class TraverseGoal implements Trigger {
	private int x, y, atTarget; // Target is WumpusConstants.CHEST GOLD SAFE
	
	public TraverseGoal(int x, int y, int atTarget){
		this.x = x;
		this.y = y;
		this.atTarget = atTarget;
	}
	
	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getAtTarget(){ return atTarget; }
	
	public boolean isProcessed(Context context){ 
		return ((WumpusContext)context).atLocation(x,y);
	}
	
	public void toJSON(StringBuffer r){ r.append("{\"Name\": \"traverse goal\", \"X\": "+x+", \"Y\": "+y+", \"AtTarget\": "+atTarget+"}"); }
}
