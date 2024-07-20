package org.smartboot.flow.helper.util;

import org.smartboot.flow.core.parser.ExecutableTypeDetector;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.util.ReflectionUtils;
import org.smartboot.flow.helper.annotated.Key;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2023-07-09 10:50:07
 * @since 1.1.2
 */
public class DecorateUtils {

    public static String decorateExecutable(Class<?> clazz, Map<String, String> bindingAttrs) {
        if (clazz == null || bindingAttrs == null || bindingAttrs.isEmpty()) {
            return null;
        }

        List<Class<?>> classes = ReflectionUtils.collectAllSuperClassAndInterfaces(clazz);
        List<Field> fields = new ArrayList<>();
        for (Class<?> type : classes) {
            // Skip interfaces.
            if (type.isInterface()) {
                continue;
            }

            Field[] declaredFields = type.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Key key = declaredField.getAnnotation(Key.class);
                if (key != null) {
                    fields.add(declaredField);
                }
            }
        }


        if (fields.isEmpty()) {
            return null;
        }

        String phrase = ExecutableTypeDetector.get().getPhrase(clazz);
        phrase = (phrase == null) ? clazz.getSimpleName() : phrase;
        StringBuilder sb = new StringBuilder();
        sb.append(phrase).append("@");

        for (int i = fields.size() - 1; i >= 0; i--) {
            String name = fields.get(i).getName();
            String value = null;
            for (Map.Entry<String, String> entry : bindingAttrs.entrySet()) {
                String attrName = entry.getKey().substring(entry.getKey().indexOf(".") + 1);
                attrName = AuxiliaryUtils.transfer2CamelCase(attrName);
                if (Objects.equals(name, attrName)) {
                    value = entry.getValue();
                }
            }
            if (value == null) {
                Key key = fields.get(i).getAnnotation(Key.class);
                value = key.value();
            }

            if (AuxiliaryUtils.isBlank(value)) {
                value = "(" + fields.get(i).getName() + " is null)";
            }

            sb.append(value).append("-");
        }

        String value = sb.toString();

        return value.substring(0, value.length() - 1);
    }
}
