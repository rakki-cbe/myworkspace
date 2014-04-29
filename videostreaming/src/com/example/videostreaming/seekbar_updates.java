package com.example.videostreaming;

import android.os.AsyncTask;

public class seekbar_updates extends AsyncTask<variable_holder_seekbar, String, String> {

	variable_holder_seekbar vh;

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		
		
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(variable_holder_seekbar... arg0) {
		// TODO Auto-generated method stub
		if(arg0[0] instanceof variable_holder_seekbar)
		{
			vh=arg0[0];
			if(arg0[0].mp!=null && arg0[0].sb!=null)
			{
				vh.seek=false;
			while (arg0[0].mp.isPlaying()) {
				
				try {
					
					publishProgress("sd");
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}
			}
		}
		return null;
	}
	 protected void onProgressUpdate(String... progress) {
		 vh.sb.setProgress(vh.mp.getCurrentPosition());
		 
     }
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		//vh.sb.setProgress(vh.mp.getCurrentPosition());
		 vh.sb.setProgress(vh.mp.getDuration());
		super.onPostExecute(result);
	}




}
