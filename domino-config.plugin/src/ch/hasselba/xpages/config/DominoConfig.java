package ch.hasselba.xpages.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import ch.hasselba.xpages.config.intf.ICacheItem;
import ch.hasselba.xpages.utils.Utils;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class DominoConfig {
	Logger log = LoggerFactory.getLogger(DominoConfig.class);

	private static DominoConfig instance;
	private static Object syncObj = new Object();
	private static final String DELIMITTER = ".";
	private static final String WILDCARD = "*";
	private String serverName;

	private DominoConfig() {
		// hidden constructor
	}

	public static DominoConfig getInstance() {
		synchronized (syncObj) {
			if (instance == null) {
				instance = new DominoConfig();
			}
			return instance;
		}
	}

	public String getCurrentServer() {
		Session session = null;
		Name nName = null;

		if (Utils.isEmptyString(serverName)) {
			try {
				synchronized (syncObj) {
					session = ExtLibUtil.getCurrentSession();
					nName = session.createName(session.getServerName());
					serverName = nName.getCommon().toLowerCase();
				}

			} catch (NotesException e) {
				log.error("Notes Exception", e);
			} finally {
				Utils.recycle(nName);
			}
		}

		return serverName;
	}

	public String getCurrentDevice() {
		String device = "*";
		return device.toLowerCase();
	}

	public String getCurrentUser() {
		Session session = null;
		Name nName = null;
		String userName = null;
		try {
			session = ExtLibUtil.getCurrentSession();
			nName = session.createName(session.getEffectiveUserName());
			userName = nName.getCommon().toLowerCase();
		} catch (NotesException e) {
			log.error("Notes Exception", e);
		} finally {
			Utils.recycle(nName);
		}
		return userName;
	}

	public ICacheItem getItem(final String key, final String fieldName) {

		String server = getCurrentServer();
		String device = getCurrentDevice();
		String user = getCurrentUser();
		ICacheItem result = null;

		DominoCache cache = DominoCache.getInstance();

		// get full personalized item
		result = cache.getItem(server + DELIMITTER + device + DELIMITTER + user + DELIMITTER + key, fieldName);
		if (result != null && result.getValue() != null) {
			return result;
		}

		result = cache.getItem(server + DELIMITTER + device + DELIMITTER + WILDCARD + DELIMITTER + key, fieldName);
		if (result != null && result.getValue() != null) {
			return result;
		}
		result = cache.getItem(server + DELIMITTER + WILDCARD + DELIMITTER + user + DELIMITTER + key, fieldName);
		if (result != null && result.getValue() != null) {
			return result;
		}

		// get full wildcard
		result = cache.getItem(WILDCARD + DELIMITTER + WILDCARD + DELIMITTER + WILDCARD + DELIMITTER + key, fieldName);

		return result;

	}
	
	public Field loadField( final String key ) {
		
		Field f = new Field();
		ICacheItem result = null;
		
		// get field Name
		result = getItem( key, "FieldName");
		f.setName( result.getValue() );
		
		
		// is Required
		result = getItem( key, "isRequired");
		if( "1".equals(result.getValue())) {
			f.setRequired(true);
		}
		
		result = getItem( key, "FieldDefaultValue");
		f.setDefaultValue( result.getValue() );
		
		return f;
	}

}
