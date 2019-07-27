package net.minecraft.entity;

import com.google.common.collect.Maps;
import net.minecraft.Logger;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityList {

	private static final Logger logger = Logger.getInstance();
	private static final Map<String, Class<? extends Entity>> stringToClassMapping = Maps.newHashMap();
	private static final Map<Class<? extends Entity>, String> classToStringMapping = Maps.newHashMap();
	private static final Map<Integer, Class<? extends Entity>> idToClassMapping = Maps.newHashMap();
	private static final Map<Class<? extends Entity>, Integer> classToIDMapping = Maps.newHashMap();
	private static final Map<String, Integer> stringToIDMapping = Maps.newHashMap();
	public static final Map<Integer, EntityList.EntityEggInfo> entityEggs = Maps.newLinkedHashMap();

	/**
	 * adds a mapping between Entity classes and both a string representation and an ID
	 */
	public static void addMapping(Class<? extends Entity> entityClass, String entityName, int id) {
		if (stringToClassMapping.containsKey(entityName)) {
			throw new IllegalArgumentException("ID is already registered: " + entityName);
		}
		if (idToClassMapping.containsKey(id)) {
			throw new IllegalArgumentException("ID is already registered: " + id);
		}
		if (id == 0) {
			throw new IllegalArgumentException("Cannot register to reserved id: " + id);
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("Cannot register null clazz for id: " + id);
		}
		stringToClassMapping.put(entityName, entityClass);
		classToStringMapping.put(entityClass, entityName);
		idToClassMapping.put(id, entityClass);
		classToIDMapping.put(entityClass, id);
		stringToIDMapping.put(entityName, id);
	}

	/**
	 * Adds a entity mapping with egg info.
	 */
	public static void addMapping(Class<? extends Entity> entityClass, String entityName, int entityID, int baseColor, int spotColor) {
		addMapping(entityClass, entityName, entityID);
		if (baseColor != -2) entityEggs.put(entityID, new EntityEggInfo(entityID, baseColor, spotColor));
	}

	public static void regEgg(int id, int baseColor, int spotColor) {
		entityEggs.put(id, new EntityEggInfo(id, baseColor, spotColor));
	}


	public static boolean removeMapping(int id) {
		Class<? extends Entity> type = idToClassMapping.get(id);
		if (type == null) return false;

		String name = classToStringMapping.get(type);

		stringToClassMapping.remove(name);
		classToStringMapping.remove(type);
		idToClassMapping.remove(id);
		classToIDMapping.remove(type);
		stringToIDMapping.remove(name);

		entityEggs.remove(id);

		return true;

	}

	/**
	 * Create a new instance of an entity in the world by using the entity name.
	 */
	public static Entity createEntityByName(String entityName, World worldIn) {
		Entity entity = null;

		try {
			Class<? extends Entity> oclass = stringToClassMapping.get(entityName);

			if (oclass != null) {
				entity = oclass.getConstructor(new Class[] {World.class}).newInstance(worldIn);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return entity;
	}

	/**
	 * create a new instance of an entity from NBT store
	 */
	public static Entity createEntityFromNBT(NBTTagCompound nbt, World worldIn) {
		Entity entity = null;

		if ("Minecart".equals(nbt.getString("id"))) {
			nbt.setString("id", EntityMinecart.EnumMinecartType.byNetworkID(nbt.getInteger("Type")).getName());
			nbt.removeTag("Type");
		}

		try {
			Class<? extends Entity> oclass = stringToClassMapping.get(nbt.getString("id"));

			if (oclass != null) {
				entity = oclass.getConstructor(new Class[] {World.class}).newInstance(worldIn);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (entity != null) {
			entity.readFromNBT(nbt);
		} else {
			logger.warn("Skipping Entity with id " + nbt.getString("id"));
		}

		return entity;
	}

	/**
	 * Create a new instance of an entity in the world by using an entity ID.
	 */
	public static Entity createEntityByID(int entityID, World worldIn) {
		Entity entity = null;

		try {
			Class<? extends Entity> oclass = getClassFromID(entityID);

			if (oclass != null) {
				entity = oclass.getConstructor(new Class[] {World.class}).newInstance(worldIn);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (entity == null) {
			logger.warn("Skipping Entity with id " + entityID);
		}

		return entity;
	}

	/**
	 * gets the entityID of a specific entity
	 */
	public static int getEntityID(Entity entityIn) {
		Integer integer = classToIDMapping.get(entityIn.getClass());
		return integer == null ? 0 : integer;
	}

	public static Class<? extends Entity> getClassFromID(int entityID) {
		return idToClassMapping.get(entityID);
	}

	/**
	 * Gets the string representation of a specific entity.
	 */
	public static String getEntityString(Entity entityIn) {
		return classToStringMapping.get(entityIn.getClass());
	}

	/**
	 * Returns the ID assigned to it's string representation
	 */
	public static int getIDFromString(String entityName) {
		Integer integer = stringToIDMapping.get(entityName);
		return integer == null ? 90 : integer;
	}

	/**
	 * Finds the class using IDtoClassMapping and classToStringMapping
	 */
	public static String getStringFromID(int entityID) {
		return classToStringMapping.get(getClassFromID(entityID));
	}

	public static List<String> getEntityNameList() {
		Set<String> set = stringToClassMapping.keySet();
		List<String> list = new ArrayList<>();

		for (String s : set) {
			Class<? extends Entity> oclass = stringToClassMapping.get(s);

			if ((oclass.getModifiers() & 1024) != 1024) {
				list.add(s);
			}
		}

		list.add("LightningBolt");
		return list;
	}

	public static boolean isStringEntityName(Entity entityIn, String entityName) {
		String s = getEntityString(entityIn);

		if (s == null && entityIn instanceof Player) {
			s = "Player";
		} else if (s == null && entityIn instanceof EntityLightningBolt) {
			s = "LightningBolt";
		}

		return entityName.equals(s);
	}

	public static boolean isStringValidEntityName(String entityName) {
		return "Player".equals(entityName) || getEntityNameList().contains(entityName);
	}

	static {
		addMapping(EntityItem.class, "Item", 1);
		addMapping(EntityXPOrb.class, "XPOrb", 2);
		addMapping(EntityEgg.class, "ThrownEgg", 7);
		addMapping(EntityPainting.class, "Painting", 9);
		addMapping(EntityArrow.class, "Arrow", 10);
		addMapping(EntitySnowball.class, "Snowball", 11);
		addMapping(EntityLargeFireball.class, "Fireball", 12);
		addMapping(EntitySmallFireball.class, "SmallFireball", 13);
		addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
		addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
		addMapping(EntityPotion.class, "ThrownPotion", 16);
		addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
		addMapping(EntityItemFrame.class, "ItemFrame", 18);
		addMapping(EntityWitherSkull.class, "WitherSkull", 19);
		addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
		addMapping(EntityFallingBlock.class, "FallingSand", 21);
		addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
		addMapping(EntityArmorStand.class, "ArmorStand", 30);
		addMapping(EntityBoat.class, "Boat", 41);
		addMapping(EntityMinecartEmpty.class, EntityMinecart.EnumMinecartType.RIDEABLE.getName(), 42);
		addMapping(EntityMinecartChest.class, EntityMinecart.EnumMinecartType.CHEST.getName(), 43);
		addMapping(EntityMinecartFurnace.class, EntityMinecart.EnumMinecartType.FURNACE.getName(), 44);
		addMapping(EntityMinecartTNT.class, EntityMinecart.EnumMinecartType.TNT.getName(), 45);
		addMapping(EntityMinecartHopper.class, EntityMinecart.EnumMinecartType.HOPPER.getName(), 46);
		addMapping(EntityMinecartCommandBlock.class, EntityMinecart.EnumMinecartType.COMMAND_BLOCK.getName(), 40);
		addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
	}

	public static class EntityEggInfo {

		public final int spawnedID;
		public final int primaryColor;
		public final int secondaryColor;
		public final StatBase field_151512_d;
		public final StatBase stat;

		public EntityEggInfo(int id, int baseColor, int spotColor) {
			this.spawnedID = id;
			this.primaryColor = baseColor;
			this.secondaryColor = spotColor;
			this.field_151512_d = StatList.getStatKillEntity(this);
			this.stat = StatList.getStatEntityKilledBy(this);
		}

	}

}
