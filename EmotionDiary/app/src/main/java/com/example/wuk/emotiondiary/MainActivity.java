package com.example.wuk.emotiondiary;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.litepal.LitePal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnPre, btnNext, btnCalendar, btnCard;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    public static ImageButton btnSet;

    public static PopupWindow deletePopupWindow;
    public static LinearLayout pop_linearLayout;
    public static PopupWindow watching_popupWindow;
    public static TextView  pop_year_text;
    public static TextView pop_month_text;
    public static TextView pop_day_text;
    public static TextView pop_content_text;
    public static ImageButton pop_share_button;
    public static ImageButton pop_exit_button;
    public static ImageView pop_emotion_View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.getDatabase();
        initView();
        getPermission();
    }

    private void initView() {
        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);
        btnCard = (ImageButton) findViewById(R.id.btnCard);
        btnSet = (ImageButton) findViewById(R.id.btnSet);

        btnCalendar.setOnClickListener(this);
        btnCard.setOnClickListener(this);

        replaceFragment(new Fgm_Calendar());
        btnCalendar.setImageResource(R.drawable.calendar_plus);
        btnCard.setImageResource(R.drawable.card);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCalendar:
                replaceFragment(new Fgm_Calendar());
                btnCalendar.setImageResource(R.drawable.calendar_plus);
                btnCard.setImageResource(R.drawable.card);
                break;
            case R.id.btnCard:
                replaceFragment(new Fgm_Card());
                btnCalendar.setImageResource(R.drawable.calendar);
                btnCard.setImageResource(R.drawable.card_plus);
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mFragment, fragment);
        transaction.commit();
    }

    private static int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static  String[] PERMISSIONS_STORAGE2 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void getPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE2,REQUEST_PERMISSION_CODE);
        }
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
