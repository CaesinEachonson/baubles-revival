/**
 * Rendering of equipped baubles on the player model. Based on the Botania render layer
 * that shipped with the original Baubles.
 */
package baubles.client;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import baubles.common.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public class BaublesRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public BaublesRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!Config.renderBaubles()) return;
        if (player.hasEffect(MobEffects.INVISIBILITY)) return;

        IBaublesItemHandler inventory = BaublesApi.getBaublesHandler(player);
        this.dispatchRenders(poseStack, buffer, packedLight, inventory, player, RenderType.BODY, partialTicks);

        float yaw = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        float yawOffset = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        float pitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-yawOffset));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 270.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
        this.dispatchRenders(poseStack, buffer, packedLight, inventory, player, RenderType.HEAD, partialTicks);
        poseStack.popPose();
    }

    private void dispatchRenders(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                 IBaublesItemHandler inventory, AbstractClientPlayer player,
                                 RenderType type, float partialTicks) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof IRenderBauble renderBauble) {
                poseStack.pushPose();
                renderBauble.onPlayerBaubleRender(stack, player, type, poseStack, partialTicks);
                poseStack.popPose();
            }
        }
    }
}
