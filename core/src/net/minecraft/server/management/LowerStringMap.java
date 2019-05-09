package net.minecraft.server.management;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LowerStringMap<V> implements Map<String, V> {

	private final Map<String, V> internalMap = Maps.newLinkedHashMap();

	public int size() {
		return this.internalMap.size();
	}

	public boolean isEmpty() {
		return this.internalMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return this.internalMap.containsKey(key.toString().toLowerCase());
	}

	public boolean containsValue(Object value) {
		return this.internalMap.containsKey(value);
	}

	public V get(Object key) {
		return this.internalMap.get(key.toString().toLowerCase());
	}

	public V put(String key, V value) {
		return this.internalMap.put(key.toLowerCase(), value);
	}

	public V remove(Object key) {
		return this.internalMap.remove(key.toString().toLowerCase());
	}

	public void putAll(Map<? extends String, ? extends V> map) {
		for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		this.internalMap.clear();
	}

	public Set<String> keySet() {
		return this.internalMap.keySet();
	}

	public Collection<V> values() {
		return this.internalMap.values();
	}

	public Set<Entry<String, V>> entrySet() {
		return this.internalMap.entrySet();
	}

}
