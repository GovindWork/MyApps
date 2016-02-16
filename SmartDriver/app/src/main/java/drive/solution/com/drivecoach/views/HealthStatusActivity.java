package drive.solution.com.drivecoach.views;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.util.Locale;

import drive.solution.com.drivecoach.R;
import drive.solution.com.drivecoach.services.HealthStatusUpdate;


public class HealthStatusActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;

    private TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_status);

        textToSpeech = new TextToSpeech(this, this);

        textName = (TextView) findViewById(R.id.label_name);

        Intent intent = new Intent(getApplicationContext(), HealthStatusUpdate.class);
        startService(intent);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            } else {
                textToSpeech.setSpeechRate(1);
                textToSpeech.speak("Welcome Mister" + textName.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            Log.e("ERROR", "TEXT TO SPEECH NOT INITIALIZED.");
        }
    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
