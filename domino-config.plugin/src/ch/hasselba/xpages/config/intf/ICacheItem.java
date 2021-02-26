package ch.hasselba.xpages.config.intf;

public interface ICacheItem {

	public String getKey();
	public void setKey( final String key);
	public String getValue();
	public void setValue( final String value);
}
