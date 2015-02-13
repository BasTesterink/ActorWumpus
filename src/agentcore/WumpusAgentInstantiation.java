package agentcore;

import messaging.Messenger;
import agentcore.AgentInstantiation; 

public class WumpusAgentInstantiation extends AgentInstantiation {
	
	public WumpusAgentInstantiation(WumpusAgent agentClass, WumpusContext context, Messenger messenger){
		super(agentClass, messenger);
		this.context = context; 
	}
}
