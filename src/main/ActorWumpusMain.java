package main;

import javax.swing.JFrame;

import messaging.LocalMessenger;
import messaging.Messenger;

import actors.ExternalEventHandler;
import actors.GoalAchiever;
import actors.MessageHandler;
import actors.PlanExecutor;
import actors.Repairer;
import agentcore.WumpusAgent;
import agentcore.WumpusConstants;
import agentplatform.PlatformNode;
import environment.AgentInterface;
import environment.Wumpus;
import environment.WumpusGUI;

public class ActorWumpusMain {
	// Wumpus world attributes
	private Wumpus w_env;
	private WumpusGUI w_gui;
	private AgentInterface agentInterface;
	private JFrame w_frame;
	private PlatformNode platform;
	private Messenger messenger;
	
	public static void main(String[] arg){
		ActorWumpusMain m = new ActorWumpusMain();
		m.startWumpusWorld();
		m.createAgentPlatform();
		m.createAgents();
	}
	
	public void startWumpusWorld(){
		w_env = new Wumpus();
		w_env.loadFromFile("./resources/standard_wumpus.txt", false); // Change this to load other worlds
		//w_env.loadFromFile("./resources/bigwumpus2.txt", false);
		w_gui = new WumpusGUI(w_env, 30);
		w_gui.addMouseListener(w_gui);
		w_frame = new JFrame();
		w_frame.add(w_gui);
		w_frame.setSize(1000,800);
		w_gui.start_animation();
		w_frame.setVisible(true); 
		agentInterface = new AgentInterface(w_env, w_gui);  
	}
	
	public void createAgentPlatform(){
		platform = new PlatformNode();
		platform.reset();
		platform.setDebugMode(true);
		
		// Generate execution actors, add them to the repository and instantiate one of each.
		GoalAchiever goalActor = new GoalAchiever(5,platform);
		ExternalEventHandler eventActor = new ExternalEventHandler(5,platform);
		MessageHandler messageActor = new MessageHandler(5,platform);
		PlanExecutor executorActor = new PlanExecutor(5,platform);
		Repairer repairActor = new Repairer(5,platform);
		
		platform.addActorToRepository(PlatformNode.GOAL_ACHIEVER, goalActor);
		platform.addActorToRepository(PlatformNode.EXTERNAL_EVENT_HANDLER, eventActor);
		platform.addActorToRepository(PlatformNode.MESSAGE_HANDLER, messageActor);
		platform.addActorToRepository(PlatformNode.PLAN_EXECUTOR, executorActor);
		platform.addActorToRepository(PlatformNode.REPAIRER, repairActor);
		
		platform.instantiateActor(PlatformNode.GOAL_ACHIEVER);
		platform.instantiateActor(PlatformNode.EXTERNAL_EVENT_HANDLER);
		platform.instantiateActor(PlatformNode.MESSAGE_HANDLER);
		platform.instantiateActor(PlatformNode.PLAN_EXECUTOR);
		platform.instantiateActor(PlatformNode.REPAIRER);
		
		messenger = new LocalMessenger();
		platform.addMessenger(0, messenger);
	}
	
	public void createAgents(){
		// Create the agent class and an instantiation. (first we do single agent system)
		WumpusAgent agent = new WumpusAgent(agentInterface);
		Object[] args = new Object[]{messenger};
		platform.addAgentToRepository(WumpusConstants.VERSION1, agent); 
		// Copy/remove to add more agents
		platform.instantiateAgent(WumpusConstants.VERSION1, args); 
		platform.instantiateAgent(WumpusConstants.VERSION1, args);
		platform.instantiateAgent(WumpusConstants.VERSION1, args);
	}
}
