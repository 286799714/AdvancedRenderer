package com.maydaymemory.ar;

import com.maydaymemory.ar.render.ARModel;
import com.maydaymemory.ar.render.ARRenderer;
import com.maydaymemory.ar.util.Buffers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import static org.lwjgl.assimp.Assimp.*;

@Mod.EventBusSubscriber
@Mod(Reference.MOD_ID)
public class AdvancedRendererMod
{
    private boolean init = false;

    public static ARModel testModel;

    public static final ARRenderer renderer = new ARRenderer();

    public AdvancedRendererMod(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) {
        if(!init){
            try {
                try {
                    //test1是正常的模型
                    //test2是多材质模型
                    //test3是复杂的多分层多材质模型
                    //test4是复杂的多材质模型（模型无分层）
                    ResourceLocation modelResource = new ResourceLocation("advancedrenderer", "models/test.gltf");
                    String[] spilt = modelResource.getPath().split("\\.");
                    String hint = spilt[spilt.length - 1];
                    try( AIScene aiScene = Assimp.aiImportFileFromMemory(
                            Buffers.getByteBufferFromResource(modelResource),
                            aiProcess_Triangulate | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights,
                            new StringBuffer(hint)) ){
                        if(aiScene == null) return;
                        testModel = new ARModel(aiScene);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

                testModel.load();
                renderer.load();
                init = true;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
