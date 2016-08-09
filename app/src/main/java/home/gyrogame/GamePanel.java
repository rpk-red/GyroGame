package home.gyrogame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
    private Background bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private long smokeStartTime;
    private ArrayList<Missile> missiles;
    private long missileStartTime;
    private Random random;
    private ArrayList<TopBorder> topBorders;
    private ArrayList<BottomBorder> bottomBorders;
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown;
    private boolean bottomDown;
    private int progressDenom;
    private boolean newGameCreated;
    private Explosion explosion;
    private long startRestart;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;


    public GamePanel(Context context){
        super(context);
        // add the callback to the surfaceHolder to interrupt events
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle envents
        setFocusable(true);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        random = new Random();
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter), 65, 25, 3);
        smoke = new ArrayList<Smokepuff>();
        smokeStartTime = System.nanoTime();
        missiles = new ArrayList<Missile>();
        missileStartTime = System.nanoTime();
        topBorders = new ArrayList<>();
        bottomBorders = new ArrayList<>();
        //increase to slow down difficulty progression, decrease to speed up difficulty progression
        progressDenom = 20;
        topDown = true;
        bottomDown = true;

        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
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
                thread = null;
            }catch (Exception e){e.printStackTrace();}

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (!player.getPlaying() && newGameCreated && reset){

                player.setPlaying(true);
                player.setUp(true);
            }
            if (player.getPlaying()){

                if (!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }
    public void update(){

        if (player.getPlaying()){

            if (bottomBorders.isEmpty() || topBorders.isEmpty()){

                player.setPlaying(false);
                return;
            }
            bg.update();
            player.update();
            //calculate the threshold of height that the border can have based on score
            //max and min border height are updated, and the border switched direction when either max or min is set
            maxBorderHeight = 30 + player.getScore()/progressDenom;
            //cap max border height so that borders can only take a total of 1/2 of the screen
            if (maxBorderHeight > HEIGHT/4) {
                maxBorderHeight = HEIGHT/4;
            }
            minBorderHeight = 5 + player.getScore()/progressDenom;

            //check top borderd coliision
            for(int i = 0; i<topBorders.size(); i++){
                if (collision(topBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }
            //check bottom borderd collison
            for(int i = 0; i<bottomBorders.size(); i++){
                if (collision(bottomBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }
            //update Top border
            this.updateTopBorder();
            //update Bottom border
            this.updateBottomBorder();
            //add missiles on timer
            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if(missileElapsed > (2000 - player.getScore()/4)){

                if(missiles.size() == 0){
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            getWidth()+10,HEIGHT/2, 45, 15, player.getScore(), 13));
                }else{

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                            getWidth()+10,(int)(random.nextDouble() * (HEIGHT - (maxBorderHeight * 2) + maxBorderHeight)),
                            45, 15, player.getScore(), 13));
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
                if (missiles.get(i).getX() < - 100){
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
        else{

            player.resetDY();
            if (!reset){
                newGameCreated = false;
                startRestart = System.nanoTime();
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion),
                    player.getX(), player.getY() - 30, 100, 100, 25);
                reset = true;
                dissapear = true;
            }
            explosion.update();
            long resetElapsed = (System.nanoTime() - startRestart)/1000000;

            if (resetElapsed > 950 && !newGameCreated){
                newGame();
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
           //  final int savedState = canvas.save();
            //scale canvas
            canvas.scale(scaleFactorX,scaleFactorY);
            //draw background
            bg.draw(canvas);
            //draw player
            if (!dissapear){player.draw(canvas);}
            //draw smoke puffs
            for (Smokepuff sp: smoke){
                sp.draw(canvas);
            }
            //draw missiles
            for (Missile m: missiles){
                m.draw(canvas);
            }
            //draw topBorder
            for (TopBorder tb: topBorders){tb.draw(canvas);}

            //draw bottomBorder
            for (BottomBorder bb: bottomBorders){bb.draw(canvas);}

            //draw explosion
            if (started){
                explosion.draw(canvas);
            }
            drawText(canvas);
            //we return to before scaled state because canvas keeps scaling all the time, cause we call draw method in MainThread all the time
           // canvas.restoreToCount(savedState);
            // You don't need the saved state because it's initializing the factors every time!!!
        }

    }

    public void updateTopBorder(){

        //every 50 points insert randomly placed top blocks that break the pattern
        if (player.getScore()%50 == 0){

            topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    topBorders.get(topBorders.size()-1).getX()+20, 0, (int) ((random.nextDouble()*(maxBorderHeight))+1)));
        }
        for (int i = 0; i<topBorders.size(); i++){

            topBorders.get(i).update();

            if (topBorders.get(i).getX() < -20){
                //remove the element in arrayList replace it by adding a new one
                topBorders.remove(i);
                //calculate topdown which determines the direction the boreder is moving (up or down)
                if (topBorders.get(topBorders.size()-1).getHeight() > maxBorderHeight){
                    topDown = false;
                }
                if (topBorders.get(topBorders.size()-1).getHeight() < minBorderHeight){
                    topDown = true;
                }
                //new border will have larger height
                if (topDown){
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20, 0,
                            topBorders.get(topBorders.size()-1).getHeight()+1));
                }
                //new border added will have smaller height
                else {
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20, 0,
                            topBorders.get(topBorders.size()-1).getHeight()-1));
                }

            }
        }
    }
    public void updateBottomBorder(){

        //every 40 points insert randomly placed bottom blocks that break the pattern
        if (player.getScore()%40 == 0){

            bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    bottomBorders.get(bottomBorders.size()-1).getX()+20,
                    (int)((random.nextDouble()*maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }
        for (int i = 0; i<bottomBorders.size(); i++){

            bottomBorders.get(i).update();
            // if borderd is moving off screen, remove it and add a corresponding new one
            if (bottomBorders.get(i).getX() < -20){

                bottomBorders.remove(i);
                //determine if borded will move up or down
                if (bottomBorders.get(bottomBorders.size()-1).getHeight() > maxBorderHeight){
                    bottomDown = false;
                }
                if (bottomBorders.get(bottomBorders.size()-1).getHeight() < minBorderHeight){
                    bottomDown = true;
                }

                if (bottomDown){
                    bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            bottomBorders.get(bottomBorders.size()-1).getX()+20,
                            bottomBorders.get(bottomBorders.size()-1).getY() + 1));
                }

                else {
                    bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            bottomBorders.get(bottomBorders.size()-1).getX()+20,
                            bottomBorders.get(bottomBorders.size()-1).getY() - 1));
                }

            }
        }
    }
    public void newGame(){

        dissapear = false;

        bottomBorders.clear();
        topBorders.clear();

        missiles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetScore();
        player.setY(HEIGHT/2);

        //initial top border
        for (int i = 0; i*20<WITDH+40; i++){
            //first top border created
            if (i == 0) {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i, 0, 10));
            }
            else{
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i*20, 0, topBorders.get(i-1).getHeight()+1));
            }
        }
        //initial bottom border
        for (int i = 0; i*20<WITDH+40; i++){
            //first bottom border created
            if (i == 0) {
                bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i, HEIGHT-4*minBorderHeight));
            }
            //adding borders until initial screen is filed
            else{
                bottomBorders.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i*20,bottomBorders.get(i-1).getY()-1));
            }
        }
        newGameCreated = true;
    }

    private void drawText(Canvas canvas) {

        if (player.getScore() > best){
            best = player.getScore();
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + player.getScore(), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WITDH- 215, HEIGHT - 10, paint);

        if (!player.getPlaying() && newGameCreated && reset){
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WITDH/2 - 50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WITDH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WITDH/2-50, HEIGHT/2 + 40, paint1);

        }
    }
}
