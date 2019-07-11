import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import lombok.Getter;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_T;

@Getter
public class Sholimp {
	
	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	private final String[] ads;	
	private final Robot robot;
	private final LogReader logReader;
	private final Queue<String> commands = new ConcurrentLinkedQueue<>();
	private final Queue<String> inviteQueue = new ConcurrentLinkedQueue<>();
	private BufferedWriter configurator;
	private Set<String> invited = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private String warp;
	private volatile boolean waiting;
	private boolean advertising;
	
	@SneakyThrows
	public Sholimp() {
		String file = "output-client.log";
		if (readConfig()) System.exit(0);
		System.out.println("Working with /warp " + warp);
		robot = new Robot();
		logReader = new LogReader(file, this::handleChat);
		logReader.start();
		String[] ads1;
		try {
			FileInputStream inputStream = new FileInputStream("sholimp_ads.txt");
			BufferedReader adsReader = new BufferedReader(new InputStreamReader(inputStream, "cp1251"));
			int i;
			List<String> ads = new ArrayList<>();
			while ((i = adsReader.read()) != -1) ads.add((char) i + adsReader.readLine());
			ads1 = ads.toArray(new String[0]);
		} catch (FileNotFoundException ex) {
			System.out.println("Advertisement file wasn't found, creating a new one.");
			new File("sholimp_ads.txt").createNewFile();
			ads1 = new String[0];
		}
		this.ads = ads1;
		new Thread(this::processCommands).start();
		new Thread(this::advertise).start();
	}

	
	@SuppressWarnings("serial")
	public static class WindowNotFoundException extends Exception {
		public WindowNotFoundException(String className, String windowName) {
			super(String.format("Window null for className: %s; windowName: %s",
					className, windowName));
		}
	}
	
	@SuppressWarnings("serial")
	public static class GetWindowRectException extends Exception {
		public GetWindowRectException(String windowName) {
			super("Window Rect not found for " + windowName);
		}
	}
	
	private void advertise() {
		try {
			while (logReader.isRunning()) {
				if (!advertising) {
					Thread.sleep(2000);
					continue;
				}
				if (ads.length != 0) commands.offer(ads[(int) (ads.length * Math.random())]);
				commands.offer("/list");
				Thread.sleep(120_000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processCommands() {
		try {
			configurator = new BufferedWriter(new FileWriter("sholimp.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (logReader.isRunning()) {
			try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				try {
					configurator.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
			String cmd = commands.poll();
			if (cmd == null) {
				if (waiting) {
					System.out.println("Server is lagging, pausing...");
					continue;
				}
				String player = inviteQueue.poll();
				if (player == null) continue;
				cmd = "/warp invite " + player + " " + warp;
				waiting = true;
				try {
					invited.add(player);
					configurator.write("|" + player);
					configurator.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			chat(cmd);
		}
	}
	
	public static void main(String[] args) {
		new Sholimp();
	}
	
	private void handleChat(String line) {
		String pattern = "[CLIENT] [INFO] [CHAT] ";
		int i = line.indexOf(pattern);
		if (i == -1) return;
		String msg = line.substring(i + pattern.length());
//		if (msg.replaceAll("Сейчас \\d* игроков на сервере\\.", "").length() != 0) return;
		System.out.println(line.substring(0, i) + msg);
		if (msg.startsWith("You have invited") ||
				msg.contains(" is already invited to this warp.") ||
				msg.contains(" of course he is invited") ||
				msg.startsWith("Request to teleport")) waiting = false;
		if (msg.startsWith("/region <redefine")) waiting = true;
		if (msg.startsWith("Sends a private")) advertising = !advertising;
		if (!msg.startsWith("default: ") &&
				!msg.startsWith("Premium: ")) return;
		String[] players = msg.substring(9).split(", ");
		List<String> list = new ArrayList<>(Arrays.asList(players));
		for (String player : list) {
			player = player.replace("[Отошел]", "");
			if (invited.contains(player)) continue;
			System.out.println("Detected " + player + ".");
			inviteAsync(player);
		}
		
		
//		System.out.println(players[0] + ", " + players[1]);
		
	}
	
	private void inviteAsync(String player) {
		inviteQueue.offer(player);
	}
	
	@SneakyThrows
	public boolean readConfig() {
		try {
			BufferedReader r = new BufferedReader(new FileReader("sholimp.txt"));
			warp = r.readLine();
			invited.addAll(Arrays.asList(r.readLine().split("\\|")));
			r.close();
			return false;
		} catch (IOException ex) {
			new File("sholimp.txt").createNewFile();
			System.out.println("NASTROY CONFIG MOO_DEE_LA");
			return true;
		}
	}
	
	@Getter
	public static class LogReader implements AutoCloseable {
		
		private final File file;
		private final BufferedReader reader;
		private final Consumer<String> callback;
		private Thread thread;
		private volatile boolean running;
		
		@SneakyThrows
		public LogReader(String file, Consumer<String> callback) {
			this.file = new File(file);
			this.callback = callback;
			this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-1251"));
			long length = this.file.length();
			reader.skip(length);
		}
		
		public void start() {
			thread = new Thread(this::loop);
			running = true;
			thread.start();
		}
		
		@SneakyThrows
		private void loop() {
			while (running) {
				int c = reader.read();
				if (c == -1) {
					Thread.sleep(300);
					continue;
				}
				String line = (char) c + reader.readLine();
				callback.accept(line);
			}
		}
		
		@Override
		public void close() throws Exception {
			running = false;
			reader.close();
		}
	}
}

