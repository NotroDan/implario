package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {

	protected final ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
	protected final Map<V, K> inverseObjectRegistry;

	public RegistryNamespaced() {
		this.inverseObjectRegistry = ((BiMap) this.registryObjects).inverse();
	}

	public void register(int id, K key, V value) {
		this.underlyingIntegerMap.put(value, id);
		this.putObject(key, value);
	}

	protected Map<K, V> createUnderlyingMap() {
		return HashBiMap.create();
	}

	public V getObject(K name) {
		return super.getObject(name);
	}

	@Override
	protected void put0(K key, V value) {
		((BiMap<K, V>) registryObjects).forcePut(key, value);
	}

	/**
	 * Gets the name we use to identify the given object.
	 */
	public K getNameForObject(V p_177774_1_) {
		return this.inverseObjectRegistry.get(p_177774_1_);
	}

	/**
	 * Does this registry contain an entry for the given key?
	 */
	public boolean containsKey(K p_148741_1_) {
		return super.containsKey(p_148741_1_);
	}

	/**
	 * Gets the integer ID we use to identify the given object.
	 */
	public int getIDForObject(V p_148757_1_) {
		return this.underlyingIntegerMap.get(p_148757_1_);
	}

	@Override
	public V remove(K key) {
		V v = super.remove(key);
		underlyingIntegerMap.remove(v);
		inverseObjectRegistry.remove(v);
		return v;
	}

	/**
	 * Gets the object identified by the given ID.
	 */
	public V getObjectById(int id) {
		return (V) this.underlyingIntegerMap.getByValue(id);
	}

	public Iterator<V> iterator() {
		return this.underlyingIntegerMap.iterator();
	}

}
