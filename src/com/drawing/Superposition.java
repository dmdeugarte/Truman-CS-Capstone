import java.util.ArrayList;

public class Superposition
{
  private ArrayList<Integer> options;
  
  // the options are the indexes to the array of superpositions. 
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
  
  public Superposition(Superposition other)
  {
    this.options = new ArrayList<Integer>();
    
    for (int each : other.getOptions())
    {
      this.options.add(each);
    }
  }
  
  public boolean isCollapsed()
  {
    return (this.options.size() == 1);
  }
  
  public ArrayList<Integer> getOptions()
  {
    return options;
  }
  
  public void removeOption(int index)
  {
    options.remove(index);
  }
  
  public void setNewOptions (ArrayList<Integer> newOptions)
  {
    options.clear();
    for (int option : newOptions)
    {
      options.add(option);
    }
  }
}