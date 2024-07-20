package org.smartboot.plugin.resovler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qinluo
 * @version 1.1.0
 * @date 2021/3/15 12:28 上午
 *
 * 1、多个不嵌套的占位符
 * 2、不完全的嵌套占位符，例如${${java}.home}  类似这种
 */
public class PlaceHolderResolver {

    /**
     * Place holder prefix, such as ${
     */
    private final String prefix;

    /**
     * Placeholder suffix, such as '}'
     */
    private final String suffix;

    private final AbstractPropertyResolver propertyResolver;

    public PlaceHolderResolver(String prefix, String suffix, AbstractPropertyResolver propertyResolver) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.propertyResolver = propertyResolver;
    }

    public String resolve(String path) {
        return internalResolve(path, new HashSet<>(32));
    }

    private String internalResolve(String path, Set<String> placeHolders) {
        int index = path.indexOf(prefix);
        while (index != -1) {
            int suffixIndex = searchSuffix(path, index  + prefix.length());
            if (suffixIndex == -1) {
                return path;
            }

            // 拿到${}包装的最外层
            String placeHolder = path.substring(index  + prefix.length(), suffixIndex);
            String originPlaceHolder = placeHolder;
            // 检查一下是否有嵌套
            if (!placeHolders.add(originPlaceHolder)) {
                throw new IllegalStateException("Nest place holder : " + placeHolder);
            }
            // 拿到的${}里面也需还有${}需要将里层的再进行处理掉，这儿循环递归
            placeHolder = internalResolve(placeHolder, placeHolders);

            String placeHolderValue = placeHolder;
            if (placeHolder.length() > 0) {
                // 获取placeHolder实际对应的值
                placeHolderValue = getRawPlaceHolder(placeHolder);
                // 可能没有，先默认取placeHolder的值
                if (placeHolderValue == null) {
                    placeHolderValue = placeHolder;
                }
            }

            // 将当前的${}替换到原字符串中
            path = path.substring(0, index)
                    // 获取出来的placeHolderValue也许存在${}，需要再处理下
                    + internalResolve(placeHolderValue, placeHolders)
                    + path.substring(suffixIndex + suffix.length());
            placeHolders.remove(originPlaceHolder);
            // 处理完当前${}, 进行下一个${}的处理
            index = path.indexOf(prefix);
        }

        return path;
    }

    private int searchSuffix(String path, int index) {
        int length = path.length();
        int nestPlaceHolder = 0;
        while (index < length) {
            if (endsWith(path, index, prefix)) {
                nestPlaceHolder++;
                index += prefix.length();
            } else if (endsWith(path, index, suffix)) {
                if (nestPlaceHolder == 0)  {
                    return index;
                }
                nestPlaceHolder--;
                index += suffix.length();
            } else {
                index++;
            }
        }

        return -1;
    }

    private boolean endsWith(String path, int index, String matchedValue) {
        if (index +  matchedValue.length() - 1  > path.length()) {
            return false;
        }

        for (int i = 0; i < matchedValue.length(); i++) {
            if (matchedValue.charAt(i) != path.charAt(index++)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get placeholder value from system properties by key.
     *
     * @param key key
     * @return    system property.
     */
    public String getRawPlaceHolder(String key) {
        return propertyResolver.getProperty(key);
    }
}
