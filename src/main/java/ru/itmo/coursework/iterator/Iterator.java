package ru.itmo.coursework.iterator;

import javafx.scene.image.Image;

public interface Iterator {
    boolean hasNext();
    Image next();
    Image preview();
    boolean hasPreview();

    void goToFirst();
    void goToLast();

    int getCurrentIndex();
    int getSize();
    Image getCurrentImage();
}