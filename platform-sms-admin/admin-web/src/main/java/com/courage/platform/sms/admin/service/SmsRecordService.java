package com.courage.platform.sms.admin.service;

import com.courage.platform.sms.admin.dao.domain.TSmsRecordDetail;
import com.courage.platform.sms.admin.vo.BaseModel;

import java.util.List;
import java.util.Map;

public interface SmsRecordService {

    List<TSmsRecordDetail> queryRecordDetailList(Map<String, Object> param);

    Long queryCountRecordDetail(Map<String, Object> param);

    BaseModel<String> adminSendRecord(String mobile, String templateId, String templateParam);

}