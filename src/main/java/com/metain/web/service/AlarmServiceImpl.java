package com.metain.web.service;

import com.metain.web.dto.AlarmResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class AlarmServiceImpl implements AlarmService {
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private EmitterRepository  EmitterRepository ;

    public void send(Long userId, AlarmResponse response) {
        SseEmitter sseEmitter = EmitterRepository.get(userId).orElse(null);
        System.out.println(sseEmitter);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name(ALARM_NAME).data(response));
            } catch (IOException e) {
                EmitterRepository.delete(userId);
            }
        } else {
            logger.info("NO EMITTER FOUND!");
        }
    }

    public SseEmitter connectAlarm(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        EmitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> EmitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> EmitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("").name("subscribe").data(AlarmResponse.comment("connected")));
        } catch (IOException exception) {
            throw new IllegalStateException("");
        }

        return sseEmitter;
    }
}
