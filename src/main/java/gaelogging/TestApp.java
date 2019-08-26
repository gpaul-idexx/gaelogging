package gaelogging;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;


@SpringBootApplication
public class TestApp extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(TestApp.class);
    private Subscriber subscriber;

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);


    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestApp.class);
    }

    @PostConstruct
    public void initSubscriber() {
        log.info("Setting up pull subscriber");
        String projectId = SystemProperty.applicationId.get();

        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, "test1-pull");

        subscriber = Subscriber.newBuilder(subscriptionName, (message, consumer) -> {
            try {
                String mess = message.getData().toString("UTF-8");
                log.info("Info: We got a message {}", mess);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                consumer.ack();
            }
        }).build();

        subscriber.startAsync();
    }

    @PreDestroy
    public void stopSubscriber() {
        log.info("Shutting down subscriber");
        subscriber.stopAsync().awaitTerminated();
    }

    @RestController
    public static class TestController {

        @GetMapping("/")
        public String ping() {
            return "pong";
        }
    }
}
