package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queues =
            new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = new Resp(req.httpRequestType(), "501 Not Implemented");
        String param = req.getParam();
        String sourceName = req.getSourceName();
        if ("POST".equals(req.httpRequestType())) {
            queues.putIfAbsent(sourceName, new ConcurrentLinkedQueue<>());
            queues.get(sourceName).offer(param);
            resp = param.isEmpty() ? new Resp(param, "200 Ok") : new Resp(param, "201 Created");
        } else if ("GET".equals(req.httpRequestType())) {
            String text = queues.getOrDefault(sourceName, new ConcurrentLinkedQueue<>()).poll();
            resp = text == null ? new Resp("", "204 No Content") : new Resp(text, "200 Ok");
        }
        return resp;
    }
}

