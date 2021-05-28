package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;

    public static List<StationResponse> listOf(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public StationResponse() {
    }

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
