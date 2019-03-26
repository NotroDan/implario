package net.minecraft.client.resources;

public class Language implements Comparable<Language> {

	private final String languageCode;
	private final String region;
	private final String name;

	public Language(String languageCodeIn, String regionIn, String nameIn) {
		this.languageCode = languageCodeIn;
		this.region = regionIn;
		this.name = nameIn;
	}

	public String getLanguageCode() {
		return this.languageCode;
	}

	public String toString() {
		return this.name + " (" + this.region + ")";
	}

	public boolean equals(Object o) {
		return this == o || o instanceof Language && this.languageCode.equals(((Language) o).languageCode);
	}

	public int hashCode() {
		return this.languageCode.hashCode();
	}

	public int compareTo(Language l) {
		return this.languageCode.compareTo(l.languageCode);
	}

}
