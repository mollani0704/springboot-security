package com.study.security_jongseong.service.notice;

import com.study.security_jongseong.web.dto.notice.AddNoticeReqDto;

public interface NoticeService {
	
	public int addNotice(AddNoticeReqDto addNoticeReqDto) throws Exception;
	
}
