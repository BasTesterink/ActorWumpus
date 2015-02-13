package pathplanning;

import java.util.ArrayList;
/**
 * Aux class for path planning.
 * 
 * @author Bas Testerink
 */
public class Vertex implements Comparable<Vertex>{
	private int distance = Integer.MAX_VALUE;
	private Vertex previous = null;
	protected ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
	private boolean scanned = false;
	
	public int getDistance(){ return distance; }	public void setDistance(int distance){ this.distance = distance; }
	public Vertex getPrevious(){ return previous; }	public void setPrevious(Vertex previous){ this.previous = previous; }
	public ArrayList<Vertex> getNeighbors(){ return neighbors; }
	public boolean isScanned(){ return scanned; } 	public void setScanned(boolean scanned){ this.scanned = scanned; }
	
	public void reInit(){
		distance = Integer.MAX_VALUE;
		previous = null;
		scanned = false;
	}
	
	/**
	 * Standard implementation returns 1 if v is a neighbor, otherwise Integer.MAX_VALUE.
	 * @param v The target vertex. 
	 * @return The distance between this vertex and v. 
	 */
	public int length(Vertex v){
		if(neighbors.contains(v)) return 1; 
		else return Integer.MAX_VALUE;
	}
	
	@Override
	public int compareTo(Vertex n) { 
		return n.getDistance()==distance?0:(n.getDistance()<distance?(-1):1);
	}
} 