package %PACKAGE_NAME%;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import android.content.res.Resources;

public class IpackReceiver extends BroadcastReceiver {

	private final static String TAG = "IpackReceiver";
	
	@Override
	public void onReceive( Context c, Intent i ) {

		if ( i == null )
			Log.e( TAG, "null intent" );
		else {
			String action = i.getAction();
			
			if ( TextUtils.isEmpty( action ) ) 
				Log.e( TAG, "null or empty action" );

			else if ( action.equals( IpackKeys.Actions.NOTIFY ) ) {

				int id = i.getIntExtra( IpackKeys.Extras.NOTIFICATION_ID, -1 );
				Parcelable p = i.getParcelableExtra( IpackKeys.Extras.NOTIFICATION );
				Parcelable pi = i.getParcelableExtra( IpackKeys.Extras.NOTIFICATION_PI );
				String title = i.getStringExtra( IpackKeys.Extras.NOTIFICATION_TITLE );
				String text = i.getStringExtra( IpackKeys.Extras.NOTIFICATION_TEXT );
				
				if ( p == null )
					Log.e( TAG, "null notification" );
				else if ( id == -1 )
					Log.e( TAG, "no notification ID specified" );
				else if ( pi == null )
					Log.e( TAG, "no content intent specified" );
				else if ( TextUtils.isEmpty( title ) ) 
					Log.e( TAG, "no title specified" );
				else
					doNotification( c, id, (Notification) p, title, text, (PendingIntent) pi );
			}
			else if ( action.equals( IpackKeys.Actions.NOTIFY_CANCEL ) ) {
				int id = i.getIntExtra( IpackKeys.Extras.NOTIFICATION_ID, -1 );

				if ( id == -1 )
					Log.e( TAG, "no notification ID specified" );
				else
					doCancelNotification( c, id );
			}
			
			else if ( action.equals( IpackKeys.Actions.QUERY_PACKS ) ) {
				
				Bundle infoBundle = new Bundle();
			
				infoBundle.putString( IpackKeys.Extras.LABEL, IpackContent.LABEL );
				infoBundle.putBoolean( IpackKeys.Extras.ALL_SAME_SIZE, IpackContent.ALL_SAME_SIZE );
				infoBundle.putString( IpackKeys.Extras.ATTRIBUTION, IpackContent.ATTRIBUTION );
			
				getResultExtras( true ).putBundle( c.getPackageName(), infoBundle );
			}
			
			else if ( action.equals( IpackKeys.Actions.QUERY_ICONS ) ) {
				
				Bundle b = getResultExtras( true );

				IpackContent.fillBundle( c.getResources(), b );
			}
			else
				Log.e( TAG, "unknown action: " + action );
		}
	}

	private void doNotification( Context c, int id, Notification not,
		String title, String text, PendingIntent pi
 	) {
		// 4.0+ doesn't resize notification icons, select small version of that specified
		if ( not.icon != 0 ) {
			Resources res = c.getResources();

			try {
				String name = res.getResourceEntryName( not.icon );

				if ( name != null )
					name = name + "_";
		
				int smallID = res.getIdentifier( name, "drawable", c.getPackageName() );

				if ( smallID != 0 )
					not.icon = smallID;
			}
			catch ( Resources.NotFoundException e ) {
				Log.e( TAG, "unknown icon ID: " + not.icon );
			}
		}

		NotificationManager nm = (NotificationManager) c.getSystemService( Context.NOTIFICATION_SERVICE );

		if ( nm == null )
			Log.e( TAG, "no notification manager" ); 
		else {
			try {

				not.setLatestEventInfo( c, title, TextUtils.isEmpty( text ) ? "" : text, pi );
				nm.notify( id, not );
			}
			catch ( Exception e ) {
				Log.e(TAG, "exception submitting notify: " + e.toString() );
			}
		}
	}

	private void doCancelNotification( Context c, int id ) {
		NotificationManager nm = (NotificationManager) c.getSystemService( Context.NOTIFICATION_SERVICE );
		
		if ( nm != null ) {
			try {
				nm.cancel( id );
			}
			catch ( Exception e ) {
				Log.e(TAG, "exception cancelling notification: " + e.toString() );
			}
		}
	}
}
