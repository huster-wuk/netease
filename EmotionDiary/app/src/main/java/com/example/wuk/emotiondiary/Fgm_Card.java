package com.example.wuk.emotiondiary;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fgm_Card extends Fragment implements View.OnClickListener {

    private MyRecyclerView day_recyclerView;
    private static List<Day> dayList = new ArrayList<>();
    private DayAdapter dayAdapter;
    private static int currentMonth;
    private static int currentYear;

    private ImageButton btnPre, btnNext, btnSet;
    private TextView tvDate;

    public Fgm_Card() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fgm__card, null);

        currentMonth = DateUtil.getMonth();
        currentYear = DateUtil.getYear();
        tvDate = getActivity().findViewById(R.id.tvDate);
        tvDate.setText(currentMonth+"月  "+currentYear);

        dayList = initData(currentYear, currentMonth);
        day_recyclerView = (MyRecyclerView) view.findViewById(R.id.day_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        day_recyclerView.setLayoutManager(layoutManager);
        dayAdapter = new DayAdapter(getContext(),dayList);

        day_recyclerView.setAdapter(dayAdapter);

        day_recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }

            @Override
            public void onScroll(View view, int position) {
                view.scrollTo(400, 0);
            }

            @Override
            public void onCancel(View view, int position) {
                view.scrollTo(0, 0);
            }
        }));


        btnPre = getActivity().findViewById(R.id.btnPre);
        btnNext = getActivity().findViewById(R.id.btnNext);
        btnSet = getActivity().findViewById(R.id.btnSet);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSet.setOnClickListener(this);

        return view;
    }

    public List<Day> initData(int currentYear, int currentMonth) {
        return LitePal.where("year = ? and month = ?", ""+currentYear, ""+currentMonth).find(Day.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                currentMonth++;
                if (currentMonth == 13) {
                    currentMonth = 1;
                    currentYear++;
                }
                dayList = initData(currentYear, currentMonth);
                tvDate.setText(currentMonth + "月  " + currentYear);
                dayAdapter = new DayAdapter(getContext(),dayList);
                day_recyclerView.setAdapter(dayAdapter);
                break;
            case R.id.btnPre:
                currentMonth--;
                if (currentMonth == 0) {
                    currentMonth = 12;
                    currentYear--;
                }
                dayList = initData(currentYear, currentMonth);
                tvDate.setText(currentMonth + "月  " + currentYear);
                dayAdapter = new DayAdapter(getContext(),dayList);
                day_recyclerView.setAdapter(dayAdapter);
                break;
            default:
                break;
        }
    }
}
