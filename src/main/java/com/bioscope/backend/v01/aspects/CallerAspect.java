package com.bioscope.backend.v01.aspects;


import com.bioscope.backend.v01.utils.CallerContext;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CallerAspect {

    @Before("execution(* com.bioscope.backend.v01.services.impl.UserServiceImpl.*(..))")
    public void setUserCaller() {
        CallerContext.setCaller("USER");
    }

    @Before("execution(* com.bioscope.backend.v01.services.impl.HostServiceImpl.*(..))")
    public void setHostCaller() {
        CallerContext.setCaller("HOST");
    }

    @After("execution(* com.bioscope.backend.v01.services.impl..*(..))")
    public void clearCaller() {
        CallerContext.clear();
    }
}
