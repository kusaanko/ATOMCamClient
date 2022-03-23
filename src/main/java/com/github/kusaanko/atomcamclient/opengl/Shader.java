package com.github.kusaanko.atomcamclient.opengl;

import org.joml.Matrix4f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public int vertexShader;
    public int fragmentShader;
    public int shaderProgram;
    private int projectionMatrixLocation;

    private Matrix4f projectionMatrix;

    public Shader(String vertexShaderFileName, String fragmentShaderFileName) throws IOException {
        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        String vertexSource = "";
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream stream = Shader.class.getResourceAsStream(vertexShaderFileName);
            byte[] buff = new byte[4096];
            int len;
            while ((len = stream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            stream.close();
            vertexSource = baos.toString();
            baos.close();
        }
        glShaderSource(this.vertexShader, vertexSource);
        glCompileShader(this.vertexShader);
        if (glGetShaderi(this.vertexShader, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(this.vertexShader, 512));
        }

        String fragmentSource = "";
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream stream = Shader.class.getResourceAsStream(fragmentShaderFileName);
            byte[] buff = new byte[4096];
            int len;
            while ((len = stream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            stream.close();
            fragmentSource = baos.toString();
            baos.close();
        }
        glShaderSource(this.fragmentShader, fragmentSource);
        glCompileShader(this.fragmentShader);
        if (glGetShaderi(this.fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(this.fragmentShader, 512));
        }

        this.shaderProgram = glCreateProgram();
        glAttachShader(this.shaderProgram, this.vertexShader);
        glAttachShader(this.shaderProgram, this.fragmentShader);
        glLinkProgram(this.shaderProgram);
        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);
        glValidateProgram(this.shaderProgram);
        this.projectionMatrixLocation = glGetUniformLocation(this.shaderProgram, "projectionMatrix");
    }

    public void start() {
        glUseProgram(this.shaderProgram);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void release() {
        glDeleteShader(this.shaderProgram);
    }
}
