package strategies;

import java.util.ArrayList;
import java.util.List;

import triggers.KnowledgeMessage;
import triggers.TraverseGoal;

import agentcore.AgentInstantiation;
import agentcore.Context;
import agentcore.ExecutionError;
import agentcore.GridCell;
import agentcore.Strategy;
import agentcore.StrategyInstantiation;
import agentcore.Trigger;
import agentcore.WumpusAgentInstantiation;
import agentcore.WumpusConstants;
import agentcore.WumpusContext;
import agentcore.WumpusPathPlanner;
/**
 * The traverse strategy looks at the quickest path to the goal location and then executes the first move.
 * 
 * @author Bas Testerink
 */
public class TraverseStrategy extends Strategy {

	public boolean isRelevant(Trigger t){
		return t instanceof TraverseGoal;
	}

	public boolean isApplicable(Context c, Trigger t){
		WumpusContext context = ((WumpusContext)c);
		TraverseGoal g = (TraverseGoal) t;
		GridCell targetCell = context.getCell(g.getX(), g.getY()); 
		if(targetCell.getDistance()==Integer.MAX_VALUE) return false;
		switch(g.getAtTarget()){										// Check if the target spot fits the goal
		case WumpusConstants.SAFE: return targetCell.isSafe();
		case WumpusConstants.CHEST: return targetCell.hasChest();
		case WumpusConstants.GOLD: return targetCell.hasGold();
		}
		return false; 
	}

	public StrategyInstantiation instantiate(Trigger trigger, AgentInstantiation agent) { 
		return new WumpusTraverseStrategyInstantiation((TraverseGoal) trigger); 
	}

	public class WumpusTraverseStrategyInstantiation extends StrategyInstantiation {
		TraverseGoal goal;

		public WumpusTraverseStrategyInstantiation(TraverseGoal goal){
			this.goal = goal;
		}

		public ExecutionError getError(){ 
			return null;
		}

		public boolean executeNextStep(AgentInstantiation agent){
			setFinished(true);
			WumpusContext context = (WumpusContext)((WumpusAgentInstantiation)agent).getContext();
			List<Integer> plan = WumpusPathPlanner.planPath(context.getCell(goal.getX(), goal.getY())); 
			if(plan.size()>0) context.move(agent, plan.get(plan.size()-1));
			return true;
		}
		
		// For debugging
		private String path(List<Integer> l){
			StringBuffer s = new StringBuffer();
			for(int i : l){
				switch(i){
				case WumpusConstants.UP: s.append("up "); break;
				case WumpusConstants.DOWN: s.append("down "); break;
				case WumpusConstants.LEFT: s.append("left "); break;
				case WumpusConstants.RIGHT: s.append("right "); break;
				}
			}
			return s.toString();
		}
		
		public synchronized void toJSON(StringBuffer r){
			r.append('{');
			r.append("\"Name\": \"traverse strategy instantiation\",");
			r.append("\"Trigger\": "); goal.toJSON(r); r.append(',');
			r.append("\"Description\" :");
			r.append("\"Execute an action to get closer to ("+goal.getX()+","+goal.getY()+").\"");
			r.append('}');
		}

	}

	public void toJSON(StringBuffer b){ 
		b.append("{\"Name\":\"traverse strategy\"}");
	}
}
