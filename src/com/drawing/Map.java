// A Map is an array of Tiles, with an image representing the board
// This image is created through the many Tiles, each holding their own image of the board

package com.drawing;

import java.util.Arrays;
import javax.media.opengl.GL2;

public class Map implements GShape
{
  private float vertex2f[];
  private Tile[][] grid;

  // Constructor for a map of a grid of Tiles
  public Map(final GL2 gl, float vertex2f[], int numRows, int numCols)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the lower left corner point
    // 3rd, 4th element is the width and height
    
    grid = new Tile[numRows][numCols];
    
    int[][] edgeData = {{0, 0, 0, 0}, 
        {1, 0, 0, 0}, 
        {0, 1, 0, 1}, 
        {1, 1, 0, 0}, 
        {1, 1, 1, 0}, 
        {1, 1, 1, 1}};
    int[] numRotationsUntilAgain = {1, 4, 2, 4, 4, 1};
    WaveFunction wf = new WaveFunction(numCols, numRows, edgeData, numRotationsUntilAgain);
    wf.collapse();
    
    int[][] mapping = wf.getMapping();
    
    for (int i = 0; i < numRows; i++)
    {
      for (int j = 0; j < numCols; j++)
      {
        float tileData[] = {j,i, 1, 1};
        
        int gridIndex = j+i*numCols;
        int value = wf.getSuperpositionValue(gridIndex);
        
        String textureString = "/Textures/PathSet/" + mapping[value][0] + ".png";
        grid[i][j] = new Tile(gl, tileData, textureString, mapping[value][1]);
      }
    }
  }
  
  public void render(final GL2 gl) 
  {
    // move stuff over to new 
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    
    for (int i = 0; i < grid.length; i++)
    {
      for (int j = 0; j < grid[i].length; j++)
      {
        grid[i][j].render(gl);
      }
    }

    gl.glPopMatrix();
  }
}
