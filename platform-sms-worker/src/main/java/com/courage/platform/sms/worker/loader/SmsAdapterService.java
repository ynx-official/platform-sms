package com.courage.platform.sms.worker.loader;

import com.courage.platform.sms.dao.TSmsChannelDAO;
import com.courage.platform.sms.domain.TSmsChannel;
import com.courage.platform.sms.worker.config.SmsAdapterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class SmsAdapterService {

    private final static Logger logger = LoggerFactory.getLogger(SmsAdapterService.class);

    private volatile boolean running = false;

    private SmsAdapterLoader smsAdapterLoader;

    @Autowired
    private SmsAdapterConfig smsAdapterConfig;

    @Autowired
    private TSmsChannelDAO tSmsChannelDAO;

    @PostConstruct
    public synchronized void init() {
        if (running) {
            return;
        }
        try {
            logger.info("start the sms adapters.");
            List<TSmsChannel> channelList = tSmsChannelDAO.queryChannels();
            smsAdapterLoader = new SmsAdapterLoader(smsAdapterConfig);
            smsAdapterLoader.init();
            running = true;
            logger.info("the sms adapters are running now ......");
        } catch (Exception e) {
            logger.error("something goes wrong when starting up the sms adapters:", e);
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        if (!running) {
            return;
        }
        try {
            running = false;
            logger.info("## stop the sms adapters");
            if (this.smsAdapterLoader != null) {
                smsAdapterLoader.destroy();
            }
        } catch (Throwable e) {
            logger.warn("## something goes wrong when stopping sms adapters:", e);
        } finally {
            logger.info("## sms adapters are down.");
        }
    }

}
