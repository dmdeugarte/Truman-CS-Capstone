// Some wave function collapse algorithm based on neighbors are similar.
// That is, tile 0 can be next to 0 or 1, 1 next to 0, 1, or 2, 2 next to 1, 2, or 3 and so on. 
package com.drawing;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;

/**
 * A floating class used to sort Superpositions by their entropy
 * @author Damian Deugarte
 * @version 2023-03-04
 */
class SortByOptionsLength implements Comparator<Superposition>
{
  public int compare(Superposition a, Superposition b)
  {
    return a.getOptions().size() - b.getOptions().size();
  }
}

/**
 * The WaveFunction class requires the Superposition class and the State class to
 * generate a grid of integers representing square tiles based on the received edge data 
 * and each set of edge's rotational symmetry. 
 * 
 * In the future, the rotational symmetry should be detected rather than manually inputed.
 * 
 * @author Damian Deugarte
 * @version 2023-03-04
 */
public class WaveFunction
{
  int width;
  int height;
  Superposition[] grid;
  State[] states;
  int[][] edgeData;
  
  /**
   * The WaveFunction constructor
   * @param width the width of the grid to generate
   * @param height the height of the grid to generate
   * @param edgeData a matrix of integer edge data of the tiles to look at
   * @param rotationSymmetry an array of values representing the rotational symmetry of the edge data
   */
  public WaveFunction(int width, int height, int[][] edgeData, int[] rotationSymmetry)
  {
    this.width = width;
    this.height = height;
    grid = new Superposition[this.width*this.height];
    
    this.edgeData = new int[edgeData.length][edgeData[0].length];
    for (int i = 0; i < edgeData.length; i++)
    {
      for (int j = 0; j < edgeData[i].length; j++)
      {
        this.edgeData[i][j] = edgeData[i][j];
      }
    }
    
    int count = 0;
    for (int i = 0; i < rotationSymmetry.length; i++)
    {
      count += rotationSymmetry[i];   // adds the total number of possible states, via their rotations.
    }
    states = new State[count];
    
    // populate states. Each state refers back to a possible tile, and edge data
    int index = 0;      // an adjusted index to correctly position each new State in states
    int savedIndex = 0; // a value to ensure the correct State is rotated
    for (int i = 0; i < rotationSymmetry.length; i++)
    {
      for (int j = 0; j < rotationSymmetry[i]; j++)
      {
        if (j == 0)
        {
          states[index] = new State(i, this.edgeData[i], j);
          savedIndex = index;
        }
        else
        {
          states[index] = states[savedIndex].rotate(j);
        }
        index++;
      }
    }
    
    // generate the adjacency rules within each State in states
    for (State state : states)
    {
      state.analyze(states);
    }
    
    // populate the grid using the total number of States in states
    for (int i = 0; i < grid.length; i++)
    {
        grid[i] = new Superposition(states.length);
    }
  }
  
  /**
   * Collapses every Superposition in the grid
   */
  public void collapse()
  {
    for (int i = 0; i < grid.length; i++)
    {
      collapseOnce();
    }
  }
  
  /**
   * Finds and chooses a single Superposition with the least entropy to collapse
   */
  public void collapseOnce()
  {
    // Copy the array, to be safe
    Superposition[] copy = grid.clone();
    
    // Sort the Copy
    Arrays.sort(copy, new SortByOptionsLength());
    
    // remove Superpositions that are already collapsed
    int startIndex = 0;
    while (startIndex < copy.length - 1 && copy[startIndex].isCollapsed())
    {
      startIndex++;
    }
    
    // Trim the sorted copy for array of Superpositions with least entropy
    int length = copy[startIndex].getOptions().size();
    int index = startIndex;
    while (index < copy.length && copy[index].getOptions().size() <= length)
    {
      index++;
    }
    Superposition[] slicedCopy = Arrays.copyOfRange(copy, startIndex, index);
    
    // There *can* be errors, here, no-op
    if (slicedCopy.length == 0)
    {
      System.out.println("Grid is fully collapsed.");
      return;
    }
    
    // Pick one of the cells in the sliced copy(randomly), collapse it, pick one of its options, and setNewOptions for that new sole option
    int randomIndex = (int)(Math.random()*slicedCopy.length);    // pick a cell in the copy
    int randomOptionsIndex = (int)(Math.random()*slicedCopy[randomIndex].getOptions().size());  // pick the index of an option of the cell
    ArrayList<Integer> temp = new ArrayList<Integer>();
    temp.add(slicedCopy[randomIndex].getOptions().get(randomOptionsIndex));
    slicedCopy[randomIndex].setNewOptions(temp);
    
    // Now, create a newArray with neighbor's possible values being changed.
    Superposition[] newArray = new Superposition[width*height];
    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        index = j+i*width;
        
        newArray[index] = grid[index];
        
        if (!grid[index].isCollapsed())
        {
          ArrayList<Integer> options = new ArrayList<Integer>();
          for (int o = 0; o<states.length; o++)
            options.add(o);
          
          // Check above
          if (i > 0)
          {
            Superposition up = grid[j + (i-1) * width];
            ArrayList<Integer> validOptions = new ArrayList<Integer>();
            for (int option : up.getOptions())
            {
              ArrayList<Integer> valid = states[option].getDown(); // Pass by Reference
              for (int validOption : valid)
                validOptions.add(validOption);
            }
            checkValid(options, validOptions); // options is passed by reference
          }
          
          // Check right
          if (j < width - 1)
          {
            Superposition right = grid[j + 1 + i * width];
            ArrayList<Integer> validOptions = new ArrayList<Integer>();
            for (int option : right.getOptions())
            {
              ArrayList<Integer> valid = states[option].getLeft(); // Pass by Reference
              for (int validOption : valid)
                validOptions.add(validOption);
            }
            checkValid(options, validOptions); // options is passed by reference
          }
          
          // Check below
          if (i < height - 1)
          {
            Superposition down = grid[j + (i+1) * width];
            ArrayList<Integer> validOptions = new ArrayList<Integer>();
            for (int option : down.getOptions())
            {
              ArrayList<Integer> valid = states[option].getUp(); // Pass by Reference
              for (int validOption : valid)
                validOptions.add(validOption);
            }
            checkValid(options, validOptions); // options is passed by reference
          }
          
          // Check Left
          if (j > 0)
          {
            Superposition left = grid[j - 1 + i * width];
            ArrayList<Integer> validOptions = new ArrayList<Integer>();
            for (int option : left.getOptions())
            {
              ArrayList<Integer> valid = states[option].getRight(); // Pass by Reference
              for (int validOption : valid)
                validOptions.add(validOption);
            }
            checkValid(options, validOptions); // options is passed by reference
          }
          
          newArray[index].setNewOptions(options);
        }
      }
    }
    
    // Push the Superpositions of the newArray into the old array
    for (int i = 0; i<grid.length; i++)
    {
      grid[i] = new Superposition(newArray[i]);
    }
    
    /*
    // print the grid's option sizes 
    // a Debugging section
    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        System.out.print(grid[j+i*width].getOptions().size() + " ");
      }
      System.out.println();
    }*/
  }
  
  /**
   * A method to remove invalid values in an array by-reference
   * @param array the ArrayList of integers to check
   * @param valid the ArrayList of valid integers the array can hold
   */
  private void checkValid(ArrayList<Integer> array, ArrayList<Integer> valid)
  {
    for (int i = array.size() - 1; i >= 0; i--)  // loop through the array's elements
    {
      if (!valid.contains(array.get(i)))    // if the element is not in valid, 
      {
        array.remove(i);                    // remove it from the array
      }
    }
  }
  
  /**
   * A method to return a mapping of State indices to their original reference value and the number of rotations
   * @return an integer array of 2-tuples, the first value being the reference value, the second the number of rotations
   */
  public int[][] getMapping()
  {
    int[][] mapping = new int[states.length][2];
    
    for (int i = 0; i < states.length; i++)
    {
      mapping[i][0] = states[i].getReference();       // The original reference
      mapping[i][1] = states[i].getNumRotations();    // The number of rotations
    }
    
    return mapping;
  }
  
  /**
   * Prints the grid, using its integer representation, to the System console.
   * If an N is printed, the Superposition is not yet collapsed
   * If an integer is printed, the Superposition is collapsed to that index of an internal array of States
   */
  public void printGrid()
  {
    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        if (grid[j+i*width].isCollapsed())
          System.out.print(grid[j+i*width].getOptions().get(0) + " ");
        else
          System.out.print("N ");
      }
      System.out.println();
    }
  }
  
  /**
   * Returns an integer representation of the grid
   * @return -1 for an uncollapsed superposition, an integer if the superposition is collapsed
   */
  public int[] getGrid()
  {
    int[] output = new int[grid.length];
    
    for (int i = 0; i < output.length; i++)
    {
      getSuperpositionValue(i);
    }
    
    return output;
  }
  
  /**
   * Returns an integer representation of a specific Superposition in the grid
   * @param index the index to look at
   * @return -1 for an uncollapsed superposition, an integer if the superposition is collapsed
   */
  public int getSuperpositionValue(int index)
  {
    if (grid[index].isCollapsed())
    {
      return grid[index].getOptions().get(0);
    }
    else
    {
      return -1;
    }
  }
}
