package com.example.wuk.emotiondiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.List;

public class addActivity extends AppCompatActivity {
    private TextView year_textView;
    private TextView month_textView;
    private TextView day_textView;
    private EditText content_edit;
    private ImageButton finish_button;
    private ImageButton veryHappy_button;
    private ImageButton Happy_button;
    private ImageButton calm_button;
    private ImageButton sad_button;
    private ImageButton angry_button;
    private ImageButton exit_button;
    private Intent intent;
    private Day day_Edit = new Day();
    private int imageButton_clickCount = 0;
    private int status = 0;
    private int state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        findViewById0();
        intent = getIntent();

        if (intent.getIntExtra("put_status",0)==2){
            state = 2;
            day_Edit =(Day) intent.getSerializableExtra("day_modify");
            status = day_Edit.getState();
            image_clicklisten();//设置表情
            year_textView.setText(""+ day_Edit.getYear());
            month_textView.setText(""+ day_Edit.getMonth());
            day_textView.setText(""+ day_Edit.getDay());
            if (day_Edit.getContent().equals("一个懒汉在这天拒绝打字")) {
                day_Edit.setContent("");
            }
            content_edit.setText(day_Edit.getContent());
            content_edit.setSelection(day_Edit.getContent().length());
        }

        if (intent.getIntExtra("put_status",0)==1){
            state = 1;
            int month = intent.getIntExtra("month",1);
            int year = intent.getIntExtra("year",1);
            int day = intent.getIntExtra("day",1);
            day_Edit.setDay(day);
            day_Edit.setMonth(month);
            day_Edit.setYear(year);
            year_textView.setText(""+year);
            month_textView.setText(""+month);
            day_textView.setText(""+day);
        }

        veryHappy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = Day.state_VeryHappy;
                image_clicklisten();
            }
        });
        Happy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = Day.state_happy;
                image_clicklisten();
            }
        });
        calm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = Day.state_calm;
                image_clicklisten();
            }
        });
        sad_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = Day.state_gray;
                image_clicklisten();
            }
        });
        angry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status =Day.state_angry;
                image_clicklisten();
            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = content_edit.getText().toString();
                if (state == 1){
                    if (imageButton_clickCount == 0){
                        Toast.makeText(addActivity.this,"请选择心情",Toast.LENGTH_SHORT).show();
                    }else {
                        if(content.equals("")){
                            content = "一个懒汉在这天拒绝打字";
                        }
                        day_Edit.setState(status);
                        day_Edit.setContent(content);
                        Intent intent1 = new Intent();
                        intent1.putExtra("day_return", day_Edit);
                        setResult(RESULT_OK,intent1);
                        List<Day> dayList = LitePal.where("year = ? and month = ? and day = ?",
                                ""+day_Edit.getYear(), ""+day_Edit.getMonth(), ""+day_Edit.getDay()).find(Day.class);

                        if (dayList.size() > 0) {
                            Day day = new Day();
                            day.setContent(day_Edit.getContent());
                            day.setState(day_Edit.getState());
                            day.updateAll("year = ? and month = ? and day = ?",
                                    ""+day_Edit.getYear(), ""+day_Edit.getMonth(), ""+day_Edit.getDay());
                        } else {
                            day_Edit.save();
                        }
                        finish();
                    }
                }
                else if (state == 2){
                    if (imageButton_clickCount == 0){
                        Toast.makeText(addActivity.this,"请选择心情",Toast.LENGTH_SHORT).show();
                    }else {
                        if (content.equals("")) {
                            content = "一个懒汉在这天拒绝打字";
                        }
                    }
                    day_Edit.setState(status);
                    day_Edit.setContent(content);
                    Intent intent2 = new Intent();
                    intent2.putExtra("day_modify_return", day_Edit);
                    setResult(2,intent2);
                    List<Day> dayList = LitePal.where("year = ? and month = ? and day = ?",
                            ""+day_Edit.getYear(), ""+day_Edit.getMonth(), ""+day_Edit.getDay()).find(Day.class);

                    if (dayList.size() > 0) {
                        Day day = new Day();
                        day.setContent(day_Edit.getContent());
                        day.setState(day_Edit.getState());
                        day.updateAll("year = ? and month = ? and day = ?",
                                ""+day_Edit.getYear(), ""+day_Edit.getMonth(), ""+day_Edit.getDay());
                    } else {
                        day_Edit.save();
                    }
                    finish();
                }
            }
        });




    }
    public void image_clicklisten(){//实现点击一下选择的按钮显示到中间，再点击一下返回原样
        imageButton_clickCount ++;
        if (imageButton_clickCount == 1){
            Happy_button.setVisibility(View.INVISIBLE);
            calm_button.setBackground(null);
            sad_button.setVisibility(View.INVISIBLE);
            angry_button.setVisibility(View.INVISIBLE);
            veryHappy_button.setVisibility(View.INVISIBLE);
            switch (status){
                case Day.state_angry:
                    calm_button.setBackgroundResource(R.drawable.emotionangry);
                    break;
                case Day.state_calm:
                    calm_button.setBackgroundResource(R.drawable.emotionclam);
                    break;
                case Day.state_gray:
                    calm_button.setBackgroundResource(R.drawable.emotionsad);
                    break;
                case Day.state_happy:
                    calm_button.setBackgroundResource(R.drawable.emotionhappy);
                    break;
                case Day.state_VeryHappy:
                    calm_button.setBackgroundResource(R.drawable.emotionecstasy);
                    break;
                default:
                    break;
            }
        }
        if (imageButton_clickCount == 2){
            imageButton_clickCount = 0;
            calm_button.setBackgroundResource(R.drawable.emotionclam);
            veryHappy_button.setVisibility(View.VISIBLE);
            Happy_button.setVisibility(View.VISIBLE);
            sad_button.setVisibility(View.VISIBLE);
            angry_button.setVisibility(View.VISIBLE);
            status = Day.state_null;
        }
    }

    public void findViewById0(){
        exit_button = findViewById(R.id.exit_button);
        year_textView = findViewById(R.id.year_text);
        month_textView = findViewById(R.id.month_text);
        day_textView = findViewById(R.id.day_text);
        content_edit = findViewById(R.id.content_exit);
        finish_button = findViewById(R.id.finish_button);
        veryHappy_button = findViewById(R.id.Veryhappy_button);
        Happy_button = findViewById(R.id.happy_button);
        calm_button = findViewById(R.id.calm_button);
        angry_button = findViewById(R.id.angry_button);
        sad_button = findViewById(R.id.sad_button);
    }
}