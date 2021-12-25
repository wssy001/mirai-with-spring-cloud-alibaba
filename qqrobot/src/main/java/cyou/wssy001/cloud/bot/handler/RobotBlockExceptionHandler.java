package cyou.wssy001.cloud.bot.handler;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.service.UnhandledHttpRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class RobotBlockExceptionHandler implements BlockExceptionHandler {
    private final UnhandledHttpRequestService unhandledHttpRequestService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonBody = IoUtil.read(reader);

        UnhandledHttpRequest unhandledHttpRequest = new UnhandledHttpRequest(IdUtil.fastSimpleUUID(), jsonBody, request.getMethod());
        unhandledHttpRequestService.upset(unhandledHttpRequest);

        response.setStatus(429);
        PrintWriter out = response.getWriter();
        out.print("机器人服务忙，请稍后重试");
        out.flush();
        out.close();
    }

}
