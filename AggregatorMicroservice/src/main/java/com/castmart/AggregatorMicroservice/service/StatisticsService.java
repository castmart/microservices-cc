package com.castmart.AggregatorMicroservice.service;

import com.castmart.AggregatorMicroservice.model.DayStatistic;
import com.castmart.AggregatorMicroservice.persistence.IProductStatisticsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class StatisticsService {


    IProductStatisticsDAO statisticsDAO;

    @Autowired
    public StatisticsService(IProductStatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }

    public DayStatistic getStatisticOfDay(String start) throws ParseException {
        // Check date format.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (start == null || start.isEmpty()) {
            date = new Date(); // Today
            start = sdf.format(date);
        } else {
            date = sdf.parse(start); // Specific date
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        // The limit is the next day.
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String end = sdf.format(calendar.getTime());

        return statisticsDAO.getTodayStatistic(start, end);
    }
}
