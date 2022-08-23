package com.study.security_jongseong.web.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.security_jongseong.service.notice.NoticeService;
import com.study.security_jongseong.web.dto.CMRespDto;
import com.study.security_jongseong.web.dto.notice.AddNoticeReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeRestController {
	
	private final NoticeService noticeService;
	
	@PostMapping("") 
	public ResponseEntity<?> addNotice(AddNoticeReqDto addNoticeReqDto) {
		log.info(">>>>>>>>>>>> {}", addNoticeReqDto);
		log.info(">>>>> fileName : {}", addNoticeReqDto.getFile().get(0).getOriginalFilename());
		
		int noticeCode = 0;
		
		try {
			noticeCode = noticeService.addNotice(addNoticeReqDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "Failed to write", noticeCode));
		}
		
		return ResponseEntity.ok(new CMRespDto<>(1, "completing creation", noticeCode));
	}
	
	@GetMapping("/{noticeCode}")
	public ResponseEntity<?> getNotice(@PathVariable int noticeCode) {
		return null;
	}
	
}
