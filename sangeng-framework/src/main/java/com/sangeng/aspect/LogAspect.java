package com.sangeng.aspect;

import com.alibaba.fastjson.JSON;
import com.sangeng.annotion.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(com.sangeng.annotion.SystemLog)")
    public void pt(){}

    //环绕通知，功能最强大
    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable{
        //获取目标方法返回值
        Object ret = null;
        try {
            //目标方法之前执行
            handleBefore(joinPoint);
            ret = joinPoint.proceed();
            handleAfter(ret);
        } finally {
            //结束后换行
            log.info("=======End=======" + System.lineSeparator());
        }
        return ret;
    }


    private void handleBefore(ProceedingJoinPoint joinPoint) {
        //Holder一般都是用ThreadLocal保存信息，就是说具有线程隔离性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}",request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}",systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}",request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                ((MethodSignature)joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}",request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    private void handleAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}",JSON.toJSONString(ret));
    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = signature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }
}
