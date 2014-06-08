import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Grid 
{
	/* This class includes methods that take a text file of the puzzle and turn it into a grid on which computations
	 * can now be performed. It also consists of the methods that find the shortest path to the finish using Dijkstra's
	 * Algorithm. 
	 */
	
	private Vertex origin; // (0,0)
	private char[][] chars;
	private Vertex start, finish;
	
	//Grid Constructor
	//Reads in the text file line by line, produces the grid, and sets the start and end point
	public Grid(Scanner scan)
	{
		ArrayList<String> input = new ArrayList<String>();
		
		while(scan.hasNext())
		{
			input.add(scan.nextLine().trim());
		}
		
		chars = new char[input.size()][];
		
		for (int x = 0; x < input.size(); x++)
		{
			chars[x] = input.get(x).toCharArray();
		}
		
		createGrid(getWidth(), getHeight());
		setWalls();
		
		start = getStart();
		finish = getFinish();
		
	}
	
	//Precondition: A grid has been created
	//Postcondition: Returns the Vertex that represents the 'S' character
	public Vertex getStart()
	{
		for (int y = 0; y < getHeight(); y++)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				if(chars[y][x] == 'S')
				{
					return getVertex(x, y);
				}
			}
		}
		return null;
	}
	
	//Precondition: A grid has been created
	//Postcondition: Returns the Vertex that represents the 'F' character
	public Vertex getFinish()
	{
		for (int y = 0; y < getHeight(); y++)
		{
			for ( int x = 0; x < getWidth(); x++)
			{
				if (chars[y][x] == 'F')
				{
					Vertex temp = getVertex(x,y);
					return temp;
				}
			}
		}
		return null;
	}
	
	//Precondition: A grid has been created
	//Postcondition: The length (vertical length) of the grid is returned
	public int getHeight()
	{
		return chars.length;
	}

	//Precondition: A grid has been created
	//Postcondition: The width (horizontal length) of the grid is returned
	public int getWidth()
	{
		return chars[0].length;
	}
	
	//Precondition: A grid has been created
	//Postcondition: Returns all the individual vertices that make the grid
	private ArrayList<Vertex> getAllVertices()
	{
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (int y = 0; y < getHeight(); y++)
		{
			for ( int x = 0; x < getWidth(); x++)
			{
				vertices.add(getVertex(x,y));
			}
		}
		return vertices;
	}
	
	//Precondition: A puzzle that may have already been solved
	//Postcondition: Resets all of the reach values of the vertex back to infinity to remove error from re-execution 
	//				 of the algorithm with different start and end points
	private void resetVertices()
	{
		for (Vertex v: getAllVertices())
		{
			v.setReachValue(Integer.MAX_VALUE);
			v.setResolved(false);
		}
	}
	
	//Precondition: An instantiated grid
	//Postcondition: Solves for and returns the shortest path to the end point (vertex for char 'F') from the starting point (vertex for char 'S') using DIJKSTRA'S ALGORITHM
	public ArrayList<Vertex> getPath()
	{
		//resets all the reach values for the vertices in case the same puzzle is solved again with different
		//beginning and end points
		resetVertices();
		
		//sets all nodes as unresolved
		HashSet<Vertex> unvisited = new HashSet<Vertex>();
		unvisited.addAll(getAllVertices());
		
		//sets the reach value for the starting node to 0 and considers it solved
		start.setReachValue(0);
		unvisited.remove(start);
		
		//finds the vertex that has the lowest reach value from any of the solved vertices
		//loop continues until all nodes and/or finish has been resolved 
		Vertex current = start;
		while(!(finish.getResolved() || current.getReachValue() == Integer.MAX_VALUE))
		{
			//checks all edges for the shortest reach value and changes the adjacent node's reach value accordingly
			for(Edge e: current.getNeighboringEdges())
			{
				if(e != null && !e.getOtherLinkedVertex(current).isWall() && current.getReachValue() + e.getDistance() < e.getOtherLinkedVertex(current).getReachValue())
				{
					e.getOtherLinkedVertex(current).setReachValue(current.getReachValue() + e.getDistance());
				}
			}
			current.setResolved(true);
			unvisited.remove(current);
			current = getLowestDistance(unvisited);
		}
		
		//checks whether finish has been resolved. If not, exception is thrown.
		if(!finish.getResolved())
		{
			throw new UnsolvablePuzzleException();
		}
		
		//creates the arraylist which contains all vertices with the lowest reach values that produce the path to the finish
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		current = finish;
		path.add(current);
		while(current != start)
		{
			current = getLowestDistance(current.getNeighbors());
			path.add(0, current);
		}
		return path;
	}
	
	//Precondition: the dimensions of the puzzle
	//Postcondition: creates the grid
	private void createGrid(int east, int south)
	{
		ArrayList<ArrayList<Vertex>> grid = new ArrayList<ArrayList<Vertex>>();
		
		//columns
		for(int x = 0; x < east; x++)
		{
			grid.add(new ArrayList<Vertex>());
		}
		
		//rows
		for(int y = 0; y < south; y++)
		{
			ArrayList<Vertex> current = new ArrayList<Vertex>();
			for (int x = 0; x < east; x++)
			{
				Vertex v = new Vertex();
				current.add(v);
				grid.get(x).add(v);
			}
			//links across all the vertices
			Vertex.chain(Vertex.EAST, current);
		}
		
		//links down all the vertices
		for (int x = 0; x < east; x++)
		{
			Vertex.chain(Vertex.SOUTH, grid.get(x));
		}
		origin = grid.get(0).get(0);
	}
	
	//Precondition: a created grid
	//Postcondition: checks for all vertices with char = 'X' and sets them as a wall
	private void setWalls()
	{
		for (int y = 0; y < chars.length; y++)
		{
			for (int x = 0; x < chars[y].length; x++)
			{
				if (chars[y][x] == 'X')
				{
					getVertex(x, y).setWall(true);
				}
			}
		}
	}
	
	//Precondition: the number of nodes you need to move east and south from the origin to reach the vertex
	//Postcondition: returns the vertex that corresponds with the coordinates
	public Vertex getVertex(int east, int south)
	{
		Vertex current = origin;
		for(int x = 0; x < east; x++)
		{
			current = current.getNeighbor(Vertex.EAST);
		}
		for(int y = 0; y < south; y++)
		{
			current = current.getNeighbor(Vertex.SOUTH);
		}
		return current;
	}
	
	//Precondition: An arraylist of vertices
	//Postcondition: Returns the vertex that has a shortest reach value in the list
	private Vertex getLowestDistance(ArrayList<Vertex> list)
	{
		Vertex closest = null;
		for (Vertex v: list)
		{
			if(v != null)
			{
				if(closest == null)
				{
					closest = v;
				}
				else
				{
					if(closest.getReachValue() > v.getReachValue())
					{
						closest = v;
					}
				}
			}
		}
		return closest;
	}
	
	//Precondition: A hashset of vertices
	//Postcondition: Returns the vertex that has a shortest reach value in the set
	private Vertex getLowestDistance(HashSet<Vertex> set)
	{
		Vertex closest = null;
		for (Vertex v: set)
		{
			if(v != null)
			{
				if(closest == null)
				{
					closest = v;
				}
				else
				{
					if(closest.getReachValue() > v.getReachValue())
					{
						closest = v;
					}
				}
			}
		}
		return closest;
	}
	
	//Precondition: Takes the x and y coordinates for where the new start position should be located
	//Postcondition: Changes the location of the start vertex and relocates the 'S' in the chars array 
	public boolean setStart(int x, int y)
	{
		Vertex temp = start;
		
		if (!this.getVertex(x, y).isWall())
		{
			if (getVertex(x, y) != start)
			{
				if (getVertex(x, y) != finish)
				{
					Vertex s = this.getVertex(x, y);
					start = s;
					for (int j = 0; j < getHeight(); j++)
					{
						for (int i = 0; i < getWidth(); i++)
						{
							if(chars[j][i] == 'S')
							{
								chars[j][i] = ' ';
							}
						}
					}
					chars[y][x] = 'S';
				}
			}
		}
		
		if (start == temp)
		{
			JOptionPane.showMessageDialog(null, "Start must be set to an empty location.");
			return false;
		}
		else
		{
			return true;
		}
	}
	
	//Precondition: Takes the x and y coordinates for where the new end position should be located
	//Postcondition: Changes the location of the end vertex and relocates the 'F' in the chars array
	public boolean setFinish(int x, int y)
	{
		Vertex temp = finish;
		
		if (!this.getVertex(x, y).isWall())
		{
			if (getVertex(x, y) != start)
			{
				if (getVertex(x, y) != finish)
				{
					Vertex f = this.getVertex(x, y);
					finish = f;
					for (int j = 0; j < getHeight(); j++)
					{
						for (int i = 0; i < getWidth(); i++)
						{
							if(chars[j][i] == 'F')
							{
								chars[j][i] = ' ';
							}
						}
					}
					chars[y][x] = 'F';
				}
			}
		}
		
		if (finish == temp)
		{
			JOptionPane.showMessageDialog(null, "Finish must be set to an empty location.");
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	//Precondition: A grid object has been created
	//Post Condition: Returns a string that represents the original puzzle
	public String toString()
	{
		String str = "";
		for (int y = 0; y < getHeight(); y++)
		{
			for(int x = 0; x < getWidth(); x++)
			{
				if(getVertex(x,y).isWall())
				{
					str += "X";
				}
				else if (getVertex(x,y) == getStart())
				{
					str += "S";
				}
				else if (getVertex(x,y) == getFinish())
				{
					str += "F";
				}
				else
				{
					str += " ";
				}
			}
			str += "\n";
		}
		return str;
	}
	
	//Precondition: A grid object has been created
	//PostCondition: Returns an array of strings, each element containing one row of the grid of the puzzle
	public String[] toStringArray()
	{
		String[] str = new String[getHeight()];
		String temp;
		for (int y = 0; y < getHeight(); y++)
		{
			temp = "";
			for(int x = 0; x < getWidth(); x++)
			{
				if(getVertex(x,y).isWall())
				{
					temp += "X";
				}
				else if (getVertex(x,y) == getStart())
				{
					temp += "S";
				}
				else if (getVertex(x,y) == getFinish())
				{
					temp += "F";
				}
				else
				{
					temp += " ";
				}
			}
			str[y] = temp;
		}
		return str;
	}
	
	//Precondition: An ArrayList of vertices containing all the vertices that are in the solution path of a puzzle
	//Postcondition: Returns a string that includes the original puzzle with the solved path marked out
	public String getSolution(ArrayList<Vertex> path)
	{
		String str = "";
		for (int y = 0; y < getHeight(); y++)
		{
			for(int x = 0; x < getWidth(); x++)
			{
				if(getVertex(x,y).isWall())
				{
					str += "X";
				}
				else if (getVertex(x,y) == getStart())
				{
					str += "S";
				}
				else if (getVertex(x,y) == getFinish())
				{
					str += "F";
				}
				else if (path.contains(getVertex(x,y)))
				{
					str += "*";
				}
				else
				{
					str += " ";
				}
			}
			str += "\n";
		}
		return str;
	}

	//Precondition: A Grid object has been created
	//Postcondition: Returns a ButtonGrid displaying the puzzle stored in the Grid object
	public ButtonGrid drawPuzzle()
	{
		ButtonGrid puzzle = new ButtonGrid(this, toStringArray());
		puzzle.frame.setBounds(new Rectangle(getWidth() * 30, getHeight() * 30 + 60));
		puzzle.centerOnScreen();
		
		for (int y = 0; y < getHeight(); y++)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				if (chars[y][x] == 'X')
				{
					 puzzle.getButton(y, x).setIcon(new ImageIcon("wall.png"));
				}
				else if (chars[y][x] == 'S')
				{
					puzzle.getButton(y, x).setIcon(new ImageIcon("start.png"));
				}
				else if (chars[y][x] == 'F')
				{
					puzzle.getButton(y, x).setIcon(new ImageIcon("finish.png"));
				}
				else if (chars[y][x] == ' ')
				{
					puzzle.getButton(y, x).setIcon(new ImageIcon("empty.png"));
				}
			}
		}
		return puzzle;
	}
	
	//Precondition: The vertices leading from a Grid object's start to its finish have been found and stored in an ArrayList
	//Postcondition: Returns a ButtonGrid displaying the puzzle stored in the Grid object with its solution drawn in
	public void drawSolution(ArrayList<Vertex> path)
	{
		ButtonGrid puzzleSolved = new ButtonGrid(this, toStringArray());
		puzzleSolved.frame.setBounds(new Rectangle(getWidth() * 30, getHeight() * 30 + 60));
		puzzleSolved.centerOnScreen();
		
		for (int y = 0; y < getHeight(); y++)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				if (chars[y][x] == 'X')
				{
					 puzzleSolved.getButton(y, x).setIcon(new ImageIcon("wall.png"));
				}
				else if (getVertex(x,y) == start)
				{
					puzzleSolved.getButton(y, x).setIcon(new ImageIcon("start.png"));
				}
				else if (getVertex(x,y) == finish)
				{
					puzzleSolved.getButton(y, x).setIcon(new ImageIcon("finish.png"));
				}
				else 
				{
					puzzleSolved.getButton(y, x).setIcon(new ImageIcon("empty.png"));
				}
			}
		}
		for (int y = 0; y < getHeight(); y++)
		{
			for(int x = 0; x < getWidth(); x++)
			{
				if (path.contains(getVertex(x,y))) 
				{ 
					if (getVertex(x,y) != start)
					{
						if (getVertex(x,y) != finish)
						{
							puzzleSolved.getButton(y,x).setIcon(new ImageIcon("path.png"));
						}
					}
				}
			}
		}
	}
}