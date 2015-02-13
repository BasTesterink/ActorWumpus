package triggers;

import agentcore.Context; 
import agentcore.Trigger;
import agentcore.WumpusContext;
/**
 * This goal is translated in practice to going to every safe, yet unexplored, spot and perceive what's there. 
 * 
 * @author Bas Testerink
 */
public class ClearWorldGoal implements Trigger {
 
	public boolean isProcessed(Context context){ 
		WumpusContext c = (WumpusContext)context; 
		return c.isExplored();
	}

	public void toJSON(StringBuffer r){ r.append("{\"Name\": \"clear world goal\"}"); } 
}
