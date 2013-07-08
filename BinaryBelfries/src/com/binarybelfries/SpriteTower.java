package com.binarybelfries;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import tv.ouya.console.api.OuyaController;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SpriteTower extends RenderObject  {
	private int playerNum = -1;
    private boolean isVisible = false;
    private long lastShotTime = 0;

    private PointF shootDir;

    static final private float c_playerRadius = 0.5f;
    static final private float c_timeBetweenShots = 1.0f;
    
    public SpriteTower() {
    	super(c_playerRadius);
        this.playerNum = 0;
        shootDir = new PointF();

        setCollisionListener(new CollisionListener() {
            @Override
            public void onCollide(PointF prev, RenderObject me, RenderObject other) {
               
            }
        });
    }
    
    public void init() {
        isVisible = true;
        
        //Set In Center of Screen
        translation.y = (float) GameRenderer.CENTER.y;
        translation.x = (float) GameRenderer.CENTER.x;
        
        rotation = (float) (Math.random() * 360.0f);
    }
    
    public boolean isValid() {
        return isVisible;
    }

    public void shoot(float dirX, float dirY) {
        shootDir.set(dirX, dirY);
    }

    @Override
    protected void initModel() {
        final short[] _indicesArray = {0, 1, 2, 1, 3, 2};

        // float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(_indicesArray.length * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();

        // short has 2 bytes
        ByteBuffer ibb = ByteBuffer.allocateDirect(_indicesArray.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();

        final float[] coords = {
                -0.5f, -0.5f, 0.0f, // 0
                 0.0f, -0.2f, 0.0f, // 1
                 0.0f,  0.1f, 0.0f, // 2
                 0.5f, -0.5f, 0.0f, // 3
        };

        vertexBuffer.put(coords);
        indexBuffer.put(_indicesArray);

        vertexBuffer.position(0);
        indexBuffer.position(0);
    }
    final float c_forwardSpeed = 0.1f;

    static private float stickMag(float axisX, float axisY) {
        float stickMag = (float) Math.sqrt(axisX * axisX + axisY * axisY);
        return stickMag;
    }

    static public boolean isStickNotCentered(float axisX, float axisY) {
        float stickMag = stickMag(axisX, axisY);
        return (stickMag >= OuyaController.STICK_DEADZONE);
    }

    private void getShootDirFromController(OuyaController c) {
    	
    	if (c != null) {
    		shootDir.set(0.0f, 0.0f);
        	// Check the buttons
        	if (c.getButton(OuyaController.BUTTON_L1) || c.getButton(OuyaController.BUTTON_R1)) {
        		PointF fwdVec = getForwardVector();
        		shootDir = fwdVec;
        	}
    	}else{
    		/* ToDo: Debuging without controller */
        	PointF fwdVec = getForwardVector();
            shootDir = fwdVec;
    	}
    }
    private void setRotationFromController(OuyaController c) {
    	if (c != null) {
	        float axisX = c.getAxisValue(OuyaController.AXIS_LS_X);
	        axisX = Math.min(axisX, 1.0f);
	        float axisY = c.getAxisValue(OuyaController.AXIS_LS_Y);
	        axisY = Math.min(axisY, 1.0f);
	        
	        if (isStickNotCentered(axisX, axisY)) {
	            //Log.i("Game", "Y: "+axisY+" X: "+axisX);
	            float angle = (float) Math.toDegrees(Math.atan2(axisX, axisY));
	            //Fix Inversion
	            rotation = angle * -1;
	            //Log.i("Game", "Test: "+angle);
	        }else{
	        	//check over stick
	            axisX = Math.min(c.getAxisValue(OuyaController.AXIS_RS_X), 1.0f);
	            axisY = Math.min(c.getAxisValue(OuyaController.AXIS_RS_Y), 1.0f);
	            
	            if (isStickNotCentered(axisX, axisY)) {
	                //Log.i("Game", "Y: "+axisY+" X: "+axisX);
	                float angle = (float) Math.toDegrees(Math.atan2(axisX, axisY));
	                //Fix Inversion
	                rotation = angle * -1;
	                //Log.i("Game", "Test: "+angle);
	            }
	        }
    	}
    }
    
    @Override
    protected void update() {
        if (!isValid()) {
            return;
        }

        OuyaController c = OuyaController.getControllerByPlayer(playerNum);
        if (c == null) {
        	//ToDo: Remove non controller return after debug
            //return;
        }

        super.update();
        setRotationFromController(c);
        getShootDirFromController(c);

        
        if (shootDir.x != 0.0f || shootDir.y != 0.0f) {
            long currentTime = System.currentTimeMillis();
            float timeSinceLastShot = (currentTime - lastShotTime) / 1000.0f;
            if (timeSinceLastShot > c_timeBetweenShots) {
                lastShotTime = currentTime;
                float desiredDir = (float) Math.toDegrees( Math.atan2(-shootDir.x, shootDir.y) );
                final float c_bulletDistance = 0.0f;
                new SpriteBullet(this, translation.x + shootDir.x * c_bulletDistance, translation.y + shootDir.y * c_bulletDistance, desiredDir);
            }
        }
        
        
    }

    @Override
    protected void doRender(GL10 gl) {
        if (!isValid()) {
            return;
        }

        setColor(gl, Color.WHITE);
        super.doRender(gl);
    }

    @Override
    public boolean doesCollide(RenderObject other) {
        return super.doesCollide(other);
    }
    
    public void die() {
        //end the game
    }
}
