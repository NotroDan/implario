package vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.FenceClickedEvent;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.logging.Log;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import vanilla.block.BlockMobSpawner;
import vanilla.block.VBlockMushroom;
import vanilla.block.VBlockSapling;
import vanilla.entity.VanillaEntity;
import vanilla.entity.boss.DragonPartRedirecter;
import vanilla.entity.monster.EntityBlaze;
import vanilla.entity.monster.EntityEndermite;
import vanilla.entity.monster.EntitySilverfish;
import vanilla.entity.passive.EntityChicken;
import vanilla.entity.passive.EntityHorse;
import vanilla.inventory.ContainerMerchant;
import vanilla.item.*;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.SleepChecker;

import static net.minecraft.block.Block.*;
import static net.minecraft.inventory.creativetab.CreativeTabs.tabRedstone;

public class Vanilla extends Datapack {


	public static final Domain VANILLA = new Domain("vanilla");

	public Vanilla() {
		super(VANILLA);
	}

	@Override
	public void preinit() {

		registrar.registerItem(329, "saddle", new ItemSaddle().setUnlocalizedName("saddle"));
		registrar.registerItem(383, "spawn_egg", new ItemMonsterPlacer().setUnlocalizedName("monsterPlacer"));
		registrar.registerItem(398, "carrot_on_a_stick", new ItemCarrotOnAStick().setUnlocalizedName("carrotOnAStick"));
		registrar.registerItem(420, "lead", new ItemLead().setUnlocalizedName("leash"));
		registrar.registerItem(421, "name_tag", new ItemNameTag().setUnlocalizedName("nameTag"));

		registrar.registerBlock(52, "mob_spawner", new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats().setCreativeTab(tabRedstone));

		registrar.overrideItem(351, "dye", new VItemDye().setUnlocalizedName("dyePowder"));
		registrar.overrideBlock(6, "sapling", new VBlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));



		Block redMushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
		registrar.overrideBlock(39, "brown_mushroom", redMushroom);
		Block brownBushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
		registrar.overrideBlock(40, "red_mushroom", brownBushroom);
		registrar.overrideBlock(99, "brown_mushroom_block", new BlockHugeMushroom(Material.wood, MapColor.dirtColor, redMushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));
		registrar.overrideBlock(100, "red_mushroom_block", new BlockHugeMushroom(Material.wood, MapColor.redColor, brownBushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));

		TileEntity.register(TileEntityMobSpawner.class, "MobSpawner");

	}

	@Override
	public void load() {


		registrar.regListener(DamageByEntityEvent.class, new DragonPartRedirecter());
		registrar.regListener(TrySleepEvent.class, new SleepChecker());
		registrar.regListener(PlayerEntityActionEvent.class, e -> {
			if (e.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY)
				if (e.getPlayer().ridingEntity instanceof EntityHorse)
					((EntityHorse) e.getPlayer().ridingEntity).openGUI(e.getPlayer());
			if (e.getAction() == C0BPacketEntityAction.Action.RIDING_JUMP)
				if (e.getPlayer().ridingEntity instanceof EntityHorse)
					((EntityHorse) e.getPlayer().ridingEntity).setJumpPower(e.getAux());
		});

		registrar.regListener(UpdateEntityToSpectatorEvent.class, e -> {
			Entity entity = e.getTrackerEntry().trackedEntity;
			if (!(entity instanceof VanillaEntity)) return;
			VanillaEntity ve = (VanillaEntity) entity;
			Entity leashed = ve.getLeashedToEntity();
			if (leashed != null) {
				e.getPlayer().playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, entity, leashed));
			}
		});


		registrar.regListener(ProjectileHitEvent.class, e -> {

			EntityThrowable t = e.getThrowable();

			if (t instanceof EntityEgg) {
				if (t.worldObj.isClientSide || t.rand.nextInt(8) != 0) return;
				int i = 1;

				if (t.rand.nextInt(32) == 0) i = 4;

				for (int j = 0; j < i; ++j) {
					EntityChicken entitychicken = new EntityChicken(t.worldObj);
					entitychicken.setGrowingAge(-24000);
					entitychicken.setLocationAndAngles(t.posX, t.posY, t.posZ, t.rotationYaw, 0.0F);
					t.worldObj.spawnEntityInWorld(entitychicken);
				}
			} else if (t instanceof EntitySnowball) {
				if (e.getObject().entityHit != null) {
					int i = 0;
					if (e.getObject().entityHit instanceof EntityBlaze) i = 3;

					e.getObject().entityHit.attackEntityFrom(DamageSource.causeThrownDamage(e.getThrowable(), e.getThrowable().getThrower()), (float) i);
				}
			}

		});

		registrar.regListener(PlayerEnderPearlEvent.class, e -> {
			EntityEnderPearl p = e.getPearl();
			EntityPlayerMP m = e.getPlayer();
			if (p.rand.nextFloat() < 0.05F && p.worldObj.getGameRules().getBoolean("doMobSpawning")) {
				EntityEndermite entityendermite = new EntityEndermite(p.worldObj);
				entityendermite.setSpawnedByPlayer(true);
				entityendermite.setLocationAndAngles(m.posX, m.posY, m.posZ, m.rotationYaw, m.rotationPitch);
				p.worldObj.spawnEntityInWorld(entityendermite);
			}

		});

		registrar.regInterceptor(C17PacketCustomPayload.class, (p, l) -> {

			if ("MC|TrSel".equals(p.getChannelName())) {
				try {
					int i = p.getBufferData().readInt();
					Container container = l.getPlayer().openContainer;

					if (container instanceof ContainerMerchant) {
						((ContainerMerchant) container).setCurrentRecipeIndex(i);
					}
				} catch (Exception e) {
					Log.MAIN.error("Couldn\'t select trade");
					Log.MAIN.exception(e);
				}
				return false;
			}
			return true; // ToDo: Обработка ретурнеда
		});

		registrar.regListener(FenceClickedEvent.class, e -> {
			e.returnValue = ItemLead.attachToFence(e.getPlayer(), e.getWorld(), e.getPos());
		});

		registrar.regListener(BlockDropEvent.class, e -> {
			World w = e.getWorld();
			BlockPos pos = e.getPosition();
			if (e.getBlock().getBlock() == Blocks.monster_egg) {
				e.cancelDefaultDrop();
				if (!w.isClientSide && w.getGameRules().getBoolean("doTileDrops")) {
					EntitySilverfish entitysilverfish = new EntitySilverfish(w);
					entitysilverfish.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
					w.spawnEntityInWorld(entitysilverfish);
					entitysilverfish.spawnExplosionParticle();
				}
			}
		});


		registerGuis();

		registerDispenserBehaviours();
	}

	private void registerGuis() {
	}

	private void registerDispenserBehaviours() {
		Dispensers.init();
	}

	@Override
	protected void unload() {

	}

}
