package com.example.videostreaming;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	MediaPlayer mediaPlayer;
	SeekBar progrssing;
	variable_holder_seekbar vh;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fragment_main);
	    vh=new variable_holder_seekbar();
	    vh.seek=false;
	    
	    int i =0;
	    getWindow().setFormat(PixelFormat.UNKNOWN);
	    surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
	    surfaceHolder = surfaceView.getHolder();
	    surfaceHolder.addCallback(this);
	    surfaceHolder.setFixedSize(176, 144);
	    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    mediaPlayer = new MediaPlayer();
	    progrssing=(SeekBar) findViewById(R.id.seekbar);
	   progrssing.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			mediaPlayer.start();
			vh.seek=false;
			new seekbar_updates().execute(vh);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			if(mediaPlayer.isPlaying())
				mediaPlayer.pause();
			
		}
		
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			if(vh.seek)
			{
				int totalDuration = arg0.getMax();
				double val=((double)arg0.getProgress())/((double)arg0.getMax());	
				int  currentDuration =(int) (val *totalDuration);
				mediaPlayer.seekTo(currentDuration);
			}
			
		     
		}
	});
	   progrssing.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			System.out.println(arg1.getAction()+"="+MotionEvent.ACTION_DOWN);
			if(arg1.getAction()==MotionEvent.ACTION_DOWN)
			{
				vh.seek=true;
			}
			if(arg1.getAction()==MotionEvent.ACTION_UP)
			{
				vh.seek=false;
			}
			return false;
		}
	});
	   mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
  		  public void onPrepared(MediaPlayer mp) {
  			progrssing.setMax(mediaPlayer.getDuration());
   		  vh.mp=mediaPlayer;
   		 vh.sb=progrssing;
  		      mp.start();
  		     new seekbar_updates().execute(vh); 
  		  }
  		});
	   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	 private void playVideo()
     {
		    	 String stringPath =  "http://publiclyaccessible.s3.amazonaws.com/KindleR3.mp4";
		    			 //Environment.getExternalStorageDirectory()+"/sample_mpeg4.mp4";
            	  if (mediaPlayer.isPlaying()) {
     		        mediaPlayer.reset();
            	  }
     		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
     		    mediaPlayer.setDisplay(surfaceHolder);
     		   
     		    try {
     		    	/*mediaPlayer.setDataSource(MainActivity.this,Uri.parse(stringPath));
						mediaPlayer.prepareAsync();*/
     		        mediaPlayer.setDataSource(stringPath);
     		        mediaPlayer.prepareAsync();
     		    } catch (IllegalArgumentException e) {
     		        // TODO Auto-generated catch block
     		        e.printStackTrace();
     		    } catch (IllegalStateException e) {
     		        // TODO Auto-generated catch block
     		        e.printStackTrace();
     		    } catch (IOException e) {
     		        // TODO Auto-generated catch block
     		    	System.out.println(" io exception present");
     		        e.printStackTrace();
     		    }
     		    
     		   // mediaPlayer.start();
           
     }
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		playVideo();
	}
	public void relpay(View v)
	{
		//System.out.println("called correct"+progrssing.getMax());
		
		/*progrssing.setProgress(progrssing.getMax()/2);
		progrssing.invalidate();*/
		mediaPlayer.reset();
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if (mediaPlayer !=null) {
	        mediaPlayer.release();
	    }
	}

}
