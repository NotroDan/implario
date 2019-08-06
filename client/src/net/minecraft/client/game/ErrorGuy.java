package net.minecraft.client.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.Settings;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Bootstrap;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.minecraft.logging.Log.MAIN;

public class ErrorGuy {

	private final Minecraft mc;

	public ErrorGuy(Minecraft mc) {
		this.mc = mc;
	}


	/**
	 * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
	 */
	public CrashReport addGraphicsAndWorldToCrashReport(CrashReport crash) {
		crash.getCategory().addCrashSectionCallable("Версия игры", () -> "Implario Client (1.8.8)");
		crash.getCategory().addCrashSectionCallable("LWJGL", Sys::getVersion);
		crash.getCategory().addCrashSectionCallable("OpenGL", () -> GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR));
		crash.getCategory().addCrashSectionCallable("GL Caps", OpenGlHelper::getLogText);
		crash.getCategory().addCrashSectionCallable("Использование VBOs", () -> Settings.USE_VBO.b() ? "Yes" : "No");
		crash.getCategory().addCrashSectionCallable("Тип", () -> "Client (map_client.txt)");
		crash.getCategory().addCrashSectionCallable("Пакеты ресурсов", () -> {
			StringBuilder stringbuilder = new StringBuilder();

			for (Object s : Settings.resourcePacks) {
				if (stringbuilder.length() > 0) stringbuilder.append(", ");

				stringbuilder.append(s);

				if (Settings.incompatibleResourcePacks.contains(String.valueOf(s)))
					stringbuilder.append(" (несовместимый)");
			}

			return stringbuilder.toString();
		});
		crash.getCategory().addCrashSectionCallable("Язык",
				() -> mc.mcLanguageManager.getCurrentLanguage().toString());
		crash.getCategory().addCrashSectionCallable("Позиция профайлера",
				() -> mc.getProfiler().isEnabled() ? mc.getProfiler().getNameOfLastSection() : "N/A (профайлер отключен)");
		crash.getCategory().addCrashSectionCallable("CPU", OpenGlHelper::getCPU);

		if (mc.theWorld != null) {
			mc.theWorld.addWorldInfoToCrashReport(crash);
		}

		return crash;
	}

	/**
	 * Wrapper around displayCrashReportInternal
	 */
	public void displayCrashReport(CrashReport crashReportIn) {
		File file1 = new File(Minecraft.getMinecraft().mcDataDir, "gamedata/logs/crash-reports");
		File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
		Bootstrap.print(crashReportIn.getCompleteReport());

		if (crashReportIn.getFile() != null) {
			Bootstrap.print("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
			System.exit(-1);
		}
		if (crashReportIn.saveToFile(file2)) {
			Bootstrap.print("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
			System.exit(-1);
		}
		Bootstrap.print("#@?@# Game crashed! Crash report could not be saved. #@?@#");
		System.exit(-2);
	}

	/**
	 * Checks for an OpenGL error. If there is one, prints the error ID and error string.
	 */
	public void checkGLError(String message) {
		int i = GL11.glGetError();
		if (i == 0) return;

		String s = GLU.gluErrorString(i);
		MAIN.error("########## ОШИБКА OpenGL ##########");
		MAIN.error("Процесс: " + message);
		MAIN.error(i + ": " + s);
	}

}
