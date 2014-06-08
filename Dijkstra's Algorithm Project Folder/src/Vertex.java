import java.util.ArrayList;

public class Vertex
{
	/* This class defines the vertex and keeps track of all its neighbors as well as whether the vertex is 
	 * solid or not. It also notes which nodes have been solved during execution.
	 */
	
	//Directions of the adjacent vertices. Each direction's number coordinates with its index in the neighbors array below
	public static final int EAST = 0;
	public static final int NORTH = 1;
	public static final int WEST = 2;
	public static final int SOUTH = 3;
	public static final int VERTEX_DIST = 1; //Default value between vertices
	
	private Edge[] neighbors; //All edges that originate from the vertex
	private boolean isWall; 
	private int reachValue;
	private boolean resolved;
	
	//Vertex Constructor
	//Defines the vertex as having 4 adjacent vertices and a default reach value being infinity
	public Vertex()
	{
		neighbors = new Edge[4];
		reachValue = Integer.MAX_VALUE;
	}
	
	//Precondition: A potential neighboring vertex and the direction in which it will be located in reference to the original vertex
	//Postcondition: Each vertex has the opposite vertex defined as its neighbor by creating an edge between them with the default value between vertices
	public void setNeighbor(int dir, Vertex v)
	{
		Edge e = new Edge(this, v, VERTEX_DIST);
		this.neighbors[dir] = e;
		v.neighbors[getOppositeDirection(dir)] = e;
	}
	
	//Precondition: The direction in which the neighboring vertex is located
	//Postcondition: The adjacent vertex is returned
	public Vertex getNeighbor(int dir)
	{
		return neighbors[dir].getOtherLinkedVertex(this);
	}
	
	//Precondition: A vertex with defined neighbors
	//Postcondition: All neighboring vertices are returned
	public ArrayList<Vertex> getNeighbors()
	{
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		for (Edge e: neighbors)
		{
			list.add(e.getOtherLinkedVertex(this));
		}
		return list;
	}
	
	//Precondition: A vertex with defined neighbors
	//Postcondition: All edges originating from the vertex are returned
	public Edge[] getNeighboringEdges()
	{
		return neighbors;
	}
	
	//Precondition: A defined vertex
	//Postcondition: Returns whether or not the vertex is a wall in the puzzle
	public boolean isWall()
	{
		return isWall;
	}
	
	//Precondition: A boolean value stating whether the vertex is a wall (true) or is not a wall (false)
	//Postcondition: Sets the isWall variable, defining whether or not the vertex is a wall
	public void setWall(boolean wall)
	{
		isWall = wall;
	}
	
	//Precondition: A defined vertex
	//Postcondition: The reach value of the vertex is returned
	public int getReachValue()
	{
		return reachValue;
	}
	
	//Precondition: An integer defining the reach distance for the vertex
	//Postcondition: The reachValue of the vertex is set as this integer 
	public void setReachValue(int d)
	{
		reachValue = d;
	}
	
	//Precondition: A defined vertex
	//Postcondition: Returns whether or not the vertex has been resolved when solving the puzzle
	public boolean getResolved()
	{
		return resolved;
	}
	
	//Precondition: A boolean value indicating whether the vertex has been solved
	//Postcondition: Sets the resolved value to this boolean. Defines the vertex as solved (true) or unsolved (false)
	public void setResolved(boolean r)
	{
		resolved = r;
	}
	
	//Precondition: An integer in coordination with one of the 4 directions defined above
	//Postcondition: Returns an integer value in coordination with the opposite direction of the inputed integer
	private static int getOppositeDirection(int dir)
	{
		dir -= 2;
		if (dir < 0)
		{
			dir += 4;
		}
		return dir;
	}
	
	//Precondition: A vertex and the integer equivalent of a direction
	//Postcondition: All of the vertices in the specified direction are linked together (made neighbors) until there are no more vertices to link
	public static void chain(int dir, ArrayList<Vertex> v)
	{
		for (int x = 0; x < v.size() - 1; x++)
		{
			v.get(x).setNeighbor(dir, v.get(x + 1));
		}
	}
}