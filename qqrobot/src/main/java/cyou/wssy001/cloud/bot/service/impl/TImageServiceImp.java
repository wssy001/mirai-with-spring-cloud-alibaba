package cyou.wssy001.cloud.bot.service.impl;

import cyou.wssy001.cloud.bot.entity.TImage;
import cyou.wssy001.cloud.bot.dao.TImageDAO;
import cyou.wssy001.cloud.bot.service.TImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TImageServiceImp extends ServiceImpl<TImageDAO, TImage> implements TImageService {

}
