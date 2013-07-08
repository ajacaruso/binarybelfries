/**
 * 
 */
package com.binarybelfries;
import android.os.Handler;
import android.util.Log;


public class EnemyFactory {
	
	private static final String TAG = "Enemy Factory";
	
	private Handler enemyHandler = new Handler();
	
	final Runnable spawnEnemy = new Runnable()
	{
	    public void run() 
	    {
	    	enemyHandler.postDelayed(this, 4000);
	    	SpriteEnemy enemy = new SpriteEnemy();
			enemy.init();
			Log.i(TAG, "create enemy");
	        
	    }
	};
	public EnemyFactory() {		
		
		enemyHandler.postDelayed(spawnEnemy, 4000);
		
	}

}

/*

public class EnemyFactory {
	
	private static class SpawnEnemyTask implements Runnable
	{

		@Override
		public void run() {
			SpriteEnemy enemy = new SpriteEnemy();
			enemy.init();
			EnemyFactory.addEnemy(enemy);
		}
	}

	private static List<SpriteEnemy> enemies = new ArrayList<SpriteEnemy>();
	private final ScheduledExecutorService scheduler;
	private final long initialDelay;
	private final long delayBetweenRuns;
	private static final int NUM_THREADS = 1;
	private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
	private Runnable spawnEnemyTask;
	private ScheduledFuture<?> spawnEnemyFuture;
	  
	
	//create a new enemy factory with initial delay before 1st enemy spawns, and delay between enemies
	 
	public EnemyFactory(long initialDelay, long delayBetweenEnemies) {		
		this.initialDelay = initialDelay;
		this.delayBetweenRuns = delayBetweenEnemies;
		scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
		
		beginSpawning();
	}
	
	public void beginSpawning()
	{
		spawnEnemyTask = new SpawnEnemyTask();
		spawnEnemyFuture = scheduler.scheduleWithFixedDelay(spawnEnemyTask, initialDelay, delayBetweenRuns, TimeUnit.SECONDS);
	}
	
	public void endSpawning()
	{
		spawnEnemyFuture.cancel(DONT_INTERRUPT_IF_RUNNING);
		scheduler.shutdown();
		enemies.clear();
	}
	
	public static void addEnemy(SpriteEnemy enemy)
	{
		enemies.add(enemy);
	}

}
*/
