package vanilla;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideDatapack;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.Domain;
import net.minecraft.resources.load.SimpleDatapackLoader;
import net.minecraft.util.Govnokod;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import vanilla.block.BlockMobSpawner;
import vanilla.block.VBlockMushroom;
import vanilla.block.VBlockPortal;
import vanilla.block.VBlockSapling;
import vanilla.client.game.VanillaIngameModules;
import vanilla.client.game.particle.VanillaParticles;
import vanilla.client.gui.GuiCreateFlatWorld;
import vanilla.client.gui.GuiCustomizeWorldScreen;
import vanilla.client.gui.block.GuiMerchant;
import vanilla.client.gui.block.GuiScreenHorseInventory;
import vanilla.client.gui.block.HorseInv;
import vanilla.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import vanilla.entity.IMerchant;
import vanilla.entity.boss.BossStatus;
import vanilla.inventory.ContainerHorseInventory;
import vanilla.inventory.ContainerMerchant;
import vanilla.item.*;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.VanillaDimensionManager;
import vanilla.world.VanillaWorldService;
import vanilla.world.WorldProviderEnd;
import vanilla.world.WorldProviderHell;
import vanilla.world.gen.WorldTypes;
import vanilla.world.gen.feature.village.MerchantRecipeList;
import vanilla.worldedit.WorldEdit;

import static net.minecraft.block.Block.*;
import static net.minecraft.inventory.creativetab.CreativeTabs.tabRedstone;

public class Vanilla extends Datapack implements ClientSideDatapack {


	public static final Domain VANILLA = new Domain("vanilla");

	public Vanilla() {
		super(VANILLA);
		Datapacks.load(new SimpleDatapackLoader(new WorldEdit()), "worldedit");
	}

	@Override
	public void preinit() {
		registrar.setWorldServiceProvider(VanillaWorldService::new);
		registrar.replaceProvider(World.DIMENSION_PROVIDER, VanillaDimensionManager::generate);
		new VEntities().load(registrar);
	}

	@Override
	public void init() {

		new VEvents().load(registrar);
		new VPackets().load(registrar);
		new Dispensers().load(registrar);
		new WorldTypes().load(registrar);

		registerGuis();
		if (isServerSide()) return;

		WorldTypes.FLAT.setCustomizer(GuiCreateFlatWorld::new);
		WorldTypes.CUSTOMIZED.setCustomizer(GuiCustomizeWorldScreen::new);


	}

	@Override
	public void clientInit(ClientRegistrar registrar) {

		new VEntities().load(registrar);


		RenderItem r = MC.getRenderItem();

		registrar.registerItem(VanillaItems.saddle, 0, new ModelResourceLocation("saddle"));
		r.registerItem(VanillaItems.saddle, "saddle");
		r.getItemModelMesher().registerMeshDefinition(VanillaItems.spawn_egg, stack -> new ModelResourceLocation("spawn_egg", "inventory"));
		r.registerItem(VanillaItems.carrot_on_a_stick, "carrot_on_a_stick");
		r.registerItem(VanillaItems.lead, "lead");
		r.registerItem(VanillaItems.name_tag, "name_tag");

		TileEntityRendererDispatcher.instance.register(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());


		registrar.replaceProvider(MusicTicker.MUSIC_TYPE_PROVIDER, musicTicker -> {
			Player p = MC.getPlayer();
			return p != null ? p.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER :
					p.worldObj.provider instanceof WorldProviderEnd ? BossStatus.bossName != null && BossStatus.statusBarTime > 0 ?
							MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END : p.capabilities.isCreativeMode &&
							p.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME : MusicTicker.MusicType.MENU;
		});

		new VanillaParticles().load(registrar);
		new VanillaIngameModules().load(registrar);
	}

	@Govnokod (levelOfPizdec = "Небезопасное прямое взаимодействие с полями")
	private void registerGuis() {
		registrar.registerIngameGui(IMerchant.class, (p, merchant, serverSide) -> {
			if (serverSide) {
				if (!(p instanceof MPlayer)) return;
				MPlayer player = (MPlayer) p;
				player.getNextWindowId();
				player.openContainer = new ContainerMerchant(player.inventory, merchant, player.worldObj);
				player.openContainer.windowId = player.currentWindowId;
				player.openContainer.onCraftGuiOpened(player);
				IInventory iinventory = ((ContainerMerchant) player.openContainer).getMerchantInventory();
				IChatComponent ichatcomponent = merchant.getDisplayName();
				player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, "minecraft:villager", ichatcomponent, iinventory.getSizeInventory()));
				MerchantRecipeList merchantrecipelist = merchant.getRecipes(player);

				if (merchantrecipelist != null) {
					PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
					packetbuffer.writeInt(player.currentWindowId);
					merchantrecipelist.writeToBuf(packetbuffer);
					player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetbuffer));
				}

			} else {
				MC.displayGuiScreen(new GuiMerchant(p.inventory, merchant, p.worldObj));
			}
		});
		registrar.registerIngameGui(HorseInv.class, (p, horseinv, serverSide) -> {
			if (serverSide) {
				if (p.openContainer != p.inventoryContainer) {
					p.closeScreen();
				}
				if (!(p instanceof MPlayer)) return;
				MPlayer player = (MPlayer) p;
				((MPlayer) p).getNextWindowId();
				IInventory inv = horseinv.inv;
				player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, "EntityHorse", inv.getDisplayName(), inv.getSizeInventory(),
						horseinv.horse.getEntityId()));
				player.openContainer = new ContainerHorseInventory(player.inventory, inv, horseinv.horse, player);
				player.openContainer.windowId = player.currentWindowId;
				player.openContainer.onCraftGuiOpened(player);
			} else {
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


		registrar.registerBlock(6, "sapling", new VBlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));
		Block redMushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
		registrar.registerBlock(39, "brown_mushroom", redMushroom);
		Block brownBushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
		registrar.registerBlock(40, "red_mushroom", brownBushroom);
		registrar.registerBlock(90, "portal", new VBlockPortal().setHardness(-1).setStepSound(soundTypeGlass).setLightLevel(0.75F).setUnlocalizedName("portal"));

		registrar.registerBlock(99, "brown_mushroom_block",
				new BlockHugeMushroom(Material.wood, MapColor.dirtColor, redMushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));
		registrar.registerBlock(100, "red_mushroom_block",
				new BlockHugeMushroom(Material.wood, MapColor.redColor, brownBushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));

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


		registrar.registerItem(351, "dye", new VItemDye().setUnlocalizedName("dyePowder"));
		VanillaItems.init();
	}

}
