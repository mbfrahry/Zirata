package th.zirata;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;

public class Zirata extends GLGame {

	boolean firstTimeCreate = true;
	
	public Screen getStartScreen(){
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
	}
}
