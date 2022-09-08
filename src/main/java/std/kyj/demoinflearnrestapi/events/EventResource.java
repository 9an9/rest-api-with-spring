package std.kyj.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links) {
        super(event, Arrays.asList(links)); //super은 iterator Link...는 배열. Arrays.asList 추가

        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
//        add(new Link("http://localhost:80080/api/events/" + event.getId())); 와 같음. type safe
    }

/*extends RepresentationModel {

    @JsonUnwrapped //응답 데이터가 event로 감싸지게 되는데 이걸 꺼내줌
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
*/
}
