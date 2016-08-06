package home.gyrogame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.view.View;
import android.view.ViewGroup;

public class Stick {

    public static final int STICK_MIDDLE = 0;
    public static final int STICK_LEFT = 1;
    public static final int STICK_RIGHT = 2;
    private final int stick_height;
    private final int stick_width;
    private final int zeroY;
    private final int zeroX;

    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;

    private Bitmap stick;
    private Paint paint;
    private DrawCanvas draw;


    public Stick(Context context, ViewGroup layout, int res_stick_id){
        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(), res_stick_id);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
        zeroX = params.width / 2;
        zeroY = params.height/2;

    }
    public void drawStick(SensorEvent event){


    }


    private class DrawCanvas extends View {
        double x,y;

        private DrawCanvas(Context mContext){
            super(mContext);

        }
        private void position(float x, float y){

        }
    }
}
