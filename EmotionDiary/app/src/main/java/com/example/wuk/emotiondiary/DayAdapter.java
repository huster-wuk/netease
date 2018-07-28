package com.example.wuk.emotiondiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private List<Day> dayList;

    private Context context;
    private int position;
    public DayAdapter(Context context,List<Day> Day_List){
        this.context = context;
        dayList = Day_List;
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View preColorView;
        TextView date_textView;
        TextView weekday_textView;
        TextView content_textView;
        ImageView emotion_img;


        public ViewHolder(View view){
            super(view);
            preColorView = (View)view.findViewById(R.id.pre_color_view);
            date_textView =(TextView)view.findViewById(R.id.date_textView);
            weekday_textView = (TextView)view.findViewById(R.id.weekday_textView);
            content_textView = (TextView)view.findViewById(R.id.content_textView);
            emotion_img = (ImageView)view.findViewById(R.id.emotion_img);
        }

    }
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.daylist_item_layout,parent,false);
        final DayAdapter.ViewHolder holder = new DayAdapter.ViewHolder(view);
        holder.itemView.findViewById(R.id.main_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Day day_show = new Day();
                day_show = dayList.get(position);
                show_watching_PopupWindow(day_show);
            }
        });
        holder.itemView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();

                //显示popupwindow
                View contentView = LayoutInflater.from(context).inflate(R.layout.delete_popupwindow,null);
                MainActivity.deletePopupWindow = new PopupWindow(contentView,
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
                MainActivity.deletePopupWindow.setContentView(contentView);

                TextView tvConfirm = contentView.findViewById(R.id.confirm);
                TextView tvCancel = contentView.findViewById(R.id.cancel);

                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Day day = dayList.get(position);
                        LitePal.deleteAll(Day.class,"year = ? and month = ? and day = ?",
                                day.getYear()+"", day.getMonth()+"", day.getDay()+"");
                        dayList.remove(position);
                        notifyDataSetChanged();
                        MainActivity.deletePopupWindow.dismiss();
                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.deletePopupWindow.dismiss();
                    }
                });
                View rootView = LayoutInflater.from(context).inflate(R.layout.activity_main,null);
                MainActivity.deletePopupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
                if (dayList.get(position) != null) {
                    holder.itemView.scrollTo(0, 0);
                    RecyclerItemClickListener.isDeleteShown = false;
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(DayAdapter.ViewHolder holder, int position) {
        Day day = dayList.get(position);
        int state = day.getState();
        holder.date_textView.setText(""+ day.getMonth()+"/"+day.getDay());
        DateTime dateTime = new DateTime(day.getYear(),day.getMonth(),day.getDay(),0,0);
        int d = dateTime.getDayOfWeek();
        holder.weekday_textView.setText(Day.week[d % 7]);
        holder.content_textView.setText(day.getContent());
        switch (state){
            case Day.state_angry:holder.emotion_img.setImageResource(R.drawable.emotionangry);
                holder.preColorView.setBackgroundColor(Color.argb(79,255,51,51));
                break;

            case Day.state_gray:holder.emotion_img.setImageResource(R.drawable.emotionsad);
                holder.preColorView.setBackgroundColor(Color.argb(79,102,102,102));
                break;
            case Day.state_calm:holder.emotion_img.setImageResource(R.drawable.emotionclam);
                holder.preColorView.setBackgroundColor(Color.argb(79,255,187,0));
                break;
            case Day.state_happy:holder.emotion_img.setImageResource(R.drawable.emotionhappy);
                holder.preColorView.setBackgroundColor(Color.argb(79,0,210,150));
                break;
            case Day.state_VeryHappy:holder.emotion_img.setImageResource(R.drawable.emotionecstasy);
                holder.preColorView.setBackgroundColor(Color.argb(79,0,159,204));
                break;
            default:break;
        }

        holder.itemView.setTag(position);
    }

    public void show_watching_PopupWindow(Day day){
        View contentView = LayoutInflater.from(context).inflate(R.layout.watching_popupwindow_layout,null);
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_main,null);
        MainActivity.watching_popupWindow.showAtLocation(rootView, Gravity.TOP,0,0);
    }
}