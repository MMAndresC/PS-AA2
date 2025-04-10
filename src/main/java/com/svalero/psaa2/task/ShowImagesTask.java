package com.svalero.psaa2.task;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class ShowImagesTask extends Task<Image> {

    private final String link;

    public ShowImagesTask(String link) {
       this.link = link;
    }

    @Override
    protected Image call() throws Exception {
        URI uri = URI.create(link);
        URL url = uri.toURL();
        try(InputStream inputStream = url.openStream()){
            return new Image(inputStream);
        }
    }
}
