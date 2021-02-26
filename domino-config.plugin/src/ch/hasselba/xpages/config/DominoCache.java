package ch.hasselba.xpages.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import ch.hasselba.xpages.config.intf.ICacheItem;
import ch.hasselba.xpages.utils.Utils;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.RichTextItem;

public class DominoCache {

	Logger log = LoggerFactory.getLogger(DominoCache.class);

	private int cacheMaxAge = 60;
	private String confDBServer = "Dev01";
	private String confDBPath = "CacheDB.nsf";

	private static DominoCache instance;
	private static Object syncObj = new Object();
	private static CachedConcurrentHashMap<String, ICacheItem> cache;

	private DominoCache() {
		// hidden constructor
		log.info("Creating new DominoCache object.");

		Session session = null;

		try {
			session = ExtLibUtil.getCurrentSessionAsSignerWithFullAccess();
			
			confDBPath = session.getEnvironmentString("$CONFIGDB_PATH");
			if( Utils.isEmptyString( confDBPath ) ) {
				throw new IllegalArgumentException("'$CONFIGDB_PATH' is not set.");
			}
			confDBServer = session.getEnvironmentString("$CONFIGDB_SERVER");
			if( Utils.isEmptyString( confDBPath ) ) {
				throw new IllegalArgumentException("'$CONFIGDB_SERVER' is not set.");
			}
			
			String tmpStr = session.getEnvironmentString("$CONFIGDB_MAXAGE");
			if( Utils.isEmptyString( tmpStr ) ) {
				throw new IllegalArgumentException("'$CONFIGDB_MAXAGE' is not set.");
			}
			
			cacheMaxAge = Integer.parseInt(tmpStr);
			
			log.info("Using Config DB '{}!!{}'.", confDBServer, confDBPath);
			log.info("Max Age of Cache: '{}'.", cacheMaxAge);

		} catch (Exception e) {
			log.error("Error loading Notes.ini.", e);
		} finally {
			Utils.recycle(session);
		}

		DominoCache.cache = new CachedConcurrentHashMap<String, ICacheItem>(cacheMaxAge);

	}

	public void destroy() {
		cache.destroy();
	}
	
	public void clear() {
		cache.clear();
	}

	private String getValueFromDocument(final String keyName, final String fieldName, Session session) {

		Database db = null;
		View v = null;
		Document doc = null;
		String value = null;
		Item item = null;
		RichTextItem rtItem = null;

		try {
			log.debug("Opening Config Database '{}!!{}'", confDBServer, confDBPath);
			db = session.getDatabase(confDBServer, confDBPath, false);
			if (db == null) {
				log.error("Database '{}!!{}' not found or unable to open.", confDBServer, confDBPath);
				throw new RuntimeException("ConfigDB not found.");
			}
			if (!db.isOpen())
				db.open();

			log.debug("Searching for document with key '{}'...", keyName);
			v = db.getView("luByKey");
			if (v == null) {
				log.error("Lookup View not found.");
				throw new RuntimeException("Lookup View not found.");
			}
			doc = v.getDocumentByKey(keyName);
			if (doc == null) {
				log.error("No config document found with key '{}'.", keyName);
				return null;
			}

			if (!doc.hasItem(fieldName)) {
				log.error("No item found in document with name '{}'.", fieldName);
				return null;
			}

			item = doc.getFirstItem(fieldName);

			switch (item.getType()) {
			case Item.TEXT:
				// String
				value = item.getText();
				break;
			case Item.NUMBERS:
				// Number
				value = String.valueOf(item.getValueInteger());
				break;
			case Item.RICHTEXT:
				// RichText
				rtItem = (RichTextItem) item;
				value = rtItem.getText();
				break;
			default:
				log.error("Undefined item type: Type {}", item.getType());
				throw new IllegalArgumentException("Undefined item type.");
			}
		} catch (NotesException e) {
			log.error("Error during access of cache", e);
		} finally {
			Utils.recycle(rtItem);
			Utils.recycle(item);
			Utils.recycle(doc);
			Utils.recycle(v);
			Utils.recycle(db);
		}
		return value;
	}

	public static DominoCache getInstance() {
		synchronized (syncObj) {
			if (instance == null) {
				instance = new DominoCache();
			}
			return instance;
		}
	}

	public ICacheItem getItemWithFullAccess(final String key, final String fieldName) {

		Session session = null;

		try {
			session = ExtLibUtil.getCurrentSessionAsSignerWithFullAccess();
			return getItem(key, fieldName, session);
		} catch (NotesException e) {
			log.error("NotesException", e);
		} finally {
			Utils.recycle(session);
		}

		return null;
	}

	public ICacheItem getItem(final String key, final String fieldName) {

		Session session = null;

		try {
			session = ExtLibUtil.getCurrentSession();
			return getItem(key, fieldName, session);

		} catch (NotesException e) {
			log.error("NotesException", e);
		} finally {
			Utils.recycle(session);
		}

		return null;
	}

	private ICacheItem getItem(final String key, final String fieldName, Session session) throws NotesException {

		if ( Utils.isEmptyString(key) )
			throw new IllegalArgumentException("key is null or empty.");
	
		if ( Utils.isEmptyString(fieldName) )
			throw new IllegalArgumentException("fieldName is null or empty.");

		if (session == null) {
			throw new IllegalArgumentException("sesssion is null.");
		}

		final String userName = session.getEffectiveUserName();
		final String fullKey = userName + "#" + key + "#" + fieldName;

		if (cache.containsKey(fullKey)) {
			log.info("Getting key '{}' from cache.", fullKey);
			return cache.get(fullKey);
		} else {

			log.info("Loading key '{}' into cache.", fullKey);

			final String value = getValueFromDocument(key, fieldName, session);
			log.info("Adding key '{}' into cache.", fullKey);

			final ICacheItem item = new CacheItem(fullKey, value);
			cache.put(fullKey, item);
			return item;
		}

	}
}
