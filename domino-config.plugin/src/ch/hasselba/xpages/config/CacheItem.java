package ch.hasselba.xpages.config;

import java.io.Serializable;

import ch.hasselba.xpages.config.intf.ICacheItem;

public class CacheItem implements ICacheItem , Serializable {

	private static final long serialVersionUID = -585949957310312295L;
	private String key;
	private String value;
	
	@SuppressWarnings("unused")
	private CacheItem() {
		// hidden constructor
	}
	public CacheItem( final String key, final String value) {
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
	public String getValue() {
		return value;
	}
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	
}
