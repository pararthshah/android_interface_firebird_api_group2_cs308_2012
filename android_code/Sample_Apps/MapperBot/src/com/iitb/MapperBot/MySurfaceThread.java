package com.iitb.MapperBot;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MySurfaceThread extends Thread {
	private SurfaceHolder myThreadSurfaceHolder;
	private MySurfaceView myThreadSurfaceView;
	private boolean myThreadRun = false;

	public MySurfaceThread(SurfaceHolder surfaceHolder, MySurfaceView surfaceView) {
		myThreadSurfaceHolder = surfaceHolder;
		myThreadSurfaceView = surfaceView;
	}

	public void setRunning(boolean b) {
		myThreadRun = b;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(myThreadRun){
			Canvas c = null;

			try{
				c = myThreadSurfaceHolder.lockCanvas(null);
				synchronized (myThreadSurfaceHolder){
					myThreadSurfaceView.onDraw(c);
				}
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					myThreadSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}