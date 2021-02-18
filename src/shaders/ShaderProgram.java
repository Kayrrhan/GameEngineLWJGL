package shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.*;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexShaderID, String fragmentShaderID) {
        this.vertexShaderID = loadShader(vertexShaderID,GL20.GL_VERTEX_SHADER);
        this.fragmentShaderID = loadShader(fragmentShaderID,GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID,this.vertexShaderID);
        GL20.glAttachShader(programID,this.fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName){
        return GL20.glGetUniformLocation(programID,uniformName);
    }

    public void start(){
        GL20.glUseProgram(programID);
    }

    public void stop(){
        GL20.glUseProgram(0);
    }

    public void cleanUp(){
        stop();
        GL20.glDetachShader(programID,vertexShaderID);
        GL20.glDetachShader(programID,fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindFragOutput(int attachment, String variableName){
        GL30.glBindFragDataLocation(programID,attachment,variableName);
    }

    protected void loadFloat(int location, float value){
        GL20.glUniform1f(location,value);
    }

    protected void loadVector(int location, Vector3f vector3f){
        GL20.glUniform3f(location,vector3f.x,vector3f.y,vector3f.z);
    }

    protected void load2DVector(int location, Vector2f vector2f){
        GL20.glUniform2f(location,vector2f.x,vector2f.y);
    }

    protected void load4DVector(int location, Vector4f vector4f){
        GL20.glUniform4f(location,vector4f.x,vector4f.y, vector4f.z,vector4f.w);
    }

    protected void loadInt(int location, int value){
        GL20.glUniform1i(location,value);
    }

    protected void loadBoolean(int location, boolean value){
        float toLoad = 0;
        if (value){
            toLoad = 1;
        }
        GL20.glUniform1f(location,toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix4f){
        matrix4f.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location,false,matrixBuffer);
    }

    protected void bindAttribute(int attribute, String variableName){
        GL20.glBindAttribLocation(programID,attribute,variableName);
    }

    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
             BufferedReader reader = new BufferedReader(new FileReader(file));
             String line;
             while ((line = reader.readLine())!= null){
                 shaderSource.append(line).append('\n');
             }
             reader.close();
        }catch (IOException e){
            System.err.println("Could not read file !");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID,shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShader(shaderID,GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }
        return shaderID;
    }

}