package com.drawing;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.media.opengl.GL2;

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
  private String[] pauseOptions;
  
  
  public Game(final GL2 gl, float[]vertex2f)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    
    int numRows = 9, numCols = 9; // Display Rules
    factor = numRows > numCols ? (1f/numRows) : (1f/numCols);
    float[] mapData = {0, 0, factor, factor};
    this.myMap = new Map(gl, mapData, true, numRows, numCols);
    
    float[] pcData = {pcRowPos*factor + factor/4, pcColPos*factor + factor/4, factor/2, factor/2};
    int[] pcEdgeData = {0, 0, 0, 0};
    this.myPC = new PlayerCharacter(gl, pcData, pcEdgeData);
    
    float[] menuData = {0, 0, 1, 1};
    menuOptions = new String[] {"New", "Load"};
    this.mainMenu = new Menu(gl, menuData, "Capstone", menuOptions);
    
    pauseOptions =  new String[] {"New", "Load", "Save", "Continue"};
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
    if (menuOpen && !gameLive)
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
        mainMenu.resetSelection();
      }
    }
    else if (menuOpen && gameLive)
    {
      if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
      {
        pauseMenu.shiftSelection(-1);
      }
      else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
      {
        pauseMenu.shiftSelection(1);
      }
      else if (key == KeyEvent.VK_ENTER)
      {
        this.processSelection(pauseMenu.getSelectedOption());
        pauseMenu.resetSelection();
      }
      else if (key == KeyEvent.VK_ESCAPE)
      {
        pauseMenu.resetSelection();
        menuOpen = false;
      }
    }
    else
    {
      myPC.setEdgeData(myMap.getFocusEdgeData());
      if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
      {
        myPC.setRotation(0);
        // if it can move (short circuit) and the map didn't shift, then move the PC render position
        if ((myPC.canMoveUp() && pcRowPos != 0) && !myMap.shiftRowFocus(-1))
        {
          myPC.moveUp(factor);
          pcRowPos -= 1;
        }
      }
      else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
      {
        myPC.setRotation(1);
        if (myPC.canMoveRight() && pcColPos != 8 && !myMap.shiftColFocus(1))
        {
          myPC.moveRight(factor);
          pcColPos += 1;
        }
      }
      else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
      {
        myPC.setRotation(2);
        if (myPC.canMoveDown() && pcRowPos != 8 && !myMap.shiftRowFocus(1))
        {
          myPC.moveDown(factor);
          pcRowPos += 1;
        }
      }
      else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
      {
        myPC.setRotation(3);
        if (myPC.canMoveLeft() && pcColPos != 0 && !myMap.shiftColFocus(-1))
        {
          myPC.moveLeft(factor);
          pcColPos -= 1;
          
        }
      }
      else if (key == KeyEvent.VK_SPACE)
      {
        // tell player to fire OR
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
    if (selection == 0) // New
    {
        gameLive = true;
        menuOpen = false;
        
        myMap.loadWaveFunction(true, 20, 20);
        myMap.runWaveFunction(20, 20);
        
        pcRowPos = 0;
        pcColPos = 0;
        
        myPC.setX(pcRowPos*factor + factor/4);
        myPC.setY(pcColPos*factor + factor/4);
        
        myPC.setEdgeData(myMap.getFocusEdgeData());
    }
    else if (selection == 1) // Load
    {
      if (myMap.loadFromFile("test.txt"))
      {
        gameLive = true;
        menuOpen = false;
      }
    }
    else if (selection == 2) // Save
    {
      myMap.saveToFile("test.txt");
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
    
    if (menuOpen && !gameLive)
    {
      mainMenu.render(gl);
    }
    else if (menuOpen && gameLive)
    {
      pauseMenu.render(gl);
    }
    else
    {
      myMap.render(gl);
      myPC.render(gl);
    }

    gl.glPopMatrix();
    
  }
}