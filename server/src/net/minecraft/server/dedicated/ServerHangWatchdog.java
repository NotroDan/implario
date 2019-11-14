package net.minecraft.server.dedicated;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.logging.Log;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServerHangWatchdog implements Runnable {

	private static final Log LOGGER = Log.MAIN;
	private final DedicatedServer server;
	private final long maxTickTime;

	public ServerHangWatchdog(DedicatedServer server) {
		this.server = server;
		this.maxTickTime = server.getMaxTickTime();
	}

	public void run() {
		while (this.server.isServerRunning()) {
			long i = this.server.getCurrentTime();
			long j = MinecraftServer.getCurrentTimeMillis();
			long k = j - i;

			if (k > this.maxTickTime) {
				LOGGER.error("Один тик занял " + String.format("%.2f", (float) k / 1000.0F) + " сек. (Должен занимать не более 0.05)");
				LOGGER.error("С этого момента считается, что сервер упал. Принудительное выключение...");
				ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
				ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
				StringBuilder stringbuilder = new StringBuilder();
				Error error = new Error();

				for (ThreadInfo threadinfo : athreadinfo) {
					if (threadinfo.getThreadId() == this.server.getServerThread().getId()) error.setStackTrace(threadinfo.getStackTrace());
					stringbuilder.append(threadinfo);
					stringbuilder.append("\n");
				}

				CrashReport crashreport = new CrashReport("Watching Server", error);
				this.server.addServerInfoToCrashReport(crashreport);
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread Dump");
				crashreportcategory.addCrashSection("Threads", stringbuilder);
				File file1 = new File(new File(this.server.getDataDirectory(), "crash-reports"),
						new SimpleDateFormat("yyyy.MMM.dd HH-mm-ss").format(new Date()) + " Too long tick.txt");

				if (crashreport.saveToFile(file1)) LOGGER.error("Отчёт об аварии (краш-репорт) сохранён: " + file1.getAbsolutePath());
				else LOGGER.error("Не удалось сохранить отчёт об аварии.");

				this.scheduleHalt();
			}

			try {
				Thread.sleep(i + this.maxTickTime - j);
			} catch (InterruptedException ignored) {}
		}
	}

	private void scheduleHalt() {
		try {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					Runtime.getRuntime().halt(1);
				}
			}, 10000L);
			System.exit(1);
		} catch (Throwable var2) {
			Runtime.getRuntime().halt(1);
		}
	}

}
