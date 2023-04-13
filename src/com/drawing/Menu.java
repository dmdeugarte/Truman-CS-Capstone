package com.drawing;

import java.util.Arrays;

import javax.media.opengl.GL2;

public class Menu implements GShape
{
  private float[] vertex2f;
  
  // Options in the Menu
  private int selectedOption = 0;
  private StringTexture title;
  private StringTexture[] options;
  
  public Menu(final GL2 gl, float[]vertex2f, String title, String[] options)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    
    this.setOptions(gl, title, options);
  }
  
  public Menu(final GL2 gl, float[]vertex2f, String title)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    float adjust = (1f/(title.length() + 2));
    this.title = new StringTexture(gl, new float[] {adjust, (1f/3), adjust, adjust}, title);
    this.options = new StringTexture[] {};
  }
  
  public void shiftSelection(int shift)
  {
    int value = selectedOption + shift;
    if (value < options.length && value >= 0)
    {
      selectedOption = value;
    }
  }
  
  public void resetSelection()
  {
    selectedOption = 0;
  }
  
  public int getSelectedOption()
  {
    return selectedOption;
  }
  
  public void setOptions(final GL2 gl, String title, String[] options)
  {
    float vertAdjust = 1f/(3.5f+options.length);
    float scaling = (1f/(title.length() + 2));
    float[] titleVertex = {scaling, vertAdjust, scaling, scaling};
    this.title = new StringTexture(gl, titleVertex, title);
    
    int numOptions = options.length;
    this.options = new StringTexture[numOptions];
    for (int i = 0; i < numOptions; i++)
    {
      float horAdjust = 0.5f - scaling*options[i].length()/4;
      float[] optionVertex = {horAdjust, (3+i)*vertAdjust, scaling/2, scaling/2};
      this.options[i] = new StringTexture(gl, optionVertex, options[i]);
    }
  }
  
  public void render(final GL2 gl)
  {
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    
    title.render(gl);
    
    for (int i = 0; i < this.options.length; i++)
    {
      if (i == selectedOption)
      {
        gl.glColor3f(1.0f, .75f, 0f);
      }
      options[i].render(gl);
      gl.glColor3f(1f, 1f, 1f);
    }
      
    gl.glPopMatrix();
  }
}
