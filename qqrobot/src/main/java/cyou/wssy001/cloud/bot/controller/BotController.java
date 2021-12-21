package cyou.wssy001.cloud.bot.controller;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BotController {

    @GetMapping("/send/msg")
    public Mono<String> sendMsg(
            @RequestParam("msg") String msg,
            @RequestParam("botId") Long botId,
            @RequestParam(value = "groupId",required = false) Long groupId,
            @RequestParam("qq") Long qq
    ) {
        Bot bot = Bot.getInstanceOrNull(botId);
        if (bot == null) return Mono.just("Bot ID有误").cache();

        bot.getGroup(groupId).get(qq).sendMessage(msg);
        return Mono.just("成功").cache();
    }

    @GetMapping("/collect/msg")
    public Mono<String> collectMsg(
            @RequestBody Long botId,
            @RequestBody(required = false) Long groupId,
            @RequestBody Long qq
    ) {
        Bot bot = Bot.getInstanceOrNull(botId);
        if (bot == null) return Mono.just("Bot ID有误").cache();

        NormalMember normalMember = bot.getGroup(groupId).get(qq);

        return Mono.just("成功").cache();
    }
}
