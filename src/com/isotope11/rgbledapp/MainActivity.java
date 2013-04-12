package com.isotope11.rgbledapp;

import java.util.ArrayList;
import java.util.List;

import org.jruby.embed.ScriptingContainer;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.commonsware.cwac.colormixer.ColorMixer;
import com.commonsware.cwac.colormixer.ColorMixer.OnColorChangedAndStoppedListener;

public class MainActivity extends Activity {

	protected final String TAG = MainActivity.class.toString();
	protected ScriptingContainer mRubyContainer;
	protected String mDrbResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.setProperty("jruby.bytecode.version", "1.5");
		mRubyContainer = new ScriptingContainer();
		List<String> loadPaths = new ArrayList<String>();
		loadPaths.add("jruby.home/lib/ruby/shared");
		loadPaths.add("jruby.home/lib/ruby/1.8");
		mRubyContainer.setLoadPaths(loadPaths);
		
		final Activity lol = this;
		
		ColorMixer colors = (ColorMixer) findViewById(R.id.mixer);
		colors.setOnColorChangedAndStoppedListener(new OnColorChangedAndStoppedListener() {
			@Override
			public void onColorChange(int argb) {
		        int r = Color.red(argb);
		        int g = Color.green(argb);
		        int b = Color.blue(argb);
				RGBLedSetter task = new RGBLedSetter();
				task.setRed(r);
				task.setGreen(g);
				task.setBlue(b);
				task.execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class RGBLedSetter extends AsyncTask<Object, Void, String> {

		int mRed = 0;
		int mGreen = 0;
		int mBlue = 0;
		
		@Override
		protected String doInBackground(Object... arg0) {
		    String rubyDrbClient = "require 'drb/drb';SERVER_URI='druby://192.168.1.76:8787';DRb.start_service;led = DRbObject.new_with_uri(SERVER_URI);led.red=" + getMapped(mRed) + ";led.green=" + getMapped(mGreen) + ";led.blue=" + getMapped(mBlue) + ";led.pi_blast;";
			mRubyContainer.runScriptlet(rubyDrbClient);
			return "nope";
		}
		
		@Override
		protected void onPostExecute(String result){
			mDrbResult = result;
		}
		
		public void setBlue(int val){
			mBlue = val;
		}
		public void setGreen(int val){
			mGreen = val;
		}
		public void setRed(int val){
			mRed = val;
		}
	    public String getMapped(int val){
	    	float newVal = Float.valueOf(val) / Float.valueOf(255);
	    	Log.d(TAG, Float.toString(newVal));
	    	return Float.toString(newVal);
	    }

	}

}
