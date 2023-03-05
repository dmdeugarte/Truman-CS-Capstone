package com.drawing;
import java.util.ArrayList;

/**
 * The Superposition class holds a list of possible values it can be, being collapsed when it has only one value remaining.
 * 
 * @author Damian Deugarte
 * @version 2023-03-04
 */
public class Superposition
{
  private ArrayList<Integer> options;
  
  // the options are the indexes to the array of superpositions. 
  /**
   * A constructor for a Superposition
   * 
   * @param value Creates an array of [value] integers from 0 to value-1
   */
  public Superposition(int value) 
  {
    options = new ArrayList<Integer>();

    if (options.contains(value))
    {
      options.clear();
      options.add(value);
    }
    else
    {
      options.clear();
      for (int i=0; i<value; i++)
      {
        options.add(i);
      }
    }
  }
  
  /**
   * A copy constructor which safely copies the options of another Superposition
   * @param other the Superposition to copy
   */
  public Superposition(Superposition other)
  {
    this.options = new ArrayList<Integer>();
    
    for (int each : other.getOptions())
    {
      this.options.add(each);
    }
  }
  
  /**
   * Determines if the Superposition is "collapsed" by checking if it only has one value remaining.
   * @return a boolean of if the Superposition is collapsed
   */
  public boolean isCollapsed()
  {
    return (this.options.size() == 1);
  }
  
  /**
   * Returns an ArrayList of integers of the options the superposition has remaining
   * @return an ArrayList of integers
   */
  public ArrayList<Integer> getOptions()
  {
    return options;
  }
  
  /**
   * Removes an option the superposition could be
   * @param index the index of the option to remove
   */
  public void removeOption(int index)
  {
    options.remove(index);
  }
  
  /**
   * Safely sets the options of the Superposition to be of a new array
   * @param newOptions The ArrayList of new options 
   */
  public void setNewOptions (ArrayList<Integer> newOptions)
  {
    options.clear();
    for (int option : newOptions)
    {
      options.add(option);
    }
  }
}