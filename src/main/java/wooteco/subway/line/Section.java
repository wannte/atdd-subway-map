package wooteco.subway.line;

import wooteco.subway.line.exception.SectionError;
import wooteco.subway.line.exception.SectionException;
import wooteco.subway.station.Station;

public class Section {
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Station upStation, Station downStation, int distance) {
        if (upStation.equals(downStation)) {
            throw new SectionException(SectionError.SAME_STATION_INPUT);
        }
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
