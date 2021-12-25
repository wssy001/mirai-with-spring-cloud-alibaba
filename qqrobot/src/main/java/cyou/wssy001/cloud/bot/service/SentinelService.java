package cyou.wssy001.cloud.bot.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SentinelService {

    public Boolean saveOrUpdateFlowRule(FlowRule rule) {

        List<FlowRule> flowRule = getFlowRule();
        flowRule = flowRule.parallelStream()
                .filter(v -> v.getResource().equals(rule.getResource()))
                .map(v -> rule)
                .collect(Collectors.toList());

        if (flowRule.isEmpty()) {
            flowRule = new ArrayList<>();
            flowRule.add(rule);
        }

        return publishFlowRule(flowRule);
    }

    public List<FlowRule> getFlowRule() {
        String urlPara = "tenant=3e4a06ab-3139-46e8-bc82-888154383de0&" +
                "dataId=qqrobot-sentinel-flow-dev.json&" +
                "group=DEFAULT_GROUP";

        HttpResponse response = HttpUtil.createGet("http://localhost:38848/nacos/v1/cs/configs?" + urlPara)
                .execute();
        return response.isOk() ? JSONArray.parseArray(response.body(), FlowRule.class) : Collections.emptyList();
    }

    public Boolean publishFlowRule(List<FlowRule> rules) {

        String body = "tenant=3e4a06ab-3139-46e8-bc82-888154383de0&" +
                "dataId=qqrobot-sentinel-flow-dev.json&" +
                "group=DEFAULT_GROUP&" +
                "type=json&" +
                "content=" + JSON.toJSONString(rules);

        HttpResponse response = HttpUtil.createPost("http://localhost:38848/nacos/v1/cs/configs")
                .body(body)
                .execute();

        return response.isOk();
    }
}
