package net.minecraft.crash;

import net.minecraft.Logger;
import net.minecraft.util.ReportedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrashReport {

	private static final Logger logger = Logger.getInstance();

	private final String description;

	private final Throwable cause;

	private final CrashReportCategory theReportCategory = new CrashReportCategory("System Details");

	private final List<CrashReportCategory> crashReportSections = new ArrayList<>();

	private File crashReportFile;
	private boolean field_85059_f = true;
	private StackTraceElement[] stacktrace = new StackTraceElement[0];
	private boolean reported = false;

	public CrashReport(String descriptionIn, Throwable causeThrowable) {
		this.description = descriptionIn;
		this.cause = causeThrowable;
		this.populateEnvironment();
	}

	private static String getWittyComment() {
		return "У тебя всё получится, я верю в тебя!";
	}

	public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
		if (causeIn instanceof ReportedException) return ((ReportedException) causeIn).getCrashReport();
		return new CrashReport(descriptionIn, causeIn);
	}

	private void populateEnvironment() {
		this.theReportCategory.addCrashSectionCallable("Minecraft Version", () -> "1.8.8");
		this.theReportCategory.addCrashSectionCallable("Operating System", () ->
				System.getProperty("os.name") + " (" + System.getProperty("os.arch") +
						") version " + System.getProperty("os.version"));
		this.theReportCategory.addCrashSectionCallable("Java Version", () ->
				System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
		this.theReportCategory.addCrashSectionCallable("Java VM Version", () ->
				System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), "
						+ System.getProperty("java.vm.vendor"));
		this.theReportCategory.addCrashSectionCallable("Memory", () -> {
			Runtime runtime = Runtime.getRuntime();
			long i = runtime.maxMemory();
			long j = runtime.totalMemory();
			long k = runtime.freeMemory();
			long l = i / 1024L / 1024L;
			long i1 = j / 1024L / 1024L;
			long j1 = k / 1024L / 1024L;
			return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
		});
		this.theReportCategory.addCrashSectionCallable("JVM Flags", () -> {
			RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
			List<String> list = runtimemxbean.getInputArguments();
			int i = 0;
			StringBuilder stringbuilder = new StringBuilder();

			for (String arg : list)
				if (arg.startsWith("-X")) {
					if (i++ > 0)
						stringbuilder.append(" ");
					stringbuilder.append(arg);
				}
			return String.format("%d total; %s", i, stringbuilder.toString());
		});

	}

	public String getDescription() {
		return this.description;
	}

	public Throwable getCrashCause() {
		return this.cause;
	}

	public void getSectionsInStringBuilder(StringBuilder builder) {
		if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0)
			this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);

		if (this.stacktrace != null && this.stacktrace.length > 0) {
			builder.append("-- Head --\n");
			builder.append("Stacktrace:\n");

			for (StackTraceElement stacktraceelement : this.stacktrace) {
				builder.append("\t").append("at ").append(stacktraceelement.toString());
				builder.append("\n");
			}

			builder.append("\n");
		}

		for (Object crashreportcategory : this.crashReportSections) {
			((CrashReportCategory) crashreportcategory).appendToStringBuilder(builder);
			builder.append("\n\n");
		}

		this.theReportCategory.appendToStringBuilder(builder);
	}

	public String getCauseStackTraceOrString() {
		StringWriter stringwriter = null;
		PrintWriter printwriter = null;
		Throwable object = this.cause;

		if (object.getMessage() == null) {
			if (object instanceof NullPointerException)
				object = new NullPointerException(this.description);
			else if (object instanceof StackOverflowError)
				object = new StackOverflowError(this.description);
			else if (object instanceof OutOfMemoryError)
				object = new OutOfMemoryError(this.description);

			object.setStackTrace(this.cause.getStackTrace());
		}

		try {
			stringwriter = new StringWriter();
			printwriter = new PrintWriter(stringwriter);
			object.printStackTrace(printwriter);
			return stringwriter.toString();
		} finally {
			IOUtils.closeQuietly(stringwriter);
			IOUtils.closeQuietly(printwriter);
		}
	}

	public String getCompleteReport() {
		if (!this.reported) {
			this.reported = true;
		}

		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("---- Отчёт об ошибке ----\n");
		stringbuilder.append("// ");
		stringbuilder.append(getWittyComment());
		stringbuilder.append("\n\n");
		stringbuilder.append("Время: ");
		stringbuilder.append(new SimpleDateFormat().format(new Date()));
		stringbuilder.append("\n");
		stringbuilder.append("Описание: ");
		stringbuilder.append(this.description);
		stringbuilder.append("\n\n");
		stringbuilder.append(this.getCauseStackTraceOrString());
		stringbuilder.append("\n\nДетальная информация об ошибке (Покажите знакомому программисту):\n");

		for (int i = 0; i < 87; ++i)
			stringbuilder.append("-");

		stringbuilder.append("\n\n");
		this.getSectionsInStringBuilder(stringbuilder);
		return stringbuilder.toString();
	}

	public File getFile() {
		return this.crashReportFile;
	}

	public boolean saveToFile(File toFile) {
		if (this.crashReportFile != null) return false;
		if (toFile.getParentFile() != null) toFile.getParentFile().mkdirs();

		try {
			FileWriter filewriter = new FileWriter(toFile);
			filewriter.write(this.getCompleteReport());
			filewriter.close();
			this.crashReportFile = toFile;
			return true;
		} catch (Throwable throwable) {
			logger.error("Could not save crash report to " + toFile, throwable);
			return false;
		}
	}

	public CrashReportCategory getCategory() {
		return this.theReportCategory;
	}

	public CrashReportCategory makeCategory(String name) {
		return this.makeCategoryDepth(name, 1);
	}

	public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
		CrashReportCategory crashreportcategory = new CrashReportCategory(categoryName);

		if (this.field_85059_f) {
			int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
			StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
			StackTraceElement stacktraceelement = null;
			StackTraceElement stacktraceelement1 = null;
			int j = astacktraceelement.length - i;

			if (j < 0)
				System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");

			if (0 <= j && j < astacktraceelement.length) {
				stacktraceelement = astacktraceelement[j];
				if (astacktraceelement.length + 1 - i < astacktraceelement.length)
					stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
			}

			this.field_85059_f = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);

			if (i > 0 && !this.crashReportSections.isEmpty()) {
				CrashReportCategory crashreportcategory1 = this.crashReportSections.get(this.crashReportSections.size() - 1);
				crashreportcategory1.trimStackTraceEntriesFromBottom(i);
			} else if (astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
				this.stacktrace = new StackTraceElement[j];
				System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
			} else this.field_85059_f = false;
		}

		this.crashReportSections.add(crashreportcategory);
		return crashreportcategory;
	}

}
