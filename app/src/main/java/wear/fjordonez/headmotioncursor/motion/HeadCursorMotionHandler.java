package wear.fjordonez.headmotioncursor.motion;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by fjordonez on 17/12/15.
 */
public class HeadCursorMotionHandler implements SensorEventListener {

    private static final String TAG = HeadCursorMotionHandler.class.getSimpleName();

    private SensorManager mSensorManager;
    private static float[] mRange;
    private boolean isStarted;
    private float[] mPos = new float[2];
    private float[] mPos_o = new float[2];

    private static HeadCursorMotionListener mHeadCursorMotionListener;

    public HeadCursorMotionHandler(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Start the motion handler
     *
     * @param range Maximum rotational span to define the size of the 2D space.
     * @param posAxis_o Initial 2D location per axis. Each axis value must be in the range [-1,1].
     */
    public void start(float[] range, float[] posAxis_o) {
        Sensor sensor;
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (sensors.size() > 1) {
            // Google Glass has two gyroscopes: "MPL Gyroscope" and "Corrected Gyroscope Sensor". Try the later one.
            sensor = sensors.get(1);
        } else {
            sensor = sensors.get(0);
        }
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        mRange = range;
        isStarted = false;
        mPos_o[0] = posAxis_o[0] * mRange[0];
        mPos_o[1] = posAxis_o[1] * mRange[1];
    }

    /**
     * Start the motion handler
     *
     * @param range Maximum rotational span to define the size of the 2D space.
     */
    public void start(float[] range) {
        start(range, new float[]{0,0});
    }

    /**
     *  Stop the motion handler
     */
    public void stop() {
        try {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
        } catch (Exception e) {
        }
    }

    public void setCursorMotionListener(HeadCursorMotionListener headCursorMotionListener) {
        this.mHeadCursorMotionListener = headCursorMotionListener;
    }

    /**
     * Method to update the listener using the current 2D location of the cursor.
     * Both two 2D (x,y) coordenates are defined within the range [-1,+1]. Therefore the bottom left
     * corner of the view is identified by the coordenates (-1,-1) and the upper right corner by the
     * coordenates (+1,+1).
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float[] gyroValues = event.values.clone();
            //The first values define the initial relative position of the cursor
            if (!isStarted) {
                isStarted = true;
                mPos[0] = gyroValues[1] + mPos_o[0];
                mPos[1] = gyroValues[0] + mPos_o[1];
            }else{
                mPos[0] += gyroValues[1];
                mPos[1] += gyroValues[0];
                if (Math.abs(mPos[0]) > mRange[0]){
                    mPos[0] = (mPos[0]/Math.abs(mPos[0])) * mRange[0];
                }
                if (Math.abs(mPos[1]) > mRange[1]){
                    mPos[1] = (mPos[1]/Math.abs(mPos[1])) * mRange[1];
                }
                mHeadCursorMotionListener.onPositionChanged(new float[]{-1 * mPos[0]/mRange[0],mPos[1]/mRange[1]});
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
