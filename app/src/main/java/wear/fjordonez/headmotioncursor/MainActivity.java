package wear.fjordonez.headmotioncursor;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import wear.fjordonez.headmotioncursor.card.CardAdapter;
import wear.fjordonez.headmotioncursor.demo.GridDemo;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    SharedPreferences mSharedPreferences;

    static final int DEMO = 0;
    static final int SENSITIVITY_SETTINGS = 1;

    private enum Sensitivity {
        VERY_SLOW(R.string.text_card_sensitivity_speed_1),
        SLOW(R.string.text_card_sensitivity_speed_2),
        NORMAL(R.string.text_card_sensitivity_speed_3),
        FAST(R.string.text_card_sensitivity_speed_4),
        VERY_FAST(R.string.text_card_sensitivity_speed_5);
        final int textId;
        Sensitivity(int textId) {
            this.textId = textId;
        }
    }

    private final Handler mHandler = new Handler();

    private CardScrollAdapter mAdapter;
    private CardScrollView mCardScroller;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
    }

    /**
     * Create a list of cards
     */
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<>();

        cards.add(DEMO, new CardBuilder(context, CardBuilder.Layout.CAPTION)
                .setText(R.string.text_card_demo));

        Sensitivity sensitivity = Sensitivity.values()[mSharedPreferences.getInt("sensitivity",2)];
        cards.add(SENSITIVITY_SETTINGS, new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.text_card_sensitivity_settings) + " " + getString(sensitivity.textId))
                .setIcon(R.drawable.ic_sensitivity_150));

        return cards;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    /**
     * Method the define the actions when tapping on a card
     */
    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                int soundEffect = Sounds.TAP;
                switch (position) {
                    // A new activity for the demo is called
                    case DEMO:
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                startDemo();
                            }
                        });
                        break;

                    // Definition of the sensitivity for the motion handler (maximum rotational span)
                    case SENSITIVITY_SETTINGS:
                        int nb_sensitivities = Sensitivity.values().length;
                        int s = mSharedPreferences.getInt("sensitivity",2);
                        if (s == (nb_sensitivities - 1)) {
                            s = 0;
                        } else {
                            s = s + 1;
                        }
                        Sensitivity sensitivity = Sensitivity.values()[s];
                        CardBuilder cardSensitivity = (CardBuilder) mAdapter.getItem(SENSITIVITY_SETTINGS);
                        cardSensitivity.setText(getString(R.string.text_card_sensitivity_settings) + " " + getString(sensitivity.textId));
                        mCardScroller.getAdapter().notifyDataSetChanged();
                        editor.putInt("sensitivity", s);
                        editor.commit();
                        break;

                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't do anything");
                }
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

    private void startDemo() {
        startActivity(new Intent(this, GridDemo.class));
    }


}
