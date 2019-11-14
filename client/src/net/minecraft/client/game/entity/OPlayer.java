package net.minecraft.client.game.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class OPlayer extends AbstractClientPlayer {

	private boolean isItemInUse;
	private int posrotIncrements;
	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;

	public OPlayer(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
		this.stepHeight = 0.0F;
		this.noClip = true;
		this.renderOffsetY = 0.25F;
		this.renderDistanceWeight = 10.0D;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return true;
	}

	@Override
	public <T> void openGui(Class<T> type, T gui) {}

	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = (double) yaw;
		this.pitch = (double) pitch;
		this.posrotIncrements = posRotationIncrements;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.renderOffsetY = 0.0F;
		super.onUpdate();
		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

		if (f > 1.0F) {
			f = 1.0F;
		}

		this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;

		if (!this.isItemInUse && this.isEating() && this.inventory.getCurrentItem() != null) {
			ItemStack itemstack = this.inventory.getCurrentItem();
			setItemInUse(inventory.getCurrentItem(), itemstack.getItem().getMaxItemUseDuration(itemstack));
			this.isItemInUse = true;
		} else if (this.isItemInUse && !this.isEating()) {
			this.clearItemInUse();
			this.isItemInUse = false;
		}
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		if (this.posrotIncrements > 0) {
			double d0 = this.posX + (this.x - this.posX) / (double) this.posrotIncrements;
			double d1 = this.posY + (this.y - this.posY) / (double) this.posrotIncrements;
			double d2 = this.posZ + (this.z - this.posZ) / (double) this.posrotIncrements;
			double d3;

			for (d3 = this.yaw - (double) this.rotationYaw; d3 < -180.0D; d3 += 360.0D) ;

			while (d3 >= 180.0D) {
				d3 -= 360.0D;
			}

			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.posrotIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.pitch - (double) this.rotationPitch) / (double) this.posrotIncrements);
			--this.posrotIncrements;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		this.prevCameraYaw = this.cameraYaw;
		this.updateArmSwingProgress();
		float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f = (float) Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;

		if (f1 > 0.1F) f1 = 0.1F;

		if (!this.onGround || this.getHealth() <= 0.0F) f1 = 0.0F;
		if (this.onGround || this.getHealth() <= 0.0F) f = 0.0F;

		this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f - this.cameraPitch) * 0.8F;
	}


	/**
	 * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
	 */
	public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
		if (slotIn == 0) inventory.setCurrentItem(stack);
		else this.inventory.setArmor(slotIn - 1, stack);
	}

	/**
	 * Send a chat message to the CommandSender
	 */
	public void sendMessage(IChatComponent component) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
	}

	/**
	 * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
	 */
	public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
		return false;
	}

	/**
	 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
	 * the coordinates 0, 0, 0
	 */
	public BlockPos getPosition() {
		return new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
	}

}
