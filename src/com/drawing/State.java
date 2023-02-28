import java.util.ArrayList;

// The data of a single state of a superposition
public class State
{
  private int originalRef;
  private int[] edges;
  private ArrayList<Integer> up;
  private ArrayList<Integer> right;
  private ArrayList<Integer> down;
  private ArrayList<Integer> left;
  private int numRotations;
  public State(int originalRef, int[] edges, int numRotations)
  {
    this.originalRef = originalRef;
    this.edges = new int[edges.length];
    for (int i = 0; i < edges.length; i++)
      this.edges[i] = edges[i];
    
    this.numRotations = numRotations;
    
    up = new ArrayList<Integer>();
    right = new ArrayList<Integer>();
    down = new ArrayList<Integer>();
    left = new ArrayList<Integer>();
  }
  
  public int getEdge(int index)
  {
    if (index < edges.length && index >= 0)
      return edges[index];
    else
      return -1;
  }
  
  public int getReference()
  {
    return originalRef;
  }
  
  public int getNumRotations()
  {
    return numRotations;
  }
  
  // Adjusts the 2D rotation of a Superposition, and "rotates" the edge vector
  public State rotate(int num)
  {
    int[] newEdges = new int[this.edges.length];
    
    for (int i = 0; i < newEdges.length; i++)
    {
      newEdges[i] = this.edges[(i - num + newEdges.length) % newEdges.length];
    }
    
    return new State(this.getReference(), newEdges, num);
  }
  
  public void analyze(State[] tiles)
  {
    for (int i = 0; i < tiles.length; i++)
    {
      // UP
      if (tiles[i].getEdge(2) == this.getEdge(0))
      {
        this.up.add(i);
      }
      
      // RIGHT
      if (tiles[i].getEdge(3) == this.getEdge(1))
      {
        this.right.add(i);
      }
      
      // DOWN
      if (tiles[i].getEdge(0) == this.getEdge(2))
      {
        this.down.add(i);
      }
      
      // LEFT
      if (tiles[i].getEdge(1) == this.getEdge(3))
      {
        this.left.add(i);
      }
    }
  }
  
  public ArrayList<Integer> getDown()
  {
    return down;
  }
  
  public ArrayList<Integer> getLeft()
  {
    return left;
  }
  
  public ArrayList<Integer> getUp()
  {
    return up;
  }
  
  public ArrayList<Integer> getRight()
  {
    return right;
  }
}