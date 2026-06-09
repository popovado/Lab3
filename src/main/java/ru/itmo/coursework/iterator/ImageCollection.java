package ru.itmo.coursework.iterator;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageCollection {
    private final List<Image> images = new ArrayList<>();
    private int currentIndex = -1;

    public void add(Image image, String filename) {
        if (image != null && isValidImage(filename)) {
            images.add(image);
        }
    }

    private boolean isValidImage(String filename) {
        if (filename == null) return false;
        String ext = filename.toLowerCase();
        return ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".png") || ext.endsWith(".gif");
    }

    public Iterator createIterator() {
        return new ImageIterator();
    }

    public int getSize() {
        return images.size();
    }

    private class ImageIterator implements Iterator {
        @Override
        public boolean hasNext() {
            return currentIndex < images.size() - 1;
        }

        @Override
        public Image next() {
            if (hasNext()) {
                currentIndex++;
                return images.get(currentIndex);
            } else {
                // Цикл: переходим к первому
                currentIndex = 0;
                return images.get(currentIndex);
            }
        }

        @Override
        public Image preview() {
            if (hasPreview()) {
                currentIndex--;
                return images.get(currentIndex);
            } else {
                // Цикл: переходим к последнему
                currentIndex = images.size() - 1;
                return images.get(currentIndex);
            }
        }

        @Override
        public boolean hasPreview() {
            return currentIndex > 0 || !images.isEmpty();
        }

        @Override
        public void goToFirst() {
            currentIndex = 0;
        }

        @Override
        public void goToLast() {
            if (!images.isEmpty()) {
                currentIndex = images.size() - 1;
            }
        }

        @Override
        public int getCurrentIndex() {
            return currentIndex;
        }

        @Override
        public int getSize() {
            return images.size();
        }

        @Override
        public Image getCurrentImage() {
            return currentIndex >= 0 && currentIndex < images.size() ? images.get(currentIndex) : null;
        }
    }
}