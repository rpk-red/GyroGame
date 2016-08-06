package home.gyrogame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WITDH = 856;
    public static final int HEIGHT = 480;
    public static final int GAMESPEED = -5;
    private MainThread thread;
    private Backround bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private long smokeStartTime;
    private ArrayList<Missile> missiles;
    private long missileStartTime;
    private Random random;

    public GamePanel(Context context){
        super(context);
        // add the callback to the surfaceHolder to interrupt events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make gamePanel focusable so it can handle envents
        setFocusable(true);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        random = new Random();
        bg = new Backround(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter), 65, 25, 3);
        smoke = new ArrayList<Smokepuff>();
        smokeStartTime = System.nanoTime();
        missiles = new ArrayList<Missile>();
        missileStartTime = System.nanoTime();



        //we can safly start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //sometimes it need multiple atempts to stop thread, so we use while loop and try-catch to stop it
        // if does not succeed it's goes to catch and then starts the loop all over again
        boolean retry = true;
        int counter = 0;
        while (retry && counter<10000){
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            }catch (Exception e){e.printStackTrace();}

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

//        Log.d(MainActivity.LOG_TAG, "MotionEvent registerd");
        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            Log.d(MainActivity.LOG_TAG, "Action DOWN");
            if (!player.getPlaying()){
                player.setPlaying(true);
//                Log.d(MainActivity.LOG_TAG, "Player is playing");
            }
            else {
//                Log.d(MainActivity.LOG_TAG,"GO UP");
                player.setUp(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
//            Log.d(MainActivity.LOG_TAG, "ACTION UP");
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }
    public void update(){
        if (player.getPlaying()){
            bg.update();
            player.update();

            //add missiles on timer
            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if(missileElapsed > (2000 - player.getScore()/4)){

                if(missiles.size() == 0){
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            getWidth()+10,HEIGHT/2, 45, 15, player.getScore(), 13));
                }else{

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                            getWidth()+10,(int)(random.nextDouble()*(HEIGHT)), 45, 15, player.getScore(), 13));
                }
                //reset timer
                missileStartTime = System.nanoTime();
            }
            // loop through every missile and check collision and remove
            for(int i = 0; i < missiles.size(); i++){
                //update missile
                missiles.get(i).update();
                if (collision(missiles.get(i), player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if (missiles.get(i).getX()< - 100){
                    missiles.remove(i);
                    break;
                }
            }

            //add smoke puffs on timer
            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if (elapsed > 120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();

            }
            for (int i = 0; i < smoke.size(); i++){
                smoke.get(i).update();
                if(smoke.get(i).getX() < -10){
                    smoke.remove(i);
                }
            }
        }
    }

    private boolean collision(GameObject a, GameObject b) {

        if(Rect.intersects(a.getRectangle(), b.getRectangle())){

            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        //getWidth give as the Width of entire phone screen not the game but the surfaceview whitch is full screen
        final float scaleFactorX = (float) getWidth()/WITDH;
        final float scaleFactorY = (float) getHeight()/HEIGHT;
        if (canvas != null){
            //we save the state of canvas before we scale it
             final int savedState = canvas.save();
            //scale canvas
            canvas.scale(scaleFactorX,scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);
            //draw smoke puffs
            for (Smokepuff sp: smoke){
                sp.draw(canvas);
            }
            //draw missiles
            for (Missile m: missiles){
                m.draw(canvas);
            }
            //we return to before scaled state because canvas keeps scaling all the time, cause we call draw method in MainThread all the time
            canvas.restoreToCount(savedState);
            // You don't need the saved state because it's initializing the factors every time!!!
        }

    }
}
