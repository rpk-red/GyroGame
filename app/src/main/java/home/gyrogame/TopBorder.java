package home.gyrogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TopBorder extends GameObject
{
    private Bitmap image;

    public TopBorder(Bitmap res, int x, int y, int h){

        super.x = x;
        super.y = y;
        height = h;
        width = 20;
        dx = GamePanel.GAMESPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }
    public void update(){x += dx;}
    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(image, x, y, null);
        }catch (Exception e){e.printStackTrace();}
    }
}
