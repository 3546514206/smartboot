package org.smartboot.plugin.executable;

import java.util.List;

/**
 * @author yamikaze
 * @date 2023/6/17 23:17
 * @since 1.1.0
 */
public class MethodDescriptor {

    private String declaredClass;
    private String methodName;
    private Class<?>[] arguments;
    private List<String> parameterNames;

    public String getDeclaredClass() {
        return declaredClass;
    }

    public void setDeclaredClass(String declaredClass) {
        this.declaredClass = declaredClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getArguments() {
        return arguments;
    }

    public void setArguments(Class<?>[] arguments) {
        this.arguments = arguments;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }
}
