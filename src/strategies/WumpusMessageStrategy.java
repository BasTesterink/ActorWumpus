package strategies;

import triggers.ClearWorldGoal;
import triggers.KnowledgeMessage;
import messaging.LocalMessage;
import agentcore.AgentInstantiation;
import agentcore.Context;
import agentcore.ExecutionError;
import agentcore.Strategy;
import agentcore.StrategyInstantiation;
import agentcore.Trigger;
import agentcore.WumpusAgentInstantiation;
import agentcore.WumpusContext;
/**
 * The message strategy processes a message in the belief base.
 * 
 * @author Bas Testerink
 */
public class WumpusMessageStrategy extends Strategy {

	public boolean isRelevant(Trigger trigger){ return trigger instanceof LocalMessage; } 
	public boolean isApplicable(Context c, Trigger t){ return true; }

	public StrategyInstantiation instantiate(Trigger t, AgentInstantiation agent){ 
		return new WumpusMessageInstantiation((LocalMessage)t);
	}

	public class WumpusMessageInstantiation extends StrategyInstantiation {
		private LocalMessage msg;

		public WumpusMessageInstantiation(LocalMessage msg){
			this.msg = msg;
		}

		public boolean executeNextStep(AgentInstantiation agent){ 
			KnowledgeMessage k = (KnowledgeMessage)msg.getContent();
			WumpusContext context = (WumpusContext) agent.getContext();  
			if(k.hasWumpus()){ 
				context.setWumpus(agent, k.getX(), k.getY());
			} else {
				context.addInfo(agent, k.getX(),k.getY(),k.canHaveWumpus(),k.canHavePit());
				if(context.isExplored()){
					context.setExplored(false);
					((WumpusAgentInstantiation) agent).adoptGoal(new ClearWorldGoal());
					context.setPursuingClearWorldGoal(false);
				}
			} 
			setFinished(true);
			return true;
		}

		public ExecutionError getError(){ return null; }
		
		public synchronized void toJSON(StringBuffer r){
			KnowledgeMessage k = (KnowledgeMessage)msg.getContent();
			r.append('{');
			r.append("\"Name\": \"handle message instantiation\",");
			r.append("\"Trigger\": "); k.toJSON(r); r.append(',');
			r.append("\"Description\" :");
			if(k.hasWumpus()){
				r.append("\"Add to the belief state that the wumpus is at ("+k.getX()+","+k.getY()+").\"");
			} else {
				r.append("\"Add to the belief state that position ("+k.getX()+","+k.getY()+") is safe. Also make sure that exploring the world is still a goal.\"");
			}
			r.append('}');
		}
		
	}

	public void toJSON(StringBuffer b){ 
		b.append("{\"Name\":\"message strategy\"}");
	}
}
