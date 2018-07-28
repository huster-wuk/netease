package com.example.wuk.emotiondiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static org.litepal.LitePalApplication.getContext;

public class WatchingActivity extends AppCompatActivity {
    private TextView Date_text;
    private TextView Content_text;
    private TextView saying_text;
    private ImageView emotion;
    private ImageButton exit_bitton;
    private ImageButton share_button;
    private Intent intent;
    private Day day_show;
    private LinearLayout watching_Linear;
    private ImageView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching);
        findViewById0();
        intent = getIntent();
        if (intent.getIntExtra("put_status2",0)!=0){
            day_show =(Day)intent.getSerializableExtra("show_day");
            Date_text.setText(""+day_show.getYear()+"/"+day_show.getMonth()+"/"+day_show.getDay());
            String content = day_show.getContent();
            if (content.equals("一个懒汉在这天拒绝打字")){
                saying_text.setText("这天我");
                Content_text.setText("无话可讲!");
            }else {
                saying_text.setText("这天我有话讲");
                Content_text.setText(content);
            }
            switch (day_show.getState()){
                case Day.state_angry:
                    emotion.setImageResource(R.drawable.emotionangry);
                    break;
                case Day.state_gray:
                    emotion.setImageResource(R.drawable.emotionsad);
                    break;
                case Day.state_calm:
                    emotion.setImageResource(R.drawable.emotionclam);
                    break;
                case Day.state_happy:
                    emotion.setImageResource(R.drawable.emotionhappy);
                    break;
                case Day.state_VeryHappy:
                    emotion.setImageResource(R.drawable.emotionecstasy);
                    break;
                default:
                    break;
            }
        }

        exit_bitton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = getViewBitmap(watching_Linear);
                test.setImageBitmap(bitmap);
                String filePath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),     bitmap, null, null);
                //filepath是uri的路径
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                Cursor cursor = null;
                Context context = getContext();
                Uri uri = Uri.parse(filePath);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "share content");
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent,"image share "));


            }
        });


    }

    public void findViewById0(){
        watching_Linear = findViewById(R.id.watching_Linear_View);
        test = findViewById(R.id.test_emotion);
        Date_text = findViewById(R.id.watching_date_text);
        Content_text = findViewById(R.id.watching_content_text);
        saying_text = findViewById(R.id.watching_saying_text);
        emotion = findViewById(R.id.watching_emotionView);
        exit_bitton = findViewById(R.id.watching_exit_button);
        share_button = findViewById(R.id.watching_share_button);
    }

    public static Bitmap getViewBitmap(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }

        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bitmap;
    }
}
