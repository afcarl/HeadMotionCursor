# HeadMotionCursor
Google Glass specific library to calculate the head position and use it to define a two-dimensional motion relative to the view.

This code involve listening to the gyroscopes manually and translating those values into movement within the visible region of the view.
This solution relies just on the gyroscopes values, but it can be also implemented using the rotation vector sensor by modifying the operations in
the onSensorChanged method of the handler to include the call to Sensor.TYPE_ROTATION_VECTOR.

```java

public class GridDemo extends Activity implements HeadCursorMotionListener{

    private HeadCursorMotionHandler mHeadCursorMotionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	...
        mHeadCursorMotionHandler = new HeadCursorMotionHandler(this);
        mHeadCursorMotionHandler.setCursorMotionListener(this);
	...
    }


    @Override
    protected void onResume() {
	...
        mHeadCursorMotionHandler.start(new float[]{x_axis_xpan, y_axis_span});
	...
    }

    @Override
    protected void onPause() {
	...
        mHeadCursorMotionHandler.stop();
        ...
    }


    @Override
    public void onPositionChanged(float[] values) {
        //Do something
    }
}

```
