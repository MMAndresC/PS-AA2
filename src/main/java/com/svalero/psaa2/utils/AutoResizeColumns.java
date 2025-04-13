package com.svalero.psaa2.utils;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class AutoResizeColumns {

    //Check header and all texts of a column and adjust to max length
    public static void autoResizeColumns(TableView<?> tableView) {
        tableView.getColumns().forEach(column -> {
            // Not adjust badgeColumn, not contains text
            if(column.getText().equals("Placa") || column.getText().equals("Liga")) return;
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
            column.setPrefWidth(max + 20);
        });
    }
}
