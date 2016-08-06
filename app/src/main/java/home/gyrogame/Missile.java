package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Missile extends GameObject{

    private int speed;
    private int score;
    private Bitmap spritsheet;
    private Animation animation;
    private Random random;

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames){

        this.x = x;
        this.y = y;
        height = h;
        width = w;
        score = s;
        spritsheet = res;
        animation = new Animation();
        random = new Random();

        speed = 7 + (int) (random.nextDouble() * score/30);

        //cap missile speed
        if(speed > 40) speed = 40;

        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length; i++){
            image[i] = Bitmap.createBitmap(spritsheet, 0, i*height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100-speed);

    }
    public void update(){
        x-=speed;
        animation.update();

    }
    public void draw(Canvas canvas){

        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public int getWidth(){

        //offset slightly for more realistic collision detection
        return width-10;
    }

}







