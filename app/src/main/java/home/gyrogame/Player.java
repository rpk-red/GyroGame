package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap spritsheet;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation;
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames){

        playing = false;
        x = 100;
        y = GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        height = h;
        width = w;
        spritsheet = res;
        Bitmap[] image = new Bitmap[numFrames];
        animation = new Animation();

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritsheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean b){up = b;}

    public void update(){
        long elapsed = (System.nanoTime() - startTime)/1000000;
        if (elapsed>100){
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if (up){
            dy -= 1;
        }
        else {
            dy += 1;
        }

        if (dy>14)dy = 14;
        if (dy<-14)dy = -14;

        y += dy*2;

//        if (y < 0) y = 0;
//        if (y > (GamePanel.HEIGHT - height)) y = GamePanel.HEIGHT - height;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
    public int getScore(){return score;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean playing) {this.playing = playing;}
    public void resetDY(){dy = 0;}
    public void resetScore(){score =0;}

}
