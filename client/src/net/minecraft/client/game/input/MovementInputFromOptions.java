package net.minecraft.client.game.input;

import net.minecraft.util.MovementInput;

import static net.minecraft.client.settings.KeyBinding.*;

public class MovementInputFromOptions extends MovementInput {

	public void updatePlayerMoveState() {
		this.moveStrafe = 0.0F;
		this.moveForward = 0.0F;

		if (FORWARD.isKeyDown()) ++this.moveForward;
		if (BACK.isKeyDown()) --this.moveForward;
		if (LEFT.isKeyDown()) ++this.moveStrafe;
		if (RIGHT.isKeyDown()) --this.moveStrafe;

		this.jump = JUMP.isKeyDown();
		this.sneak = SNEAK.isKeyDown();

		if (this.sneak) {
			this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
			this.moveForward = (float) ((double) this.moveForward * 0.3D);
		}
	}

}
