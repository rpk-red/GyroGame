package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BottomBorder extends GameObject{

    private Bitmap image;

    public BottomBorder(Bitmap res, int x, int y){

        height = 200;
        this.x = x;
        this.y = y;
        dx = GamePanel.GAMESPEED;
        width = 20;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }
    public void update(){
        x += dx;
    }
    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(image, x, y, null);
        }catch (Exception e){e.printStackTrace();}

    }
}
