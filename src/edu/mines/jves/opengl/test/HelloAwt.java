/****************************************************************************
Copyright (c) 2004, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is 
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package edu.mines.jves.opengl.test;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import edu.mines.jves.opengl.*;
import static edu.mines.jves.opengl.Gl.*;

/**
 * Simple OpenGL program.
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.11.24
 */
public class HelloAwt {

  private static class MyCanvas extends GlAwtCanvas {
    public void glPaint() {
      if (_width!=getWidth() || _height!=getHeight()) {
        _width = getWidth();
        _height = getHeight();
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        glViewport(0,0,_width,_height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0,1.0,0.0,1.0,-1.0,1.0);
      }
      glClear(GL_COLOR_BUFFER_BIT);
      glBlendColor(1.0f,1.0f,1.0f,1.0f); // OpenGL 1.2 test
      glColor3f(1.0f,1.0f,1.0f);
      glBegin(GL_POLYGON);
        glVertex3f(0.25f,0.25f,0.00f);
        glVertex3f(0.75f,0.25f,0.00f);
        glVertex3f(0.75f,0.75f,0.00f);
        glVertex3f(0.25f,0.75f,0.00f);
      glEnd();
      glFlush();
    }
    private int _width = -1;
    private int _height = -1;
  }
  public static void main(String[] args) {
    System.setProperty("sun.awt.noerasebackground","true");
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(800,800));
    frame.getContentPane().add(new MyCanvas(),BorderLayout.CENTER);
    frame.setVisible(true);
  }
}