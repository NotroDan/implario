package net.minecraft.server;

import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

public class Todo {

	public static Todo instance = new Todo();

	public boolean isSmoothWorld() {
		return false;
	}

	public boolean isCullFacesLeaves() {
		return false;
	}

	/**
	 * Поскольку оптифайн ебучий долбаёб, оставлю всё вот так, лишь бы работало.
	 * @return true, если стоит даунский оптифайн и обрабатывает эту херню через ёбанное анальное отверстие выдры,
	 * или false, если надо будет просто вызвать super.isEntityInsideOpaqueBlock();
	 */
	public boolean shouldUseOptifineOpaquenessChecking() {
		return false; // Сдохни блядь оптифайн
	}




	public boolean isEntityInsideOpaqueBlockOptifineTupoeGovnoSdohniNahuyPozhaluysta(boolean noClip, double posX, double posY, double posZ, double width, double eyeHeight, World worldObj)  {
		throw new NotImplementedException("Так нельзя!");
	}

	public boolean shouldUseRomanianNotation(int level) {
		return level <= 100;
	}

	public boolean isServerSide() {
		return true;
	}

}
