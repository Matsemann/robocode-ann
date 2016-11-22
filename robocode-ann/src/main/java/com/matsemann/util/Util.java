package com.matsemann.util;

import java.util.List;

public class Util {

    public static <E> E getLast(List<E> list) {
        if (!list.isEmpty()) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }


    public static <E> E getLastOr(List<E> list, E def) {
        E last = getLast(list);
        return last == null ? def : last;
    }


}
