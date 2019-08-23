package com.castmart.AggregatorMicroservice;


import com.castmart.AggregatorMicroservice.model.DayStatistic;
import com.castmart.AggregatorMicroservice.persistence.IProductStatisticsDAO;
import com.castmart.AggregatorMicroservice.service.StatisticsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsServiceTest {

    private StatisticsService service;
    private IProductStatisticsDAO dao;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Date today = new Date();

    @Before
    public void init() {
        dao = Mockito.mock(IProductStatisticsDAO.class);
        service = new StatisticsService(dao);
    }

    @Test(expected = ParseException.class)
    public void givenDates_with_badFormats_then_expect_parseException() throws Exception {
        final String badDateFormat = "20180122";
        service.getStatisticOfDay(badDateFormat);
    }

    @Test
    public void givenNullDate_then_getStatisticsOfToday() throws ParseException {
        // Prepare Dao response
        DayStatistic statistic = new DayStatistic();
        statistic.setProductsCreated(100);
        statistic.setProductsUpdated(100);
        statistic.setDay(format.format(today));
        Mockito.when(dao.getTodayStatistic(anyString(), anyString())).thenReturn(statistic);

        DayStatistic result = service.getStatisticOfDay(format.format(new Date()));

        Assert.assertEquals(statistic, result);
    }

    @Test
    public void givenValidDate_then_getStatisticsOfThatDay() throws ParseException {
        // Prepare Dao response
        DayStatistic statistic = new DayStatistic();
        statistic.setProductsCreated(100);
        statistic.setProductsUpdated(100);
        statistic.setDay(format.format(today));
        Mockito.when(dao.getTodayStatistic(anyString(), anyString())).thenReturn(statistic);

        DayStatistic result = service.getStatisticOfDay(format.format(new Date()));

        Assert.assertEquals(statistic, result);
        Assert.assertEquals(statistic.getDay(), result.getDay());
    }
}
