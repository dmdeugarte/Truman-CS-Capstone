package com.drawing;

import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL2GL3.GL_FILL;

import java.util.Arrays;

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class PlayerCharacter implements GShape
{
  private float[] vertex2f;
  private int[] edgeData;
  
  //Textures and IDs
  private final String TEXIDNAME[] = {"idle", "walk", "fire"};
  private final int NUMFRAMES[] = {1, 2, 2};
  private Texture animSpriteTex[][];
  private Texture defaultTexture;
  
  private int rotation;
  
  public PlayerCharacter(final GL2 gl, float vertex2f[], int[] edgeData)
  {
    // x, y of lower left 
    // w, h of width and height
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    this.edgeData = Arrays.copyOf(edgeData, edgeData.length);
    this.rotation = 0;
    
    // building textures
    /*this.animSpriteTex = new Texture[TEXIDNAME.length][];
    try 
    {
      for (int i = 0; i < TEXIDNAME.length; i++)
      {
        this.animSpriteTex[i] = new Texture[MAXFRAMES];
        for (int j = 0; j < MAXFRAMES; j++)
        {
          String name = "/Textures/PlayerCharacter/" + TEXIDNAME[i] + j + ".png";
          this.animSpriteTex[i][j] = GTextureUtil.loadTextureProjectDir(gl, name, "PNG");
          this.animSpriteTex[i][j].setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
          this.animSpriteTex[i][j].setTexParameterf(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        }
      }
    }
    catch (Exception e)
    {
      
    }*/
    String defaultTextureName = "/Textures/DefaultPC.png";
    this.defaultTexture = GTextureUtil.loadTextureProjectDir(gl, defaultTextureName, "PNG");
    this.defaultTexture.setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    this.defaultTexture.setTexParameterf(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
  }
  
  public void setEdgeData(int[] edgeData)
  {
    this.edgeData = Arrays.copyOf(edgeData, edgeData.length);
  }
  
  // if 0, success, otherwise fail
  public boolean moveUp(float speed)
  {
    boolean failedMove = true;
    // if before bottom, allow to keep moving down
    if (vertex2f[1] + vertex2f[3] - speed > 0 && (edgeData[0]&2) == 0)
    {
      vertex2f[1] -= speed;
      failedMove = false;
    }
    
    rotation = 0;
    
    return failedMove;
  }
  public boolean moveRight(float speed)
  {
    boolean failedMove = true;
    if (vertex2f[0] + vertex2f[2] + speed < 1 && (edgeData[1]&2) == 0)
    {
      vertex2f[0] += speed;
      failedMove = false;
    }
    
    rotation = 1;
    
    return failedMove;
  }
  public boolean moveDown(float speed)
  {
    boolean failedMove = true;
    // if before halfway up, allow to keep moving up
    if (vertex2f[1] + speed < 1 && (edgeData[2]&2) == 0)
    {
      vertex2f[1] += speed;
      failedMove = false;
    }
    
    rotation = 2;
    
    return failedMove;
  }
  public boolean moveLeft(float speed)
  {
    boolean failedMove = true;
    // if before left, allow to keep moving left
    if (vertex2f[0] - speed > 0 && (edgeData[3]&2) == 0)
    {
      vertex2f[0] -= speed;
      failedMove = false;
    }
    
    rotation = 3;
    
    return failedMove;
  }
  
  private void renderQuad(final GL2 gl, Texture texture) 
  {
    gl.glEnable(GL2.GL_TEXTURE_2D);
    gl.glEnable(GL2.GL_BLEND);
    // gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
    // gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
    texture.enable(gl);
    texture.bind(gl);
    gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

    if (texture != null) 
    {
      gl.glEnable(GL2.GL_TEXTURE_2D);
      texture.enable(gl);
      texture.bind(gl);
    }

    gl.glBegin(GL_TRIANGLE_STRIP);

    gl.glTexCoord2f(1, 0);
    gl.glVertex2f(1f, 0f); // v0 bottom right
    gl.glTexCoord2f(1, 1);
    gl.glVertex2f(1f, 1f); // v1 top right
    gl.glTexCoord2f(0, 0);
    gl.glVertex2f(0f, 0f); // v2 bottom left
    gl.glTexCoord2f(0, 1);
    gl.glVertex2f(0f, 1f); // v3 top left

    gl.glEnd();

    // drawOutline(gl);

    if (texture != null) {
      gl.glDisable(GL2.GL_TEXTURE_2D);
      texture.disable(gl);
    }

    gl.glDisable(GL2.GL_BLEND);
    gl.glDisable(GL2.GL_TEXTURE_2D);

  }
  
  public void render(final GL2 gl)
  {
    //calculateNextFrame();
    
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    
    gl.glRotatef(90.0f * rotation ,0.0f ,0.0f ,1.0f);
    if (rotation == 1 || rotation == 2)
      gl.glTranslatef(0f, -1f, 0f);
    if (rotation == 2 || rotation == 3)
      gl.glTranslatef(-1f,  0f,  0f);
    
    //renderQuad(gl, spriteTex[currentAnimationType][curFrame]);
    renderQuad(gl, defaultTexture);

    gl.glPopMatrix();
  }
}
