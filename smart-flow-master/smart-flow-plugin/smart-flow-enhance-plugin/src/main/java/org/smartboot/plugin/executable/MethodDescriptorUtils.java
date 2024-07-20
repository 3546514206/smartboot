package org.smartboot.plugin.executable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yamikaze
 * @date 2023/6/17 23:23
 * @since 1.1.0
 */
public final class MethodDescriptorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodDescriptorUtils.class);

    private static final Map<Class<?>, List<MethodDescriptor>> DESCRIPTOR_MAP = new ConcurrentHashMap<>();

    public static MethodDescriptor getMethodDescriptor(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        List<MethodDescriptor> methodDescriptors = DESCRIPTOR_MAP.get(declaringClass);
        if (methodDescriptors == null) {
            try {
                ClassReader classReader = new ClassReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(declaringClass.getName().replace(".", "/") + ".class")));
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                ParameterNameClassVisitor classVisitor = new ParameterNameClassVisitor(Opcodes.ASM8, classWriter);
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                methodDescriptors = classVisitor.getMethodParameters();
                DESCRIPTOR_MAP.put(declaringClass, methodDescriptors);
            } catch (Exception e) {
                LOGGER.error("cannot visit method parameter name from class {}", declaringClass.getName(), e);
            }
        }

        if (methodDescriptors == null) {
            return null;
        }

        for (MethodDescriptor descriptor : methodDescriptors) {
            if (Objects.equals(descriptor.getMethodName(), method.getName())
                    && argumentsMatched(method, descriptor.getArguments())) {
                return descriptor;
            }
        }

        return null;
    }

    private static boolean argumentsMatched(Method method, Class<?>[] paramTypes) {
        return (paramTypes.length == method.getParameterCount() &&
                Arrays.equals(paramTypes, method.getParameterTypes()));
    }

}
