package com.example.alarmer.Alarm;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmer.R;
import com.example.alarmer.Ui.MainActivity;

import java.util.Locale;
import java.util.Random;

public class AlarmActivity extends AppCompatActivity {


    Ringtone ringtone;

    Vibrator v;
    long[] pattern = {0, 500, 500};

    Button check_btn;

    boolean answered = false;

    int num_1, num_2, solution;

    String sign;

    TextView question;

    EditText answer;
    private int MAX_NUM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("azzazichka", "answered = " + answered);
        add_params2activity();


        setContentView(R.layout.activity_alarm);

        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);

        String get_text = getIntent().getStringExtra("question");

        if (get_text != null){
            num_1 = Integer.parseInt(get_text.split(" ")[0]);
            sign = get_text.split(" ")[1];
            num_2 = Integer.parseInt(get_text.split(" ")[2]);
            solution = sign.equals("+")? num_1 + num_2 : (sign.equals("-")? num_1 - num_2 : (sign.equals("*")? num_1 * num_2 : num_1 / num_2));
        } else {
            generate_question_values();
        }

        show_question();

        check_btn = findViewById(R.id.check_answer);

        check_btn.setOnClickListener(view -> {
            if (answer.getText().toString().equals(String.valueOf(solution))){
                answered = true;
                if (ringtone != null && ringtone.isPlaying()) {
                    ringtone.stop();
                }
                v.cancel();

                Intent main_activity_intent = new Intent(this, MainActivity.class);
                main_activity_intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);

                MainActivity.idx_turnOff = getIntent().getIntExtra("alarm_index", -1);

                Log.i("azzazichka", "alarm_index = " + MainActivity.idx_turnOff + " alarm_activity");

                startActivity(main_activity_intent);
            } else {
                Toast.makeText(this, "Неправильно", Toast.LENGTH_SHORT).show();
                answer.setText(null);
            }

        });


        start_sound();


    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("azzazichka", "stopped alarm activity");

        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        v.cancel();

        if (!answered) {

            Intent alarm_intent = new Intent();
            alarm_intent.setAction("com.example.alarmer.RELOAD_PAGE");
            alarm_intent.putExtra("alarm_index", getIntent().getIntExtra("alarm_index", -1));
            alarm_intent.putExtra("question", question.getText());
            sendBroadcast(alarm_intent);
        }
    }

    private void generate_question_values() {
        final Random rand = new Random();

        sign = new String[]{"+", "-", "*", "/"}[rand.nextInt(4)];

        MAX_NUM = "/".equals(sign) || "*".equals(sign) ? 10 : 100;

        num_2 = rand.nextInt(MAX_NUM) + 1;

        if (sign.equals("/")) {
            num_1 = num_2 * (rand.nextInt(5) + 1);
            solution = num_1 / num_2;
        } else {
            num_1 = rand.nextInt(MAX_NUM) + 1;
            solution = sign.equals("+")? num_1 + num_2 : (sign.equals("-")? num_1 - num_2 : num_1 * num_2);
        }
    }

    private void show_question() {
        question.setText(String.format(Locale.getDefault(), "%d %s %d =      ", num_1, sign, num_2));

    }


    private void start_sound() {
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);

        if (ringtone == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        }

        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        v.vibrate(pattern, 0);
    }

    private void add_params2activity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);

        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onBackPressed() {
    }

}



