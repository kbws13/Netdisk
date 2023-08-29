package xyz.kbws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.kbws.annotation.GlobalInterceptor;
import xyz.kbws.annotation.VerifyParam;
import xyz.kbws.entity.enums.ResponseCodeEnum;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.utils.StringTools;
import xyz.kbws.utils.VerifyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component("globalOperatcionAspect")
public class GlobalOperatcionAspect {

    private static final Logger logger = LoggerFactory.getLogger(GlobalOperatcionAspect.class);

    private static final String TYPE_STRING = "java.lang.String";
    private static final String TYPE_INTEGER = "java.lang.Integer";
    private static final String TYPE_LONG = "java.lang.Long";

    @Pointcut("@annotation(xyz.kbws.annotation.GlobalInterceptor)")
    private void requestInterceptor(){

    }

    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws BusinessException{
        try {
            Object target = point.getTarget();
            Object[] arguments = point.getArgs();
            String methodName = point.getSignature().getName();
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null){
                return;
            }
            /**
             * 校验参数
             */
            if (interceptor.checkParams()){
                validateParams(method, arguments);
            }
        }catch (BusinessException e){
            logger.error("全局拦截器异常{}",e);
            throw e;
        }catch (Exception e){
            logger.error("全局拦截器异常{}",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }catch (Throwable e){
            logger.error("全局拦截器异常{}",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    private void validateParams(Method m, Object[] arguments) throws BusinessException{
        Parameter[] parameters = m.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null){
                continue;
            }
            //基本数据类型
            if (TYPE_STRING.equals(parameter.getParameterizedType().getTypeName()) ||
                TYPE_LONG.equals(parameter.getParameterizedType().getTypeName())){
                checkValue(value, verifyParam);
                //如果传递的是对象
            }else {
                checkObjValue(parameter, value);
            }
        }
    }

    private void checkObjValue(Parameter parameter, Object value){
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class classz = Class.forName(typeName);
            Field[] fields = classz.getDeclaredFields();
            for (Field field : fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null){
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkValue(resultValue, fieldVerifyParam);
            }
        }catch (BusinessException e){
            logger.error("校验参数失败",e);
            throw e;
        }catch (Exception e){
            logger.error("校验参数失败",e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    private void checkValue(Object value, VerifyParam verifyParam) throws BusinessException{
        Boolean isEmpty = value == null || StringTools.isEmpty(value.toString());
        Integer length = value == null ? 0 : value.toString().length();

        /**
         * 校验空
         */
        if (isEmpty && verifyParam.required()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
         * 校验长度
         */
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 &&
                verifyParam.min() > length)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
         * 校验正则
         */
        if (!isEmpty && !StringTools.isEmpty(verifyParam.regex().getRegex()) &&
                !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
