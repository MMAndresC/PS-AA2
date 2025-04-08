package com.svalero.psaa2.controller;

import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.task.GetClansApiTask;
import com.svalero.psaa2.task.GetLocationsApiTask;
import com.svalero.psaa2.utils.ErrorLogger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ClanController implements Initializable {

    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onClickGetClans(){
        ObservableList<Clan> observableData = FXCollections.observableArrayList();
        // Create new tab with table
        createClanTableView(observableData);
        //Init task
        GetClansApiTask task = new GetClansApiTask(observableData);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlFailedClanTask(task);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }

    public void onClickGetLocations(){
        ObservableList<Location> observableData = FXCollections.observableArrayList();
        // Create new tab with table
        createLocationTableView(observableData);
        //Init task
        GetLocationsApiTask task = new GetLocationsApiTask(observableData);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlFailedLocationTask(task);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }

    private void controlFailedClanTask(GetClansApiTask task){
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing clans");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Notificacion");
            alert.setContentText("Error listando los clanes");
            alert.show();
        });
    }

    private void controlFailedLocationTask(GetLocationsApiTask task){
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing locations");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Notificacion");
            alert.setContentText("Error listando las localizaciones");
            alert.show();
        });
    }

    //Generic type T to wrap both types of tableView
    public <T> void createNewTab(TableView<T> tableView, String label) {
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setPadding(new Insets(0, 0, 10, 10));

        Tab tab = new Tab(label);

        try{
            //Add new tab
            tab.setContent(scrollPane);
            tabPane.getTabs().add(tab);
            // Focus on new tab
            tabPane.getSelectionModel().select(tab);
        }catch(Exception e){
            System.out.println(e.getMessage());
            ErrorLogger.log(e.getMessage());
        }
    }

    private void createLocationTableView(ObservableList<Location> observableLocations){
        TableView<Location> tableView = new TableView<>();

        TableColumn<Location, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));

        TableColumn<Location, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

        TableColumn<Location, Boolean> isCountryColumn = new TableColumn<>("Es pais?");
        isCountryColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().isCountry()));

        TableColumn<Location, String> countryCodeColumn = new TableColumn<>("Código del país");
        countryCodeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCountryCode()));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(isCountryColumn);
        tableView.getColumns().add(countryCodeColumn);

        tableView.setItems(observableLocations);

        createNewTab(tableView, "Localizaciones");
    }

    private void createClanTableView(ObservableList<Clan> observableClans){
        TableView<Clan> tableView = new TableView<>();

        //
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

        //Set columns in table
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

        createNewTab(tableView, "Clanes");
    }
}