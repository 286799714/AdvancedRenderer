package com.maydaymemory.ar.render;

import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;

public class ARMaterial {
    private ARTexture diffuse;

    public ARMaterial(AIMaterial material, AIScene scene) {
        init(material, scene);
    }

    public ARMaterial(){
    }

    public void init(AIMaterial material, AIScene scene){
        AIString texName = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, texName, (IntBuffer) null, null, null, null, null, null);
        diffuse = ARTexture.fromScene(scene, texName.dataString());
    }

    public void load(){
        if(diffuse != null) diffuse.load();
    }

    public ARTexture getDiffuse() {
        return diffuse;
    }
}
