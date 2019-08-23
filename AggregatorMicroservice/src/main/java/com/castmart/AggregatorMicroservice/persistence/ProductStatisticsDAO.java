package com.castmart.AggregatorMicroservice.persistence;

import com.castmart.AggregatorMicroservice.model.DayStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductStatisticsDAO implements IProductStatisticsDAO {

    private static final String DAY_STATISTIC_SQL_QUERY =
            "select sum(case when creation_timestamp > :start AND creation_timestamp < :end then 1 else 0 end) creations, " +
                    "sum(case when edition_timestamp is not NULL AND edition_timestamp > :start AND edition_timestamp < :end then 1 else 0 end) editions, :start as 'day' from product;";

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public DayStatistic getTodayStatistic(String start, String end) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("start", start);
        params.addValue("end", end);

        List<DayStatistic> statisticList = jdbcTemplate.query(DAY_STATISTIC_SQL_QUERY, params, new RowMapper<DayStatistic>() {
            @Override
            public DayStatistic mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new ResultSetExtractor<DayStatistic>() {
                        @Override
                        public DayStatistic extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                            DayStatistic statistic = new DayStatistic();
                            statistic.setProductsCreated(resultSet.getLong("creations"));
                            statistic.setProductsUpdated(resultSet.getLong("editions"));
                            statistic.setDay(resultSet.getString("day"));
                            return statistic;
                        }
                    }.extractData(resultSet);
            }

        });

        return statisticList.isEmpty() ? null : statisticList.get(0);
    }
}
