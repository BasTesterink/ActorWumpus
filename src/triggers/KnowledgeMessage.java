package triggers;

import messaging.LocalMessage;
/**
 * This message informs other agents whether a location is certainly safe or has the wumpus. 
 * 
 * @author Bas Testerink
 */
public class KnowledgeMessage extends LocalMessage {
	private int x, y;
	private boolean hasWumpus, canHaveWumpus, canHavePit;
	
	public KnowledgeMessage(int x, int y, boolean hasWumpus, boolean canHaveWumpus, boolean canHavePit){
		this.x = x;
		this.y = y;
		this.hasWumpus = hasWumpus;
		this.canHaveWumpus = canHaveWumpus;
		this.canHavePit = canHavePit;
	}
	
	public int getX(){ return x; }
	public int getY(){ return y; }
	public boolean hasWumpus(){ return hasWumpus; }
	public boolean canHaveWumpus(){ return canHaveWumpus; } 
	public boolean canHavePit(){ return canHavePit; }
	
	public void toJSON(StringBuffer r){
		r.append('{');
		r.append("\"Location\": \"("+x+","+y+")\",");
		r.append("\"HasWumpus\": \""+hasWumpus+"\",");
		r.append("\"CanHaveWumpus\": \""+canHaveWumpus+"\",");
		r.append("\"CanHavePit\": \""+canHavePit+"\"");
		r.append('}'); 
	} 
}
