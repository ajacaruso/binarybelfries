package com.binarybelfries;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import tv.ouya.console.api.OuyaController;

import static com.binarybelfries.R.*;

public class GameManager extends Activity {
	private SpriteTower spriteTower;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        OuyaController.init(this);

	        setContentView(layout.activity_game_manager);
	        
	        Button quitGame = (Button) findViewById(id.quit_button);
	        quitGame.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                finish();
	            }
	        });

	        	spriteTower = new SpriteTower();
	            // Create Tower if controller attached. Otherwise Display Error Screen.
	            OuyaController ouyaController = OuyaController.getControllerByPlayer(0);
	            if (ouyaController != null) {
	                findOrCreatePlayer(ouyaController.getDeviceId());
	            }else{
	            	//ToDo: No Controller Error State. For now create player anyway.
	            	findOrCreatePlayer(0);
	            }
	        
	    }

	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        boolean handled = OuyaController.onKeyDown(keyCode, event);
	        return handled || super.onKeyDown(keyCode, event);
	    }

	    @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event) {
	        boolean handled = OuyaController.onKeyUp(keyCode, event);
	        return handled || super.onKeyUp(keyCode, event);
	    }

	    @Override
	    public boolean onGenericMotionEvent(MotionEvent event) {
	        boolean handled = OuyaController.onGenericMotionEvent(event);
	        return handled || super.onGenericMotionEvent(event);
	    }

	    private SpriteTower findOrCreatePlayer(int deviceId) {
	        int playerNum = OuyaController.getPlayerNumByDeviceId(deviceId);
	        if (playerNum < 0) {
	        	/*ToDo: Add Null for non-dev version with no controller attached.
	            return null;
	            */
	        }

	        if (spriteTower.isValid()) {
	            return spriteTower;
	        }

	        spriteTower.init();
	        return spriteTower;
	    }
}
