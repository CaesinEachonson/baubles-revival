/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Aug 27, 2014, 8:55:00 PM (GMT)]
 */

package baubles.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

/**
 * A bauble item that implements this will be able to render something on the
 * player while it is equipped.
 *
 * This interface does not extend {@link baubles.api.IBauble} to keep the render
 * helper independent of the rest of the API, but the item still needs to
 * implement {@code IBauble} to be wearable.
 */
public interface IRenderBauble {

    /**
     * Called for the rendering of the bauble on the player. The player instance can be
     * acquired through the event parameter. Transformations are already applied for
     * the {@link RenderType} passed in.
     */
    void onPlayerBaubleRender(ItemStack stack, Player player, RenderType type, PoseStack poseStack, float partialTicks);

    /**
     * A few helper methods for the render.
     */
    final class Helper {

        /**
         * Rotates the render for a bauble correctly if the player is sneaking.
         * Use for renders under {@link RenderType#BODY}.
         */
        public static void rotateIfSneaking(PoseStack poseStack, Player player) {
            if (player.isCrouching()) {
                applySneakingRotation(poseStack);
            }
        }

        /**
         * Rotates the render for a bauble correctly for a sneaking player.
         * Use for renders under {@link RenderType#BODY}.
         */
        public static void applySneakingRotation(PoseStack poseStack) {
            poseStack.translate(0F, 0.2F, 0F);
            poseStack.mulPose(new Quaternionf().rotateAxis((float) (Math.PI / 2.0), 1.0F, 0.0F, 0.0F));
        }

        /**
         * Shifts the render for a bauble correctly to the head, including sneaking rotation.
         * Use for renders under {@link RenderType#HEAD}.
         */
        public static void translateToHeadLevel(PoseStack poseStack, Player player) {
            poseStack.translate(0, -player.getEyeHeight(), 0);
            if (player.isCrouching()) {
                poseStack.translate(
                        0.25F * Mth.sin(player.getXRot() * (float) Math.PI / 180),
                        0.25F * Mth.cos(player.getXRot() * (float) Math.PI / 180),
                        0F);
            }
        }

        /**
         * Shifts the render for a bauble correctly to the face.
         * Use for renders under {@link RenderType#HEAD}, and usually after calling
         * {@link Helper#translateToHeadLevel(PoseStack, Player)}.
         */
        public static void translateToFace(PoseStack poseStack) {
            poseStack.mulPose(new Quaternionf().rotateAxis((float) Math.toRadians(90F), 0F, 1F, 0F));
            poseStack.mulPose(new Quaternionf().rotateAxis((float) Math.toRadians(180F), 1F, 0F, 0F));
            poseStack.translate(0f, -4.35f, -1.27f);
        }

        /**
         * Scales down the render to a correct size.
         * Use for any render.
         */
        public static void defaultTransforms(PoseStack poseStack) {
            poseStack.translate(0.0, 3.0, 1.0);
            poseStack.scale(0.55F, 0.55F, 0.55F);
        }

        /**
         * Shifts the render for a bauble correctly to the chest.
         * Use for renders under {@link RenderType#BODY}, and usually after calling
         * {@link Helper#rotateIfSneaking(PoseStack, Player)}.
         */
        public static void translateToChest(PoseStack poseStack) {
            poseStack.mulPose(new Quaternionf().rotateAxis((float) Math.toRadians(180F), 1F, 0F, 0F));
            poseStack.translate(0F, -3.2F, -0.85F);
        }
    }

    enum RenderType {
        /**
         * Render type for the player's body, translations apply on the player's rotation.
         */
        BODY,

        /**
         * Render type for the player's head, translations apply on the player's head rotation.
         */
        HEAD
    }
}
