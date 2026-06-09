package ru.itmo.coursework.iterator;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryController {

    @FXML private ImageView imageView;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button firstButton;
    @FXML private Button lastButton;
    @FXML private Button startAutoButton;
    @FXML private Button stopAutoButton;
    @FXML private Label positionLabel;
    @FXML private Label infoLabel; // <-- Добавлено
    @FXML private ComboBox<String> filterComboBox; // <-- Добавлено

    private Timeline autoPlayTimeline;
    private ImageCollection collection;
    private Iterator iterator;

    private final List<String> allImagePaths = List.of(
            "/images/image1.jpg",
            "/images/image2.png",
            "/images/image3.jpg",
            "/images/image4.png",
            "/images/image5.jpg",
            "/images/image6.jpg",
            "/images/image7.jpg",
            "/images/image8.gif",
            "/images/image9.bmp", // <-- Не пройдёт фильтр
            "/images/image10.txt"  // <-- Не пройдёт фильтр
    );

    public void initialize() {
        setupFilterComboBox();
        loadImagesForFilter("All");
    }

    private void setupFilterComboBox() {
        ObservableList<String> filters = FXCollections.observableArrayList("All", "JPG", "PNG", "GIF");
        filterComboBox.setItems(filters);
        filterComboBox.setValue("All");
        filterComboBox.setOnAction(e -> onFilterChanged());
    }

    private void onFilterChanged() {
        String selectedFilter = filterComboBox.getValue();
        loadImagesForFilter(selectedFilter);
    }

    private void loadImagesForFilter(String filter) {
        collection = new ImageCollection();
        List<String> pathsToLoad = new ArrayList<>();

        for (String path : allImagePaths) {
            String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
            boolean include = false;

            switch (filter) {
                case "All":
                    include = true;
                    break;
                case "JPG":
                    include = ext.equals("jpg") || ext.equals("jpeg");
                    break;
                case "PNG":
                    include = ext.equals("png");
                    break;
                case "GIF":
                    include = ext.equals("gif");
                    break;
            }

            if (include) {
                pathsToLoad.add(path);
            }
        }

        try {
            for (String path : pathsToLoad) {
                addIfValid(path);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображения: " + e.getMessage());
        }

        iterator = collection.createIterator();
        if (iterator.hasNext()) {
            Image firstImage = iterator.next();
            if (firstImage != null) {
                imageView.setImage(firstImage);
            }
        }
        updatePositionAndButtons();
    }

    private void addIfValid(String path) throws Exception {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Файл не найден: " + path);
            return;
        }
        Image img = new Image(stream);
        if (img.isError()) {
            System.err.println("Не удалось загрузить изображение: " + path);
            return;
        }
        collection.add(img, filename);
    }

    @FXML
    protected void onNextButtonClick() {
        if (iterator.hasNext()) {
            Image nextImage = iterator.next();
            if (nextImage != null) {
                transition(nextImage);
            }
        }
        updatePositionAndButtons();
    }

    @FXML
    protected void onPrevButtonClick() {
        if (iterator.hasPreview()) {
            Image prevImage = iterator.preview();
            if (prevImage != null) {
                transition(prevImage);
            }
        }
        updatePositionAndButtons();
    }

    @FXML
    protected void onGoToFirst() {
        iterator.goToFirst();
        updatePositionAndButtons();
        Image currentImg = iterator.getCurrentImage();
        if (currentImg != null) {
            transitionInstantly(currentImg);
        }
    }

    @FXML
    protected void onGoToLast() {
        iterator.goToLast();
        updatePositionAndButtons();
        Image currentImg = iterator.getCurrentImage();
        if (currentImg != null) {
            transitionInstantly(currentImg);
        }
    }

    @FXML
    protected void onStartAutoPlay() {
        if (autoPlayTimeline != null && autoPlayTimeline.getStatus() == Animation.Status.RUNNING) return;
        autoPlayTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> onNextButtonClick()));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    @FXML
    protected void onStopAutoPlay() {
        if (autoPlayTimeline != null) autoPlayTimeline.stop();
    }

    private void transition(Image newImage) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(400), imageView);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);

        ParallelTransition out = new ParallelTransition(fadeOut, scaleOut);

        out.setOnFinished(e -> {
            imageView.setImage(newImage);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), imageView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), imageView);
            scaleIn.setFromX(0.8);
            scaleIn.setFromY(0.8);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);

            ParallelTransition in = new ParallelTransition(fadeIn, scaleIn);
            in.play();
        });

        out.play();
    }

    private void transitionInstantly(Image newImage) {
        imageView.setImage(newImage);
    }

    private void updatePositionAndButtons() {
        if (iterator == null) return;

        int size = iterator.getSize();
        int pos = iterator.getCurrentIndex() + 1; // 1-based
        positionLabel.setText(pos + " из " + size);

        prevButton.setDisable(!iterator.hasPreview());
        nextButton.setDisable(!iterator.hasNext());

        // --- Обновление информации о файле (базовая)---
        if (iterator.getCurrentIndex() >= 0) {
            String filename = "image" + (iterator.getCurrentIndex() + 1) + ".jpg"; // Простая заглушка

            // --- Объявляем переменную до if ---
            String sizeStr = "неизвестный";

            Image currentImg = iterator.getCurrentImage();
            if (currentImg != null) {
                // Проверяем, что изображение полностью загружено
                if (currentImg.getWidth() > 0 && currentImg.getHeight() > 0) {
                    int width = (int) currentImg.getWidth();
                    int height = (int) currentImg.getHeight();
                    sizeStr = width + "×" + height;
                } else {
                    sizeStr = "загрузка..."; // На случай, если размеры ещё не известны
                }
            }

            // --- Теперь используем sizeStr ---
            infoLabel.setText("Файл: " + filename + " | Размер: " + sizeStr);
        } else {
            infoLabel.setText("");
        }
    }
}