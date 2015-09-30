package th.zirata;

import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;

import th.zirata.Help.MainHelpScreen;

public class Zirata extends GLGame {

	boolean firstTimeCreate = true;
	public RevMob revmob;
	public RevMobAdsListener revmobListener;
	
	public Screen getStartScreen(){
		revmobListener = new RevMobAdsListener() {

			// Required
			@Override
			public void onRevMobSessionIsStarted() {
				Log.i("[RevMob]", "RevMob session is started");
			}

			@Override
			public void onRevMobSessionNotStarted(String message) {
				Log.i("[RevMob]", "RevMob session failed to start");
			}

			// Optional
			@Override
			public void onRevMobAdDisplayed() {
				Log.i("[RevMob]", "onAdDisplayed");
			}

			@Override
			public void onRevMobAdReceived() {
				Log.i("[RevMob]", "onAdReceived");
			}

			@Override
			public void onRevMobAdNotReceived(String message) {
				Log.i("[RevMob]", "onAdNotReceived");
			}

			@Override
			public void onRevMobAdDismissed() {
				Log.i("[RevMob]", "onAdDismissed");
			}

			@Override
			public void onRevMobAdClicked() {
				Log.i("[RevMob]", "onAdClicked");
			}
		};
		revmob = RevMob.startWithListener(this, revmobListener);
		//return new MainHelpScreen(this, revmob, revmobListener);
		return new MainMenuScreen(this);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config){

		super.onSurfaceCreated(gl, config);
		if(firstTimeCreate){
			Settings.load(getFileIO());
			Assets.load(this);
			firstTimeCreate = false;
		}else{
			Assets.reload();
		}

	}
	
	public void onPause(){
		super.onPause();
		Assets.menuMusic.pause();
	}
}
