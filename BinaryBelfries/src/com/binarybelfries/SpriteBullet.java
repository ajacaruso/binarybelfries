package com.binarybelfries;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;

import javax.microedition.khronos.opengles.GL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SpriteBullet extends RenderObject {
    static private final float c_bulletRadius = 0.25f;
    static private final float c_bulletSpeed = 0.20f;
    static private final int c_bulletColor = Color.GREEN;

    static private final float c_bulletLifetime = 2.0f;
    private long startTime;

    public SpriteBullet(SpriteTower shooter, float translationX, float translationY, float rotation) {
        super(c_bulletRadius);
        this.translation.set(translationX, translationY);
        this.rotation = rotation;

        startTime = System.currentTimeMillis();

        setCollisionListener(new CollisionListener() {
            @Override
            public void onCollide(PointF prev, RenderObject me, RenderObject other) {
            	
            	if (other instanceof SpriteWall) {
                    SpriteBullet.this.destroy();
                }
            	else if (other instanceof SpriteEnemy) {
            		SpriteEnemy e = (SpriteEnemy) other;
                    e.destroy();
                    SpriteBullet.this.destroy();
            	}
                
            }
        });
    }

    @Override
    protected void initModel() {
        final short[] _indicesArray = {0, 1, 2};

        // float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(_indicesArray.length * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();

        // short has 2 bytes
        ByteBuffer ibb = ByteBuffer.allocateDirect(_indicesArray.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();

        final float[] coords = {
                0.1f,   0.0f, 0.0f, // 0
               -0.05f, -0.0866f, 0.0f, // 1
               -0.05f,  0.0866f, 0.0f, // 2
        };

        vertexBuffer.put(coords);
        indexBuffer.put(_indicesArray);

        vertexBuffer.position(0);
        indexBuffer.position(0);
    }

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

    @Override
    protected void update() {
        super.update();
        goForward(c_bulletSpeed);

        long currentTime = System.currentTimeMillis();
        float elapsedTime = (currentTime - startTime) / 1000.0f;
        if (elapsedTime >= c_bulletLifetime) {
            destroy();
        }
    }

    @Override
    protected void doRender(GL10 gl) {
        setColor(gl, c_bulletColor);
        super.doRender(gl);
    }
}
