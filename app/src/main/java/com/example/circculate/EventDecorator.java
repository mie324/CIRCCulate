package com.example.circculate;

import android.app.usage.UsageEvents;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private int color;
    private HashSet<CalendarDay> dates;
    private final Drawable highlightDrawable;
    private static final int color = Color.parseColor("#228BC34A");

    public EventDecorator(HashSet<CalendarDay> dates){
        this.dates = dates;
        highlightDrawable = new ColorDrawable(color);

<<<<<<< HEAD
=======
    public EventDecorator(Collection <CalendarDay> dates){
        this.color = Color.parseColor("#FF4081");
        this.dates = new HashSet<>(dates);
>>>>>>> parent of 77a3e03... dot is not displayed
    }

    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return dates.contains(calendarDay);
    }

    @Override
<<<<<<< HEAD
    public void decorate(DayViewFacade view) {
//        dayViewFacade.addSpan(new DotSpan(5, color));
          view.setBackgroundDrawable(highlightDrawable);
=======
    public void decorate(DayViewFacade dayViewFacade) {
        dayViewFacade.addSpan(new DotSpan(5, color));

>>>>>>> parent of 77a3e03... dot is not displayed
    }

//    private CalendarDay date;
//
//    public EventDecorator(){
//        date = CalendarDay.today();
//    }
//
//    @Override
//    public boolean shouldDecorate(CalendarDay day) {
//        return date != null && day.equals(date);
//    }
//
//    @Override
//    public void decorate(DayViewFacade view) {
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.4f));
//    }
//
//
//    public void setDate(LocalDate date) {
//        this.date = CalendarDay.from();
//    }
}
