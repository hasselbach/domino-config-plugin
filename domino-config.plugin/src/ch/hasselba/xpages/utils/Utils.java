package ch.hasselba.xpages.utils;

import lotus.domino.Base;
import lotus.domino.NotesException;

public class Utils {

	private Utils() {
		// hidden constructor
	}
	
	public static void recycle(Base obj) {
		if (obj == null)
			return;
		try {
			obj.recycle();
		} catch (NotesException e) {
			// DO NOTHING
		}
	}
	
	public static boolean isEmptyString( String toTest ) {
		if( toTest == null )
			return true;
		
		if( "".equals( toTest ) )
			return true;
		
		return false;
	}
}
