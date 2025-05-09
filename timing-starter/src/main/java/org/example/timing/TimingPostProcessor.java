package org.example.timing;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class TimingPostProcessor implements BeanPostProcessor {

    private static final Map<String, Class<?>> map = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> javaClass = bean.getClass();
        if (javaClass.isAnnotationPresent(Timing.class)) {
            map.put(beanName, javaClass);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (map.get(beanName) != null) {
            var proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvice(new TimingInterceptor());
            return proxyFactory.getProxy();
        }
        else {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
    }

    static class TimingInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String methodName = invocation.getMethod().getName();
            Class<?> className = Objects.requireNonNull(invocation.getThis()).getClass();
            log.info("\u001B[35m" + "Method [{}] from [{}] has started...", methodName, className);
            Instant startTime = Instant.now();
            Object result = invocation.proceed();
            Instant endTime = Instant.now();
            log.info("\u001B[35m" + "Method [{}] from [{}] has finished in [{}] nanos.", methodName, className, Duration.between(startTime, endTime).getNano());
            return result;
        }
    }
}


