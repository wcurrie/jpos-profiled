package x.foo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import x.foo.tracker.Tracker;

@Aspect
public class AllocationTrackingAspect {

    @Pointcut("call(java.lang..*.new(..)) || call(org.jpos..*.new(..))")
    public void tracked() {}

    @Before("tracked() && !within(x.foo..*)")
    public void track(JoinPoint joinPoint) {
        String clazz = joinPoint.getSignature().toString();
        Tracker.current().constructed(clazz);
    }
}
