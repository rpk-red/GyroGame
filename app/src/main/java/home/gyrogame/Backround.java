package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Backround {

    private Bitmap image;
    private int x, y, dx;

    public Backround(Bitmap res) {

        image = res;
        dx = GamePanel.GAMESPEED;
    }
    public void update() {
        x += dx;
        if(x<-GamePanel.WITDH){
            x = 0;
        }
    }
    public void draw(Canvas canvas) {

        canvas.drawBitmap(image,x,y,null);
        if (x<0){
            canvas.drawBitmap(image, x+GamePanel.WITDH, y, null);
        }

    }


}
