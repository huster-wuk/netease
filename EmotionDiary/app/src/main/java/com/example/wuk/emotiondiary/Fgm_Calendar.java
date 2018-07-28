package com.example.wuk.emotiondiary;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fgm_Calendar extends Fragment implements CalendarCard.OnCellClickListener, View.OnClickListener {

    private ViewPager mViewPager;
    private int mCurrentIndex = 233;
    private int currentMonth;
    private int currentYear;
    private int currentDay;
    private CalendarCard[] mShowViews;
    private CalendarViewAdapter<CalendarCard> adapter;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private ImageButton btnPre, btnNext, btnSet, btnAddDiary;
    private TextView tvHint;
    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    public Fgm_Calendar() {
        // Required empty public constructor
    }

    //test
    private static int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fgm__calendar, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_calendar);
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this.getContext(), this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();

        currentMonth = DateUtil.getMonth();
        currentYear = DateUtil.getYear();
        currentDay = DateUtil.getCurrentMonthDay();

        tvHint = view.findViewById(R.id.tvHint);

        btnPre = getActivity().findViewById(R.id.btnPre);
        btnNext = getActivity().findViewById(R.id.btnNext);
        btnSet = getActivity().findViewById(R.id.btnSet);
        btnAddDiary = view.findViewById(R.id.btnAddDiary);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSet.setOnClickListener(this);
        btnAddDiary.setOnClickListener(this);

        return view;
    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(233);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);
                setHint();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }
    @Override
    public void clickDate(CustomDate date) {
        Day day = new Day();

        List<Day> dayList = LitePal.where("year = ? and month = ? and day = ?",
                ""+date.year, "" +date.month, ""+date.day).find(Day.class);
        if (dayList.size() > 0) {
            day = dayList.get(0);
        } else {
            Toast.makeText(getActivity(), date.month+"月"+date.day+"日"+"你什么都没有写呀", Toast.LENGTH_SHORT).show();
            return;
        }
        show_watching_PopupWindow(day);
    }

    @Override
    public void changeDate(CustomDate date) {
        TextView tvDate = getActivity().findViewById(R.id.tvDate);
        tvDate.setText(date.month + "月  " + date.year);
        currentMonth = date.month;
        currentYear = date.year;
        Log.w("测试", currentYear + " " + currentMonth);
    }

    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;
        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPre:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.btnNext:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case R.id.btnSet:
                break;
            case R.id.btnAddDiary:
                Intent intent = new Intent(getActivity(),addActivity.class);
                Day day = getDayFromDb(DateUtil.getYear(), DateUtil.getMonth(), DateUtil.getCurrentMonthDay());
                if (day == null) {
                    intent.putExtra("year",DateUtil.getYear());
                    intent.putExtra("month", DateUtil.getMonth());
                    intent.putExtra("day", DateUtil.getCurrentMonthDay());
                    intent.putExtra("put_status",1);
                } else {
                    intent.putExtra("day_modify", day);
                    intent.putExtra("put_status", 2);
                }
                startActivityForResult(intent,1);
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHint();
        List<Day> dayList = LitePal.where("year = ? and month = ? and day = ?",
                currentYear+"", currentMonth+"", currentDay +"").find(Day.class);

        if (dayList.size() > 0) {
            btnAddDiary.setBackground(getResources().getDrawable(R.drawable.edit));
        } else {
            btnAddDiary.setBackground(getResources().getDrawable(R.drawable.add));
        }
    }

    @Override
    public void onPause(){
        // 当fragment不可见时，暂停其余的UI更新、挂起线程或暂停不再需要的处理
        super.onPause();
        Log.w("frament生命周期", "fragment可见周期结束");
    }


    private int max;
    private int maxNum;
    private int[] stateCount = new int[6];

    private void setHint() {

        for (int i = 1; i <= 5; i++) {
            stateCount[i] = 0;
        }

        getCount();

        boolean isWritten = false;
        for (int i = 1; i <= 5; i++) {
            if (stateCount[i] != 0) {
                isWritten = true;
                break;
            }
        }
        if (!isWritten) {
            tvHint.setText("这个月还没有写日记喔");
            return;
        }

        if (maxNum == 1) {
            if (stateCount[Day.state_VeryHappy] == max) {
                tvHint.setText("这个月开心的时候最多噢，愉悦主导的日子可真不错！");
            } else if (stateCount[Day.state_happy] == max) {
                tvHint.setText("这个月开心就完事了！");
            } else if (stateCount[Day.state_calm] == max) {
                tvHint.setText("本月比较平淡，但起码不难受！");
            } else if (stateCount[Day.state_gray] == max) {
                tvHint.setText("虽然有点悲伤，但请坚持下去！");
            } else if (stateCount[Day.state_angry] == max) {
                tvHint.setText("都经历了这样的一个月，之后有什么都不在怕的！");
            }
        } else if (maxNum == 2) {
            if (stateCount[Day.state_calm] == max) {
                if (stateCount[Day.state_VeryHappy] == max) {
                    tvHint.setText("这个月开心的时候最多噢，愉悦主导的日子可真不错！");
                } else if (stateCount[Day.state_happy] == max) {
                    tvHint.setText("这个月开心就完事了！");
                } else if (stateCount[Day.state_gray] == max) {
                    tvHint.setText("虽然有点悲伤，但请坚持下去！");
                } else if (stateCount[Day.state_angry] == max) {
                    tvHint.setText("都经历了这样的一个月，之后有什么都不在怕的！");
                }
            } else {
                if (stateCount[Day.state_VeryHappy] == max && stateCount[Day.state_happy] == max) {
                    tvHint.setText("这个月开心的时候最多噢，愉悦主导的日子可真不错！");
                } else if (stateCount[Day.state_angry] == max && stateCount[Day.state_gray] == max) {
                    tvHint.setText("都经历了这样的一个月，之后有什么都不在怕的！");
                } else {
                    tvHint.setText("本月心情比较跌宕orz");
                }
            }
        } else if (maxNum == 3) {
            if (stateCount[Day.state_happy] == max && stateCount[Day.state_VeryHappy] == max) {
                tvHint.setText("这个月开心就完事了！");
            } else if (stateCount[Day.state_angry] == max && stateCount[Day.state_gray] == max) {
                tvHint.setText("虽然有点悲伤，但请坚持下去！");
            } else {
                tvHint.setText("本月心情比较跌宕orz");
            }
        } else if (maxNum == 4) {
            if (stateCount[Day.state_calm] == max) {
                if (stateCount[Day.state_happy] == max && stateCount[Day.state_VeryHappy] == max) {
                    tvHint.setText("这个月开心就完事了！");
                } else if (stateCount[Day.state_angry] == max && stateCount[Day.state_gray] == max) {
                    tvHint.setText("虽然有点悲伤，但请坚持下去！");
                }
            } else {
                tvHint.setText("本月心情比较跌宕orz");
            }
        } else {
            tvHint.setText("本月心情比较跌宕orz");
        }

    }

    private void getCount() {
        List<Day> dayList = LitePal.where("year = ? and month = ?", currentYear+"", currentMonth+"").find(Day.class);
        for (int i = 0; i < dayList.size(); i++) {
            Day day = dayList.get(i);
            if (day != null) {
                stateCount[day.getState()]++;
            }
        }

        max = 0;

        for (int i = 1; i <= 5; i++) {
            if (stateCount[i] > max) {
                max = stateCount[i];
                maxNum = 1;
            } else if (stateCount[i] == max) {
                maxNum++;
            }
        }
    }

    private Day getDayFromDb(int year, int month, int day) {
        List<Day> dayList = LitePal.where("year = ? and month = ? and day = ?", year+"", month+"", day+"").find(Day.class);
        if (dayList.size() > 0) {
            return dayList.get(0);
        } else {
            return null;
        }
    }

    public void show_watching_PopupWindow(Day day){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.watching_popupwindow_layout,null);
        MainActivity.watching_popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,true);
        MainActivity.watching_popupWindow.setContentView(contentView);
        MainActivity.pop_content_text = contentView.findViewById(R.id.watching_pop_content_text);
        MainActivity.pop_day_text = contentView.findViewById(R.id.watching_pop_day_text);
        MainActivity.pop_month_text = contentView.findViewById(R.id.watching_pop_month_text);
        MainActivity.pop_year_text = contentView.findViewById(R.id.watching_pop_year_text);
        MainActivity.pop_emotion_View = contentView.findViewById(R.id.watching_pop_emotionView);
        MainActivity.pop_exit_button = contentView.findViewById(R.id.watching_pop_exit2_button);
        MainActivity.pop_share_button = contentView.findViewById(R.id.watching_pop_share_button);
        MainActivity.pop_linearLayout = contentView.findViewById(R.id.watching_pop_Linear_View);

        MainActivity.pop_day_text.setText(""+day.getDay());
        MainActivity.pop_month_text.setText(""+day.getMonth());
        MainActivity.pop_year_text.setText(""+day.getYear());
        MainActivity.pop_content_text.setText(""+day.getContent());
        int state = day.getState();
        switch (state){
            case Day.state_angry:
                MainActivity.pop_emotion_View.setImageResource(R.drawable.emotionangry);
                break;
            case Day.state_gray:
                MainActivity.pop_emotion_View.setImageResource(R.drawable.emotionsad);
                break;
            case Day.state_calm:
                MainActivity.pop_emotion_View.setImageResource(R.drawable.emotionclam);
                break;
            case Day.state_happy:
                MainActivity.pop_emotion_View.setImageResource(R.drawable.emotionhappy);
                break;
            case Day.state_VeryHappy:
                MainActivity.pop_emotion_View.setImageResource(R.drawable.emotionecstasy);
                break;
            default:break;
        }

        MainActivity.pop_exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.watching_popupWindow.dismiss();
            }
        });

        MainActivity.pop_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = MainActivity.getViewBitmap(MainActivity.pop_linearLayout);
                String filePath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, null, null);
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
                Intent chooserIntent = Intent.createChooser(shareIntent, "image share");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooserIntent);
            }
        });
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main,null);
        MainActivity.watching_popupWindow.showAtLocation(rootView, Gravity.TOP,0,0);

    }
}
