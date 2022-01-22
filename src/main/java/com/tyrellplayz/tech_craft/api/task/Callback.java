package com.tyrellplayz.tech_craft.api.task;

import javax.annotation.Nullable;

public interface Callback<T> {

    void execute(@Nullable T t, boolean b);

}
