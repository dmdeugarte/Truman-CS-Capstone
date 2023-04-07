// A Map is an array of Tiles, with an image representing the board
// This image is created through the many Tiles, each holding their own image of the board

package com.drawing;

import java.util.Arrays;
import javax.media.opengl.GL2;

public class Map implements GShape
{
  private float vertex2f[];
  private Tile[][] grid;
  private int[][] edgeData;

  // Constructor for a map of a grid of Tiles
  public Map(final GL2 gl, float vertex2f[], int numRows, int numCols)
  {
    boolean doMazeSet = true;
    
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the lower left corner point
    // 3rd, 4th element is the width and height
    
    grid = new Tile[numRows][numCols];
    
    int[] rotationData;
    int[] weights;
    
    if (!doMazeSet)
    {
      int[][] edgeDataPath = {{0, 0, 0, 0}, 
          {2, 0, 0, 0}, 
          {0, 2, 0, 2}, 
          {2, 2, 0, 0}, 
          {2, 2, 2, 0}, 
          {2, 2, 2, 2}};
      int[] rotationDataPath = {1, 4, 2, 4, 4, 1};
      int[] weightsPath = {100, 1, 3, 3, 3, 1};
      
      edgeData = edgeDataPath;
      rotationData = rotationDataPath;
      weights = weightsPath;
    }
    else
    {
      int[][] edgeDataMaze = {{7, 7, 7, 7}, 
          {5, 7, 7, 7}, 
          {5, 7, 5, 7},
          {5, 5, 7, 7},
          {4, 1, 7, 7},
          {5, 5, 7, 5},
          {0, 1, 7, 4},
          {5, 5, 5, 5},
          {0, 0, 0, 0},
          {5, 4, 0, 1},
          {4, 0, 0, 1},
          {4, 1, 5, 7},
          {5, 4, 1, 7},
          {5, 5, 4, 1},
          {4, 1, 4, 1}};
      int[] rotationDataMaze = {1, 4, 2, 4, 4, 4, 4, 1, 1, 4, 4, 4, 4, 4, 2};
      int[] weightsMaze =      {20, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0};
      
      edgeData = edgeDataMaze;
      rotationData = rotationDataMaze;
      weights = weightsMaze;
    }
    WaveFunction wf = new WaveFunction(numCols, numRows, edgeData, rotationData, weights);
    wf.collapse();
    
    int[][] mapping = wf.getMapping();
    
    for (int i = 0; i < numRows; i++)
    {
      for (int j = 0; j < numCols; j++)
      {
        float tileData[] = {j,i, 1, 1};
        
        int gridIndex = j+i*numCols;
        int value = wf.getSuperpositionValue(gridIndex);
        
        String textureString = "/Textures/test.png"; //default png
        if (!doMazeSet)
        {
          textureString = "/Textures/PathSet/" + mapping[value][0] + ".png";
        }
        else
        {
          textureString = "/Textures/MazeSet/" + mapping[value][0] + ".png";
        }
        grid[i][j] = new Tile(gl, tileData, textureString, mapping[value][1], mapping[value][0]);
      }
    }
  }
  
  public int[] getEdgeData(int row, int col)
  {
    int edgeRef = grid[row][col].getEdgeDataRef();
    int rotation = grid[row][col].getRotation();
    
    int[] newEdges = new int[this.edgeData[edgeRef].length];
    
    for (int i = 0; i < newEdges.length; i++)
    {
      newEdges[i] = this.edgeData[edgeRef][(i - rotation + newEdges.length) % newEdges.length];
    }
    
    return newEdges;
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
