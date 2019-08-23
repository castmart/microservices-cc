package com.castmart.AggregatorMicroservice.persistence;

import com.castmart.AggregatorMicroservice.model.DayStatistic;

public interface IProductStatisticsDAO {

    public DayStatistic getTodayStatistic(String start, String end);
}
