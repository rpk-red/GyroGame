package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Explosion {
    private int x;
    private int y;
    private int height;
    private int width;
    private int row;
    private Animation animation;
    private Bitmap spritesheet;

    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames){

        this.x = x;
        this.y = y;
        height = h;
        width = w;
        spritesheet = res;
        animation = new Animation();
        row = 0;

        Bitmap[] image = new Bitmap[numFrames];

        for(int i=0; i<image.length; i++){
            if (i % 5 == 0 && i > 0) row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
    }
    public void update(){

        if (!animation.playedOnce()){
            animation.update();
        }
    }
    public void draw(Canvas canvas){

        if (!animation.playedOnce()){
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }

    public int getHeight() {return height;}

    public int getWidth() {return width;}
}
