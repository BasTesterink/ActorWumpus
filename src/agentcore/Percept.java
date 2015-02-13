package agentcore;
/**
 * Auxiliary class that is a data container for perception.
 * 
 * @author Bas Testerink
 */
public class Percept {
	private boolean breeze, stench, glitter, agent0, agent1, agent2, agent3, chest;
	private int x, y;
	public boolean isBreeze() {
		return breeze;
	}
	public void setBreeze(boolean breeze) {
		this.breeze = breeze;
	}
	public boolean isStench() {
		return stench;
	}
	public void setStench(boolean stench) {
		this.stench = stench;
	}
	public boolean isGlitter() {
		return glitter;
	}
	public void setGlitter(boolean glitter) {
		this.glitter = glitter;
	}
	public boolean isAgent0() {
		return agent0;
	}
	public void setAgent0(boolean agent0) {
		this.agent0 = agent0;
	}
	public boolean isAgent1() {
		return agent1;
	}
	public void setAgent1(boolean agent1) {
		this.agent1 = agent1;
	}
	public boolean isAgent2() {
		return agent2;
	}
	public void setAgent2(boolean agent2) {
		this.agent2 = agent2;
	}
	public boolean isAgent3() {
		return agent3;
	}
	public void setAgent3(boolean agent3) {
		this.agent3 = agent3;
	}
	public boolean isChest() {
		return chest;
	}
	public void setChest(boolean chest) {
		this.chest = chest;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
}
