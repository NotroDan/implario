package net.minecraft.network.protocol;

public class Protocols {

	/**
	 * При подключении на сервер клиент первым отправляет какие-то данные.
	 * Эти данные называются Handshake (рукопожатие) и содержат IP-адрес клиента,
	 * версию протокола клиента (47 для 1.8.8), и ID состояния, в которое клиент хочет перейти.
	 */
	public static Protocol HANDSHAKING = new ProtocolHandshaking();
	public static Protocol PLAY_IMPLARIO = new ProtocolImplario();
	public static Protocol PLAY_47 = new Protocol47();
	public static Protocol LOGIN = new ProtocolLogin();
	public static Protocol STATUS = new ProtocolStatus();

}
