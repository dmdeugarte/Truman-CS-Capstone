// Some collapse stuff without rotation, following 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class Cell
{
  private ArrayList<Integer> options;
  private boolean collapsed;
  
  public Cell() 
  {
    collapsed = false;
    options = new ArrayList<Integer>();
    options.add(0);
    options.add(1);
    options.add(2);
    options.add(3);
    options.add(4);
  }
  
  public boolean getCollapsed()
  {
    return collapsed;
  }
  
  public ArrayList<Integer> getOptions()
  {
    return options;
  }
  
  public void removeOption(int index)
  {
    options.remove(index);
  }
  
  public void setCollapsed()
  {
    collapsed = true;
  }
  
  public void setNewOptions (int[] newOptions)
  {
    options.clear();
    for (int option : newOptions)
    {
      options.add(option);
    }
  }
}

class SortByOptionsLength implements Comparator<Cell>
{
  // Sort in ascending order
  public int compare(Cell a, Cell b)
  {
    return a.getOptions().size() - b.getOptions().size();
  }
}

public class CollapseTest
{
  
  final static int DIM = 2;
  
  public static void main(String[] args)
  {
    Cell[] array = new Cell[DIM*DIM]; // x, y
    
    for (int i = 0; i < array.length; i++)
    {
        array[i] = new Cell();
    }
    
    // Manually collapsing a cell
    array[0].setNewOptions(new int[] {0, 1});
    array[2].setNewOptions(new int[] {0, 1});
    
    // Copy the array
    Cell[] copy = array.clone();
    
    // Print the copy
    for (int i = 0; i < copy.length; i++)
    {
      System.out.print(copy[i].getOptions().size() + " ");
    }
    System.out.println();
    
    // Sort the Copy
    Arrays.sort(copy, new SortByOptionsLength());
    
    // Trim the sorted copy for array of cells with least entropy
    int length = copy[0].getOptions().size();
    int index = 0;
    while (index < copy.length && copy[index].getOptions().size() <= length)
    {
      index++;
    }
    Cell[] slicedCopy = Arrays.copyOfRange(copy, 0, index);
    
    // Print the sliced copy
    for (int i = 0; i < slicedCopy.length; i++)
    {
      System.out.print(slicedCopy[i].getOptions().size() + " ");
    }
    System.out.println();
    
    // Pick one of the cells in the sliced copy(randomly), collapse it, pick one of its options, and setNewOptions for that new sole option
    ERROR CONTINUE HERE
    
    // Print the Original array and the newly sorted copy
    //printTestArray(array, DIM);   // I need to push this to a 2D array last. 
    for (int i = 0; i < array.length; i++)
    {
      if (array[i].getCollapsed())
        System.out.print(array[i].getOptions().get(0) + " ");
      else
        System.out.print("N ");
    }
    System.out.println();
    
    for (int i = 0; i < copy.length; i++)
    {
      System.out.print(copy[i].getOptions().size() + " ");
    }
    System.out.println();
  }
  
  public static void printTestArray(Cell[][] array)
  {
    for (int i = 0; i < DIM; i++)
    {
      for (int j = 0; j < DIM; j++)
      {
        if (array[i][j].getCollapsed())
          System.out.print(array[i][j].getOptions().get(0) + " ");
        else
          System.out.print("N ");
      }
      System.out.println();
    }
  }
}
