package com.github.kusaanko.atomcamclient.opengl;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;
import org.lwjgl.system.MemoryUtil;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL32C.*;

public class ImageRenderer extends AWTGLCanvas {
    private int textureID;
    private Shader shader;
    private int arrayBuffer;
    private int vertexBuffer;
    private int texCoordBuffer;
    private byte[] bgr;
    private int width;
    private int height;
    private boolean changed;
    private boolean isResized;
    private float[] vertices = {
            -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
    };

    public ImageRenderer(GLData glData) {
        super(glData);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                isResized = true;
            }
        });
    }

    public void start() {
        Runnable renderLoop = new Runnable() {
            public void run() {
                if (!ImageRenderer.this.isValid())
                    return;
                ImageRenderer.this.render();
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
    }

    @Override
    public void initGL() {
        createCapabilities();
        try {
            this.shader = new Shader("/opengl/ImageRender.vert", "/opengl/ImageRender.frag");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.textureID = glGenTextures();
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glClearColor(0, 0, 0, 1);
        this.shader.start();
        this.arrayBuffer = glGenVertexArrays();
        this.vertexBuffer = glGenBuffers();
        this.vertexBuffer = glGenBuffers();
        glBindVertexArray(this.arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glViewport(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void paintGL() {
        if (this.isResized) {
            this.isResized = false;
            glViewport(0, 0, this.getWidth(), this.getHeight());
            if (this.width != 0 && this.height != 0) {
                this.remakeVertices();
            }
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glBindVertexArray(this.arrayBuffer);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        if (this.changed) {
            this.changed = false;
            ByteBuffer buffer = MemoryUtil.memAlloc(bgr.length);
            buffer.put(bgr);
            buffer.flip();
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, this.textureID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGR, GL_UNSIGNED_BYTE, buffer);
            glGenerateMipmap(GL_TEXTURE_2D);
            MemoryUtil.memFree(buffer);
        }
        this.shader.start();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glBindVertexArray(0);
        swapBuffers();
    }

    private void remakeVertices() {
        float horizonScale = 1.0f;
        float verticalScale = 1.0f;
        if ((float) this.getWidth() / this.getHeight() < (float) this.width / this.height) {
            verticalScale = (float) this.getWidth() / this.getHeight() * ((float) this.height / this.width);
        } else {
            horizonScale = (float) this.getHeight() / this.getWidth() * ((float) this.width / this.height);
        }
        this.vertices = new float[]{
                -1.0f * horizonScale, 1.0f * verticalScale, 0.0f, 0.0f, 0.0f,
                -1.0f * horizonScale, -1.0f * verticalScale, 0.0f, 0.0f, 1.0f,
                1.0f * horizonScale, -1.0f * verticalScale, 0.0f, 1.0f, 1.0f,
                1.0f * horizonScale, 1.0f * verticalScale, 0.0f, 1.0f, 0.0f,
        };
        glBindVertexArray(this.arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
    }

    public void putImage(byte[] bgr, int width, int height) {
        if (this.width != width || this.height != height) {
            this.isResized = true;
        }
        if (bgr != null) {
            this.bgr = bgr;
            this.width = width;
            this.height = height;
            this.changed = true;
        }
    }
}
