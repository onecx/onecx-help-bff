package org.tkit.onecx.help.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.help.bff.rs.controller.HelpRestController;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.help.bff.rs.internal.model.*;
import gen.org.tkit.onecx.help.client.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(HelpRestController.class)
class HelpRestControllerTest extends AbstractTest {

    private static final String HELP_SVC_INTERNAL_API_BASE_PATH = "/internal/helps";

    @InjectMockServerClient
    MockServerClient mockServerClient;

    static final String MOCK_ID = "MOCK";

    @AfterEach
    void resetMockserver() {
        try {
            mockServerClient.clear(MOCK_ID);
        } catch (Exception ex) {
            // mockId not existing
        }
    }

    @Test
    void createNewHelp_shouldReturnHelp() {
        var offsetDateTime = OffsetDateTime.parse("2023-11-30T13:53:03.688710200+01:00");
        OffsetDateTimeMapper offsetDateTimeMapper = new OffsetDateTimeMapper();

        Help data = createHelp("appId1",
                "123-456-789",
                "testContext",
                offsetDateTime,
                "http://localhost:8080/mfe",
                "testUser",
                "itemId1");

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH)
                        .withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        CreateHelpDTO input = createHelpDTO("appId1",
                "testContext",
                "http://localhost:8080/mfe",
                "itemId1");

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HelpDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(data.getAppId(), response.getAppId());
        Assertions.assertEquals(offsetDateTimeMapper.map(data.getCreationDate()),
                offsetDateTimeMapper.map(response.getCreationDate()));
        Assertions.assertEquals(data.getBaseUrl(), response.getBaseUrl());
        Assertions.assertEquals(data.getId(), response.getId());
        Assertions.assertEquals(data.getContext(), response.getContext());
    }

    @Test
    void createNewHelp_shouldReturnBadRequest_whenBodyDoesNotExist() {

        // bff call
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

    }

    @Test
    void createNewHelp_shouldReturnBadRequest_whenBodyEmpty() {
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("404");

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH)
                        .withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withBody(JsonBody.json(problemDetailResponse)));

        CreateHelpDTO input = createHelpDTO(null,
                null,
                null,
                null);

        // bff call
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

    }

    @Test
    void deleteHelp() {
        String id = "82689h23-9624-2234-c50b-8749d073c287";

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // Assertions
        Assertions.assertNotNull(response);

    }

    @Test
    void getAllAppsWithHelpItems_shouldReturnHelpAppIdsDTO() {

        HelpAppIds data = new HelpAppIds();
        List<String> appIds = new ArrayList<>();
        appIds.add("appId1");
        appIds.add("appId2");
        data.setAppIds(appIds);

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/appIds")
                        .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/appIds")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(HelpAppIdsDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(data.getAppIds(), response.getAppIds());
        Assertions.assertEquals(2, data.getAppIds().size());
    }

    @Test
    void getHelpById() {

        String id = "82689h23-9624-2234-c50b-8749d073c287";
        var offsetDateTime = OffsetDateTime.parse("2023-11-30T13:53:03.688710200+01:00");
        OffsetDateTimeMapper offsetDateTimeMapper = new OffsetDateTimeMapper();
        Help data = createHelp("appId1",
                "123-456-789",
                "testContext",
                offsetDateTime,
                "http://localhost:8080/mfe",
                "testUser",
                "itemId1");

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(id)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(HelpDTO.class);

        Assertions.assertNotNull(response);

    }

    @Test
    void getHelpById_shouldReturnNotFound_whenHelpDoesNotExist() {

        String id = "82689h23-9624-2234-c50b-8749d073c287";

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(Response.status(Response.Status.NOT_FOUND).build())));

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(id)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Assertions.assertNotNull(response);

    }

    @Test
    void getHelpByItemId() {

        String id = "82689h23-9624-2234-c50b-8749d073c287";

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/itemId/" + id)
                        .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(Response.status(Response.Status.NOT_FOUND).build())));

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/itemId/" + id)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Assertions.assertNotNull(response);

    }

    @Test
    void searchHelps() {

        HelpPageResult data = new HelpPageResult();
        data.setNumber(2);
        data.setTotalPages(1L);
        data.setSize(1);
        List<Help> helpList = new ArrayList<>();
        helpList.add(createHelp("appId1",
                "123-456-789",
                "testContext",
                null,
                "http://localhost:8080/mfe",
                "testUser",
                "itemId1"));
        helpList.add(createHelp("appId2",
                "123-456-987",
                "testContext2",
                null,
                "http://localhost:8080/mfe",
                "testUser2",
                "itemId2"));
        data.setStream(helpList);

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        HelpSearchCriteriaDTO helpSearchCriteriaDTO = new HelpSearchCriteriaDTO();
        helpSearchCriteriaDTO.setAppId("appId1");
        helpSearchCriteriaDTO.setBaseUrl("http://localhost:8080/mfe");
        helpSearchCriteriaDTO.setItemId("itemId1");

        // bff call
        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(helpSearchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(HelpPageResultDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getNumber());
        Assertions.assertEquals(2, response.getStream().size());

    }

    @Test
    void updateHelp() {
        String id = "82689h23-9624-2234-c50b-8749d073c287";

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.PUT))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        UpdateHelpDTO updateHelpDTO = new UpdateHelpDTO();
        updateHelpDTO.setModificationCount(0);
        updateHelpDTO.setAppId("appId1");
        updateHelpDTO.setBaseUrl("http://localhost:8080/mfe");
        updateHelpDTO.setItemId("itemId1");

        // bff call
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateHelpDTO)
                .put(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }

    @Test
    void updateHelp_shouldReturnNotFound_whenHelpDoesNotExist() {
        String id = "82689h23-9624-2234-c50b-8749d073c287";

        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("404");

        mockServerClient
                .when(request().withPath(HELP_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.PUT))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(
                        httpRequest -> response()
                                .withStatusCode(Response.Status.NOT_FOUND.getStatusCode())
                                .withBody(JsonBody.json(problemDetailResponse)));

        UpdateHelpDTO updateHelpDTO = new UpdateHelpDTO();
        updateHelpDTO.setModificationCount(0);
        updateHelpDTO.setAppId("appId1");
        updateHelpDTO.setBaseUrl("http://localhost:8080/mfe");
        updateHelpDTO.setItemId("itemId1");

        // bff call
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateHelpDTO)
                .put("/82689h23-9624-2234-c50b-8749d073c287")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

    }

    @Test
    void errorSecurityTest() {
        String id = "82689h23-9624-2234-c50b-8749d073c287";

        // do not send auth header
        given()
                .when()
                //.auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/itemId/" + id)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        UpdateHelpDTO updateHelpDTO = new UpdateHelpDTO();
        updateHelpDTO.setModificationCount(0);
        updateHelpDTO.setAppId("appId1");
        updateHelpDTO.setBaseUrl("http://localhost:8080/mfe");
        updateHelpDTO.setItemId("itemId1");

        // FORBIDDEN when needed write permission and USER have only read
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(USER))
                .header(APM_HEADER_PARAM, USER)
                .contentType(APPLICATION_JSON)
                .body(updateHelpDTO)
                .put(id)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        // FORBIDDEN when needed delete permission and USER have only read
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(USER))
                .header(APM_HEADER_PARAM, USER)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    private Help createHelp(String appId, String id, String context, OffsetDateTime creationDate, String baseUrl,
            String creationUser, String itemId) {
        Help help = new Help();
        help.setAppId(appId);
        help.setId(id);
        help.setContext(context);
        help.setCreationDate(creationDate);
        help.setBaseUrl(baseUrl);
        help.setCreationUser(creationUser);
        help.setItemId(itemId);

        return help;
    }

    private CreateHelpDTO createHelpDTO(String appId, String context, String baseUrl, String itemId) {
        CreateHelpDTO helpDTO = new CreateHelpDTO();
        helpDTO.setAppId(appId);
        helpDTO.setContext(context);
        helpDTO.setBaseUrl(baseUrl);
        helpDTO.setItemId(itemId);
        return helpDTO;
    }

}
