package strategies;

import triggers.ClearWorldGoal;
import agentcore.AgentInstantiation;
import agentcore.ExecutionError;
import agentcore.Strategy;
import agentcore.StrategyInstantiation;
import agentcore.Trigger;
import agentcore.WumpusAgentInstantiation;
import agentcore.WumpusContext;
/**
 * This strategy kicks-off the agent. It obtains the initial percepts and announces the agent to the others.
 * 
 * @author Bas Testerink
 */
public class InitialStrategy extends Strategy {

	public StrategyInstantiation instantiate(Trigger t, AgentInstantiation agent){ 
		return new InitialStrategyInstantiation();
	}

	public class InitialStrategyInstantiation extends StrategyInstantiation {

		public boolean executeNextStep(AgentInstantiation agent){
			WumpusAgentInstantiation w_agent = (WumpusAgentInstantiation)agent;
			WumpusContext context = (WumpusContext)w_agent.getContext();
			context.perceive(agent); 
			context.announceMyself(agent);
			w_agent.adoptGoal(new ClearWorldGoal());
			setFinished(true);
			return true;
		}

		public ExecutionError getError(){
			return null;
		}
		
		public synchronized void toJSON(StringBuffer r){
			r.append('{');
			r.append("\"Name\": \"initial strategy instantiation\",");
			r.append("\"Trigger\": \"none\""); r.append(',');
			r.append("\"Description\" :");
			r.append("\"Initial perception and announcement. Also adopt the clear world goal.\"");
			r.append('}');
		} 
	}

	public void toJSON(StringBuffer b){ 
		b.append("{\"Name\":\"traverse strategy\"}");
	}
}
