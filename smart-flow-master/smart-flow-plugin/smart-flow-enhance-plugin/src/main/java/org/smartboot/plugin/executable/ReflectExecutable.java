package org.smartboot.plugin.executable;

import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Key;
import org.smartboot.flow.core.executable.AbstractExecutable;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Adapter custom code without implementation interface {@link org.smartboot.flow.core.executable.Executable}.
 *
 * @author yamikaze
 * @date 2023/6/17 18:25
 * @since 1.1.0
 */
public class ReflectExecutable<T, S> extends AbstractExecutable<T, S> {

    protected volatile boolean initialized = false;

    /**
     * 执行方法
     */
    @org.smartboot.flow.helper.annotated.Key("execute")
    private String executeMethod = "execute";
    private Method targetMethod;
    private final List<ParameterDescriptor> parameters = new ArrayList<>(0);

    /**
     * 回滚方法
     */
    private String rollbackMethod;
    private Method rollbackTargetMethod;
    private final List<ParameterDescriptor> rollbackParameters = new ArrayList<>(0);


    /**
     * 执行目标
     */
    @org.smartboot.flow.helper.annotated.Key
    private Object target;

    /**
     * 回滚目标
     */
    private Object rollbackTarget;

    /**
     * 结果id
     */
    private String resultId;

    @Override
    public String describe() {
        return "Reflect[" + target.getClass().getName() + "#" + executeMethod + "]";
    }

    private synchronized void init() {
        if (initialized) {
            return;
        }

        AssertUtil.notNull(target, "target must not be null");
        AssertUtil.notBlank(executeMethod, "execute-method must not be null");
        rollbackTarget = rollbackTarget != null ? rollbackTarget : target;

        // 第一个方法为执行方法
        targetMethod = ReflectionUtils.lookUpMethod(target.getClass(), executeMethod, m -> true);
        AssertUtil.notNull(targetMethod, "cannot find execute-method [" + executeMethod + "] in type " + target.getClass().getName());
        resolveParameters(targetMethod, parameters);

        if (AuxiliaryUtils.isNotBlank(rollbackMethod)) {
            rollbackTargetMethod = ReflectionUtils.lookUpMethod(rollbackTarget.getClass(), rollbackMethod, m -> true);
            AssertUtil.notNull(rollbackTargetMethod, "cannot find rollback-method " + rollbackMethod + " in type " + rollbackTarget.getClass().getName());
            resolveParameters(rollbackTargetMethod, rollbackParameters);
        }

        if (AuxiliaryUtils.isBlank(resultId)) {
            resultId = target.getClass().getName() + "-" + executeMethod;
        }

        initialized = true;
    }

    private void resolveParameters(Method method, List<ParameterDescriptor> parameterList) {
        int index = 0;
        Parameter[] parametersList = method.getParameters();
        for (Parameter p : parametersList) {
            ParameterDescriptor descriptor = new ParameterDescriptor();
            Param param = p.getAnnotation(Param.class);
            descriptor.type = ReflectionUtils.getWrappedType(p.getType());
            descriptor.parameterIndex = index++;
            descriptor.defaultValue = getDefaultValue(p.getType());
            if (param != null) {
                descriptor.name = param.value();
            } else {
                MethodDescriptor md = MethodDescriptorUtils.getMethodDescriptor(method);
                if (md != null) {
                    descriptor.name = md.getParameterNames().get(descriptor.parameterIndex);
                } else {
                    descriptor.name = p.getName();
                }
            }
            parameterList.add(descriptor);
        }
    }

    @Override
    public void execute(EngineContext<T, S> context) {
        if (!initialized) {
            init();
        }

        Object[] args = prepareArgs(context, parameters);
        Object result = ReflectionUtils.invokeMethod(target, targetMethod, args);
        if (targetMethod.getReturnType() != void.class) {
            context.putExt(Key.of(resultId), result);
        }
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        if (!initialized || rollbackTargetMethod == null) {
            return;
        }

        Object[] args = prepareArgs(context, rollbackParameters);
        ReflectionUtils.invokeMethod(rollbackTarget, rollbackTargetMethod, args);
    }

    private Object[] prepareArgs(EngineContext<T, S> context, List<ParameterDescriptor> parameterList) {
        List<Object> args = new ArrayList<>(parameterList.size());
        Object request = context.getReq();
        Object result = context.getResult();

        for (ParameterDescriptor descriptor : parameterList) {
            if (EngineContext.class.isAssignableFrom(descriptor.type)) {
                args.add(context);
                continue;
            }

            if (descriptor.type.isInstance(request) && (Objects.equals(descriptor.name, "req") || Objects.equals(descriptor.name, "request"))) {
                args.add(request);
                continue;
            }

            if (descriptor.type.isInstance(result) && (Objects.equals(descriptor.name, "result") || Objects.equals(descriptor.name, "response"))) {
                args.add(result);
                continue;
            }

            // by parameter name.
            Object ext = context.getExt(Key.of(descriptor.name));
            if (descriptor.type.isInstance(ext)) {
                args.add(ext);
                continue;
            }

            // find Key.name == fieldName objects.
            boolean found = false;
            Map<Key<?>, Object> allExt = context.getAllExt();
            for (Map.Entry<Key<?>, Object> entry : allExt.entrySet()) {
                if (Objects.equals(entry.getKey().toString(), descriptor.name)
                        && descriptor.type.isInstance(entry.getValue())) {
                    args.add(ext);
                    found = true;
                    break;
                }
            }

            if (!found) {
                args.add(descriptor.defaultValue);
            }
        }

        return args.toArray();
    }

    private static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }

        if (type == byte.class) {
            return (byte)0;
        } else if (type == short.class) {
            return (short)0;
        } else if (type == int.class) {
            return 0;
        } else if (type == long.class) {
            return 0L;
        } else if (type == float.class) {
            return 0f;
        } else if (type == double.class) {
            return 0d;
        } else if (type == char.class) {
            return (char)0;
        } else if (type == boolean.class) {
            return false;
        }

        AssertUtil.shouldNotReachHere();
        return null;
    }

    static class ParameterDescriptor {
        private String name;
        private Class<?> type;
        private int parameterIndex;
        private Object defaultValue;
    }

    public String getExecuteMethod() {
        return executeMethod;
    }

    public void setExecuteMethod(String executeMethod) {
        this.executeMethod = executeMethod;
    }

    public String getRollbackMethod() {
        return rollbackMethod;
    }

    public void setRollbackMethod(String rollbackMethod) {
        this.rollbackMethod = rollbackMethod;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getRollbackTarget() {
        return rollbackTarget;
    }

    public void setRollbackTarget(Object rollbackTarget) {
        this.rollbackTarget = rollbackTarget;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}
