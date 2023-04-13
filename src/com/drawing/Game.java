package com.drawing;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class Game implements GShape
{
  // general items
  private float[] vertex2f;
  private Map myMap;
  private PlayerCharacter myPC;
  private Menu mainMenu;
  private Menu pauseMenu;
  private Menu loadingMenu;
  
  // which screen is up, what has happened
  private boolean menuOpen;
  private boolean gameLive;
  
  // Player position and scaling
  private float factor;
  private int pcColPos = 0, pcRowPos = 0;
  
  // Menu Items
  private String[] menuOptions;
  
  
  public Game(final GL2 gl, float[]vertex2f)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    
    int numRows = 10, numCols = 10;
    factor = numRows > numCols ? (1f/numRows) : (1f/numCols);
    float[] mapData = {0, 0, factor, factor};
    this.myMap = new Map(gl, mapData, numRows, numCols);
    
    float[] pcData = {pcRowPos*factor + factor/4, pcColPos*factor + factor/4, factor/2, factor/2};
    int[] pcEdgeData = {0, 0, 0, 0};
    this.myPC = new PlayerCharacter(gl, pcData, pcEdgeData);
    
    float[] menuData = {0, 0, 1, 1};
    menuOptions = new String[] {"New", "Load"};
    this.mainMenu = new Menu(gl, menuData, "Capstone", menuOptions);
    
    String[] pauseOptions =  new String[] {"New", "Load", "Save", "Continue"};
    this.pauseMenu = new Menu(gl, menuData, "Capstone", pauseOptions);
    
    this.loadingMenu = new Menu(gl, menuData, "Loading");
    
    this.menuOpen = true;
    this.gameLive = false;
  }
  
  public void resetKeyBoardEvent() 
  {
    // No Actions
  }
  
  public void processKeyBoardEvent(int key) 
  {
    if (menuOpen)
    {
      if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
      {
        mainMenu.shiftSelection(-1);
      }
      else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
      {
        mainMenu.shiftSelection(1);
      }
      else if (key == KeyEvent.VK_ENTER)
      {
        this.processSelection(mainMenu.getSelectedOption());
      }
      else if (gameLive && (key == KeyEvent.VK_ESCAPE))
      {
        menuOpen = false;
      }
    }
    else
    {
      if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
      {
        // 0 means success
        if (!myPC.moveUp(factor))
        {
          pcRowPos -= 1;
          myPC.setEdgeData(myMap.getEdgeData(pcRowPos, pcColPos));
        }
      }
      else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
      {
        if (!myPC.moveRight(factor))
        {
          pcColPos += 1;
          myPC.setEdgeData(myMap.getEdgeData(pcRowPos, pcColPos));
        }
      }
      else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
      {
        if (!myPC.moveDown(factor))
        {
          pcRowPos += 1;
          myPC.setEdgeData(myMap.getEdgeData(pcRowPos, pcColPos));
        }
      }
      else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
      {
        if (!myPC.moveLeft(factor))
        {
          pcColPos -= 1;
          myPC.setEdgeData(myMap.getEdgeData(pcRowPos, pcColPos));
        }
      }
      else if (key == KeyEvent.VK_SPACE)
      {
        // tell player to fire
        // tell map to fix that tile
        System.out.println("Player attempted to fire");
      }
      else if (key == KeyEvent.VK_ESCAPE)
      {
        // open the menu
        menuOpen = true;
      }
    }
  }
  
  private void processSelection(int selection)
  {
    System.out.println(selection + " Selected: " + menuOptions[selection]);
    
    if (selection == 0) // New
    {
      if (!gameLive)
      {
        gameLive = true;
        menuOpen = false;
      }
    }
    else if (selection == 1) // Load
    {
      System.out.println("Loading Not Implemented Yet");
    }
    else if (selection == 2) // Save
    {
      System.out.println("Saving Not Implemented Yet");
    }
    else if (selection == 3) // Continue
    {
      menuOpen = false;
    }
  }
  
  public void printArrayToConsole(int[] arr)
  {
    for (int i = 0; i < arr.length; i++)
    {
      System.out.print(arr[i] + ", ");
    }
    System.out.println();
  }
  
  public void render(final GL2 gl)
  {
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    
    if (menuOpen)
    {
      mainMenu.render(gl);
    }
    else
    {
      myMap.render(gl);
      myPC.render(gl);
    }

    gl.glPopMatrix();
    
  }
}