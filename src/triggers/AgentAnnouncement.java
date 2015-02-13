package triggers;

import agentcore.Context;
import agentcore.Trigger;
/**
 * An agent announcement is an environment event that tells the location of another agent. 
 * Agents announce themselves upon entering the environment. 
 * 
 * @author Bas Testerink
 */
public class AgentAnnouncement implements Trigger {
	private int agent, x, y;
	
	public AgentAnnouncement(int agent, int x, int y){ 
		this.agent = agent; 
		this.x = x;
		this.y = y;
	}
	
	public boolean isProcessed(Context context) { 
		return false;
	}

	public int getAgent(){ return agent; }
	public int getX(){ return x; }
	public int getY(){ return y; }
	
	public void toJSON(StringBuffer r){ 
		r.append("{\"Name\": \"agent announcement\",\"Who\": \""+agent+"\",\"Location\": \"("+x+","+y+")\"}"); 
	} 
}
