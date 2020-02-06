package net.minecraft.logging;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Profiler implements IProfiler {

	private final List<String> sectionList = new ArrayList<>();
	private final List<Long> timestampList = new ArrayList<>();
	private final Map<String, Long> profilingMap = new ConcurrentHashMap<>();

	@Getter @Setter
	private boolean enabled;

	/**
	 * Current profiling section
	 */
	private String profilingSection = "";

	@Override
	public void clearProfiling() {
		this.profilingMap.clear();
		this.profilingSection = "";
		this.sectionList.clear();
	}

	@Override
	public void startSection(String name) {
		if (!this.enabled) return;

		if (this.profilingSection.length() > 0) this.profilingSection += ".";
		this.profilingSection += name;
//		System.out.println(Thread.currentThread().getName() + " > " + Arrays.toString(sectionList.toArray(new String[0])) + " ДОБАВЛЕНА "  + profilingSection);

		this.sectionList.add(this.profilingSection);
		this.timestampList.add(System.nanoTime());
	}

	@Override
	public void endSection() {
		if (!this.enabled) return;

		long endTime = System.nanoTime();
		long startTime = this.timestampList.remove(this.timestampList.size() - 1);
//		System.out.println(Thread.currentThread().getName() + " < " + Arrays.toString(sectionList.toArray(new String[0])) + " УДАЛЕНА " + sectionList.get(sectionList.size() - 1));
		this.sectionList.remove(this.sectionList.size() - 1);
		long time = endTime - startTime;

		long previousTime = profilingMap.getOrDefault(profilingSection, 0L);
		profilingMap.put(profilingSection, previousTime + time);

		if (time > 100_000_000L) {
			Log.MAIN.warn("Задача '" + this.profilingSection + "' заняла " + (double) time / 1000000.0D + " мс");
		}

		this.profilingSection = this.sectionList.isEmpty() ? "" : this.sectionList.get(this.sectionList.size() - 1);
	}

	@Override
	public List<ProfilerResult> getProfilingData(String task) {
		if (!this.enabled) return null;

		// Общее время, потраченное на все существующие задачи
		long rootTime = this.profilingMap.getOrDefault("root", 0L);

		// Время, потраченное на запрошенную задачу
		long taskTime = this.profilingMap.getOrDefault(task, -1L);

		List<ProfilerResult> list = new ArrayList<>();

		// Префикс, по которому производится поиск подзадач
		String childPrefix = task.length() > 0 ? task + "." : "";

		// Время, за которое выполнились задокументированные дочерние задачи
		long childrenTime = 0L;
		for (String child : this.profilingMap.keySet()) {
			if (child.length() <= childPrefix.length()) continue;
			if (!child.startsWith(childPrefix)) continue;
			if (child.indexOf(".", childPrefix.length() + 1) < 0)
				childrenTime += this.profilingMap.get(child);
		}

		// Время, которое проведено внутри этой задачи без прыжков в подзадачи
		long unspecifiedTime = taskTime - childrenTime;

		if (childrenTime < taskTime) childrenTime = taskTime;
		if (rootTime < childrenTime) rootTime = childrenTime;

		for (String child : this.profilingMap.keySet()) {
			if (child.length() > childPrefix.length() && child.startsWith(childPrefix) && child.indexOf(".", childPrefix.length() + 1) < 0) {
				long childTime = this.profilingMap.get(child);
				double localPercentage = (double) childTime * 100.0D / (double) childrenTime;
				double globalPercentage = (double) childTime * 100.0D / (double) rootTime;
				String localName = child.substring(childPrefix.length());
				list.add(new ProfilerResult(localName, localPercentage, globalPercentage));
			}
		}

		this.profilingMap.replaceAll((s, v) -> v * 999L / 1000L);

		if (unspecifiedTime > 0) {
			double localPercentage = unspecifiedTime / (double) childrenTime * 100.0;
			double globalPercentage = unspecifiedTime / (double) rootTime * 100.0;
			list.add(new ProfilerResult("unspecified", localPercentage, globalPercentage));
		}

		Collections.sort(list);
		list.add(0, new ProfilerResult(childPrefix, 100.0D, (double) childrenTime * 100.0D / (double) rootTime));
		return list;
	}

	@Override
	public void endStartSection(String name) {
		this.endSection();
		this.startSection(name);
	}

	@Override
	public String getNameOfLastSection() {
		return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
	}

}