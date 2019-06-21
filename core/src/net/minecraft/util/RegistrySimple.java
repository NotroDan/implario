package net.minecraft.util;

import com.google.common.collect.Maps;
import net.minecraft.Logger;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RegistrySimple<K, V> implements IRegistry<K, V> {

	private static final Logger logger = Logger.getInstance();
	protected final Map<K, V> registryObjects = this.createUnderlyingMap();

	protected Map<K, V> createUnderlyingMap() {
		return Maps.newHashMap();
	}

	public Map<K, V> getRegistryObjects() {
		return registryObjects;
	}

	public V getObject(K name) {
		return this.registryObjects.get(name);
	}

	/**
	 * Register an object on this registry.
	 */
	public void putObject(K key, V value) {
		Validate.notNull(key);
		Validate.notNull(value);

		if (this.registryObjects.containsKey(key)) {
			logger.debug("Adding duplicate key \'" + key + "\' to registry");
		}

		put0(key, value);

	}

	protected void put0(K key, V value) {
		this.registryObjects.put(key, value);
	}

	public Set<K> getKeys() {
		return Collections.unmodifiableSet(this.registryObjects.keySet());
	}

	public V remove(K key) {
		return registryObjects.remove(key);
	}

	/**
	 * Does this registry contain an entry for the given key?
	 */
	public boolean containsKey(K p_148741_1_) {
		return this.registryObjects.containsKey(p_148741_1_);
	}

	public Iterator<V> iterator() {
		return this.registryObjects.values().iterator();
	}

}
