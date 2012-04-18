package com.iitb.MapperBot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	private static final String TAG = "MySurfaceView";
	
	private MySurfaceThread thread;
	
	Bitmap photoBitmap;
	int c_width, c_height;
	float borderx, bordery;
	
	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init(){
		getHolder().addCallback(this);
		thread = new MySurfaceThread(getHolder(), this);
		c_width = 1;
		c_height = 1;
		setFocusable(true); // make sure we get key events
	}
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG,"initializing canvas");
		Canvas c = holder.lockCanvas(null);
		if (c != null) {
			c_width = c.getWidth();
			c_height = c.getHeight();
			getHolder().unlockCanvasAndPost(c);
		}
		else {
			Log.e(TAG,"canvas uninitialized!!");
			c_width = 1;
			c_height = 1;
		}
		
		thread.setRunning(true);
		thread.start();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	public void setBitmap(Bitmap bm){
		Log.d(TAG, "setBM");
		if (c_width < 5) return; //canvas not initialized
		float xscale = (float)c_width / (float)bm.getWidth();
		float yscale = (float)c_height / (float)bm.getHeight();
		if (xscale > yscale) // make sure both dimensions fit (use the smaller scale)
			xscale = yscale;
		float newx = (float)bm.getWidth() * xscale;
		float newy = (float)bm.getHeight() * xscale; // use the same scale for both dimensions
		Log.d(TAG,"newx: " + newx + " newy: " + newy + " c_w: " + c_width + " c_h: " + c_height);
		// if you want it centered on the display (black borders)
		borderx = (float) (((float)c_width - newx) / 2.0);
		bordery = (float) (((float)c_height - newy) / 2.0);
		if (photoBitmap == null){
			photoBitmap = Bitmap.createScaledBitmap(bm, (int)newx, (int)newy, true);
		}
		else {
			synchronized (photoBitmap) {
				photoBitmap = Bitmap.createScaledBitmap(bm, (int)newx, (int)newy, true);
			}
		}
		Log.d(TAG, "bitmap set");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (photoBitmap == null){
			return;
		}
		else {
			synchronized (photoBitmap) {
				canvas.drawBitmap (photoBitmap, borderx,  bordery, null);
			}
		} 
	}
}