/**
 * 
 */
package com.binarybelfries;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author Phil
 *
 */
public class SpriteEnemy extends RenderObject {

	enum StartingEdge {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT
	};
	
	private boolean isVisible = false;
	static final private float c_enemyRadius = 0.5f;
	static final private float c_forwardSpeed = 0.05f;
	static final private float enemyLifetime = 20f;
	private long startTime;

	/**
	 * @param radius - the radius of the enemy for collision purposes
	 */
	public SpriteEnemy() {
		super(c_enemyRadius);
		startTime = System.currentTimeMillis();
		
        setCollisionListener(new CollisionListener() {
            @Override
            public void onCollide(PointF prev, RenderObject me, RenderObject other) {
               
            }
        });
	}
	
	/**
	 * Sets up the enemy
	 */
    public void init() {
        isVisible = true;
        
        // Set initial random starting position on edge of screen
        
       // StartingEdge startAt = StartingEdge.values()[(int)(Math.random() * (StartingEdge.values().length + 1))];
        StartingEdge startAt = StartingEdge.TOP;
        
        switch (startAt)
        {
	        case TOP:
	        	translation.y = (float) 0 + 1.0f;
	        	translation.x = (float) (Math.random() * (GameRenderer.BOARD_WIDTH - 1.0f) + 1.0f);
	        	break;
	        case BOTTOM:
	        	translation.y = (float) ((GameRenderer.BOARD_HEIGHT - 1.0f) + 1.0f);
	        	translation.x = (float) (Math.random() * (GameRenderer.BOARD_WIDTH - 1.0f) + 1.0f);
	        	break;
	        case LEFT:
	        	translation.y = (float) (Math.random() * (GameRenderer.BOARD_HEIGHT - 1.0f) + 1.0f);;
	        	translation.x = (float) 0 + 1.0f;
	        	break;
	        case RIGHT:
	        	translation.y = (float) (Math.random() * (GameRenderer.BOARD_HEIGHT - 1.0f) + 1.0f);;
	        	translation.x = (float) ((GameRenderer.BOARD_WIDTH - 1.0f) + 1.0f);
	        	break;
	        default:
	        	break;
        }
        
        //point it at the center of the screen-- with TRIG!
        rotation = (float) Math.toDegrees(Math.atan2(GameRenderer.CENTER.y - translation.y, translation.x - GameRenderer.CENTER.x));
        
        Log.i("Enemy", "startAt "+startAt+" posx "+translation.x+" posy "+translation.y+" rot "+rotation);
    }
    
	public boolean isValid() {
        return isVisible;
    }
	
	/**
	 * @see com.binarybelfries.RenderObject#initModel()
	 */
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
    
	/**
	 * @param gl - the graphics 
	 * @see com.binarybelfries.RenderObject#doRender(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
    protected void doRender(GL10 gl) {
        if (!isValid()) {
            return;
        }

        setColor(gl, Color.RED);
        super.doRender(gl);
    }
    
    @Override
    protected void update() {
        super.update();
        goForward(c_forwardSpeed);

        long currentTime = System.currentTimeMillis();
        float elapsedTime = (currentTime - startTime) / 1000.0f;
        if (elapsedTime >= enemyLifetime) {
            destroy();
        }
    }
	
	/**
	 * @param other - the thing we collided with
	 * @see com.binarybelfries.RenderObject#doesCollide(com.binarybelfries.RenderObject)
	 */
	@Override
    public boolean doesCollide(RenderObject other) {
		if (other instanceof SpriteTower) {
			SpriteTower t = (SpriteTower) other;
			t.die();
        }
        return super.doesCollide(other);
    }
	
    /**
     * destroys this enemy
     */
	protected void destroy() {
        // Make sure this happens from the main thread
        final RenderObject me = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                GameRenderer.s_instance.removeRenderObject(me);
            }
        });
    }
}
