package ch.hasselba.xpages.config;

import java.io.Serializable;
import java.util.Set;

import ch.hasselba.xpages.config.intf.ICacheItem;

public class CacheItem implements ICacheItem, Serializable {

	private static final long serialVersionUID = -585949957310312295L;
	private String key;
	private transient Object value;

	@SuppressWarnings("unused")
	private CacheItem() {
		// hidden constructor
	}

	public CacheItem(final String key, final Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(final String key) {
		this.key = key;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String getValueAsString() {
		return null;
	}

	@Override
	public void setValueAsString(final String value) {
		this.value = value;
	}

	@Override
	public Set<?> getMultiValue() {

		if (value instanceof Set)
			return (Set<?>) value;

		return null;
	}

	@Override
	public void setMultiValue(final Set<?> value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
