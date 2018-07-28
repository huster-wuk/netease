package com.example.wuk.emotiondiary;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CalendarCard extends View {

    private static final int TOTAL_COL = 7; // 7列
    private static final int TOTAL_ROW = 6; // 6行

    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距
    private int mCellSpaceH;
    private int mCellSpaceW;
    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop; //
    private List<Day> dayList = new ArrayList<>();

    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     *
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCard(Context context) {
        super(context);
        init(context);
    }

    public CalendarCard(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        fillDate();//
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        /*int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month - 1); // 上个月的天数*/
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek
                        && position < firstDayWeek + currentMonthDays) {
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(
                            mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    // 今天
                    if (isCurrentMonth && day == monthDay ) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i, j);
                    }

                }/* else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
                            mShowDate.month - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
                            mShowDate.month + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }*/
            }
        }
        mCellClickListener.changeDate(mShowDate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        mCellSpaceH = mViewHeight / TOTAL_ROW;
        mCellSpaceW = mViewWidth / TOTAL_COL;

        mTextPaint.setTextSize(mCellSpace / 4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpaceW);
                    int row = (int) (mDownY / mCellSpaceH);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month);
        int position = col + row * TOTAL_COL;
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (position >= (firstDayWeek + currentMonthDays) || position < firstDayWeek)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            try {
                mCellClickListener.clickDate(date);
                // 刷新界面
                update();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 组元素
     *
     * @author wuwenjie
     *
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author wuwenjie
     *
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            dayList = LitePal.where("year = ? and month = ? and day = ?",
                    ""+date.year, "" +date.month, ""+date.day).find(Day.class);

            if (dayList.size() > 0) {
                Day day = dayList.get(0);
                switch (day.getState()) {
                    case Day.state_angry:
                        mCirclePaint.setColor(getResources().getColor(R.color.angry)); // 红色圆形
                        break;
                    case Day.state_calm:
                        mCirclePaint.setColor(getResources().getColor(R.color.calm));
                        break;
                    case Day.state_gray:
                        mCirclePaint.setColor(getResources().getColor(R.color.sad));
                        break;
                    case Day.state_happy:
                        mCirclePaint.setColor(getResources().getColor(R.color.happy));
                        break;
                    case Day.state_VeryHappy:
                        mCirclePaint.setColor(getResources().getColor(R.color.veryhappy));
                        break;
                    default:
                        mCirclePaint.setColor(getResources().getColor(R.color.white));
                        break;
                }

                mCirclePaint.setAlpha(120);

                if (day.getYear() == DateUtil.getYear() && day.getMonth() == DateUtil.getMonth() && day.getDay() == DateUtil.getCurrentMonthDay()) {
                    canvas.drawCircle((float) (mCellSpaceW * (i + 0.5)),
                            (float) ((j + 0.53) * mCellSpaceH), (float) (mCellSpace / 3),
                            mCirclePaint);
                } else {
                    canvas.drawCircle((float) (mCellSpaceW * (i + 0.5)),
                            (float) ((j + 0.53) * mCellSpaceH), (float) (mCellSpace / 3.5),
                            mCirclePaint);
                }

            }

            // 绘制文字
            mTextPaint.setColor(Color.BLACK);
            String content = date.day + "";
            canvas.drawText(content,
                    (float) ((i + 0.5) * mCellSpaceW - mTextPaint.measureText(content) / 2),
                    (float) ((j + 0.7) * mCellSpaceH - mTextPaint.measureText(content, 0, 1) / 2),
                    mTextPaint);
        }
    }

    enum State {
        TODAY,CURRENT_MONTH_DAY,PAST_MONTH_DAY,NEXT_MONTH_DAY ,UNREACH_DAY;
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.month == 1) {
            mShowDate.month = 12;
            mShowDate.year -= 1;
        } else {
            mShowDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }

}