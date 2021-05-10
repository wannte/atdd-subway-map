package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Section> sectionRowMapper() {
        return (resultSet, rowNum) -> new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId, distance);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select ID, LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE from SECTION where LINE_ID = ?";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }

    public void delete(Long id) {
        String sql = "delete from LINE where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
