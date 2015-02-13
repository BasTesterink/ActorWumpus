package agentcore;

import java.util.Queue; 

import strategies.*; 
import messaging.Messenger; 
import environment.AgentInterface; 
import agentcore.Agent;
import agentcore.AgentInstantiation;
import agentcore.Strategy;
import agentplatform.PlatformNode;

/**
 * The class of all agents in the world is the same. The initial strategy is to observe the first state 
 * they find themselves in. Then they announce themselves to the other agents and start exploring the world.
 * 
 * These agents follow the standard 2APL deliberation cycle.
 * 
 * @author Bas Testerink
 */

public class WumpusAgent extends Agent {
	public static Strategy initialStrategy = new InitialStrategy();
	private AgentInterface agentInterface; 							// The interface of agents to the environment
	
	public WumpusAgent(AgentInterface agentInterface){ 
		super();
		this.agentInterface = agentInterface; 							// The interface to the environment for performing actions
		goalStrategies.add(new WumpusExploreStrategy()); 				// The strategy to explore the world
		goalStrategies.add(new TraverseStrategy());						// The strategy to get to a specific location
		messageHandlingStrategies.add(new WumpusMessageStrategy());		// The strategy to handle a message
		eventStrategies.add(new AgentAnnouncedStrategy());				// The strategy to handle the announcement of a new agent arrival
	}
	
	public AgentInstantiation instantiate(Object[] arguments) {
		// Create belief state
		WumpusContext context = new WumpusContext(agentInterface);  
		Messenger messenger = (Messenger)arguments[0];
		WumpusAgentInstantiation a = new WumpusAgentInstantiation(this, context, messenger);
		int id = agentInterface.registerAgent(a);
 
		if(id >= 0){ // Otherwise the environment is full
			// Let the agent know its personal ID
			context.setID(id);
			
			// Deliberation cycle
			Queue<Integer> actorCycle = a.getActorCycle();
			actorCycle.add(PlatformNode.GOAL_ACHIEVER);
			actorCycle.add(PlatformNode.EXTERNAL_EVENT_HANDLER);
			actorCycle.add(PlatformNode.MESSAGE_HANDLER);
			actorCycle.add(PlatformNode.PLAN_EXECUTOR);
			actorCycle.add(PlatformNode.REPAIRER);
			
			//Initial plan
			a.adoptPlan(initialStrategy.instantiate(null,a));
			
			return a;
		} else return null; // Too many agents registered
	}
}
