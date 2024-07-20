package org.smartboot.plugin.executable;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.smartboot.flow.core.util.AuxiliaryUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yamikaze
 * @date 2023/6/17 22:27
 * @since 1.1.0
 */
public class ParameterNameClassVisitor extends ClassVisitor {

    private final List<MethodDescriptor> methodDescriptors = new ArrayList<>(0);
    private String className;

    ParameterNameClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (Objects.equals(name, "<init>") || Objects.equals(name, "<clinit>")) {
            return null;
        }

        Type[] argumentTypes = Type.getArgumentTypes(descriptor);
        LocalVariableMethodVisitor localVariableMethodVisitor = new LocalVariableMethodVisitor(Opcodes.ASM8, access, argumentTypes);

        MethodDescriptor md = new MethodDescriptor();
        methodDescriptors.add(md);

        Class<?>[] javaTypes = new Class[argumentTypes.length];
        int index = 0;
        for (Type argumentType : argumentTypes) {
            javaTypes[index++] = transfer2JavaType(argumentType);
        }

        md.setMethodName(name);
        md.setArguments(javaTypes);
        md.setParameterNames(localVariableMethodVisitor.getParameterNames());
        md.setDeclaredClass(className);
        return localVariableMethodVisitor;

    }

    private static Class<?> transfer2JavaType(Type argumentType) {
        if (argumentType == Type.VOID_TYPE) {
            return void.class;
        } else if (argumentType == Type.INT_TYPE) {
            return int.class;
        } else if (argumentType == Type.SHORT_TYPE) {
            return short.class;
        } else if (argumentType == Type.BYTE_TYPE) {
            return byte.class;
        } else if (argumentType == Type.LONG_TYPE) {
            return long.class;
        } else if (argumentType == Type.FLOAT_TYPE) {
            return float.class;
        } else if (argumentType == Type.DOUBLE_TYPE) {
            return double.class;
        } else if (argumentType == Type.CHAR_TYPE) {
            return char.class;
        } else if (argumentType == Type.BOOLEAN_TYPE) {
            return boolean.class;
        } else {
            // 数组形如 [[[Ljava.lang.Long; 的形式
            String descriptor = argumentType.getDescriptor();
            descriptor = descriptor.replace("/", ".");
            int length = descriptor.length();
            descriptor = descriptor.replace("[", "");
            int dimens = length - descriptor.length();
            descriptor = descriptor.replace(";", "");
            if (descriptor.startsWith("L")) {
                descriptor = descriptor.substring(1);
            }

            Class<?> rawType = AuxiliaryUtils.asClass(descriptor);
            // 可能是基本类型
            if (rawType == null) {
                rawType = transfer2JavaType(Type.getType(descriptor));
            }

            while (dimens-- > 0) {
                rawType = Array.newInstance(rawType, 0).getClass();
            }

            return rawType;
        }
    }

    public List<MethodDescriptor> getMethodParameters() {
        return methodDescriptors;
    }
}
