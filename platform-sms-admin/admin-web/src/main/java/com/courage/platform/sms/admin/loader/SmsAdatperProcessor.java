package com.courage.platform.sms.admin.loader;

import com.courage.platform.sms.admin.loader.processor.ProcessorRequest;
import com.courage.platform.sms.admin.loader.processor.ProcessorResponse;

/**
 * 适配器处理器接口
 * Created by zhangyong on 2023/5/5.
 */
public interface SmsAdatperProcessor<P, R> {

    ProcessorResponse<R> processRequest(ProcessorRequest<P> processorRequest);

}
