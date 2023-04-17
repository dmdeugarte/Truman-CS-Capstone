// A Tile contains data about what is "in" it, be it hazards, entities, or objects
// A Tile should include height information, it's image, what object it contains, it's effects for standing in or moving out of, etc

package com.drawing;

import java.util.Arrays;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL2GL3.GL_FILL;

import com.jogamp.opengl.util.texture.Texture;

public class Tile implements GShape
{
  private float vertex2f[];
  private Texture texID;
  private int rotation;
  private int edgeDataRef;
  
  //Constructor for Tile where texture String is provided
  Tile(final GL2 gl, float vertex2f[], String textureName, int rotation, int edgeDataRef)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    this.rotation = rotation;
    this.edgeDataRef = edgeDataRef;
    // 1st and 2nd provide lower left corner point
    // 3rd provides color
    
    // texture name should be of form "/folder/extraNameString-" + clarifying int + ".png"
    this.texID = GTextureUtil.loadTextureProjectDir(gl, textureName, "PNG");
    this.texID.setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
  }
  
  Tile(final GL2 gl, float vertex2f[], Texture textureName, int rotation, int edgeDataRef)
  {
    this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
    this.rotation = rotation;
    this.edgeDataRef = edgeDataRef;
    // 1st and 2nd provide lower left corner point
    // 3rd provides color
    
    this.texID = textureName;
  }
  
  public int getEdgeDataRef()
  {
    return edgeDataRef;
  }
  
  public int getRotation()
  {
    return rotation;
  }
  
  public void setTexture(Texture textureName)
  {
    this.texID = textureName;
  }
  
  public void setRotation(int rotation)
  {
    this.rotation = rotation;
  }
  
  public void setEdgeDataRef(int edgeDataRef)
  {
    this.edgeDataRef = edgeDataRef;
  }
  
  private void renderQuad(final GL2 gl, Texture texture) 
  {
    gl.glEnable(GL2.GL_TEXTURE_2D);
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

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
    
    gl.glRotatef(90.0f * rotation ,0.0f ,0.0f ,1.0f);
    if (rotation == 1 || rotation == 2)
      gl.glTranslatef(0f, -1f, 0f);
    if (rotation == 2 || rotation == 3)
      gl.glTranslatef(-1f,  0f,  0f);
    
    gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
    gl.glScalef(vertex2f[2], vertex2f[3], 1.0f);
    renderQuad(gl, texID);

    gl.glPopMatrix();
  }
  
  
}
