package com.svalero.psaa2.controller;

import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.task.GetClansApiTask;
import com.svalero.psaa2.utils.ErrorLogger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClansController implements Initializable {

    @FXML
    private TabPane tabPane;

    private final ObservableList<Clan> observableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onClickSearchClans(){
        // Create new tab with table
        createClanTableView(observableData);
        //Init task
        GetClansApiTask task = new GetClansApiTask(observableData);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlStatesTask(task);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }

    private void controlStatesTask(GetClansApiTask task){
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("No se han podido listar los clanes");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Notificacion");
            alert.setContentText("Error listando los clanes");
            alert.show();
        });
    }

    private void createClanTableView(ObservableList<Clan> observableClans){
        TableView<Clan> tableView = new TableView<>();

        TableColumn<Clan, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

        TableColumn<Clan, String> tagColumn = new TableColumn<>("Tag");
        tagColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTag()));

        TableColumn<Clan, Integer> membersColumn = new TableColumn<>("Nº miembros");
        membersColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMembers()));

        TableColumn<Clan, String> locationColumn = new TableColumn<>("Localización");
        locationColumn.setCellValueFactory(cellData -> {
            String locationName = cellData.getValue().getLocation() != null
                    ? cellData.getValue().getLocation().getName()
                    : "International";
            return new ReadOnlyStringWrapper(locationName);
        });

        TableColumn<Clan, Integer> clanLevelColumn = new TableColumn<>("Nivel");
        clanLevelColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getClanLevel()));

        TableColumn<Clan, Integer> clanPointsColumn = new TableColumn<>("Puntos");
        clanPointsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getClanPoints()));

        TableColumn<Clan, String> warFrequencyColumn = new TableColumn<>("Frecuencia guerra");
        warFrequencyColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getWarFrequency()));

        TableColumn<Clan, Integer> warWinStreakColumn = new TableColumn<>("Victorias");
        warWinStreakColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWarWinStreak()));

        TableColumn<Clan, Integer> warWinsColumn = new TableColumn<>("Victorias");
        warWinsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWarWins()));

        TableColumn<Clan, Integer> warTiesColumn = new TableColumn<>("Empates");
        warTiesColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWarTies()));

        TableColumn<Clan, Integer> warLossesColumn = new TableColumn<>("Derrotas");
        warLossesColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getWarLosses()));

        TableColumn<Clan, String> warLeagueColumn = new TableColumn<>("Liga");
        warLeagueColumn.setCellValueFactory(cellData -> {
            String warLeague = cellData.getValue().getWarLeague() != null
                    ? cellData.getValue().getWarLeague().getName()
                    : "N/D";
            return new ReadOnlyStringWrapper(warLeague);
        });

        TableColumn<Clan, String> chatLanguageColumn = new TableColumn<>("Idioma");
        chatLanguageColumn.setCellValueFactory(cellData -> {
            String chatLanguage = cellData.getValue().getChatLanguage() != null
                    ? cellData.getValue().getChatLanguage().getName()
                    : "English";
            return new ReadOnlyStringWrapper(chatLanguage);
        });

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(tagColumn);
        tableView.getColumns().add(membersColumn);
        tableView.getColumns().add(locationColumn);
        tableView.getColumns().add(clanLevelColumn);
        tableView.getColumns().add(clanPointsColumn);
        tableView.getColumns().add(warFrequencyColumn);
        tableView.getColumns().add(warLeagueColumn);
        tableView.getColumns().add(warWinsColumn);
        tableView.getColumns().add(warTiesColumn);
        tableView.getColumns().add(warLossesColumn);
        tableView.getColumns().add(chatLanguageColumn);

        tableView.setItems(observableClans);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setPadding(new Insets(0, 0, 10, 10));

        Tab tab = new Tab("Clanes");

        Platform.runLater(() -> {
            try{
                //scrollPane.setContent(tableView);
                tab.setContent(scrollPane);
                tabPane.getTabs().add(tab);
            }catch(Exception e){
                System.out.println(e.getMessage());
                ErrorLogger.log(e.getMessage());
            }
        });
    }
}