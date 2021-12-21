package cyou.wssy001.cloud.bot.service.impl;

import cyou.wssy001.cloud.bot.service.TPlainTextService;
import cyou.wssy001.cloud.bot.entity.TPlainText;
import cyou.wssy001.cloud.bot.dao.TPlainTextDAO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TPlainTextServiceImp extends ServiceImpl<TPlainTextDAO, TPlainText> implements TPlainTextService {

}
