// A Map is an array of Tiles, with an image representing the board
// This image is created through the many Tiles, each holding their own image of the board

package com.drawing;

import java.util.Arrays;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;  
import java.util.Scanner;

public class Map implements GShape
{
  private int displayRows, displayCols;
  private Tile[][] grid;
  private int focusRow, focusCol;
  private int rowShift, colShift;
  
  private float vertex2f[];
  private Texture[] tileTextures;
  
  private WaveFunction wf;
  private int[][] edgeData;
  private int[][] mapping;
  private int[][] wfValues;

  // default "null" constructor
  public Map(final GL2 gl, float vertex2f[], boolean doMazeSet, int displayRows, int displayCols)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the lower left corner point
    // 3rd, 4th element is the width and height
    
    this.loadTextures(gl, true);
    
    this.displayRows = displayRows;
    this.displayCols = displayCols;
    
    grid = new Tile[displayRows][displayCols];
    
    this.loadTextures(gl, doMazeSet);
    this.loadNullGrid(gl);
  }
  
  // Constructor for a map of a grid of Tiles
  // sizeData is of {displayRows, displayCols, gridRows, gridCols}
  public Map(final GL2 gl, float vertex2f[], boolean doMazeSet, int[] sizeData)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the lower left corner point
    // 3rd, 4th element is the width and height
    
    this.displayRows = sizeData[0];
    this.displayCols = sizeData[1];
    
    grid = new Tile[displayRows][displayCols];
    
    this.loadTextures(gl, doMazeSet);
    this.loadWaveFunction(doMazeSet, sizeData[2], sizeData[3]);
    this.loadNullGrid(gl);
    
    this.runWaveFunction(sizeData[2], sizeData[3]);
  }
  
  private void loadTextures(final GL2 gl, boolean doMazeSet)
  {
    String textureNameStub;
    int length;
    if(!doMazeSet)
    {
      textureNameStub = "/Textures/PathSet/";
      length = 6;
    }
    else
    {
      textureNameStub = "/Textures/MazeSet/";
      length = 15;
    }
    
    tileTextures = new Texture[length];
    for (int i = 0; i < length; i++)
    {
      String textureName = textureNameStub + (i + ".png");
          
      this.tileTextures[i] = GTextureUtil.loadTextureProjectDir(gl, textureName, "PNG");
      this.tileTextures[i].setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    }
  }
  
  private void loadNullGrid(final GL2 gl)
  {
    // Load nulls into the grid
    for (int i = 0; i < displayRows; i++)
    {
      for (int j = 0; j < displayCols; j++)
      {
        float tileData[] = {j,i, 1, 1};
        
        grid[i][j] = new Tile(gl, tileData, tileTextures[0], 0, 0);
      }
    }
  }
  
  private void fixTextures()
  {
    // To ensure that I don't check over the boundary of either matrix, wfValues or the grid of Tiles
    int row = displayRows, col = displayCols;
    int numRows = wfValues.length, numCols = wfValues[0].length;
    
    if (numRows < displayRows)
    {
      row = numRows;
    }
    
    if (numCols < displayCols)
    {
      col = numCols;
    }
    
    for (int i = 0; i < row; i++)
    {
      for (int j = 0; j < col; j++)
      {
        int r = rowShift + i;
        int c = colShift + j;
        
        int value = wfValues[r][c]; // written out for readability
        grid[i][j].setTexture(tileTextures[mapping[value][0]]);
        grid[i][j].setEdgeDataRef(mapping[value][0]);
        grid[i][j].setRotation(mapping[value][1]);
      }
    }
  }
  
  public boolean shiftRowFocus(int n)
  {
    int test = focusRow + n;
    int temp = rowShift;
    int maxRowShift = wfValues.length - displayRows;
    int middlingRow = (displayRows + 1)/ 2;
    
    if (test < 0)
      focusRow = 0;
    else if (test >= wfValues.length)
      focusRow = wfValues.length - 1;
    else
      focusRow = test;
    
    if (focusRow < middlingRow)
      rowShift = 0;
    else if (focusRow >= wfValues.length - middlingRow)
      rowShift = maxRowShift;
    else
      rowShift = focusRow + 1 - middlingRow;
    
    this.fixTextures();
    
    if (rowShift != temp)
      return true;
    else 
      return false;
  }
  
  public boolean shiftColFocus(int n)
  {
    int test = focusCol + n;
    int temp = colShift;
    int maxColShift = wfValues[0].length - displayCols;
    int middlingCol = (displayCols + 1)/ 2;
    
    if (test < 0)
      focusCol = 0;
    else if (test >= wfValues[0].length)
      focusCol = wfValues[0].length - 1;
    else
      focusCol = test;
    
    if (focusCol < middlingCol)
    {
      colShift = 0;
    }
    else if (focusCol >= wfValues[0].length - middlingCol)
    {
      colShift = maxColShift;
    }
    else
    {
      colShift = focusCol + 1 - middlingCol;
    }
    
    this.fixTextures();
    
    if (colShift != temp)
      return true;
    else
      return false;
    
  }
  
  public void loadWaveFunction(boolean doMazeSet, int numRows, int numCols)
  {
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
      int[] weightsMaze =      {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0};
      
      edgeData = edgeDataMaze;
      rotationData = rotationDataMaze;
      weights = weightsMaze;
    }
    this.wf = new WaveFunction(numCols, numRows, edgeData, rotationData, weights);
  }
  
  public void runWaveFunction(int numRows, int numCols)
  {
    this.wf.collapse();
    
    this.mapping = this.wf.getMapping();
    
    wfValues = new int[numRows][numCols];
    
    for (int i = 0; i < numRows; i++)
    {
      for (int j = 0; j < numCols; j++)
      {
        int gridIndex = j+i*numCols;
        wfValues[i][j] = this.wf.getSuperpositionValue(gridIndex);
      }
    }
    
    focusRow = 0;
    focusCol = 0;
    rowShift = 0;
    colShift = 0;
    this.fixTextures();
  }
  
  public int[] getEdgeData(int row, int col)
  {
    int value = wfValues[row][col];
    int edgeRef = mapping[value][0];
    int rotation = mapping[value][1];
    
    int[] newEdges = new int[this.edgeData[edgeRef].length];
    
    for (int i = 0; i < newEdges.length; i++)
    {
      newEdges[i] = this.edgeData[edgeRef][(i - rotation + newEdges.length) % newEdges.length];
    }
    
    return newEdges;
  }
  
  public int[] getFocusEdgeData()
  {
    return getEdgeData(focusRow, focusCol);
  }
  
  public boolean saveToFile(String filename)
  {
    try 
    {
      FileWriter saveFile = new FileWriter(filename);
      saveFile.write(mapping.length + "," + wfValues.length + "," + wfValues[0].length + "\n");
      
      for (int i = 0; i < mapping.length; i++)
      {
        saveFile.write(mapping[i][0] + "," + mapping[i][1] + "\n");
      }
      
      for (int r = 0; r < wfValues.length; r++)
      {
        for (int c = 0; c < wfValues[r].length; c++)
        {
          saveFile.write("" + wfValues[r][c]);
          //System.out.print(wfValues[r][c]);
          
          if (!(c == (wfValues[r].length - 1)))
          {
            saveFile.write(",");
            //System.out.print(",");
          }
        }
        saveFile.write("\n");
        //System.out.println();
      }
      
      saveFile.close();
    } 
    catch (IOException e) 
    {
      System.out.println("An error occurred.");
      e.printStackTrace();
      return false;
    }
    catch (Exception e)
    {
      System.out.println("An error occurred.");
      e.printStackTrace();
      return false;
    }
    
    return true;
  }
  
  public boolean loadFromFile(String filename)
  {
    try 
    {
      File saveFile = new File(filename);
      Scanner scan = new Scanner(saveFile);
      // get mapping length, numRows, numCols
      int n=0, r=0, c=0;
      if (scan.hasNextLine())
      {
        String firstLine = scan.nextLine();
        String[] data = firstLine.split(",");
        
        n = Integer.parseInt(data[0]);
        r = Integer.parseInt(data[1]);
        c = Integer.parseInt(data[2]);
      }
      
      int lineHead = 0;
      mapping = new int[n][2];
      while (scan.hasNextLine() && lineHead < n) 
      {
        String mappingLine = scan.nextLine();
        String[] data = mappingLine.split(",");
        
        mapping[lineHead][0] = Integer.parseInt(data[0]);
        mapping[lineHead][1] = Integer.parseInt(data[1]);
        
        lineHead++;
      }
      
      wfValues = new int[r][c];
      lineHead = 0;
      while (scan.hasNextLine()) 
      {
        String valuesLine = scan.nextLine();
        String[] data = valuesLine.split(",");
        
        for (int i = 0; i < c; i++)
        {
          wfValues[lineHead][i] = Integer.parseInt(data[i]);
        }
        lineHead++;
      }
      scan.close();
      
      this.loadWaveFunction(true, r, c);
      rowShift = 0;
      colShift = 0;
      focusRow = 0;
      focusCol = 0;
      displayRows = 9;
      displayCols = 9;
      
      this.fixTextures();
    } 
    catch (FileNotFoundException e) 
    {
      System.out.println("An error occurred.");
      e.printStackTrace();
      return false;
    }
    catch (Exception e)
    {
      System.out.println("An error occurred.");
      e.printStackTrace();
      return false;
    }
    
    return true;
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
