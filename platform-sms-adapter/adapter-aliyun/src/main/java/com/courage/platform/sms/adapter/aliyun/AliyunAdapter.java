package com.courage.platform.sms.adapter.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.AddSmsTemplateResponse;
import com.aliyun.dysmsapi20170525.models.AddSmsTemplateResponseBody;
import com.courage.platform.sms.adapter.OuterAdapter;
import com.courage.platform.sms.adapter.command.AddSmsTemplateCommand;
import com.courage.platform.sms.adapter.command.SendSmsCommand;
import com.courage.platform.sms.adapter.command.SmsResponseCommand;
import com.courage.platform.sms.adapter.support.SPI;
import com.courage.platform.sms.adapter.support.SmsChannelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@SPI("aliyun")
public class AliyunAdapter implements OuterAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AliyunAdapter.class);

    private SmsChannelConfig smsChannelConfig;

    private Client client;

    private final static int SUCCESS_CODE = 200;

    @Override
    public void init(SmsChannelConfig smsChannelConfig) throws Exception {
        this.smsChannelConfig = smsChannelConfig;
        logger.info("阿里云短信客户端 渠道编号:[" + smsChannelConfig.getId() + "] appkey:[" + smsChannelConfig.getChannelAppkey() + "]");
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(smsChannelConfig.getChannelAppkey())
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(smsChannelConfig.getChannelAppsecret());
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        this.client = new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Override
    public SmsResponseCommand sendSmsByTemplateId(SendSmsCommand sendSmsCommand) {
        try {
            com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest().
                    setSignName(sendSmsCommand.getSignName()).
                    setTemplateCode(sendSmsCommand.getTemplateCode()).
                    setPhoneNumbers(sendSmsCommand.getPhoneNumbers()).
                    setTemplateParam(sendSmsCommand.getTemplateParam());
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.dysmsapi20170525.models.SendSmsResponse resp = client.sendSmsWithOptions(sendSmsRequest, runtime);
            logger.info("aliyun resp code:{} body:{}" , resp.getStatusCode() , JSON.toJSON(resp.getBody()));
            if (SUCCESS_CODE == resp.getStatusCode() && "OK".equals(resp.getBody().getCode())) {
                return new SmsResponseCommand(SmsResponseCommand.SUCCESS_CODE, resp.getBody().getBizId());
            }
            return new SmsResponseCommand(SmsResponseCommand.FAIL_CODE);
        } catch (Exception e) {
            logger.error("aliyun sendSms:", e);
            return new SmsResponseCommand(SmsResponseCommand.FAIL_CODE);
        }
    }

    @Override
    public SmsResponseCommand addSmsTemplate(AddSmsTemplateCommand addSmsTemplateCommand) {
        try {
            com.aliyun.dysmsapi20170525.models.AddSmsTemplateRequest addSmsTemplateRequest = new com.aliyun.dysmsapi20170525.models.AddSmsTemplateRequest();
            addSmsTemplateRequest.setRemark(addSmsTemplateCommand.getRemark());
            addSmsTemplateRequest.setTemplateContent(addSmsTemplateCommand.getTemplateContent());
            addSmsTemplateRequest.setTemplateType(addSmsTemplateCommand.getTemplateType());
            addSmsTemplateRequest.setTemplateName(addSmsTemplateCommand.getTemplateName());
            AddSmsTemplateResponse resp = client.addSmsTemplate(addSmsTemplateRequest);
            logger.info("resp=" + JSON.toJSONString(resp));
            if (SUCCESS_CODE == resp.getStatusCode()) {
                AddSmsTemplateResponseBody body = resp.getBody();
                Map<String, String> bodyMap = new HashMap<>();
                bodyMap.put("templateCode", body.getTemplateCode());
                bodyMap.put("templateContent", addSmsTemplateCommand.getTemplateContent());
                return new SmsResponseCommand(SmsResponseCommand.SUCCESS_CODE, bodyMap);
            }
            return new SmsResponseCommand(SmsResponseCommand.FAIL_CODE);
        } catch (Exception e) {
            logger.error("aliyun addSmsTemplate error :", e);
            return new SmsResponseCommand(SmsResponseCommand.FAIL_CODE, e.getMessage());
        }
    }

    @Override
    public void destroy() {
        logger.info("销毁阿里云短信客户端渠道编号:[" + smsChannelConfig.getId() + "] appkey:[" + smsChannelConfig.getChannelAppkey() + "]");
    }

}
