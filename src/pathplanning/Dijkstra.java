package pathplanning;
 
import java.util.List;
import java.util.PriorityQueue;
/**
 * A standard Dijkstra implementation.
 * @author Bas Testerink
 *
 */
public class Dijkstra {
	/**
	 * Will for each vertex set the previous field for the route towards the
	 * source in the shortest way possible. The previous field will be null
	 * and the distance <code>Integer.MAX_VALUE</code> if the vertex was 
	 * unreachable from the source. 
	 * @param vertices
	 * @param source
	 */
	public static void dijkstra(List<Vertex> vertices, Vertex source){
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.offer(source);
		for(Vertex v : vertices) v.reInit();
		source.setDistance(0);
		
		while(!queue.isEmpty()){
			Vertex next = queue.poll();
			next.setScanned(true);
			
			for(Vertex v : next.getNeighbors()){
				if(!v.isScanned()){
					int fromNext =  next.getDistance()+next.length(v);
					if(v.getDistance() > fromNext){
						v.setDistance(fromNext);
						v.setPrevious(next);
						if(queue.contains(v)) queue.remove(v);
						queue.offer(v);
					}
				}
			}
		}
	}
	
}
