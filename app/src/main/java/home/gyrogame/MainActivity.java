package home.gyrogame;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 10;
            static final String LOG_TAG = "MainActivity";
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    TextView textX, textY, textZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //turn tittle off
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //sets full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new GamePanel(this));
//        initGyro();
//
//        textX = (TextView) findViewById(R.id.textViewX);
//        textY = (TextView) findViewById(R.id.textViewY);
//        textZ = (TextView) findViewById(R.id.textViewZ);
//        Log.d(LOG_TAG,"onCreate");

    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
//
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
//            float axisX = event.values[0];
//            float axisY = event.values[1];
//            float axisZ = event.values[2];
//            Log.d(LOG_TAG,"Sensor changed");
//            textX.setText("X: " + (int)axisX + " rad/s");
//            textY.setText("Y: " + (int)axisY + " rad/s");
//            textZ.setText("Z: " + (int)axisZ + " rad/s");
//
//        }
//
////            if (timestamp != 0) {
////                final float dT = (event.timestamp - timestamp) * NS2S;
////
////                float axisX = event.values[0];
////                float axisY = event.values[1];
////                float axisZ = event.values[2];
////
////                float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
////
////
////                if (omegaMagnitude > EPSILON) {
////                    axisX /= omegaMagnitude;
////                    axisY /= omegaMagnitude;
////                    axisZ /= omegaMagnitude;
////                }
////
////
////                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
////                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
////                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
////                deltaRotationVector[0] = sinThetaOverTwo * axisX;
////                deltaRotationVector[1] = sinThetaOverTwo * axisY;
////                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
////                deltaRotationVector[3] = cosThetaOverTwo;
////            }
////            timestamp = event.timestamp;
////            float[] deltaRotationMatrix = new float[9];
////            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
//
//    }
//
//    private void initGyro(){
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        Log.d(LOG_TAG,"Sensor init");
//
//    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
      //  mSensorManager.unregisterListener(this,mSensor);
        Log.d(LOG_TAG,"onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_GAME);
        Log.d(LOG_TAG,"onResume");

    }
}
