package com.test.common.mysql.sharding.plugin;

import com.google.common.base.Preconditions;

public class ShardHint {
    private static final ThreadLocal<Object> HINT_HOLDER = new ThreadLocal();

    public ShardHint() {
    }

    public static void setHint(Object hint) {
        Preconditions.checkState(null == HINT_HOLDER.get(), "ShardHint has previous value, please clear first.");
        HINT_HOLDER.set(hint);
    }

    public static Object getHint() {
        return HINT_HOLDER.get();
    }

    public static void clear() throws Exception {
        HINT_HOLDER.remove();
    }
}
