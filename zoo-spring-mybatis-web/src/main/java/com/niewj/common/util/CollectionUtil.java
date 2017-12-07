package com.niewj.common.util;

import java.util.List;
import java.util.Map;

public class CollectionUtil {
    public static <T> boolean notEmpty(T[] array) {
        return array != null && array.length > 0;
    }

    public static <T> boolean notEmpty(List<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <K, V> boolean notEmpty(Map<K, V> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <K, V> Map<K, V> notEmptyReturn(Map<K, V> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new RuntimeException("collection is null or empty");
        } else {
            return collection;
        }
    }
}
