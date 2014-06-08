import java.io.*; 
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonGrid 
{
	/* This class creates the GUI and ButtonGrids that represent the individual puzzles and allows
	 * the user to open new puzzles, have the computer solve for the shortest path, and change the
	 * locations of the start and end points.  
	 */
	
	private Grid puzzle;
    JFrame frame = new JFrame();	
    JButton[][] grid;	//2-D Array of buttons
     
    //ButtonGrid Constructor
    //Creates a basic grid of buttons
    public ButtonGrid()
    {
       	//Sets the dimensions for the ButtonGrid
       	frame.setLayout(new GridLayout(10, 10));
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        
        
        //Adds a file and puzzle drop down to the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu puzzleMenu = new JMenu("Puzzle");
        menuBar.add(fileMenu);
        menuBar.add(puzzleMenu);
                
        //Creates menu items and adds actionlisteners to them
        JMenuItem openAction = new JMenuItem("Open");
        JMenuItem exitAction = new JMenuItem("Exit");
        JMenuItem solveAction = new JMenuItem("Solve");
        JMenuItem newStartAction = new JMenuItem("New Start");
        JMenuItem newFinishAction = new JMenuItem("New Finish");
        openAction.addActionListener(new MenuActionListener(this));
        exitAction.addActionListener(new MenuActionListener(this));
        solveAction.addActionListener(new MenuActionListener(this));
        newStartAction.addActionListener(new MenuActionListener(this));
        newFinishAction.addActionListener(new MenuActionListener(this));
             
        //Adds menu items to the drop-down menus
        fileMenu.add(openAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        puzzleMenu.add(solveAction);
        puzzleMenu.add(newStartAction);
        puzzleMenu.add(newFinishAction);
           
        //Creates the grid of buttons with the default size being 10 by 10
        grid = new JButton[10][10];
        for(int y = 0; y < 10; y++)
        {
           	for(int x = 0; x < 10; x++)
            {
           		grid[y][x] = new JButton(""); 
                frame.add(grid[y][x]);
            }
        }            
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); 
        frame.setBounds(new Rectangle(330, 360));
        centerOnScreen();
        frame.setVisible(true); 
    }
        
    //ButtonGrid Constructor
    //Creates a grid that represents the puzzle
    public ButtonGrid(Grid p, String[] s) 
    {         	
    	puzzle = p;
        	
        //Sets the dimensions for the ButtonGrid according to the puzzle size
        frame.setLayout(new GridLayout(s.length, s[0].length()));
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        //Creates menu items
        JMenu fileMenu = new JMenu("File");
        JMenu puzzleMenu = new JMenu("Puzzle");
        menuBar.add(fileMenu);
        menuBar.add(puzzleMenu);
                
        //Creates menu items and adds actionlisteners to them
        JMenuItem openAction = new JMenuItem("Open");
        JMenuItem exitAction = new JMenuItem("Exit");
        JMenuItem solveAction = new JMenuItem("Solve");
        JMenuItem newStartAction = new JMenuItem("New Start");
        JMenuItem newFinishAction = new JMenuItem("New Finish");
        openAction.addActionListener(new MenuActionListener(this));
        exitAction.addActionListener(new MenuActionListener(this));
        solveAction.addActionListener(new MenuActionListener(this));
        newStartAction.addActionListener(new MenuActionListener(this));
        newFinishAction.addActionListener(new MenuActionListener(this));
             
        //Adds menu items to the drop-down menus	
        fileMenu.add(openAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        puzzleMenu.add(solveAction);
        puzzleMenu.add(newStartAction);
        puzzleMenu.add(newFinishAction);
                
        //Create the button matrix and size it according to the puzzle
        grid = new JButton[s.length][s[0].length()];
        for(int y = 0; y < s.length; y++)
        {
        	for(int x = 0; x < s[0].length(); x++)
            {
        		grid[y][x] = new JButton("");
                frame.add(grid[y][x]); 
            }
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); 
        frame.setVisible(true); 
    }

    //Precondition: Requires the x and y coordinates of the button
    //Postcondition: Returns the button that corresponds with the provided coordinates
    public JButton getButton(int x, int y)
    {
    	return grid[x][y];
    }
        
    //Precondition: The button grid must have a reference to the puzzle grid
    //Postcondition: Returns the puzzle grid
    public Grid getGrid()
    {
    	return puzzle;
    } 
    
    //Precondition: An initialized frame
    //Postcondition: Centers the frame on the screen
    public void centerOnScreen()
    {
    	// Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         
        // Determine the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int xloc = (dim.width-w)/2;
        int yloc = (dim.height-h)/2;

        // Move the window
        frame.setLocation(xloc, yloc);
    }
}

class MenuActionListener implements ActionListener 
{
	/* This class registers any actions made on the menu and acts accordingly in response to 
	 * the command. 
	 */
	
	private ButtonGrid reference;
	
	//MenuActionListener Constructor
	//Places the grid in question into a reference variable for access
	public MenuActionListener(ButtonGrid bg)
	{
		reference = bg;
	}
	
	//Precondition: An action is performed on any of the menu items
	//Postcondition: The proper action is performed
	public void actionPerformed(ActionEvent a)
	{
		String command = a.getActionCommand();
		//Checks if open has been selected
		if (command.equals("Open"))
		{
			//Asks for a file to be opened and then opens the puzzle. If file is not present, opens a
			//window that explains the error.
			try
			{
				String fileName;
				fileName = JOptionPane.showInputDialog("Enter the name of the text file that contains the puzzle:");
				if(fileName == null)
				{
					throw new ExitException();
				}
				Scanner scan = new Scanner(new File(fileName));
				Grid openedGrid = new Grid(scan);
				if (reference.getButton(0, 0).getIcon() == null)
				{
					reference.frame.dispose();
				}
				openedGrid.drawPuzzle();
			}
			catch (ExitException e)
			{
			}
			catch (IOException i)
			{
				JOptionPane.showMessageDialog(null, "Error: File does not exist within the project folder.");
			}
		}
		//Checks if exit has been selected
		else if (command.equals("Exit"))
		{
			//Closes the frame
			reference.frame.dispose();
		}
		//Checks if solve has been selected
		else if (command.equals("Solve"))
		{
			//Solves and displays the path. If unsolvable, opens a dialog box that explains the error
			try
			{
				if (reference.getButton(0, 0).getIcon() == null)
				{
					throw new NullPointerException();
				}
				ArrayList<Vertex> path = reference.getGrid().getPath();
				reference.frame.dispose();
				reference.getGrid().drawSolution(path);
				JOptionPane.showMessageDialog(null, "Path Length: " + (path.size() - 1));
			}
			catch (NullPointerException n)
			{
				JOptionPane.showMessageDialog(null, "Error: No valid puzzle has been opened.");
			}
			catch (UnsolvablePuzzleException u)
			{
				JOptionPane.showMessageDialog(null, "Error: Puzzle has no solution.");
			}
		}
		//Checks if new start has been selected
		else if (command.equals("New Start"))
		{
			//Takes an input for the coordinates of a new start location and redraws the grid, placing the
			//start marker in the specified location. If the location is not accessible, then a message is 
			//displayed.
			try
			{
				if (reference.getButton(0, 0).getIcon() == null)
				{
					throw new NullPointerException();
				}
				String xcoor, ycoor;
				int x, y;
				xcoor = JOptionPane.showInputDialog("Enter the x-coordinate of the new starting point:");
				if(xcoor == null)
				{
					throw new ExitException();
				}
				ycoor = JOptionPane.showInputDialog("Enter the y-coordinate of the new starting point:");
				if(ycoor == null)
				{
					throw new ExitException();
				}
				x = Integer.parseInt(xcoor);
				y = Integer.parseInt(ycoor);
				if (x <= reference.getGrid().getWidth() && y <= reference.getGrid().getHeight())
				{
					if (reference.getGrid().setStart(x, y))
					{
						reference.frame.dispose();
						reference.getGrid().drawPuzzle();						
					}
				}
				else 
				{
					throw new IndexOutOfBoundsException();
				}
			}
			catch (NullPointerException n)
			{
				JOptionPane.showMessageDialog(null, "Error: No valid puzzle has been opened.");
			}
			catch (ExitException e)
			{
			}
			catch (NumberFormatException nf)
			{
				JOptionPane.showMessageDialog(null, "Error: Input must be an integer.");
			}
			catch (IndexOutOfBoundsException i)
			{
				JOptionPane.showMessageDialog(null, "Error: Input must be within the bounds of the puzzle.");
			}
			
		}
		//Checks if new finish has been selected
		else if (command.equals("New Finish"))
		{
			//Takes an input for the coordinates of a new end location and redraws the grid, placing the
			//end marker in the specified location. If the location is not accessible, then a message is 
			//displayed.
			try
			{
				if (reference.getButton(0, 0).getIcon() == null)
				{
					throw new NullPointerException();
				}
				String xcoor, ycoor;
				int x, y;
				xcoor = JOptionPane.showInputDialog("Enter the x-coordinate of the new ending point:");
				if(xcoor == null)
				{
					throw new ExitException();
				}
				ycoor = JOptionPane.showInputDialog("Enter the y-coordinate of the new ending point:");
				if(ycoor == null)
				{
					throw new ExitException();
				}
				x = Integer.parseInt(xcoor);
				y = Integer.parseInt(ycoor);
				if (x <= reference.getGrid().getWidth() && y <= reference.getGrid().getHeight())
				{
					if (reference.getGrid().setFinish(x, y))
					{
						reference.frame.dispose();
						reference.getGrid().drawPuzzle();						
					}
				}
				else 
				{
					throw new IndexOutOfBoundsException();
				}
			}
			catch (NullPointerException n)
			{
				JOptionPane.showMessageDialog(null, "Error: No valid puzzle has been opened.");
			}
			catch (ExitException e)
			{
			}
			catch (NumberFormatException n)
			{
				JOptionPane.showMessageDialog(null, "Error: Input must be an integer.");
			}
			catch (IndexOutOfBoundsException i)
			{
				JOptionPane.showMessageDialog(null, "Error: Input must be within the bounds of the puzzle.");
			}
		}
	}
}