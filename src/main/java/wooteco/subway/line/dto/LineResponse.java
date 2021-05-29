package wooteco.subway.line.dto;

import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(LineEntity lineEntity) {
        this(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), Collections.emptyList());
    }

    public LineResponse(LineEntity lineEntity, List<StationResponse> stations) {
        this(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public void setStations(List<StationResponse> stations) {
        this.stations = stations;
    }
}
