package vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.game.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.server.Todo;
import vanilla.block.BlockMobSpawner;
import vanilla.block.VBlockDispenser;
import vanilla.block.VBlockMushroom;
import vanilla.block.VBlockSapling;
import vanilla.client.game.model.*;
import vanilla.client.game.particle.VanillaParticles;
import vanilla.client.gui.GuiCreateFlatWorld;
import vanilla.client.gui.GuiCustomizeWorldScreen;
import vanilla.client.gui.block.GuiMerchant;
import vanilla.client.gui.block.GuiScreenHorseInventory;
import vanilla.client.gui.block.HorseInv;
import vanilla.client.renderer.entity.RenderLeashKnot;
import vanilla.client.renderer.entity.RenderMinecartMobSpawner;
import vanilla.client.renderer.entity.vanilla.*;
import vanilla.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import vanilla.entity.EntityLeashKnot;
import vanilla.entity.IMerchant;
import vanilla.entity.ai.EntityMinecartMobSpawner;
import vanilla.entity.boss.BossStatus;
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;
import vanilla.item.*;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.VanillaWorldService;
import vanilla.world.WorldProviderEnd;
import vanilla.world.WorldProviderHell;
import vanilla.world.gen.WorldTypes;

import static net.minecraft.block.Block.*;
import static net.minecraft.inventory.creativetab.CreativeTabs.tabRedstone;

public class Vanilla extends Datapack {


	public static final Domain VANILLA = new Domain("vanilla");

	public Vanilla() {
		super(VANILLA);
	}

	@Override
	public void preinit() {

		registrar.setWorldServiceProvider(VanillaWorldService::new);
		new VEntities().load(registrar);

	}

	@Override
	public void init() {

		new VEvents().load(registrar);
		new VPackets().load(registrar);
		new Dispensers().load(registrar);

		if (isServerSide()) return;

		WorldTypes.FLAT.setCustomizer(GuiCreateFlatWorld::new);
		WorldTypes.CUSTOMIZED.setCustomizer(GuiCustomizeWorldScreen::new);

		registerGuis();

	}

	@Override
	public void postinit() {

		if (Todo.instance.isServerSide()) return;

		RenderManager m = MC.i().getRenderManager();

		m.regMapping(EntityCaveSpider.class, new RenderCaveSpider(m));
		m.regMapping(EntitySpider.class, new RenderSpider(m));
		m.regMapping(EntityPig.class, new RenderPig(m, new ModelPig(), 0.7F));
		m.regMapping(EntitySheep.class, new RenderSheep(m, new ModelSheep2(), 0.7F));
		m.regMapping(EntityCow.class, new RenderCow(m, new ModelCow(), 0.7F));
		m.regMapping(EntityMooshroom.class, new RenderMooshroom(m, new ModelCow(), 0.7F));
		m.regMapping(EntityWolf.class, new RenderWolf(m, new ModelWolf(), 0.5F));
		m.regMapping(EntityChicken.class, new RenderChicken(m, new ModelChicken(), 0.3F));
		m.regMapping(EntityOcelot.class, new RenderOcelot(m, new ModelOcelot(), 0.4F));
		m.regMapping(EntityRabbit.class, new RenderRabbit(m, new ModelRabbit(), 0.3F));
		m.regMapping(EntitySilverfish.class, new RenderSilverfish(m));
		m.regMapping(EntityEndermite.class, new RenderEndermite(m));
		m.regMapping(EntityCreeper.class, new RenderCreeper(m));
		m.regMapping(EntityEnderman.class, new RenderEnderman(m));
		m.regMapping(EntitySnowman.class, new RenderSnowMan(m));
		m.regMapping(EntitySkeleton.class, new RenderSkeleton(m));
		m.regMapping(EntityWitch.class, new RenderWitch(m));
		m.regMapping(EntityBlaze.class, new RenderBlaze(m));
		m.regMapping(EntityPigZombie.class, new RenderPigZombie(m));
		m.regMapping(EntityZombie.class, new RenderZombie(m));
		m.regMapping(EntitySlime.class, new RenderSlime(m, new ModelSlime(16), 0.25F));
		m.regMapping(EntityMagmaCube.class, new RenderMagmaCube(m));
		m.regMapping(EntityGiantZombie.class, new RenderGiantZombie(m, new ModelZombie(), 0.5F, 6.0F));
		m.regMapping(EntityGhast.class, new RenderGhast(m));
		m.regMapping(EntitySquid.class, new RenderSquid(m, new ModelSquid(), 0.7F));
		m.regMapping(EntityVillager.class, new RenderVillager(m));
		m.regMapping(EntityIronGolem.class, new RenderIronGolem(m));
		m.regMapping(EntityBat.class, new RenderBat(m));
		m.regMapping(EntityGuardian.class, new RenderGuardian(m));
		m.regMapping(EntityDragon.class, new RenderDragon(m));
		m.regMapping(EntityWither.class, new RenderWither(m));
		m.regMapping(EntityLeashKnot.class, new RenderLeashKnot(m));
		m.regMapping(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(m));
		m.regMapping(EntityHorse.class, new RenderHorse(m, new ModelHorse(), 0.75F));

		RenderItem r = MC.getRenderItem();
		r.registerItem(VanillaItems.saddle, "saddle");
		r.getItemModelMesher().register(VanillaItems.spawn_egg, stack -> new ModelResourceLocation("spawn_egg", "inventory"));
		r.registerItem(VanillaItems.carrot_on_a_stick, "carrot_on_a_stick");
		r.registerItem(VanillaItems.lead, "lead");
		r.registerItem(VanillaItems.name_tag, "name_tag");

		TileEntityRendererDispatcher.instance.register(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());

		MC.i().getMusicTicker().musicTypeSupplier = () -> {
			EntityPlayer p = MC.getPlayer();
			return p != null ? p.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER :
					p.worldObj.provider instanceof WorldProviderEnd ? BossStatus.bossName != null && BossStatus.statusBarTime > 0 ?
							MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END : p.capabilities.isCreativeMode &&
							p.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME : MusicTicker.MusicType.MENU;
		};

		VanillaParticles.register();
		VanillaIngameModules.register();
	}

	private void registerGuis() {
		registrar.regGui(IMerchant.class, (p, merchant, serverSide) -> {
			if (!serverSide) {
				MC.displayGuiScreen(new GuiMerchant(p.inventory, merchant, p.worldObj));
			}
		});
		registrar.regGui(HorseInv.class, (p, horseinv, serverSide) -> {
			if (!serverSide) {
				MC.displayGuiScreen(new GuiScreenHorseInventory(p.inventory, horseinv.inv, horseinv.horse));
			}
		});

	}

	@Override
	protected void unload() {
	}

	@Override
	public void loadBlocks() {

		registrar.registerBlock(52, "mob_spawner",
				new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats().setCreativeTab(tabRedstone));


		registrar.overrideBlock(6, "sapling", new VBlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));
		Block redMushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
		registrar.overrideBlock(39, "brown_mushroom", redMushroom);
		Block brownBushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
		registrar.overrideBlock(40, "red_mushroom", brownBushroom);
		registrar.overrideBlock(99, "brown_mushroom_block",
				new BlockHugeMushroom(Material.wood, MapColor.dirtColor, redMushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));
		registrar.overrideBlock(100, "red_mushroom_block",
				new BlockHugeMushroom(Material.wood, MapColor.redColor, brownBushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));

		registrar.overrideBlock(23, "dispenser", new VBlockDispenser().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dispenser"));

		// Todo wrap with registrar
		registrar.registerTileEntity(TileEntityMobSpawner.class, "MobSpawner");
	}

	@Override
	public void loadItems() {

		registrar.registerItemBlock(Blocks.mob_spawner);
		registrar.registerItem(329, "saddle", new ItemSaddle().setUnlocalizedName("saddle"));
		registrar.registerItem(383, "spawn_egg", new ItemMonsterPlacer().setUnlocalizedName("monsterPlacer"));
		registrar.registerItem(398, "carrot_on_a_stick", new ItemCarrotOnAStick().setUnlocalizedName("carrotOnAStick"));
		registrar.registerItem(420, "lead", new ItemLead().setUnlocalizedName("leash"));
		registrar.registerItem(421, "name_tag", new ItemNameTag().setUnlocalizedName("nameTag"));


		registrar.overrideItem(351, "dye", new VItemDye().setUnlocalizedName("dyePowder"));
		VanillaItems.init();
	}

}
