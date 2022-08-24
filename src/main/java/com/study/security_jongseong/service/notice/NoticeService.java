package com.study.security_jongseong.service.notice;

import com.study.security_jongseong.web.dto.notice.AddNoticeReqDto;
import com.study.security_jongseong.web.dto.notice.GetNoticeResponseDto;

public interface NoticeService {
	
	public int addNotice(AddNoticeReqDto addNoticeReqDto) throws Exception;

	public GetNoticeResponseDto getNotice(String flag, int noticeCode) throws Exception;
	
}
