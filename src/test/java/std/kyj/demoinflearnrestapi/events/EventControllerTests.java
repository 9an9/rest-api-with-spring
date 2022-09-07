package std.kyj.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import std.kyj.demoinflearnrestapi.common.TestDescription;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static std.kyj.demoinflearnrestapi.events.EventStatus.PUBLISHED;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /*@MockBean
    EventRepository eventRepository;*/

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                        .name("Spring")
                        .description("REST API Development with Spring")
                        .beginEnrollmentDateTime(LocalDateTime.of(2022, 9, 7, 13, 32))
                        .closeEnrollmentDateTime(LocalDateTime.of(2022, 9, 8, 13, 32))
                        .beginEventDateTime(LocalDateTime.of(2022, 9, 9, 13, 32))
                        .endEventDateTime(LocalDateTime.of(2022, 9, 10, 13, 32))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("마곡 LG 사이언스 파크")
                        .build();
        // Mockito.when(eventRepository.save(event)).thenReturn(event); -> eventDto로 mapping한 event가 이 event 객체와 다른 객체이기 때문에 NullPointerException 발생

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))  // exists("Location"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8")) // string("Content-Type", "application/hal+json;charset=UTF-8")
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;

    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용하는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 9, 7, 13, 32))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 9, 8, 13, 32))
                .beginEventDateTime(LocalDateTime.of(2022, 9, 9, 13, 32))
                .endEventDateTime(LocalDateTime.of(2022, 9, 10, 13, 32))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("마곡 LG 사이언스 파크")
                .free(true)
                .offline(false)
                .eventStatus(PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                            .name("Spring")
                            .description("REST API Development with Spring")
                            .beginEnrollmentDateTime(LocalDateTime.of(2022, 9, 10, 13, 32))
                            .closeEnrollmentDateTime(LocalDateTime.of(2022, 9, 9, 13, 32))
                            .beginEventDateTime(LocalDateTime.of(2022, 9, 8, 13, 32))
                            .endEventDateTime(LocalDateTime.of(2022, 9, 7, 13, 32))
                            .basePrice(10000)
                            .maxPrice(200)
                            .limitOfEnrollment(100)
                            .location("마곡 LG 사이언스 파크")
                            .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }
}
