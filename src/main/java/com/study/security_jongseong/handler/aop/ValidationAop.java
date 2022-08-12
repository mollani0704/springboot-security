package com.study.security_jongseong.handler.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class ValidationAop {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Pointcut("@annotation(com.study.security_jongseong.handler.aop.annotation.ValidCheck)")
	public void enableValid(){};
	
	@Before("enableValid()")
	public void validCheck(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		
		for(Object arg : args) {
			if(arg.getClass() == BeanPropertyBindingResult.class) {
				BindingResult bindingResult = (BindingResult) arg;
				if(bindingResult.hasErrors()) {
					
				}
			}
		}
	}
}
