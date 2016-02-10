package wear.fjordonez.headmotioncursor.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;

import wear.fjordonez.headmotioncursor.R;
import wear.fjordonez.headmotioncursor.motion.HeadCursorMotionListener;
import wear.fjordonez.headmotioncursor.motion.HeadCursorMotionHandler;


/**
 * Created by fjordonez on 08/12/15.
 */
public class GridDemo extends Activity implements HeadCursorMotionListener{

    private static float[][] MOTION_SENSITIVITIES = {{55, 31}, {43, 24}, {31, 17}, {19, 10}, {7, 3}};

    private static final String TAG = GridDemo.class.getSimpleName();

    private HeadCursorMotionHandler mHeadCursorMotionHandler;

    private int mHoverCellIndex;
    private int mIinitHoverCellIndex;
    private int mNumberColumns;
    private int mNumberRows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHeadCursorMotionHandler = new HeadCursorMotionHandler(this);
        mHeadCursorMotionHandler.setCursorMotionListener(this);
        setContentView(R.layout.grid_layout);
        setupLayout();
    }

    /**
     * Method to dynamically generate the grid to move the demo cursor
     */
    private void setupLayout() {
        GridLayout gl = (GridLayout)findViewById(R.id.grid_layout);
        gl.removeAllViews();
        mNumberColumns = getResources().getInteger(R.integer.grid_columns);
        mNumberRows = getResources().getInteger(R.integer.grid_rows);
        mIinitHoverCellIndex = ( (((int)Math.floor(mNumberRows / 2) * mNumberColumns) + ((int) Math.floor(mNumberColumns/2))));
        mHoverCellIndex = mIinitHoverCellIndex;
        int cell_size = (int) getResources().getDimension(R.dimen.grid_cell);
        gl.setColumnCount(mNumberColumns);
        gl.setRowCount(mNumberRows);
        for (int i = 0, c = 0, r = 0; i < mNumberColumns * mNumberRows; i++, c++) {
            if (c == mNumberColumns) {
                c = 0;
                r++;
            }
            TextView oTextView = new TextView(this);
            oTextView.setId(i);
            oTextView.setTextColor(Color.BLACK);
            oTextView.setHeight(cell_size);
            oTextView.setWidth(cell_size);
            oTextView.setGravity(Gravity.CENTER);
            oTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getResources().getDimension(R.dimen.grid_cell_text_size));
            gl.addView(oTextView);
        }
        setHover(mIinitHoverCellIndex);
    }

    /**
     * Method to update the hovered cell in the grid
     *
     * @param hoverIndex Index of the cell to hover
     */
    private void updateGridLayout_HoverCell(int hoverIndex) {
        if (hoverIndex != mHoverCellIndex){
            removeHover(mHoverCellIndex);
            setHover(hoverIndex);
            mHoverCellIndex = hoverIndex;
        }
    }

    private void setHover(int index){
        TextView textView = (TextView) findViewById(index);
        textView.setBackgroundResource(R.color.hover);
    }

    private void removeHover(int index){
        TextView textView = (TextView) findViewById(index);
        textView.setBackgroundResource(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int idx_s = PreferenceManager.getDefaultSharedPreferences(this).getInt("sensitivity", 2);
        mHeadCursorMotionHandler.start(MOTION_SENSITIVITIES[idx_s]);
    }

    @Override
    protected void onPause() {
        mHeadCursorMotionHandler.stop();
        super.onPause();
    }

    /**
     * Method to calculate the index of the cell to hover, based on the 2D coordenates
     *
     * @param x Row location of the cell to hover
     * @param y Column location of the cell to hover
     */
    private void updateCoordenates(float x, float y){
        int hoverIndex = mIinitHoverCellIndex - (mNumberColumns * ((int) y));
        hoverIndex = hoverIndex + ((int) x);
        updateGridLayout_HoverCell(hoverIndex);
    }

    /**
     * Method to calculate which cell must be hovered, based on the 2D index
     *
     * @param values Two 2D (x,y) coordenates defining the location of the cursor within the range [-1,+1]
     */
    @Override
    public void onPositionChanged(float[] values) {
        int grid_x = Math.round(values[0]*(mNumberColumns/2));
        int grid_y = Math.round(values[1]*(mNumberRows/2));
        if (grid_x>(mNumberColumns/2)){
            grid_x=(mNumberColumns/2);
        }else if (grid_x<mNumberColumns/-2){
            grid_x=mNumberColumns/-2;
        }
        if (grid_y>mNumberRows/2){
            grid_y=mNumberRows/2;
        }else if (grid_y<mNumberRows/-2){
            grid_y=mNumberRows/-2;
        }
        updateCoordenates(grid_x, grid_y);
    }
}
