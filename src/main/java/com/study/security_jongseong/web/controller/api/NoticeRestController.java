package com.study.security_jongseong.web.controller.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.study.security_jongseong.service.notice.NoticeService;
import com.study.security_jongseong.web.dto.CMRespDto;
import com.study.security_jongseong.web.dto.notice.AddNoticeReqDto;
import com.study.security_jongseong.web.dto.notice.GetNoticeListResponseDto;
import com.study.security_jongseong.web.dto.notice.GetNoticeResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeRestController {
	
	@Value("${file.path}")
	private String filePath;
	
	private final NoticeService noticeService;
	
	
	@GetMapping("/list")
	public ResponseEntity<?> getNoticeList(@PathVariable int page, @RequestParam String searchFlag ,@RequestParam String searchValue) {
		
		List<GetNoticeListResponseDto> listDto = null;
		
		try {
			listDto = noticeService.getNoticeList(page, searchFlag, searchValue);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "database error", listDto));
		}
		
		return ResponseEntity.ok().body(new CMRespDto<>(1, "lookup successful", listDto));
	}
	
	
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
		
		GetNoticeResponseDto getNoticeResponseDto = null;
		
		try {
			getNoticeResponseDto = noticeService.getNotice(null, noticeCode);
			if(getNoticeResponseDto == null) {
				return ResponseEntity.badRequest().body(new CMRespDto<>(-1, "request failed", null));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "database error", null));
		}
		
		return ResponseEntity.ok().body(new CMRespDto<>(1, "lookup successful", getNoticeResponseDto));
	}
	
	@GetMapping("/{flag}/{noticeCode}")
	public ResponseEntity<?> getNotice(@PathVariable String flag, @PathVariable int noticeCode) {
		
		GetNoticeResponseDto getNoticeResponseDto = null;
		
		if(flag.equals("pre") || flag.equals("next")) {
			try {
				getNoticeResponseDto = noticeService.getNotice(flag, noticeCode);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "database error", null));
			}
			
		}  else {
			return ResponseEntity.badRequest().body(new CMRespDto<>(-1, "request failed", null));
		}
		
		return ResponseEntity.ok().body(new CMRespDto<>(1, "lookup successful", getNoticeResponseDto));
	}
	
	@GetMapping("/file/download/{fileName}")
	public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {
		
		Path path = Paths.get(filePath + "notice/" + fileName);
		String contentType = Files.probeContentType(path); // 해당 파일의 MIME타입을 찾아준다. 
		
		log.info("contentType: {} ", contentType);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.builder("attachment") // 파일을 다운로드 받을 때 파일명을 인코딩을 해줘야하는데 그런 역할을 하는 아이이다. 
											.filename(fileName, StandardCharsets.UTF_8)
											.build());
		
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);
		
		Resource resource = new InputStreamResource(Files.newInputStream(path));
									
		return ResponseEntity.ok().headers(headers).body(resource);
	}
}
