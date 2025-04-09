package com.svalero.psaa2.utils;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class AutoResizeColumns {

    public static void autoResize(TableView<?> tableView) {
        tableView.getColumns().forEach(column -> {
            Text tempText = new Text(column.getText());
            double max = tempText.getLayoutBounds().getWidth();

            for (int i = 0; i < tableView.getItems().size(); i++) {
                Object cellData = column.getCellData(i);
                if (cellData != null) {
                    tempText = new Text(cellData.toString());
                    double width = tempText.getLayoutBounds().getWidth();
                    if (width > max) {
                        max = width;
                    }
                }
            }

            column.setPrefWidth(max + 20); // Margen adicional para padding
        });
    }
}
