package net.minecraft.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.WorldServer;

public class EntityBot extends EntityPlayerMP {

	public EntityBot(MinecraftServer server, WorldServer world, GameProfile profile, ItemInWorldManager interactionManager) {
		super(server, world, profile, interactionManager);
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

}
