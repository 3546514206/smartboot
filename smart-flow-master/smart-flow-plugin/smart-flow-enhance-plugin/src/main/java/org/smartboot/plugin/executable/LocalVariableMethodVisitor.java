package org.smartboot.plugin.executable;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yamikaze
 * @date 2023/6/17 22:30
 * @since 1.1.0
 */
public class LocalVariableMethodVisitor extends MethodVisitor {

    private final Type[] argumentTypes;
    private int variableIndex;
    private final List<String> parameterNames = new ArrayList<>();
    private final boolean isStatic;

    public LocalVariableMethodVisitor(int api, int access, Type[] argumentTypes) {
        super(api);
        this.argumentTypes = argumentTypes;
        this.isStatic = Modifier.isStatic(access);
    }

    @Override
    public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
        // 非static方法第一个参数为this，跳过
        if (!isStatic && variableIndex == 0) {
            variableIndex++;
            return;
        }

        // 跳过非参数的本地变量
        if (variableIndex > argumentTypes.length) {
            return;
        }

        parameterNames.add(name);
        variableIndex++;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }
}
