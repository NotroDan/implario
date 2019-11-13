package net.minecraft.server;

import com.google.common.collect.Lists;
import net.minecraft.LogManager;
import net.minecraft.Logger;
import net.minecraft.logging.Log;
import net.minecraft.logging.ProfilerResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Profiler1 {

//	public static Profiler in = new Profiler();

	protected static final Log logger = Log.MAIN;
	protected final List<String> sectionList = Collections.synchronizedList(new ArrayList<>());
	protected final List<Long> timestampList = Collections.synchronizedList(new ArrayList<>());

	public boolean profilingEnabled;

	protected String profilingSection = "";
	protected final Map<String, Long> profilingMap = new ConcurrentHashMap<>();

	public Profiler() {}

	public void clearProfiling() {
		synchronized (this) {
			this.profilingMap.clear();
			this.profilingSection = "";
			this.sectionList.clear();
		}
	}

	public void startSection(String name) {
		synchronized (this) {
			if (!this.profilingEnabled) return;

			if (this.profilingSection.length() > 0)
				this.profilingSection = this.profilingSection + ".";

			this.profilingSection = this.profilingSection + name;
			this.sectionList.add(this.profilingSection);
			this.timestampList.add(System.nanoTime());
		}
	}

	public void endSection() {
		synchronized (this) {
			if (this.profilingEnabled) {
				long i = System.nanoTime();
				long j = this.timestampList.remove(this.timestampList.size() - 1);
				this.sectionList.remove(this.sectionList.size() - 1);
				long k = i - j;

				if (this.profilingMap.containsKey(this.profilingSection)) {
					this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + k);
				} else {
					this.profilingMap.put(this.profilingSection, k);
				}

				if (k > 100000000L) {
					logger.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double) k / 1000000.0D + " ms");
				}

				this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
			}
		}
	}

	public List<Profiler.Result> getProfilingData(String str) {
		synchronized (this) {
			if (!this.profilingEnabled) {
				return null;
			}
			long i = this.profilingMap.containsKey("root") ? (Long) this.profilingMap.get("root") : 0L;
			long j = this.profilingMap.containsKey(str) ? (Long) this.profilingMap.get(str) : -1L;
			List<Profiler.Result> list = new ArrayList<>();

			if (str.length() > 0) str += ".";

			long k = 0L;

			for (String s : this.profilingMap.keySet()) {
				if (s.length() > str.length() && s.startsWith(str) && s.indexOf(".", str.length() + 1) < 0) {
					k += this.profilingMap.get(s);
				}
			}

			float f = (float) k;

			if (k < j) k = j;
			if (i < k) i = k;

			for (String s1 : this.profilingMap.keySet()) {
				if (s1.length() > str.length() && s1.startsWith(str) && s1.indexOf(".", str.length() + 1) < 0) {
					long l = this.profilingMap.get(s1);
					double d0 = (double) l * 100.0D / (double) k;
					double d1 = (double) l * 100.0D / (double) i;
					String s2 = s1.substring(str.length());
					list.add(new Profiler.Result(s2, d0, d1));
				}
			}

			for (String s3 : this.profilingMap.keySet()) {
				this.profilingMap.put(s3, this.profilingMap.get(s3) * 999L / 1000L);
			}

			if ((float) k > f) {
				list.add(new Profiler.Result("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
			}

			Collections.sort(list);
			list.add(0, new Profiler.Result(str, 100.0D, (double) k * 100.0D / (double) i));
			return list;
		}
	}

	/**
	 * End current section and start a new section
	 */
	public void endStartSection(String name) {
		this.endSection();
		this.startSection(name);
	}

	public String getNameOfLastSection() {
		return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
	}

	public static final class Result implements Comparable<Result> {

		public double a;
		public double b;
		public String s;

		public Result(String s, double a, double b) {
			this.s = s;
			this.a = a;
			this.b = b;
		}

		public int hash() {
			return (this.s.hashCode() & 11184810) + 4473924;
		}

		public int compareTo(Result r) {
			return r.a < this.a ? -1 : r.a > this.a ? 1 : r.s.compareTo(this.s);
		}

	}

}
