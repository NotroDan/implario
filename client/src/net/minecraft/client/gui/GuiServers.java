package net.minecraft.client.gui;

import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.gui.element.Animation;
import net.minecraft.client.gui.element.Animator;
import net.minecraft.client.gui.element.GuiTextField;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.G;
import net.minecraft.util.Easing;
import net.minecraft.util.Govnokod;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiServers extends GuiScreen {

	private static final long SMOOTH_SCROLL_TIME = 150;
	private List<Server> servers = new ArrayList<>();
	private final GuiScreen parent;
	private ServerList serverList;
	private int columns = -1;
	private int rows = -1;
	private int rightBorder;
	public static int H = 84;
	public static int W = 72;
	protected volatile int scroll, lastScroll;
	protected volatile long lastScrolled;
	protected boolean lastUpwards;
	protected int cw, ch;
	private long lastClick;
	private int wantDrag = -1;
	public Server dragged;
	private long addBtnHovertime;
	private boolean addBtnHovered;
	private Animator.Cycle addBtnAnim;
	private boolean addingServer;
	public int lastMx = -1, lastMy = -1;
	private int draggedId = -1;
	private Animator.Cycle darkenerAnim;
	private GuiTextField serverName, serverIp;

	private static final int darkColor = 0xa0101010;

	public GuiServers(GuiScreen parent) {
		this.parent = parent;
	}

	public GuiScreen getParent() {
		return parent;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		cw = width / 2;
		ch = height / 2;

		if (columns == -1 || servers.isEmpty()) {
			serverList = new ServerList(mc);
			serverList.loadServerList();
			for (ServerData serverData : serverList.getServers()) servers.add(new Server(serverData));
		}
		columns = width / 72;
		rightBorder = width / 2 - columns * 36;
		columns--;
		int size = servers.size();
		rows = columns > 0 ? size / columns + 1 : -1;
		if (columns == 0) columns = -1;
		scroll = 0;
		lastScroll = 0;
		lastScrolled = 0;
		serverName = new GuiTextField(21, MC.FR, cw - 100, ch - 5, 200, 20);
		serverName.setMaxStringLength(128);
		serverIp = new GuiTextField(22, MC.FR, cw - 100, ch - 40, 200, 20);
		serverIp.setMaxStringLength(128);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();
		if (wheel != 0) {
			if (wheel < -1) wheel = -1;
			if (wheel > 1) wheel = 1;
			long time = System.currentTimeMillis();
			int n = wheel * H / 2;
			if (time - lastScrolled > SMOOTH_SCROLL_TIME) { //|| n > 0 != lastUpwards) {
				lastScroll = scroll;
				lastScrolled = time;
			}
			lastUpwards = n > 0;
			scroll -= n;
			if (scroll > rows * H - height) scroll = rows * H - height;
			if (scroll < 0) scroll = 0;

		}
	}

	@Override
	public void updateScreen() {
		serverIp.updateCursorCounter();
		serverName.updateCursorCounter();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (addingServer) {
			serverIp.mouseClicked(mouseX, mouseY, mouseButton);
			serverName.mouseClicked(mouseX, mouseY, mouseButton);
		}
		lastMx = mouseX;
		lastMy = mouseY;
		lastClick = System.currentTimeMillis();

	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (dragged == null) {
			if (MC.sqd(mouseX - lastMx, mouseY - lastMy) < 49 || lastMy == -1 || lastMx == -1) return;
			draggedId = getServerId(lastMx, lastMy);
			dragged = draggedId == -1 ? null : servers.get(draggedId);
		}
		if (dragged != null) {
			mouseX -= rightBorder;
			mouseY += scroll;
			wantDrag = mouseX / W % columns + mouseY / H * columns;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		//		super.mouseReleased(mouseX, mouseY, state);
		if (dragged == null && System.currentTimeMillis() - lastClick < 5000 && !addingServer) {
			Server server = getServerAt(mouseX, mouseY);
			if (server != null) {
				this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server.serverData));
				lastClick = 0;
			}
		}
		if (dragged != null && wantDrag > -1 && wantDrag < servers.size() && wantDrag != draggedId && !addingServer) {
			Iterator<Server> iter = servers.iterator();
			List<Server> list = new ArrayList<>();
			int i = -1;
			boolean inserted = false;


			while (iter.hasNext()) {
				i++;
				Server server = iter.next();
				if (i == wantDrag) {
					list.add(dragged);
					inserted = true;
				}
				if (i == draggedId) {
					if (iter.hasNext()) server = iter.next();
					else break;
				}
				list.add(server);
			}
			if (!inserted) list.add(dragged);
			servers = list;
		}

		if (addBtnHovered && addBtnAnim == null && !addingServer) {
			updateAnims();
			addBtnAnim = addButton.new Cycle(System.currentTimeMillis());
			darkenerAnim = darkener.new Cycle(System.currentTimeMillis());
			addBtnHovered = false;
		}

		wantDrag = -1;
		dragged = null;
		draggedId = -1;
		lastMy = -1;
		lastMx = -1;

	}

	private int getServerId(int x, int y) {
		x -= rightBorder;
		y += scroll;
		int i = x / W % columns + y / H * columns;
		if (i < 0 || i >= servers.size()) return -1;
		return i;
	}

	private Server getServerAt(int x, int y) {
		int a = getServerId(x, y);
		return a == -1 ? null : servers.get(a);
	}


	private final int addX = -150, addY = -100;

	public final Animator darkener = new Animator(0, 0,
			new Animation(0, 0, 0, 0, 0, 0, 500,
					p -> drawRect(0, 0, width, height, Utils.gradient(darkColor, 0, p))));

	public final Animator addButton = new Animator(0, 0,
			new Animation(-W / 2 + 2, -H / 2 + 2, 0x0, addX, addY, 0x0, 500,
					this::drawForm, Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, -addX, -addY, 0xffd02030, 500,
					p -> drawRect(cw - 24, ch - 3, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, -addX, -addY, 0xffd02030, 500,
					p -> drawRect(cw - 3, ch - 24, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, addX, -addY, 0xffd02030, 500,
					p -> drawRect(cw + 24, ch - 3, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, addX, -addY, 0xffd02030, 500,
					p -> drawRect(cw + 3, ch - 24, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, addX, addY, 0xffd02030, 500,
					p -> drawRect(cw + 24, ch + 3, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, addX, addY, 0xffd02030, 500,
					p -> drawRect(cw + 3, ch + 24, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, -addX, addY, 0xffd02030, 500,
					p -> drawRect(cw - 24, ch + 3, cw, ch, 0xff20ff20), Easing.QUAD_B),
			new Animation(0, 0, 0xff20ff20, -addX, addY, 0xffd02030, 500,
					p -> drawRect(cw - 3, ch + 24, cw, ch, 0xff20ff20), Easing.QUAD_B)
	);


	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (addingServer) {
			switch (keyCode) {
				case 1:
					closeAddingServerGUI();
					break;
				case 15:
					boolean b = serverIp.isFocused();
					serverIp.setFocused(!b);
					serverName.setFocused(b);
					break;
				default:
					serverName.textboxKeyTyped(typedChar, keyCode);
					serverIp.textboxKeyTyped(typedChar, keyCode);
			}
		} else super.keyTyped(typedChar, keyCode);
	}

	private void openAdddingServerGui() {
		addingServer = true;
		serverIp.setFocused(true);

	}

	private void closeAddingServerGUI() {
		addingServer = false;
		serverIp.setFocused(false);
		serverName.setFocused(false);
		serverName.setText("");
		serverIp.setText("");
	}

	private void drawAddingServerGUI() {
		drawRect(0, 0, width, height, darkColor);
		drawRect(cw + addX, ch + addY, cw - addX, ch - addY, 0x90f4d442);
		drawRect(cw + addX, ch + addY, cw - addX, ch + addY + 24, 0xff232323);
		drawRect(cw - 24 - addX, ch - 3 - addY, cw - addX, ch - addY, 0xff20ff20);
		drawRect(cw - 3 - addX, ch - 24 - addY, cw - addX, ch - addY, 0xff20ff20);
		drawRect(cw + 24 + addX, ch - 3 - addY, cw + addX, ch - addY, 0xff20ff20);
		drawRect(cw + 3 + addX, ch - 24 - addY, cw + addX, ch - addY, 0xff20ff20);
		drawRect(cw + 24 + addX, ch + 3 + addY, cw + addX, ch + addY, 0xff20ff20);
		drawRect(cw + 3 + addX, ch + 24 + addY, cw + addX, ch + addY, 0xff20ff20);
		drawRect(cw - 24 - addX, ch + 3 + addY, cw - addX, ch + addY, 0xff20ff20);
		drawRect(cw - 3 - addX, ch + 24 + addY, cw - addX, ch + addY, 0xff20ff20);
		serverName.drawTextBox();
		serverIp.drawTextBox();
		MC.FR.drawString("IP-адрес сервера§c *", cw - 100, ch - 50, -1);
		MC.FR.drawString("Название", cw - 100, ch - 15, -1);
		G.scale(2, 2, 2);
		MC.FR.drawString("Добавить сервер", (cw - MC.FR.getStringWidth("Добавить сервер")) / 2,
				(ch + addY) / 2 + 2, 0xffffe0e0);
		G.scale(0.5, 0.5, 0.5);
	}

	private void drawForm(float p) {
		int dx = (int) (p * addX);
		int dy = (int) (p * addY);
		drawRect(cw, ch,
				cw - 2 * dx + (int) ((1 - p) * (W - 4)),
				ch - 2 * dy + (int) ((1 - p) * (H - 4)), 0x90f4d442);
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		Iterator<Server> iter = servers.iterator();
		if (columns < 1) return;

		// плавная прокрутка
		long time = System.currentTimeMillis();
		int scroll = this.scroll - lastScroll;
		if (lastScrolled != 0) {
			long diff = time - lastScrolled;
			if (diff < 0 || diff > SMOOTH_SCROLL_TIME) {
				lastScrolled = 0;
				lastScroll = 0;
				scroll = this.scroll;
			} else {
				float d = (float) diff / (float) SMOOTH_SCROLL_TIME;
				scroll = this.lastScroll + (int) (d * scroll);
			}
		}

		int c = 0;
		int row;
		int i = 0;


		a:
		for (row = 0; row < rows; row++) {
			int x = rightBorder + 4 - W;
			int y0 = row * 84 - scroll, y1 = y0 + 4, y2 = y0 + H;
			for (i = 0; i < columns; i++) {
				x += W;
				if (c++ == wantDrag) {
					drawRect(x - 2, y0 + 2, x + 66, y2 - 2, 0xc0c0c0c0);
					continue;
				}
				if (!iter.hasNext()) break a;
				Server server = iter.next();
				if (server == dragged)
					if (iter.hasNext()) server = iter.next();
					else break a;

				int color = 0xa0202020;
				if (dragged == null) {
					boolean hovered = mouseX >= x - 4 && mouseX < x + 68 && mouseY >= y0 && mouseY < y2;
					if (hovered != server.hovered) {
						server.hovered = hovered;
						server.hoverTime = time;
					}
					long ago = time - server.hoverTime;
					if (server.hoverTime > 0) {
						if (ago < 250) {
							if (hovered) color = Utils.gradient(0xa07fff3f, 0xa0202020, (float) (ago % 250) / 250f);
							else color = Utils.gradient(0xa0202020, 0xa07fff3f, (float) (ago % 250) / 250f);
						} else if (hovered) color = 0xa07fff3f;
						else server.hoverTime = 0;
					}
				}

				drawRect(x - 2, y0 + 2,
						x + 66, y2 - 2, color);
				server.prepareServerIcon();
				server.drawFavicon(x, y1);
				drawCenteredString(MC.FR, server.getTitle(), x + 32, y0 + 72, -1);
				if (!(iter.hasNext() || c == wantDrag)) break a;
			}
		}
		if (++i == columns) {
			i = 0;
			row++;
		}
		int x = i * W + rightBorder + 2;
		int y = row * H - scroll + 2;
		if (dragged == null && addBtnAnim == null && !addingServer) drawAddButton(mouseX, mouseY, x, y, time);
		drawHUD(mouseX, mouseY);
		if (dragged != null) dragged.drawFavicon(mouseX - W / 2, mouseY - H / 2);
		if (darkenerAnim != null) if (darkenerAnim.draw(time)) darkenerAnim = null;
		if (addBtnAnim != null) if (addBtnAnim.draw(time)) {
			addBtnAnim = null;
			openAdddingServerGui();
		}
		G.color(1, 1, 1, 1);
		if (addingServer) drawAddingServerGUI();
	}


	private void drawAddButton(int mouseX, int mouseY, int x, int y, long time) {

		boolean hovered = mouseX >= x && mouseX < x + W && mouseY >= y && mouseY < y + H;
		if (hovered != addBtnHovered) {
			addBtnHovered = hovered;
			addBtnHovertime = time;
		}
		int color = 0xa0202020;
		long ago = time - addBtnHovertime;
		if (addBtnHovertime > 0) {
			if (ago < 250) {
				if (hovered) color = Utils.gradient(0x90f4d442, 0xa0202020, (float) (ago % 250) / 250f);
				else color = Utils.gradient(0xa0202020, 0x90f4d442, (float) (ago % 250) / 250f);
			} else if (hovered) color = 0x90f4d442;
			else addBtnHovertime = 0;
		}

		drawRect(x, y, x + W - 4, y + H - 4, color);
		int dx = x + W / 2 - 2;
		int dy = y + H / 2 - 2;
		drawRect(dx - 24, dy - 3, dx + 24, dy + 3, 0xff20ff20);
		drawRect(dx - 3, dy - 24, dx + 3, dy + 24, 0xff20ff20);
	}


	private void updateAnims() {
		int x = servers.size() % columns * W + rightBorder - cw + W / 2;
		int y = servers.size() / columns * H - scroll - ch + H / 2;
		addButton.setOffsets(x, y);


	}

	private void drawHUD(int mouseX, int mouseY) {
		drawRect(width - 72, 0, width, height, 0x80505050);
	}


}
