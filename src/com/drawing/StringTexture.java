package com.drawing;

import java.util.Arrays;

import javax.media.opengl.GL2;

public class StringTexture implements GShape
{
  private float[] vertex2f;
  private String word;
  
  StringTexture(final GL2 gl, float vertex2f[], String word)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the bottom left point
    // 3rd, 4th element is the character height and width
     this.word = word.toLowerCase();
  }
  
  public String getWord()
  {
    return word;
  }
  
  public void setWord(String word)
  {
    this.word = word;
  }
  
  public void render(final GL2 gl) 
  {
    // move stuff over to new 
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    
    for (int i = 0; i < word.length(); i++)
    {
      int ascii = word.charAt(i);
      
      // if 0-9 or a-z
      if((ascii>=48 && ascii<=57) ||(ascii>=97 && ascii<=122))
      {
        float chInfo[] = {i, 0, 1f, 1f};
        AlphanumericTexture ch = new AlphanumericTexture(gl, chInfo, word.charAt(i));
        ch.render(gl);
      }
    }

    gl.glPopMatrix();
  }
}
