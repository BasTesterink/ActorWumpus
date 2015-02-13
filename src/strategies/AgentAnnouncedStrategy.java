package strategies;

import strategies.WumpusMessageStrategy.WumpusMessageInstantiation;
import triggers.AgentAnnouncement;
import messaging.LocalMessage;
import agentcore.AgentInstantiation;
import agentcore.Context;
import agentcore.ExecutionError;
import agentcore.Strategy;
import agentcore.StrategyInstantiation;
import agentcore.Trigger;
import agentcore.WumpusContext;
/**
 * This strategy processes agent announcements. It adds the announced agent into the belief state.
 * 
 * @author Bas Testerink
 */
public class AgentAnnouncedStrategy extends Strategy {

	public boolean isRelevant(Trigger trigger){ return trigger instanceof AgentAnnouncement; } 
	public boolean isApplicable(Context c, Trigger t){ return true; }
	
	public StrategyInstantiation instantiate(Trigger t, AgentInstantiation agent){ 
		return new AgentAnnounceInstantiation((AgentAnnouncement)t);
	}

	public class AgentAnnounceInstantiation extends StrategyInstantiation {
		private AgentAnnouncement newAgent;
		
		public AgentAnnounceInstantiation(AgentAnnouncement newAgent){
			this.newAgent = newAgent;
		}
		
		public boolean executeNextStep(AgentInstantiation agent){
			WumpusContext context = (WumpusContext)agent.getContext();
			context.addOtherAgent(newAgent.getAgent());
			context.addInfo(agent, newAgent.getX(),newAgent.getY(), false, false);
			setFinished(true);
			return true;
		}

		public ExecutionError getError(){
			return null;
		}
		
		public synchronized void toJSON(StringBuffer r){
			r.append('{');
			r.append("\"Name\": \"agent announcement instantiation\",");
			r.append("\"Trigger\": "); newAgent.toJSON(r); r.append(',');
			r.append("\"Description\" :");
			r.append("\"Add to the belief state that agent "+newAgent.getAgent()+" was spawned at location ("+newAgent.getX()+","+newAgent.getY()+").\"");
			r.append('}');
		} 
	}
 
	public void toJSON(StringBuffer b) { 
		b.append("{\"Name\":\"agent announce strategy\"}");
	}
}
