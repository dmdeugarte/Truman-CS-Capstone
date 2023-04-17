package com.drawing;
import java.util.ArrayList;

/**
 * The State class holds the data of a single state of a 2D tile
 * When analyzed with other States, it can generate who it's neighbors can be
 * 
 * This current version only works with squares, but can later be adjusted to 
 *   work with any tileable set of 2D shapes. 
 * The edges of a tile are counted clockwise around the tile, with 0 as the 
 *   top-most edge. 
 * 
 * @author Damian Deugarte
 * @version 2023-03-04
 */
public class State
{
  private int originalRef;
  private int[] edges;
  private ArrayList<Integer> up;
  private ArrayList<Integer> right;
  private ArrayList<Integer> down;
  private ArrayList<Integer> left;
  private int numRotations;
  
  /**
   * A State constructor, holding the data of a tile.
   * @param originalRef An integer to assist grouping of rotationally equivalent states.
   * @param edges An array of integers representing the sockets of a square tile-state. The northernmost edge of the state is 0, then incremented clockwise.
   * @param numRotations An integer representing the number of clockwise rotations from a rotationally equivalent tile-state.
   */
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
  
  /**
   * This method is used to safely retrieve the value of an edge
   * @param index The index of the edge around the tile
   * @return -1 if the index is out of bounds 
   * @return edges[index] if the index is within proper bounds
   */
  public int getEdge(int index)
  {
    if (index < edges.length && index >= 0)
      return edges[index];
    else
      return -1;
  }
  
  /**
   * This method returns the original reference integer of the state
   * @return the original reference integer of the state
   */
  public int getReference()
  {
    return originalRef;
  }
  
  /**
   * This method returns the integer number of clockwise rotations from the original reference 
   * @return the integer number of clockwise rotations from the original reference 
   */
  public int getNumRotations()
  {
    return numRotations;
  }
  
  /**
   * This method "rotates" a square tile-state clockwise, returning a new state
   * @param num the integer number of 90 degree clockwise rotations to makes
   * @return a State with the same reference integer, a rotated edge vector, and the saved value of the number of rotations
   */
  public State rotate(int num)
  {
    int[] newEdges = new int[this.edges.length];
    
    for (int i = 0; i < newEdges.length; i++)
    {
      newEdges[i] = this.edges[(i - num + newEdges.length) % newEdges.length];
    }
    
    return new State(this.getReference(), newEdges, num+this.numRotations);
  }
  
  /**
   * Populates internal arrays of index values of an array of States indicating which index can connect to the sockets above, below, right and left of the current state
   * If opposite socket values match, then the States can connect on their respective edges
   * @param tiles the array of States to analyze against
   */
  public void analyze(State[] tiles)
  {
    for (int i = 0; i < tiles.length; i++)
    {
      // UP
      if (tiles[i].getEdge(2) == reverseInt(this.getEdge(0)))
      {
        this.up.add(i);
      }
      
      // RIGHT
      if (tiles[i].getEdge(3) == reverseInt(this.getEdge(1)))
      {
        this.right.add(i);
      }
      
      // DOWN
      if (tiles[i].getEdge(0) == reverseInt(this.getEdge(2)))
      {
        this.down.add(i);
      }
      
      // LEFT
      if (tiles[i].getEdge(1) == reverseInt(this.getEdge(3)))
      {
        this.left.add(i);
      }
    }
  }
  
  /**
   * Reverses a 3 bit integer
   * @param num the integer to reverse
   * @return the reverse of the integer
   */
  private int reverseInt(int num)
  {
    int result = ((num&4)>>2)|(num&2)|((num&1)<<2);
    return result;
  }
  
  /**
   * Retrieves the Arraylist of this tile-State's array of indices of possible connections on it's down edge
   * @return the ArrayList of integers of indices of possible connections on the down edge
   */
  public ArrayList<Integer> getDown()
  {
    return down;
  }
  
  /**
   * Retrieves the Arraylist of this tile-State's array of indices of possible connections on it's left edge
   * @return the ArrayList of integers of indices of possible connections on the left edge
   */
  public ArrayList<Integer> getLeft()
  {
    return left;
  }
  
  /**
   * Retrieves the Arraylist of this tile-State's array of indices of possible connections on it's up edge
   * @return the ArrayList of integers of indices of possible connections on the up edge
   */
  public ArrayList<Integer> getUp()
  {
    return up;
  }
  
  /**
   * Retrieves the Arraylist of this tile-State's array of indices of possible connections on it's right edge
   * @return the ArrayList of integers of indices of possible connections on the right edge
   */
  public ArrayList<Integer> getRight()
  {
    return right;
  }
}