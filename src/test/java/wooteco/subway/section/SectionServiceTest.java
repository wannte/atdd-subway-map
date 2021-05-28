package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
public class SectionServiceTest {
    private static final Station 잠실역 = new Station(1L, "잠실역");
    private static final Station 강남역 = new Station(2L, "강남역");
    private static final Station 강변역 = new Station(3L, "강변역");

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private StationDao stationDao;
    @Mock
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        given(stationDao.findById(1L)).willReturn(Optional.of(잠실역));
        given(stationDao.findById(2L)).willReturn(Optional.of(강남역));
        given(stationDao.findById(3L)).willReturn(Optional.of(강변역));
    }

    @Test
    @DisplayName("노선에 해당하는 구간 탐색")
    void findSections() {
        sectionService.sectionsByLineId(1L);
        verify(sectionDao).findSections(1L);
    }

    @Test
    @DisplayName("초기 구간 등록")
    void initSection() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 3);

        sectionService.initSection(1L, sectionRequest);
        verify(sectionDao).save(1L, sectionRequest);
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        Long lineId = 1L;
        Section section = new Section(잠실역, 강남역, 3);
        given(sectionDao.findSections(lineId)).willReturn(Collections.singletonList(section));

        Section sectionToAdd = new Section(강남역, 강변역, 5);
        SectionRequest addSectionRequest = SectionRequest.of(sectionToAdd);

        sectionService.addSection(lineId, addSectionRequest);

        verify(sectionDao).deleteSectionsOf(lineId);
        verify(sectionDao).saveSections(lineId, Arrays.asList(section, sectionToAdd));
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        Long lineId = 1L;
        Section section1 = new Section(잠실역, 강남역, 3);
        Section section2 = new Section(강남역, 강변역, 5);

        given(sectionDao.findSections(lineId)).willReturn(Arrays.asList(section1, section2));

        sectionService.deleteSection(lineId, 잠실역.getId());

        verify(sectionDao).deleteSectionsOf(lineId);
        verify(sectionDao).saveSections(lineId, Collections.singletonList(section2));
    }
}
