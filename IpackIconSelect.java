package %PACKAGE_NAME%;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class IpackIconSelect extends Activity 
{
	private final static int 	DEFAULT_ICON_SIZE = 60;
	private final static int 	DEFAULT_CELL_WIDTH = 90;

	private final static int 	GRID_PADDING = 10;
	private final static int 	INFO_PADDING = 5;
	
	// specified by launch intent
	private int 				iconSize = DEFAULT_ICON_SIZE;
	private int 				cellSize = DEFAULT_CELL_WIDTH;
	private int 				gridBackColour = 0x33777777;

	// state
	private GridView 			grid;
	private List<Integer> 		sortedIDs = new ArrayList<Integer>();
	private Bundle 				icons = new Bundle();
	private IconSortTask 		sortTask = null;

	public void onCreate( Bundle icicle ) {
		
		super.onCreate( icicle );

		setResult( Activity.RESULT_CANCELED );

		IpackContent.fillBundle( getResources(), icons );

		parseCallIntent();

		setupGrid();
		
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );

 		setTitle( IpackContent.LABEL );

 		LinearLayout layout = new LinearLayout( this );
 		layout.setOrientation( 1 );
 		
 		layout.addView( getInfoTextView() );
 		layout.addView( grid );
 		
		setContentView( layout );

		setProgressBarIndeterminateVisibility( true );
		
		sortTask = new IconSortTask();
       	sortTask.execute( IpackContent.ALL_SAME_SIZE );
	}

	private TextView getInfoTextView() {
 
		TextView info = new TextView( this );
 		
 		StringBuilder b = new StringBuilder();

 		b.append( "#" ).append( icons.size() );
 		
 		if ( IpackContent.ALL_SAME_SIZE ) {
 			Point p = getOneIconSize();

 			if ( p != null )
 				b.append( " " ).append( p.x ).append( "x" ).append( p.y );
 		}
 			
 		if ( ! TextUtils.isEmpty( IpackContent.ATTRIBUTION ) )
 			b.append( " [" ) .append( IpackContent.ATTRIBUTION ).append( "]" );
 		
 		info.setText( b.toString() );
 		info.setTextSize( 16 );
 		info.setPadding( INFO_PADDING,INFO_PADDING,INFO_PADDING,INFO_PADDING );
 		info.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);

 		
 		return info;
	}

	private void parseCallIntent() {
		Bundle extras = getIntent().getExtras();

		if ( extras != null ) {
			try {
				if ( extras.containsKey( IpackKeys.Extras.GRID_BACK_COLOUR ) )
					gridBackColour = extras.getInt( IpackKeys.Extras.GRID_BACK_COLOUR );

				if ( extras.containsKey( IpackKeys.Extras.CELL_SIZE ) )
					cellSize = extras.getInt( IpackKeys.Extras.CELL_SIZE );

				if ( extras.containsKey( IpackKeys.Extras.ICON_DISPLAY_SIZE ) )
					cellSize = extras.getInt( IpackKeys.Extras.ICON_DISPLAY_SIZE );
			}
			// probably used wrong value class
			catch ( Exception e ) {
				Log.d( IpackContent.LABEL, "exception parsing intent: " + e.toString() );
			}
		}
	}

	private void setGridAdapter() {

		grid.setAdapter( 
				new BaseAdapter() {
					public View getView(int position, View convertView, ViewGroup parent) {
			            ImageView i;

			            if ( convertView == null ) {
			                i = new ImageView( IpackIconSelect.this);
			                i.setScaleType( ImageView.ScaleType.FIT_CENTER );
			                i.setLayoutParams( new GridView.LayoutParams( cellSize, cellSize ) );
			                
			            	int padding = ( cellSize - iconSize ) / 2 ;
			                i.setPadding( padding, padding, padding, padding );
			            }
			            else
			                i = (ImageView) convertView;

			            i.setImageResource( sortedIDs.get( position ) );

			            return i;
			        }

			        public final int getCount() 				{ 	return sortedIDs.size();  }
			        public final Object getItem( int position ) { 	return sortedIDs.get(position); }
			        public final long getItemId( int position ) {	return position; }
				}
		);	
	}

	public Point getOneIconSize() {
		Point p = null;
		
		String name = icons.keySet().iterator().next();
		
		Drawable d = getResources().getDrawable( icons.getInt( name ) );

		if ( d != null ) {
			
			float density = getResources().getDisplayMetrics().density;
			
			p = new Point(  
					(int) ( d.getIntrinsicWidth() / density ), 
					(int) (d.getIntrinsicHeight() / density )
			);
		}

		return p;
	}

	public void sortIcons( final boolean allSameSize ) {
		
		Resources res = getResources();
			
		final Map<Integer,Integer> sizes = new HashMap<Integer,Integer>();
		final Map<Integer,String> names = new HashMap<Integer,String>();

		for ( String name : icons.keySet() ) {

			int id = icons.getInt( name );
				
			try {
				if ( ! allSameSize ) {
					Drawable d = res.getDrawable( id );
					sizes.put( id, d.getIntrinsicHeight() * d.getIntrinsicWidth() );
				}
				sortedIDs.add( id );
				names.put( id, name );
			}
			catch ( Resources.NotFoundException e ) {
				Log.e( IpackContent.LABEL, "sort icons: resource not found: " + name );
			}
		}

		Collections.sort( 
				sortedIDs,
				new Comparator<Integer>() {
					public int compare( Integer x, Integer y ) {	
						if ( allSameSize )
							return names.get( y ).compareToIgnoreCase( names.get( x ) );
						else {
							Integer xsize = sizes.get( x );
							Integer ysize = sizes.get( y );
							
							if ( xsize == ysize )
								return names.get( y ).compareToIgnoreCase( names.get( x ) );
							else
								return ysize.compareTo( xsize );
						}
					}
				}
		);
	}
	
	private void setupGrid() {

		grid = new GridView( this );
		grid.setBackgroundColor( gridBackColour );
		grid.setGravity( Gravity.CENTER );
		grid.setPadding( GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING );
		
		//grid.setNumColumns( grid.getWidth() / cellSize );
		
		grid.setNumColumns( getResources().getDisplayMetrics().widthPixels / cellSize );
		
		grid.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick( AdapterView<?> parent, View view, int pos, long id ) {
				
				int selID = sortedIDs.get( pos );
				String selName = null;

				String resourceName = getResources().getResourceEntryName( selID );
				
				for ( String name : icons.keySet() ) {
					if ( icons.getInt( name ) == selID ) {
						selName = name;
						break;
					}
				}

				Intent result = new Intent();
				result.setData( 
	            		Uri.parse( 
			            		IpackKeys.ANDROID_RESOURCE_PREFIX + getPackageName()
			            		+ "/" + resourceName
	            		)
				);

				if ( selName != null )
					result.putExtra( IpackKeys.Extras.ICON_LABEL, selName );
				
				result.putExtra( IpackKeys.Extras.ICON_NAME, resourceName );
				result.putExtra( IpackKeys.Extras.ICON_ID, selID );

				setResult( RESULT_OK, result );
	                
				finish();
			}
		});
	}

	public class IconSortTask extends AsyncTask<Boolean,Integer,Boolean> {
    	
		public Boolean doInBackground( Boolean... args ) {
    		
			sortIcons( args[0] );
			
			return true;
        }

    	protected void onPostExecute( Boolean result ) {
    		
    		try {
    			sortTask = null;
    	
    			setGridAdapter();

    			setProgressBarIndeterminateVisibility( false );
    			

    		}
    		// maybe we left already , grid doesn't exist
    		catch ( Exception e ) {
    		}
    	}
    }
}
