package com.example.wuk.emotiondiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private View childView;
    private RecyclerView touchView;
    public static boolean isDeleteShown;

    public RecyclerItemClickListener(Context context, RecyclerItemClickListener.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, touchView.getChildAdapterPosition(childView));
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (childView != null && mListener != null) {
                    mListener.onLongClick(childView, touchView.getChildAdapterPosition(childView));
                }
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (childView != null && mListener != null && distanceX > 30) {
                    isDeleteShown = true;
                    mListener.onScroll(childView, touchView.getChildAdapterPosition(childView));
                }
                if (isDeleteShown && childView != null && mListener != null && distanceX < -20) {
                    mListener.onCancel(childView, touchView.getChildAdapterPosition(childView));
                    isDeleteShown = false;
                }
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int position);

        void onScroll(View view, int position);

        void onCancel(View view, int position);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        touchView = rv;
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}