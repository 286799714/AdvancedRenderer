package com.maydaymemory.ar.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.maydaymemory.ar.TestRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "renderRightHand", at = @At(value = "HEAD"))
    public void onRender(PoseStack p_117771_, MultiBufferSource p_117772_, int p_117773_, AbstractClientPlayer p_117774_, CallbackInfo ci){
        TestRenderer.INSTANCE.render(p_117771_);
    }
}
