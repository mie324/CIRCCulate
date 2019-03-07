package com.example.circculate;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;
    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }


    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return dates.contains(calendarDay);
    }

    @Override
    public void decorate(DayViewFacade dayViewFacade) {
        dayViewFacade.addSpan(new DotSpan(5, color));

    }
}
