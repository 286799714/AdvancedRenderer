package com.maydaymemory.ar.render;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class ARTexture {
    private int textureId;
    private final ByteBuffer buf;
    private final int width;
    private final int height;

    private static final WeakHashMap<AIScene, Map<String, ARTexture>> textureBuffer = new WeakHashMap<>();

    private static final HashMap<String, HashMap<String, ARTexture>> fileTextureBuffer = new HashMap<>();

    private ARTexture(int width, int height, ByteBuffer buf) {
        this.buf = buf;
        this.width = width;
        this.height = height;
    }

    public static ARTexture fromScene(AIScene scene, String name){
        if(textureBuffer.containsKey(scene)){
            Map<String, ARTexture> textureMap = textureBuffer.get(scene);
            if(textureMap.containsKey(name)){
                return textureMap.get(name);
            }
        }else {
            textureBuffer.put(scene, new HashMap<>());
        }
        PointerBuffer textures = scene.mTextures();
        if(textures == null) throw new NullPointerException("Error when loading texture data in scene '" + scene.mName().dataString()+ "' .");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int i = Integer.parseInt(name.replace("*",""));
            AITexture texture = AITexture.create(textures.get(i));
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer buf = stbi_load_from_memory(texture.pcDataCompressed(), w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Embedded textures named '" + name + "' is not loaded successfully: " + stbi_failure_reason());
            }
            int width = w.get();
            int height = h.get();
            ARTexture arTexture = new ARTexture(width, height, buf);
            textureBuffer.get(scene).put(name, arTexture);
            return arTexture;
        }catch (NumberFormatException ignore){}
        return null;
    }

    public static ARTexture fromByteBuffer(ByteBuffer imageBuffer, String name, String namespace){
        if(fileTextureBuffer.containsKey(namespace)) {
            Map<String, ARTexture> textureMap = fileTextureBuffer.get(namespace);
            if(textureMap.containsKey(name)){
                return textureMap.get(name);
            }
        }else {
            fileTextureBuffer.put(namespace, new HashMap<>());
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Image [" + name + "(fromByteBuffer)] not loaded: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();
            ARTexture arTexture = new ARTexture(width, height, buf);
            fileTextureBuffer.get(namespace).put(name, arTexture);
            return arTexture;
        }
    }

    public void load(){
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buf);
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
