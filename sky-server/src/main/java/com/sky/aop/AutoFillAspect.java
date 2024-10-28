package com.sky.aop;

import com.sky.anno.AutoFill;
import com.sky.context.BaseContext;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.anno.AutoFill)")
    void pt() {
    }

    @Before("pt()")
    public void setTimeUser(JoinPoint joinPoint) {
        log.info("开始添加");
        //1.获取当前被拦截方法上的数据库操作类型（insert 还是update）
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature(); //方法签名对象
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);//方法上的注释对象
        //2.获取当前被拦截对象的参数--emp实体
        Object args[] = joinPoint.getArgs();
        Employee employee = new Employee();
        try{
        employee = (Employee) args[0];
        }catch(Exception e){
            e.printStackTrace();
        }
        //3.准备赋值数据
        LocalDateTime localDateTime=LocalDateTime.now();
        Long id = BaseContext.getCurrentId();
        //4.根据当前不同操作类型，为对应的属性通过反射赋值
        if(autoFill.value()==(OperationType.INSERT)){
            employee.setCreateTime(localDateTime);
            employee.setUpdateTime(localDateTime);
            employee.setCreateUser(id);
            employee.setUpdateUser(id);
        }else if(autoFill.value()==(OperationType.UPDATE)) {
            employee.setUpdateTime(localDateTime);
            employee.setUpdateUser(id);
        }

    }
}
