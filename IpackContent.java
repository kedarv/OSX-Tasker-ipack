package %PACKAGE_NAME%;

import android.os.Bundle;
import android.content.res.Resources;

// This file is not used by the automatic pack script, it's for example only

public class IpackContent {

	public final static String 	LABEL = %LABEL%";
	public final static boolean 	ALL_SAME_SIZE = %ALL_SAME_SIZE%;
	public final static String 	ATTRIBUTION = "%ATTRIBUTION";
	
	public static void fillBundle( Resources res, Bundle b ) {
		// Icon data here, you probably want to auto-generate it
		// The receiving application may not use the name
	
		// Example:
		// b.putInt( "Small Arrow Left", R.drawable.icon_left_small);
	}
}
