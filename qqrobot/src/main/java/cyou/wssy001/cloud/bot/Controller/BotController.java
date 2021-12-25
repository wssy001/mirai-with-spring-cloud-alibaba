package cyou.wssy001.cloud.bot.Controller;

import cyou.wssy001.cloud.bot.vo.CollectMsgVo;
import cyou.wssy001.cloud.bot.vo.SendMsgVo;
import net.mamoe.mirai.Bot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

    @GetMapping("/send/msg")
    public String sendMsg(
            @RequestBody SendMsgVo sendMsgVo
    ) {
        Bot bot = Bot.getInstanceOrNull(sendMsgVo.getBotId());
        if (bot == null) return "Bot ID有误";

        bot.getGroup(sendMsgVo.getGroupId())
                .get(sendMsgVo.getQQ())
                .sendMessage(sendMsgVo.getMsg());

        return "成功";
    }

    @GetMapping("/collect/msg")
    public String collectMsg(
            @RequestBody CollectMsgVo collectMsgVo
    ) {
        Bot bot = Bot.getInstanceOrNull(collectMsgVo.getBotId());
        if (bot == null) return "Bot ID有误";

        bot.getGroup(collectMsgVo.getGroupId())
                .get(collectMsgVo.getQQ());

        return "成功";
    }
}
