package net.minecraft.client.game.input;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.game.worldedit.WorldEdit;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerItems;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.ScreenShotHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.SelectorSetting;
import net.minecraft.client.settings.Settings;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.server.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.util.List;

import static net.minecraft.client.Minecraft.getSystemTime;
import static net.minecraft.client.settings.KeyBinding.*;
import static net.minecraft.logging.Log.MAIN;
import static net.minecraft.server.Profiler.in;
import static net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK;

public final class InputHandler {

	private final Minecraft mc;
	private long debugCrashKeyPressTime = -1L;
	private int rightClickDelayTimer;
	private int leftClickCounter;
	/**
	 * Profiler currently displayed in the debug screen pie chart
	 */
	private String debugProfilerName = "root";


	public InputHandler(Minecraft mc) {
		this.mc = mc;
	}

	public String getDebugProfilerName() {
		return debugProfilerName;
	}

	public void runTick() {
		if (rightClickDelayTimer > 0) --rightClickDelayTimer;
		if (mc.currentScreen != null) leftClickCounter = 10000;
	}

	/*
		  Called when user clicked he's mouse right button (place)
		 */
	public void rightClickMouse() {
		if (mc.playerController.isHittingBlock()) return;
		rightClickDelayTimer = Settings.FAST_PLACE.b() ? 0 : 4;
		boolean flag = true;
		ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

		if (mc.objectMouseOver == null) {
			MAIN.warn("При получении объекта под курсором вернулся null");
			return;
		}
		switch (mc.objectMouseOver.typeOfHit) {
			case ENTITY:
				if (mc.playerController.interactAt(mc.thePlayer, mc.objectMouseOver.entityHit, mc.objectMouseOver)) flag = false;
				else if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer, mc.objectMouseOver.entityHit)) flag = false;

				break;

			case BLOCK:
				BlockPos blockpos = mc.objectMouseOver.getBlockPos();

				if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) break;
				if (WorldEdit.rightClick(blockpos)) break;

				int i = itemstack != null ? itemstack.stackSize : 0;

				if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
					flag = false;
					mc.thePlayer.swingItem();
				}
				if (Settings.FAST_PLACE.b()) flag = false;

				if (itemstack == null) return;

				if (itemstack.stackSize == 0) mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
				else if (itemstack.stackSize != i || mc.playerController.isInCreativeMode()) mc.entityRenderer.itemRenderer.resetEquippedProgress();
		}

		if (flag) {
			ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

			if (itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1)) {
				mc.entityRenderer.itemRenderer.resetEquippedProgress2();
			}
		}
	}

	public void middleClickMouse() {
		if (mc.objectMouseOver == null) return;
		boolean flag = mc.thePlayer.capabilities.isCreativeMode;
		int i = 0;
		boolean flag1 = false;
		TileEntity tileentity = null;
		Item item;

		if (mc.objectMouseOver.typeOfHit == BLOCK) {
			BlockPos blockpos = mc.objectMouseOver.getBlockPos();
			Block block = mc.theWorld.getBlockState(blockpos).getBlock();

			if (block.getMaterial() == Material.air) return;

			item = block.getItem(mc.theWorld, blockpos);
			if (item == null) return;

			if (flag && GuiScreen.isCtrlKeyDown()) {
				tileentity = mc.theWorld.getTileEntity(blockpos);
			}

			Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
			i = block1.getDamageValue(mc.theWorld, blockpos);
			flag1 = item.getHasSubtypes();
		} else {
			if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || mc.objectMouseOver.entityHit == null || !flag) return;

			if (mc.objectMouseOver.entityHit instanceof EntityPainting) {
				item = Items.painting;
			} /*else if (mc.objectMouseOver.entityHit instanceof EntityLeashKnot) {
				item = Items.lead; // ToDo: Кастомный результат при нажатии СКМ
			} */ else if (mc.objectMouseOver.entityHit instanceof EntityItemFrame) {
				EntityItemFrame entityitemframe = (EntityItemFrame) mc.objectMouseOver.entityHit;
				ItemStack itemstack = entityitemframe.getDisplayedItem();

				if (itemstack == null) {
					item = Items.item_frame;
				} else {
					item = itemstack.getItem();
					i = itemstack.getMetadata();
					flag1 = true;
				}
			} else if (mc.objectMouseOver.entityHit instanceof EntityMinecart) {
				EntityMinecart entityminecart = (EntityMinecart) mc.objectMouseOver.entityHit;

				switch (entityminecart.getMinecartType()) {
					case FURNACE:
						item = Items.furnace_minecart;
						break;

					case CHEST:
						item = Items.chest_minecart;
						break;

					case TNT:
						item = Items.tnt_minecart;
						break;

					case HOPPER:
						item = Items.hopper_minecart;
						break;

					case COMMAND_BLOCK:
						item = Items.command_block_minecart;
						break;

					default:
						item = Items.minecart;
				}
			} else if (mc.objectMouseOver.entityHit instanceof EntityBoat) {
				item = Items.boat;
			} else if (mc.objectMouseOver.entityHit instanceof EntityArmorStand) {
				item = Items.armor_stand;
			} else {
				//				item = VanillaItems.spawn_egg;
				//				i = EntityList.getEntityID(mc.objectMouseOver.entityHit);
				//				flag1 = true;
				//
				//				if (!EntityList.entityEggs.containsKey(i)) {
				//					return;
				//				}
				return;
			}
		}

		InventoryPlayer inventoryplayer = mc.thePlayer.inventory;

		if (tileentity == null) {
			inventoryplayer.setCurrentItem(item, i, flag1, flag);
		} else {
			ItemStack itemstack1 = getTileItemStack(item, i, tileentity);
			inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
		}

		if (flag) {
			int j = mc.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
			mc.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
		}
	}

	public void processMouse() throws IOException {

		while (Mouse.next()) {
			int i = Mouse.getEventButton();
			KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

			if (Mouse.getEventButtonState()) {
				if (mc.thePlayer.isSpectator() && i == 2) {
					mc.ingameGUI.getSpectatorGui().func_175261_b();
				} else {
					KeyBinding.onTick(i - 100);
				}
			}

			long i1 = getSystemTime() - mc.systemTime;

			if (i1 <= 200L) {
				int j = Mouse.getEventDWheel();

				if (j != 0) {
					if (mc.thePlayer.isSpectator()) {
						j = j < 0 ? -1 : 1;

						if (mc.ingameGUI.getSpectatorGui().func_175262_a()) {
							mc.ingameGUI.getSpectatorGui().func_175259_b(-j);
						} else {
							float f = MathHelper.clamp_float(mc.thePlayer.capabilities.getFlySpeed() + (float) j * 0.005F, 0.0F, 0.2F);
							mc.thePlayer.capabilities.setFlySpeed(f);
						}
					} else {
						mc.thePlayer.inventory.changeCurrentItem(j);
					}
				}

				if (mc.currentScreen == null) {
					if (!mc.inGameHasFocus && Mouse.getEventButtonState()) {
						mc.inputHandler.setIngameFocus();
					}
				} else if (mc.currentScreen != null) {
					mc.currentScreen.handleMouseInput();
				}
			}
		}

		if (leftClickCounter > 0) {
			--leftClickCounter;
		}

	}

	public void processKeyboard() throws IOException {

		while (Keyboard.next()) {
			int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
			KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());

			if (Keyboard.getEventKeyState()) {
				KeyBinding.onTick(k);
			}

			if (debugCrashKeyPressTime > 0L) {
				if (getSystemTime() - debugCrashKeyPressTime >= 600L) {
					throw new ReportedException(new CrashReport("Нарочный вызов краша в благих (нет) целях", new Throwable()));
				}

				if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
					debugCrashKeyPressTime = -1L;
				}
			} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
				debugCrashKeyPressTime = getSystemTime();
			}

			dispatchKeypresses();

			if (Keyboard.getEventKeyState()) {
				if (k == Keyboard.KEY_F4 && mc.entityRenderer != null)
					mc.entityRenderer.switchUseShader();

				if (mc.currentScreen != null) mc.currentScreen.handleKeyboardInput();
				else {
					if (k == Keyboard.KEY_ESCAPE) mc.displayInGameMenu();

					if (Keyboard.isKeyDown(Keyboard.KEY_F3)) switch (k) {
						case 32:
							MC.clearChat();
							break; // D
						case 31:
						case 20:
							mc.refreshResources();
							break; // S & T
						case 30:
							mc.renderGlobal.loadRenderers();
							break; // A
						case 35:
							Settings.ITEM_TOOLTIPS.toggle();
							Settings.saveOptions();
							break; // H
						case 48:
							MC.toggleHitboxes();
							break; // B
						case 25:
							Settings.PAUSE_FOCUS.toggle();
							Settings.saveOptions();
							break; // P
					}

					if (k == Keyboard.KEY_F1) Settings.HIDE_GUI.toggle();

					if (k == Keyboard.KEY_F3) {
						Settings.SHOW_DEBUG.toggle();
						Settings.PROFILER.set(GuiScreen.isShiftKeyDown());
						Settings.LAGOMETER.set(GuiScreen.isAltKeyDown());
					}

					if (KeyBinding.PERSPECTIVE.isPressed()) {
						int view = ((SelectorSetting) Settings.PERSPECTIVE.getBase()).toggle();

						if (view == 0) mc.entityRenderer.loadEntityShader(mc.getRenderViewEntity());
						else if (view == 1) mc.entityRenderer.loadEntityShader(null);

						mc.renderGlobal.setDisplayListEntitiesDirty();
					}

					if (KeyBinding.SMOOTH_CAMERA.isPressed()) Settings.SMOOTH_CAMERA.toggle();
				}

				if (Settings.SHOW_DEBUG.b() && Settings.PROFILER.b()) {
					if (k == 11) updateDebugProfilerName(0);
					for (int j1 = 0; j1 < 9; ++j1) if (k == 2 + j1) updateDebugProfilerName(j1 + 1);
				}
			}
		}

		for (int l = 0; l < 9; ++l) {
			if (KeyBinding.HOTBAR[l].isPressed()) {
				if (mc.thePlayer.isSpectator()) {
					mc.ingameGUI.getSpectatorGui().func_175260_a(l);
				} else {
					mc.thePlayer.inventory.currentItem = l;
				}
			}
		}

		boolean flag = Settings.CHAT_VISIBILITY.i() != 2;

		while (KeyBinding.INVENTORY.isPressed()) {
			if (mc.playerController.isRidingHorse()) mc.thePlayer.sendHorseInventory();
			else {
				mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
				mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
			}
		}

		while (KeyBinding.DROP.isPressed()) if (!mc.thePlayer.isSpectator()) mc.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());

		while (KeyBinding.CHAT.isPressed() && flag) mc.displayGuiScreen(new GuiChat());

		if (mc.currentScreen == null && KeyBinding.COMMAND.isPressed() && flag) mc.displayGuiScreen(new GuiChat("/"));

		if (mc.thePlayer.isUsingItem()) {
			if (!USE.isKeyDown()) mc.playerController.onStoppedUsingItem(mc.thePlayer);
			//				while (ATTACK.isPressed());
			//				while (USE.isPressed());
			//				while (PICK.isPressed());
		} else {
			while (ATTACK.isPressed()) leftClickMouse();
			while (USE.isPressed()) rightClickMouse();
			while (PICK.isPressed()) middleClickMouse();
		}

		if (USE.isKeyDown() && rightClickDelayTimer == 0 && !mc.thePlayer.isUsingItem()) rightClickMouse();

		sendClickBlockToController(mc.currentScreen == null && ATTACK.isKeyDown() && mc.inGameHasFocus);
	}


	public void dispatchKeypresses() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

		if (i == 0 || Keyboard.isRepeatEvent()) return;
		if (mc.currentScreen instanceof GuiControls && ((GuiControls) mc.currentScreen).time > getSystemTime() - 20L) return;
		if (!Keyboard.getEventKeyState()) return;

		if (i == KeyBinding.FULLSCREEN.getKeyCode()) mc.displayGuy.toggleFullscreen();
		else if (i == KeyBinding.SCREENSHOT.getKeyCode())
			mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
	}

	public void leftClickMouse() {
		if (leftClickCounter > 0) return;
		mc.thePlayer.swingItem();

		if (mc.objectMouseOver == null) {
			MAIN.warn("При получении объекта под курсором вернулся null");
			if (mc.playerController.isNotCreative()) leftClickCounter = 10;
			return;
		}
		switch (mc.objectMouseOver.typeOfHit) {
			case ENTITY:
				mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
				break;
			case BLOCK:
				BlockPos blockpos = mc.objectMouseOver.getBlockPos();
				if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
					if (!WorldEdit.leftClick(blockpos)) mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
					break;
				}
			default:
				if (mc.playerController.isNotCreative()) leftClickCounter = 10;
		}
	}


	private ItemStack getTileItemStack(Item item, int meta, TileEntity tileEntity) {
		ItemStack itemstack = new ItemStack(item, 1, meta);
		NBTTagCompound entitysNbt = new NBTTagCompound();
		tileEntity.writeToNBT(entitysNbt);

		if (item == Items.skull && entitysNbt.hasKey("Owner")) {
			NBTTagCompound owner = entitysNbt.getCompoundTag("Owner");
			NBTTagCompound itemInfo = new NBTTagCompound();
			itemInfo.setTag("SkullOwner", owner);
			itemstack.setTagCompound(itemInfo);
			return itemstack;
		}
		itemstack.setTagInfo("BlockEntityTag", entitysNbt);
		NBTTagCompound itemInfo = new NBTTagCompound();
		NBTTagList lore = new NBTTagList();
		lore.appendTag(new NBTTagString("(+NBT)"));
		itemInfo.setTag("Lore", lore);
		itemstack.setTagInfo("display", itemInfo);
		return itemstack;
	}


	private void sendClickBlockToController(boolean leftClick) {
		if (!leftClick) this.leftClickCounter = 0;

		if (this.leftClickCounter > 0 || mc.thePlayer.isUsingItem()) return;
		if (!leftClick || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != BLOCK)
			mc.playerController.resetBlockRemoving();
		else {
			BlockPos blockpos = mc.objectMouseOver.getBlockPos();

			if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air)
				if (!WorldEdit.leftClick(blockpos))
					if (mc.playerController.onPlayerDamageBlock(blockpos, mc.objectMouseOver.sideHit)) {
						mc.effectRenderer.addBlockHitEffects(blockpos, mc.objectMouseOver.sideHit);
						mc.thePlayer.swingItem();
					}
		}
	}


	/**
	 * Update debugProfilerName in response to number keys in debug screen
	 */
	private void updateDebugProfilerName(int keyCount) {
		List<Profiler.Result> list = in.getProfilingData(this.debugProfilerName);

		if (list == null || list.isEmpty()) return;
		Profiler.Result res = list.remove(0);

		if (keyCount == 0) {
			if (res.s.length() > 0) {
				int i = this.debugProfilerName.lastIndexOf(".");
				if (i >= 0) this.debugProfilerName = this.debugProfilerName.substring(0, i);
			}
		} else {
			--keyCount;

			if (keyCount < list.size() && !list.get(keyCount).s.equals("unspecified")) {
				if (this.debugProfilerName.length() > 0) {
					this.debugProfilerName = this.debugProfilerName + ".";
				}

				this.debugProfilerName = this.debugProfilerName + list.get(keyCount).s;
			}
		}
	}


	/**
	 * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
	 * currently displayed
	 */
	public void setIngameFocus() {
		if (!Display.isActive()) return;
		if (mc.inGameHasFocus) return;
		mc.inGameHasFocus = true;
		mc.mouseHelper.grabMouseCursor();
		mc.displayGuiScreen(null);
		leftClickCounter = 10000;
	}

	/**
	 * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
	 */
	public void setIngameNotInFocus() {
		if (!mc.inGameHasFocus) return;
		KeyBinding.unPressAllKeys();
		mc.inGameHasFocus = false;
		mc.mouseHelper.ungrabMouseCursor();
	}

}
