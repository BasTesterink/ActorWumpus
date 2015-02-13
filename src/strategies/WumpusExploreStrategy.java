package strategies; 

import pathplanning.Vertex;
import triggers.ClearWorldGoal;
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
 * The explore strategy is to visit all safe spots. However, if a piece of gold is found, then it is picked
 * up and brought to the nearest chest.
 * 
 * @author Bas Testerink
 */
public class WumpusExploreStrategy extends Strategy {
	
	public boolean isRelevant(Trigger t){
		return t instanceof ClearWorldGoal;
	}
	
	public boolean isApplicable(Context c, Trigger t){ return !((WumpusContext)c).getPursuingClearWorldGoal(); }

	public StrategyInstantiation instantiate(Trigger goal, AgentInstantiation agent){
		WumpusExploreStrategyInstantiation inst = new WumpusExploreStrategyInstantiation();
		((WumpusContext)((WumpusAgentInstantiation)agent).getContext()).setPursuingClearWorldGoal(true);
		return inst;
	}

	
	public class WumpusExploreStrategyInstantiation extends StrategyInstantiation {
		private TraverseGoal goalLocation = null;
		
		public ExecutionError getError(){ 
			return null;
		}
 
		public boolean executeNextStep(AgentInstantiation agent){
			if(goalLocation != null && !goalLocation.isProcessed(agent.getContext())) return true;
			WumpusContext context = (WumpusContext) agent.getContext(); 
			if(goalLocation != null){
				if(goalLocation.getAtTarget()==WumpusConstants.CHEST){
					context.drop(agent);
				} else if(goalLocation.getAtTarget()==WumpusConstants.GOLD){
					context.grab(agent);
				} else if(goalLocation.getAtTarget()==WumpusConstants.SAFE){
					boolean b = context.foundWumpus();
					context.perceive(agent);
					if(!b && context.foundWumpus()) context.announceWumpus(agent); // Just found the Wumpus
					context.announceVisitedSpot(agent); // Announce that you safely visited a spot
					context.positionUpdate(agent);
				}
				goalLocation = null;
			} else {
				Vertex nearestChestSpot = WumpusPathPlanner.nearestSpot(context.getChestSpots()); 
				Vertex nearestGoldSpot = WumpusPathPlanner.nearestSpot(context.getGoldSpots()); 
				Vertex nearestSafeSpot = WumpusPathPlanner.nearestSpot(context.getSafeSpots());  
				if(context.holdsGold() && nearestChestSpot != null){
					GridCell g = (GridCell)nearestChestSpot;
					goalLocation = new TraverseGoal(g.getX(), g.getY(), WumpusConstants.CHEST);
					agent.adoptGoal(goalLocation);
				} else if(!context.holdsGold() && nearestGoldSpot != null){
					GridCell g = (GridCell)nearestGoldSpot;
					goalLocation = new TraverseGoal(g.getX(), g.getY(), WumpusConstants.GOLD);
					agent.adoptGoal(goalLocation);
				} else if(nearestSafeSpot != null){
					GridCell g = (GridCell)nearestSafeSpot;
					goalLocation = new TraverseGoal(g.getX(), g.getY(), WumpusConstants.SAFE);
					agent.adoptGoal(goalLocation);
				} else { 
					context.setExplored(true); 
					setFinished(true);
				}
			}
			return true;
		}

		public synchronized void toJSON(StringBuffer r){ 
			r.append('{');
			r.append("\"Name\": \"explore strategy instantiation\",");
			r.append("\"Trigger\": \"explore goal\"");  r.append(',');
			r.append("\"Description\" :");
			if(goalLocation != null){
				if(goalLocation.getAtTarget()==WumpusConstants.CHEST){
					r.append("\"Wait for reaching ("+goalLocation.getX()+","+goalLocation.getY()+") and then drop the gold.\"");
				} else if(goalLocation.getAtTarget()==WumpusConstants.GOLD){
					r.append("\"Wait for reaching ("+goalLocation.getX()+","+goalLocation.getY()+") and then grab the gold.\"");
				} else if(goalLocation.getAtTarget()==WumpusConstants.SAFE){
					r.append("\"Wait for reaching ("+goalLocation.getX()+","+goalLocation.getY()+") and then perceive the surroundings.\"");
				}
			} else {
				r.append("\"Try to bring gold to a chest, or obtain gold, or explore an unexplored safe spot.\"");
			}
			r.append('}');
		}
	}
	 
		public void toJSON(StringBuffer b) { 
			b.append("{\"Name\":\"explore strategy\"}");
		}
}
