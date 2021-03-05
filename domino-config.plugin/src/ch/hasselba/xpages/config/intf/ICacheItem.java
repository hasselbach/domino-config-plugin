package ch.hasselba.xpages.config.intf;

import java.util.Set;

public interface ICacheItem {

	public String getKey();
	public void setKey( final String key);
	public Object getValue();
	public void setValue( final Object value);
	public String getValueAsString();
	public void setValueAsString(final String value);
	public Set<?> getMultiValue();
	public void setMultiValue(final Set<?> value);
	
}
