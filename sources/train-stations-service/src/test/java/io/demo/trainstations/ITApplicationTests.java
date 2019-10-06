package io.demo.trainstations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.demo.storage.service.grpc.domain.StorageServiceGrpc;
import io.demo.trainstations.domain.GraphRoute;
import io.demo.trainstations.grpc.StorageServiceImpl;
import io.demo.trainstations.mvc.dto.CreateRouteRequest;
import io.demo.trainstations.service.TrainStationServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

/**
 * Testing scenario:
 *   call every RESTful endpoint
 *   check if data returned correctly
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ITApplicationTests {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TrainStationServiceImpl trainStationService;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Before
    public void prepareForTest() throws IOException {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new StorageServiceImpl()).build().start());
        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        StorageServiceGrpc.StorageServiceBlockingStub storageServiceMock = StorageServiceGrpc.newBlockingStub(channel);

        // replace grpc service with stub
        trainStationService.setStorageService(storageServiceMock);
    }

    @Test
    public void testRESTAPI() throws Exception {
        // create station request
        String stringResponse = mvc.perform(post("/api/stations")
                .content(asJsonString("Station 1"))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string("OK")).andReturn().getResponse().getContentAsString();

        // list stations request
        MvcResult mvcResult = mvc.perform(get("/api/stations")).andExpect(status().isOk()).andReturn();
        List<String> stations = parseResponse(mvcResult, new TypeReference<List<String>>() {});
        Assert.assertTrue("should return 5 stations", (stations.size() == 5) && stations.get(0).equals("Station 1"));

        // delete station request
        stringResponse = mvc.perform(delete("/api/stations/Station 1"))
                .andExpect(content().string("OK")).andReturn().getResponse().getContentAsString();

        // create route request
        CreateRouteRequest createRouteRequest = new CreateRouteRequest();
        createRouteRequest.setStation1("Station 1");
        createRouteRequest.setStation2("Station 2");
        createRouteRequest.setCost(0.123);
        stringResponse = mvc.perform(post("/api/routes")
                .content(asJsonString(createRouteRequest))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string("OK")).andReturn().getResponse().getContentAsString();

        // check existing route
        mvcResult = mvc.perform(get("/api/route/Station 1/Station 2")).andExpect(status().isOk()).andReturn();
        Double cost = parseResponse(mvcResult, new TypeReference<Double>() {});
        Assert.assertEquals("should return 0.123", cost, Double.valueOf(0.123));

        // check route which does not exist
        mvc.perform(get("/api/route/Station 1/Station 9")).andExpect(status().isNotFound());

        // delete route request
        mvc.perform(delete("/api/route/Station 1/Station 2"))
                .andExpect(content().string("OK"));

        // find routes request
        mvcResult = mvc.perform(get("/api/routes/Station 1/Station 2")).andExpect(status().isOk()).andReturn();
        List<GraphRoute> routes = parseResponse(mvcResult, new TypeReference<List<GraphRoute>>() {});
        Assert.assertTrue("should return 1 route with cost 1.0", (routes.size() == 1) && routes.get(0).getTotalCost().equals(1.0));
    }

    public static String asJsonString(final Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseResponse(MvcResult result, TypeReference<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
