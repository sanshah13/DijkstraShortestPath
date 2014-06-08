public class Edge 
{
	/*This class works as the connection between two or more vertices on a 
	 *graph. In our program, the edge class is the connection between each individual
	 *character in the puzzle, including the spaces. 
	 */
	
	//Connection between two vertices, in both directions, with distance
	private Vertex[] conn; 
	private int distance;
	
	//Edge Constructor
	//Precondition: The two vertices that are connected (v0, v1) and the distance between them (dist)
	//Postcondition: The vertices are set as the end points and the edge is given the inputed distance 
	public Edge(Vertex v0, Vertex v1, int dist)
	{
		conn = new Vertex[2];
		conn[0] = v0;
		conn[1] = v1;
		distance = dist;
	}
	
	//Precondition: One of the vertices at the end of an edge
	//Postcondition: The vertex that is connected to the provided vertex is returned
	public Vertex getOtherLinkedVertex(Vertex v)
	{
		if(conn[0] == v && conn[1] != v)
		{
			return conn[1];
		}
		else if(conn[0] != v && conn[1] == v)
		{
			return conn[0];
		}
		else 
		{
			throw new IllegalArgumentException();
		}
	}
	
	//Precondition: An initialized edge
	//Postcondition: Returns the distance of the edge
	public int getDistance()
	{
		return distance;
	}	
}