package net.minecraft.client.multiplayer;

import net.minecraft.Log;
import net.minecraft.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiConnecting extends GuiScreen {

	private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
	private static final Logger logger = Logger.getInstance();
	private NetworkManager networkManager;
	private boolean cancel;
	private final GuiScreen previousGuiScreen;
	private volatile long connectionStart = System.currentTimeMillis(), connectionMs = -1;

	public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_) {
		this.mc = mcIn;
		this.previousGuiScreen = p_i1181_1_;
		ServerAddress serveraddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
		mcIn.loadWorld(null);
		mcIn.setServerData(p_i1181_3_);
		this.connect(serveraddress.getIP(), serveraddress.getPort());
	}

	public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port) {
		this.mc = mcIn;
		this.previousGuiScreen = p_i1182_1_;
		mcIn.loadWorld(null);
		this.connect(hostName, port);
	}

	private void connect(final String ip, final int port) {
		Log.CHAT.warn("Подключение к серверу " + ip + ":" + port);
		new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
			public void run() {

				InetAddress inetaddress = null;

				try {
					if (GuiConnecting.this.cancel) {
						return;
					}

					inetaddress = InetAddress.getByName(ip);
					GuiConnecting.this.networkManager = NetworkManager.func_181124_a(inetaddress, port, Settings.USE_NATIVE_CONNECTION.b());
					connectionMs = System.currentTimeMillis() - connectionStart;
					connectionStart = System.currentTimeMillis();
					GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
					GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
					GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
				} catch (UnknownHostException unknownhostexception) {
					if (GuiConnecting.this.cancel) return;
					GuiConnecting.logger.error("Не удалось подключиться к серверу.", unknownhostexception);
					GuiConnecting.this.mc.displayGuiScreen(
							new GuiDisconnected(previousGuiScreen, "connect.failed",
									new ChatComponentTranslation("disconnect.genericReason", "Невозможно распознать IP-адрес")));
				} catch (Exception exception) {
					if (GuiConnecting.this.cancel) return;

					String s = exception.toString();

					if (exception instanceof ConnectException) {
						ConnectException e = (ConnectException) exception;
						if (e.getMessage().toLowerCase().contains("connection refused")) s = "§cНа данном IP-адресе нет запущенного сервера Minecraft.";
						else if (e.getMessage().contains("timed out")) s = "§cСервер не отвечает на ваше подключение.";
						else s = "§cПроизошла неизвестная ошибка. Попробуйте переподключится или проверить логи.";
					} else {
						if (inetaddress != null) {
							String s1 = inetaddress.toString() + ":" + port;
							s = s.replaceAll(s1, "");
						}

					}

					GuiConnecting.logger.error("Couldn\'t connect to server", exception);

					GuiConnecting.this.mc.displayGuiScreen(
							new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", s)));
				}
			}
		}.start();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (this.networkManager != null) {
			if (this.networkManager.isChannelOpen()) {
				this.networkManager.processReceivedPackets();
			} else {
				this.networkManager.checkDisconnected();
			}
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, Lang.format("gui.cancel")));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.cancel = true;

			if (this.networkManager != null) {
				this.networkManager.closeChannel(new ChatComponentText("Aborted"));
			}

			this.mc.displayGuiScreen(this.previousGuiScreen);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		String s = this.networkManager == null ? Lang.format("connect.connecting") : Lang.format("connect.authorizing");
		this.drawCenteredString(this.fontRendererObj, s, this.width / 2, this.height / 2 - 50, 16777215);
		long connecting = System.currentTimeMillis() - connectionStart;
		String conn = "Соединение с сервером: §e" + (connectionMs == -1 ? connecting + " ms..." : connectionMs + " ms. §a\u2714");
		this.drawCenteredString(this.fontRendererObj, conn, this.width / 2, this.height / 2 - 30, 16777215);
		String login = "Выполнение входа: " + (connectionMs == -1 ? "§7-" : "§e" + connecting + " ms...");
		this.drawCenteredString(this.fontRendererObj, login, this.width / 2, this.height / 2 - 21, 16777215);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}