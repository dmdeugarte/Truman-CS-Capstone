package com.drawing;

import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL2GL3.GL_FILL;

import java.util.Arrays;

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class AlphanumericTexture implements GShape
{
  private float[] vertex2f;
  
  private Texture texID;
  
  public AlphanumericTexture(final GL2 gl, float vertex2f[], char character) 
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    // 1st, 2nd two elements provides the lower left point
    // 3rd, 4th element is the width and height
    
    String textureName = "/Textures/Chars/" + Character.toLowerCase(character) + ".png";
      
    // storing the texture in the array
    this.texID = GTextureUtil.loadTextureProjectDir(gl, textureName, "PNG");
    this.texID.setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    this.texID.setTexParameterf(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
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
    gl.glPushMatrix();
    gl.glTranslatef(vertex2f[0], vertex2f[1], 0f);
    //gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    renderQuad(gl, this.texID);

    gl.glPopMatrix();
  }
  
}
