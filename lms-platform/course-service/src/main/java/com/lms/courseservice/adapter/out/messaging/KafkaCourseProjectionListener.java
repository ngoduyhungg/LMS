package com.lms.courseservice.adapter.out.messaging;

import com.lms.courseservice.adapter.out.messaging.event.CourseProjectionSpringEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCourseProjectionListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "course.projection.events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAfterCommit(CourseProjectionSpringEvent event) {
        // Dùng courseId làm message key để đảm bảo thứ tự xử lý trên Kafka partition
        String key = String.valueOf(event.payload().courseId());
        log.info("Sending Kafka message - topic: {}, key: {}, payload: {}",
                TOPIC, key, event.payload());
        kafkaTemplate.send(TOPIC, key, event.payload());
    }
}