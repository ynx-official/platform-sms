package com.courage.platform.sms.admin.loader;

import com.courage.platform.sms.adapter.OuterAdapter;
import com.courage.platform.sms.adapter.support.ExtensionLoader;
import com.courage.platform.sms.adapter.support.SmsChannelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配器加载器
 */
@Component
public class SmsAdapterLoader {

    private final static Logger logger = LoggerFactory.getLogger(SmsAdapterLoader.class);

    private static ExtensionLoader<OuterAdapter> EXTENSION_LOADER = ExtensionLoader.getExtensionLoader(OuterAdapter.class);

    private static ConcurrentHashMap<Long, OuterAdapter> ADAPTER_MAP = new ConcurrentHashMap<>(16);

    private void loadAdapter(SmsChannelConfig smsChannelConfig) {
        String adapterName = smsChannelConfig.getChannelType();
        try {
            OuterAdapter adapter = EXTENSION_LOADER.getExtension(adapterName);
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            // 替换 ClassLoader
            Thread.currentThread().setContextClassLoader(adapter.getClass().getClassLoader());
            adapter.init(smsChannelConfig);
            Thread.currentThread().setContextClassLoader(cl);
            ADAPTER_MAP.put(smsChannelConfig.getId(), adapter);
            logger.info("Load sms adapter: {} succeed", adapterName);
        } catch (Exception e) {
            logger.error("Load canal adapter: {} failed", adapterName, e);
        }
    }

}