package cyou.wssy001.cloud.bot.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SentinelService {

    public Mono<Boolean> saveOrUpdateFlowRule(FlowRule rule) {

        Flux<FlowRule> flowRule = getFlowRule();
        flowRule = flowRule
                .filter(v -> v.getResource().equals(rule.getResource()))
                .map(v -> rule)
                .switchIfEmpty(Flux.defer(() -> Flux.just(rule)))
                .mergeWith(flowRule);

        return publishFlowRule(flowRule);
    }

    public Flux<FlowRule> getFlowRule() {
        String urlPara = "tenant=3e4a06ab-3139-46e8-bc82-888154383de0&" +
                "dataId=qqrobot-sentinel-flow-dev.json&" +
                "group=DEFAULT_GROUP";

        HttpResponse response = HttpUtil.createGet("http://localhost:38848/nacos/v1/cs/configs?" + urlPara)
                .execute();
        return response.isOk() ? Flux.fromIterable(JSONArray.parseArray(response.body(), FlowRule.class)) : Flux.empty();
    }

    public Mono<Boolean> publishFlowRule(Flux<FlowRule> rules) {
        List<FlowRule> ruleList = rules.collectList().share().block();

        String body = "tenant=3e4a06ab-3139-46e8-bc82-888154383de0&" +
                "dataId=qqrobot-sentinel-flow-dev.json&" +
                "group=DEFAULT_GROUP&" +
                "type=json&" +
                "content=" + JSON.toJSONString(ruleList);

        HttpResponse response = HttpUtil.createPost("http://localhost:38848/nacos/v1/cs/configs")
                .body(body)
                .execute();

        return Mono.just(response.isOk());
    }
}
