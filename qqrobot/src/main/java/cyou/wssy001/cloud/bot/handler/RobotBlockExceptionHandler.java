package cyou.wssy001.cloud.bot.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.service.UnhandledHttpRequestService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RobotBlockExceptionHandler implements BlockRequestHandler {
    private final UnhandledHttpRequestService unhandledHttpRequestService;

    @SneakyThrows
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        URI uri = exchange.getRequest().getURI();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(decodeBody(uri.getRawQuery()));

        UnhandledHttpRequest unhandledHttpRequest = new UnhandledHttpRequest(jsonObject.toJSONString());
        if (uri.getPath().contains("/send/msg")) {
            unhandledHttpRequest.setMethod("/send/msg");
        } else {
            unhandledHttpRequest.setMethod("/collect/msg");
        }

        unhandledHttpRequestService.upset(unhandledHttpRequest);
        jsonObject.clear();
        jsonObject.put("msg", "机器人服务正忙，请稍后重试");
        jsonObject.put("code", HttpStatus.TOO_MANY_REQUESTS.value());

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonObject.toJSONString());
    }

    private Map<String, Object> decodeBody(String body) {
        if (StrUtil.isBlank(body)) return new HashMap<>();

        if (body.contains("&") && body.contains("="))
            return Arrays.stream(body.split("&"))
                    .map(s -> s.split("="))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        return JSON.parseObject(body)
                .getInnerMap();
    }
}
