package vanilla;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.resources.load.SimpleDatapackLoader;
import net.minecraft.util.Govnokod;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import vanilla.block.BlockMobSpawner;
import vanilla.block.VBlockMushroom;
import vanilla.block.VBlockPortal;
import vanilla.block.VBlockSapling;
import vanilla.client.gui.GuiCreateFlatWorld;
import vanilla.client.gui.GuiCustomizeWorldScreen;
import vanilla.client.gui.block.GuiMerchant;
import vanilla.client.gui.block.GuiScreenHorseInventory;
import vanilla.client.gui.block.HorseInv;
import vanilla.entity.IMerchant;
import vanilla.inventory.ContainerHorseInventory;
import vanilla.inventory.ContainerMerchant;
import vanilla.item.*;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.VanillaDimensionManager;
import vanilla.world.VanillaWorldService;
import vanilla.world.gen.WorldTypes;
import vanilla.world.gen.feature.village.MerchantRecipeList;

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
//				MC.displayGuiScreen(new GuiMerchant(p.inventory, merchant, p.worldObj));
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
//				MC.displayGuiScreen(new GuiScreenHorseInventory(p.inventory, horseinv.inv, horseinv.horse));
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
