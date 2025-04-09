package com.svalero.psaa2.controller;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.constants.WarFrequencyStructure;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.task.GetClansApiTask;
import com.svalero.psaa2.task.GetLocationsApiTask;
import com.svalero.psaa2.utils.ErrorLogger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClanController implements Initializable {

    @FXML
    private TabPane tabPane;

    private final String LABEL_CLANS = "Clanes";

    private final String LABEL_LOCATIONS = "Locations";

    private final List<String> attrAfterClans = new ArrayList<>();

    private final List<String> attrAfterLocations = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void onClickGetClans(){
        ObservableList<Clan> observableData = FXCollections.observableArrayList();
        // Create new table
        TableView<Clan> tableView = createClanTableView(observableData);
        tableView.setItems(observableData);
        // Create new tab
        Tab tab = createNewTab(tableView, LABEL_CLANS);
        //Init task
        GetClansApiTask task = new GetClansApiTask(observableData, attrAfterClans);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlStatesClanTask(task, observableData, tableView, tab);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }

    public void onClickGetLocations(){
        ObservableList<Location> observableData = FXCollections.observableArrayList();
        // Create new table
        TableView<Location> tableView = createLocationTableView(observableData);
        tableView.setItems(observableData);
        // Create new tab
        Tab tab = createNewTab(tableView, LABEL_LOCATIONS);
        //Init task
        GetLocationsApiTask task = new GetLocationsApiTask(observableData, attrAfterLocations);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlStatesLocationTask(task, observableData, tableView, tab);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }


    private void controlStatesClanTask(
            GetClansApiTask task,
            ObservableList<Clan> observableData,
            TableView<Clan> tableView,
            Tab tab
    ){
        task.setOnSucceeded(event -> {
            tab.setText(LABEL_CLANS + " ✅");
            // Set active search and filter
            HBox searchFilterBar = (HBox) ((VBox) tab.getContent()).getChildren().getFirst();
            for( Node component : searchFilterBar.getChildren()){
                component.setDisable(false);
            }
            TextField input = (TextField) searchFilterBar.getChildren().getFirst();
            ComboBox<WarFrequencyStructure> cmb = (ComboBox) searchFilterBar.getChildren().getLast();

            FilteredList<Clan> filteredData = addChangeEventListener(observableData, input, LABEL_CLANS, cmb);

            tableView.setItems(filteredData);
        });
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing clans");
            tab.setText(LABEL_CLANS + " ⛔");
        });
    }

    private void controlStatesLocationTask(
            GetLocationsApiTask task,
            ObservableList<Location> observableData,
            TableView<Location> tableView,
            Tab tab
    ){
        task.setOnSucceeded(event -> {
            tab.setText(LABEL_LOCATIONS + " ✅");
            // Set active search and filter
            HBox searchFilterBar = (HBox) ((VBox) tab.getContent()).getChildren().getFirst();
            for( Node component : searchFilterBar.getChildren()){
                component.setDisable(false);
            }
            TextField input = (TextField) searchFilterBar.getChildren().getFirst();
            ComboBox<String> cmb = (ComboBox) searchFilterBar.getChildren().getLast();

            // Link tableView to filtered data
            FilteredList<Location> filteredData = addChangeEventListener(observableData, input, LABEL_LOCATIONS, cmb);

            tableView.setItems(filteredData);
        });
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing locations");
            tab.setText(LABEL_LOCATIONS + " ⛔");
        });
    }

    /**
     * Add change event listener to textField to search in tableView
     * Abstract method to work with both types Clan/Location
     * @param observableData
     * @param input
     * @param label
     * @return
     * @param <T>
     */
    private <T> FilteredList<T> addChangeEventListener(
            ObservableList<T> observableData,
            TextField input,
            String label,
            ComboBox<?> cmb
    ){
        FilteredList<T> filteredData = new FilteredList<>(observableData, p -> true);

        // Create a method or use this, save me pass params again
        Runnable setFilter = () -> {
            String searchText = input.getText();
            Object selectedFilter = cmb.getSelectionModel().getSelectedItem();
            int indexCmb = cmb.getSelectionModel().getSelectedIndex();

            filteredData.setPredicate(item ->{
                boolean matchesSearch = true;
                boolean matchesFilter = true;

                // Clan tableView data
                if(label.equals(LABEL_CLANS)){
                    Clan clan = (Clan) item;
                    // Check if searchText is in clan name or tag
                    if(!searchText.isBlank()){
                        matchesSearch = clan.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                clan.getTag().toLowerCase().contains(searchText.toLowerCase());
                    }
                    // Filter by war frequency value selected in combobox, discard first value
                    if (selectedFilter instanceof WarFrequencyStructure wf
                            && !wf.getValue().equals(Constants.WAR_FRECUENCY.getFirst().getValue())
                    ) {
                        matchesFilter = clan.getWarFrequency().equals(wf.getValue());
                    }

                // Locations tableView data
                }else if(label.equals(LABEL_LOCATIONS)){
                    Location location = (Location) item;
                    // Check if searchText is in location name or countryCode if exist
                    if(!searchText.isBlank()){
                        matchesSearch = location.getName().toLowerCase().contains(searchText.toLowerCase())
                                || (location.getCountryCode() != null &&
                                location.getCountryCode().toLowerCase().contains(searchText.toLowerCase()));
                    }
                    // Filter by is country value selected in combobox, discard first value
                    if (selectedFilter instanceof String value && indexCmb != 0) {
                        boolean boolValue = value.equalsIgnoreCase("si");
                        matchesFilter = location.isCountry() == boolValue;
                    }
                }
                return matchesSearch && matchesFilter;
            });
        };

        //Add listeners
        input.textProperty().addListener(
                (obs, oldVal, newVal) -> setFilter.run());
        cmb.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> setFilter.run());

        return filteredData;
    }



    //Generic type T to wrap both types of tableView
    public <T> Tab createNewTab(TableView<T> tableView, String label) {
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(0, 0, 10, 10));

        Tab tab = new Tab(label + " ⌛");

        HBox searchFilterBar = createSearchAndFilter(label);
        VBox container = new VBox(10,searchFilterBar, scrollPane);
        // Set components attributes to growth to expand all height
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        tableView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        //Add new tab
        tab.setContent(container);
        tabPane.getTabs().add(tab);
        // Focus on new tab
        tabPane.getSelectionModel().select(tab);
        return tab;
    }

    private TableView<Location> createLocationTableView(ObservableList<Location> observableLocations){
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

       return tableView;
    }

    private TableView<Clan> createClanTableView(ObservableList<Clan> observableClans){
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

        return tableView;
    }

    public HBox createSearchAndFilter(String label) {
        TextField input = new TextField();
        input.setDisable(true);
        input.setMinWidth(250);
        HBox hbox = new HBox();
        if(label.equals(LABEL_CLANS)){
            input.setPromptText("Buscar en nombre o tag");
            ComboBox<WarFrequencyStructure> cmb = new ComboBox<>();
            for (WarFrequencyStructure wf : Constants.WAR_FRECUENCY) {
                cmb.getItems().add(wf);
            }
            cmb.getSelectionModel().selectFirst();
            cmb.setDisable(true);
            hbox.getChildren().addAll(input, cmb);
            HBox.setMargin(cmb, new Insets(0, 0, 0, 10));
        }else{
            input.setPromptText("Buscar en nombre o código de pais");
            ComboBox<String> cmb = new ComboBox<>();
            cmb.getItems().addAll("Filtrar por Es país?", "Si", "No");
            cmb.getSelectionModel().selectFirst();
            cmb.setDisable(true);
            hbox.getChildren().addAll(input, cmb);
            HBox.setMargin(cmb, new Insets(0, 0, 0, 10));
        }
        hbox.setPadding(new Insets(20, 10, 10, 10));
        hbox.setAlignment(Pos.CENTER);
        HBox.setMargin(input, new Insets(0, 10, 0, 0));
        return hbox;
    }
}