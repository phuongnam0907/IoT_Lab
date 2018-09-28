package com.example.nxnam.blinky_rgb_led;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static int INTERVAL_BETWEEN_BLINKS_MS = 500;
    private Handler mHandler = new Handler();
    private Gpio mLedGpio;
    private boolean mLedState = false;
    private Gpio[] mRGBLedGpio = new Gpio[3];
    private int mRGBLedState = 0;
    private static final int exercise = 3;
    private static Button mButton;

    private static final String PWM_NAME = "PWM1";
    private Pwm mPwm;
    private static int mPwnState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mButton = findViewById(R.id._mBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRate();
                Log.i(TAG, "On Click _mBtn " + INTERVAL_BETWEEN_BLINKS_MS);
            }
        });
        Log.i(TAG, "Starting BlinkActivity");
        String pinName = BoardDefaults.getGPIOForLED();
        if (pinName == "RPI3") {
            switch (exercise){
                case 1:
                    try {
                        mLedGpio = PeripheralManager.getInstance().openGpio("BCM6");
                        mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        Log.i(TAG, "Start blinking LED GPIO pin");
                        mHandler.post(mBlinkRunnable);
                    } catch (IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                    break;
                case 2:
                    try {
                        mRGBLedGpio[0] = PeripheralManager.getInstance().openGpio("BCM5");
                        mRGBLedGpio[0].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        Log.i(TAG, "Start RGB LED GPIO pin BCM5");

                        mRGBLedGpio[1] = PeripheralManager.getInstance().openGpio("BCM6");
                        mRGBLedGpio[1].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        Log.i(TAG, "Start RGB LED GPIO pin BCM6");

                        mRGBLedGpio[2] = PeripheralManager.getInstance().openGpio("BCM13");
                        mRGBLedGpio[2].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                        Log.i(TAG, "Start RGB LED GPIO pin BCM13");

                        mHandler.post(mBlinkRunnable);
                    } catch (IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                    break;
                case 3:

                    try {

                        PeripheralManager manager = PeripheralManager.getInstance();
                        mPwm = manager.openPwm(PWM_NAME);
                        Log.i(TAG, "Start PWM GPIO pin BCM13");
                        initializePwm(mPwm);

                        mHandler.post(mBlinkRunnable);
                    } catch (IOException e) {
                        Log.w(TAG, "Unable to access PWM", e);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private void changeRate() {
        switch (INTERVAL_BETWEEN_BLINKS_MS)
        {
            case 2000:
                INTERVAL_BETWEEN_BLINKS_MS = 1000;
                break;
            case 1000:
                INTERVAL_BETWEEN_BLINKS_MS = 500;
                break;
            case 500:
                INTERVAL_BETWEEN_BLINKS_MS = 100;
                break;
            case 100:
                INTERVAL_BETWEEN_BLINKS_MS = 2000;
                break;
                default:
                    INTERVAL_BETWEEN_BLINKS_MS = 2000;
                    break;

        }
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            switch (exercise){
                case 1:
                    if (mLedGpio == null){
                        return;
                    }

                    try {
                        mLedState = !mLedState;
                        mLedGpio.setValue(mLedState);
                        Log.d(TAG, "State set to " + mLedState);
                        mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
                    } catch (IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; i++) {
                        if (mRGBLedGpio[i] == null)
                            return;
                    }

                    try {
                        mRGBLedState = (mRGBLedState + 1) % 8;
                        for (int i = 0; i < 3; i++) {
                            mRGBLedGpio[i].setValue((mRGBLedState & i) == i);

                            //mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
                        }
                        Log.d(TAG, "State set to " + mRGBLedState);
                        mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
                    } catch(IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                    break;
                case 3:
                    if (mPwm == null){
                        return;
                    }

                    mPwnState = (mPwnState + 1) % 101;

                    try {
                        mPwm.setPwmDutyCycle(mPwnState);
                        Log.i(TAG,"PWM Duty Cycle" + mPwnState);
                    } catch (IOException e) {
                        Log.e(TAG, "Error on PeripheralIO API", e);
                    }
                    mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
                    break;
                default:
                        break;
            }

        }
    };
    public void initializePwm(Pwm pwm) throws IOException {
        pwm.setPwmFrequencyHz(120);
        pwm.setPwmDutyCycle(10);
        Log.i(TAG,"Init PWM" + pwm);

        // Enable the PWM signal
        pwm.setEnabled(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mBlinkRunnable);
        Log.i(TAG, "Closing GPIO pin");
        switch (exercise) {
            case 1:
                try {
                    mLedGpio.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error on PeripheralIO API", e);
                } finally {
                    mLedGpio = null;
                }
                break;
            case 2:

                try {
                    for (int i = 0; i < 3; i++) {
                        mRGBLedGpio[i].close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error on PeripheralIO API", e);
                } finally {
                    for (int i = 0; i < 3; i++){
                        mRGBLedGpio[i] = null;
                    }
                }
            case 3:
                if (mPwm != null) {
                    try {
                        mPwm.close();
                        mPwm = null;
                    } catch (IOException e) {
                        Log.w(TAG, "Unable to close PWM", e);
                    }
                }
                break;
        }

    }

}
