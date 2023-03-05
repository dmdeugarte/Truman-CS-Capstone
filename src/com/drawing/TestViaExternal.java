public class TestViaExternal
{
  public static void main(String[] args)
  {
    // Rotationally complete set of path tiles. 
    int[][] edgeData = {{0, 0, 0, 0}, 
                        {1, 0, 0, 0}, 
                        {0, 1, 0, 1}, 
                        {1, 1, 0, 0}, 
                        {1, 1, 1, 0}, 
                        {1, 1, 1, 1}};
    int[] numRotationsUntilAgain = {1, 4, 2, 4, 4, 1};
    WaveFunction wf = new WaveFunction(2, 2, edgeData, numRotationsUntilAgain);
    
    int[][] mapping = wf.getMapping();
    
    for (int i = 0; i < mapping.length; i++)
    {
      System.out.println("(" + i + ", " + mapping[i][0] + ", " + mapping[i][1] + ")");
    }
    
    System.out.println();
    
    wf.printGrid();
    
    System.out.println();
    
    wf.collapse();
    wf.printGrid();
  }
}
