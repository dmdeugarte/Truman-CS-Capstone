package com.drawing;

// AWT based window
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.*;  
import javax.media.opengl.awt.GLCanvas; 

import com.jogamp.opengl.util.FPSAnimator;

/**
 * Creates the main window and adds the canvas; uses animator to call the
 * GLUT_Canvas object at the specified FPS
 */

public class DrawWindow extends Frame implements KeyListener, MouseListener 
{
  final static boolean DEBUG_OUTPUT = false;
  final static long serialVersionUID = 0l;

  final public String TITLE = "JOGL Interactive Drawing"; // window's title
  final public int FPS = 30; // animator's target frames per second

  GLCanvas canvas;

  // Create a animator that drives canvas' display() at the specified FPS.
  FPSAnimator animator;
  GKeyBoard keyBoard;

  DrawWindow() 
  {
    keyBoard = new GKeyBoard();

    GLProfile profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities capabilities = new GLCapabilities(profile);

    capabilities.setDoubleBuffered(true); // animation
    canvas = new GLUTCanvas(capabilities, keyBoard);
    animator = new FPSAnimator(canvas, FPS, true);

    canvas.addKeyListener(this);
    canvas.addMouseListener(this);
    // Create the top-level container

    // Now creating a frame using Frame class of AWT
    this.setTitle(TITLE);
    this.add(canvas);
    canvas.requestFocusInWindow();

    this.addWindowListener(new WindowAdapter() 
    {
      @Override
      public void windowClosing(WindowEvent e) 
      {
        // Use a dedicate thread to run the stop() to ensure that the
        // animator stops before program exits.
        new Thread() 
        {
          @Override
          public void run() 
          {
            if (getAnimator().isStarted())
              getAnimator().stop();

            System.exit(0);
          }
        }.start();
      }
    });
  }

  public void startGame() 
  {
    this.setSize(GLUTCanvas.WIDTH, GLUTCanvas.HEIGHT);
    this.pack();
    this.setVisible(true);
    getAnimator().start(); // start the animation loop
  }

  public GLCanvas getCanvas() 
  {
    return this.canvas;
  }

  public FPSAnimator getAnimator() 
  {
    return this.animator;
  }

  // keyboard events
  public void keyPressed(KeyEvent e) 
  {
    int key = e.getKeyCode();

    if (e != null)
    {
      keyBoard.setCharPressed(e.getKeyChar());
      keyBoard.setPressReleaseStatus(true);

      ((GLUTCanvas) canvas).processKeyBoardEvents(key);
    }
  }

  public void keyReleased(KeyEvent e) 
  {
    if (e != null)
    {
      keyBoard.setPressReleaseStatus(false);
      ((GLUTCanvas) canvas).processKeyBoardEventsStop();
    }
  }

  public void keyTyped(KeyEvent e) {
  }

  // Mouse events
  public void mouseClicked(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  // create a window and start the drawing loop
  public static void main(String[] args) 
  {
    DrawWindow game = new DrawWindow();
    
    // potential fix may be required for Mac OS
    
    game.startGame();

  }

}


