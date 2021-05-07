package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private static final String color1 = "초록색";
    private static final String name1 = "2호선";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("delete from LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineInsertResponse(name1, color1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성시 실패.")
    @Test
    void createLineWithDuplicateName() {
        // given & when
        createLineInsertResponse(name1, color1);
        ExtractableResponse<Response> response = createLineInsertResponse(name1, color1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 전체 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineInsertResponse(name1, color1);
        ExtractableResponse<Response> createResponse2 = createLineInsertResponse("파란색", "1호선");

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .when()
                                                            .get("/lines")
                                                            .then()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                                           .map(it -> Long.parseLong(it.header("Location")
                                                                       .split("/")[2]))
                                           .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList("lineResponses", LineResponse.class)
                                           .stream()
                                           .map(LineResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 1개를 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse(name1, color1);
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .get(uri)
                                                            .then()
                                                            .extract();
        LineResponse lineResponse = response.body()
                                            .as(LineResponse.class);

        assertThat(lineResponse.getColor()).isEqualTo(color1);
        assertThat(lineResponse.getName()).isEqualTo(name1);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse(name1, color1);
        String uri = extract.header("Location");

        LineRequest lineRequest = new LineRequest("9호선", "남색", 0L, 0L, 0);

        ExtractableResponse<Response> response = RestAssured.given()
                                                            .body(lineRequest)
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .put(uri)
                                                            .then()
                                                            .extract();


        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse(name1, color1);

        String uri = extract.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when()
                                                            .delete(uri)
                                                            .then()
                                                            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineInsertResponse(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color, 0L, 0L, 0);
        return RestAssured.given()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post("/lines")
                          .then()
                          .extract();
    }
}