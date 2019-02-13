package com.internshiporganizer.Fragments;

import java.util.List;

public interface Updatable<T> {
    void update(List<T> items);
}
