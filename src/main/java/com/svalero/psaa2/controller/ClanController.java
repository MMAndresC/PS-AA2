package com.svalero.psaa2.controller;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.constants.WarFrequencyStructure;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.domain.ClashRoyaleClan;
import com.svalero.psaa2.domain.Label;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.task.*;
import com.svalero.psaa2.utils.AutoResizeColumns;
import com.svalero.psaa2.utils.CreateCsv;
import com.svalero.psaa2.utils.ErrorLogger;
import com.svalero.psaa2.utils.ZipFile;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ClanController implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private javafx.scene.control.Label lblCountrySelected;

    @FXML
    private Button btnGetRanking;

    private final List<String> attrAfterClans = new ArrayList<>();

    private final List<String> attrAfterLocations = new ArrayList<>();

    private int selectedLocationId;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    // ON ACTION BUTTONS

    public void onClickGetClans(){
        ObservableList<Clan> observableData = FXCollections.observableArrayList();
        // Create new table
        TableView<Clan> tableView = createClanTableView();
        tableView.setItems(observableData);
        // Create new tab
        Tab tab = createNewTab(tableView, Constants.LABEL_CLANS, observableData);
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
        TableView<Location> tableView = createLocationTableView();
        tableView.setItems(observableData);
        addSelectEvent(tableView);
        // Create new tab
        Tab tab = createNewTab(tableView, Constants.LABEL_LOCATIONS, observableData);
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

    public void onClickGetRanking(){
        ObservableList<ClashRoyaleClan> observableData = FXCollections.observableArrayList();
        // Create new table
        TableView<ClashRoyaleClan> tableView = createRankingTableView();
        tableView.setItems(observableData);
        // Create new tab
        Tab tab = createNewTab(tableView, Constants.LABEL_RANKING, observableData);
        //Init task
        GetClashRoyalRankingTask task = new GetClashRoyalRankingTask(observableData, this.selectedLocationId);
        //Init thread
        Thread thread = new Thread(task);
        // Control states of task
        controlStatesRankingTask(task, tableView, tab);
        //Close thread if app closes
        thread.setDaemon(true);
        thread.start();
    }

    // CONTROL STATES TASK

    private void controlStatesClanTask(
            GetClansApiTask task,
            ObservableList<Clan> observableData,
            TableView<Clan> tableView,
            Tab tab
    ){
        task.setOnSucceeded(event -> {
            tab.setText(Constants.LABEL_CLANS + " ✅");
            // Set active search and filter
            HBox searchFilterBar = (HBox) ((VBox) tab.getContent()).getChildren().getFirst();
            for( Node component : searchFilterBar.getChildren()){
                component.setDisable(false);
            }
            TextField input = (TextField) searchFilterBar.getChildren().getFirst();
            ComboBox<WarFrequencyStructure> cmb = (ComboBox) searchFilterBar.getChildren().get(1);

            FilteredList<Clan> filteredData = addChangeEventListener(observableData, input, Constants.LABEL_CLANS, cmb);

            tableView.setItems(filteredData);

            AutoResizeColumns.autoResizeColumns(tableView);
        });
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing clans");
            tab.setText(Constants.LABEL_CLANS + " ⛔");
        });
    }

    private void controlStatesLocationTask(
            GetLocationsApiTask task,
            ObservableList<Location> observableData,
            TableView<Location> tableView,
            Tab tab
    ){
        task.setOnSucceeded(event -> {
            tab.setText(Constants.LABEL_LOCATIONS + " ✅");
            // Set active search and filter
            HBox searchFilterBar = (HBox) ((VBox) tab.getContent()).getChildren().getFirst();
            for( Node component : searchFilterBar.getChildren()){
                component.setDisable(false);
            }

            TextField input = (TextField) searchFilterBar.getChildren().getFirst();
            ComboBox<String> cmb = (ComboBox) searchFilterBar.getChildren().get(1);

            // Link tableView to filtered data
            FilteredList<Location> filteredData = addChangeEventListener(observableData, input, Constants.LABEL_LOCATIONS, cmb);

            tableView.setItems(filteredData);

            AutoResizeColumns.autoResizeColumns(tableView);
        });
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing locations");
            tab.setText(Constants.LABEL_LOCATIONS + " ⛔");
        });
    }

    private void controlStatesRankingTask(
            GetClashRoyalRankingTask task,
            TableView<ClashRoyaleClan> tableView,
            Tab tab
    ){
        task.setOnSucceeded(event -> {
            tab.setText(Constants.LABEL_RANKING + " ✅");
            AutoResizeColumns.autoResizeColumns(tableView);
        });
        //Task ends before complete sending
        task.setOnFailed(event -> {
            ErrorLogger.log("Failed listing locations");
            tab.setText(Constants.LABEL_RANKING + " ⛔");
        });
    }

    // EVENT LISTENERS

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
                if(label.equals(Constants.LABEL_CLANS)){
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
                }else if(label.equals(Constants.LABEL_LOCATIONS)){
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

    public void addSelectEvent(TableView<Location> tableView) {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblCountrySelected.setText(newSelection.getName());
                selectedLocationId = newSelection.getId() + Constants.DIFFERENCE;
                btnGetRanking.setDisable(false);
            }
        });
    }

    public <T> void addOnActionBtnExport(Button btnExport, ObservableList<T> data, String label) {
        btnExport.setOnAction(event -> {
            // Choose directory to save csv file
            Stage stage = (Stage) btnExport.getScene().getWindow();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            String basePath = System.getProperty("user.home");
            File initialDirectory = new File(basePath);
            if (initialDirectory.exists()) {
                directoryChooser.setInitialDirectory(initialDirectory);
            }
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                // create csv file & zip if option is selected
              CompletableFuture
                      .supplyAsync(() -> {

                          // Get if checkbox is checked, option to zip csv
                          HBox container = (HBox) btnExport.getParent();
                          CheckBox cbZip = (CheckBox) container.getChildren().getLast();
                          boolean selectedZip = cbZip.isSelected();

                          // Create file path
                          long millis = System.currentTimeMillis();
                          String separator = File.separator;
                          String filename = separator + label + "_" + millis + ".csv";
                          String filePath = selectedDirectory.getAbsolutePath() + filename;

                          try {
                              // Create csv
                              File csvFile;
                              if (data.get(0) instanceof Clan)
                                  csvFile = CreateCsv.exportClan((ObservableList<Clan>) data, filePath);
                              else
                                  csvFile = CreateCsv.exportLocation((ObservableList<Location>) data, filePath);

                              if (!selectedZip) return csvFile;

                              //Create zip csv
                              File zipFile = ZipFile.zip(csvFile);
                              csvFile.delete();

                              return zipFile;
                          } catch (IOException e) {
                              ErrorLogger.log(e.getMessage());
                              return null;
                          }
                      })
                      .thenAccept(file -> {
                          Platform.runLater(() -> {
                              try {
                                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                  alert.setHeaderText("Notificacion");
                                  alert.setContentText("Datos exportados en el archivo " + file.getName());
                                  alert.show();
                              } catch (Exception e) {
                                  ErrorLogger.log(e.getMessage());
                              }
                          });
                      })
                      .exceptionally(ex -> {
                          ErrorLogger.log(ex.getMessage());
                          return null;
                      });
            }
        });
    }

    //UI

    //Generic type T to wrap both types of tableView
    public <T> Tab createNewTab(TableView<T> tableView, String label, ObservableList<T> data) {
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(0, 0, 10, 10));

        Tab tab = new Tab(label + " ⌛");

        VBox container;

        if(label.equals(Constants.LABEL_RANKING)){
            javafx.scene.control.Label lbCountry = new javafx.scene.control.Label(this.lblCountrySelected.getText());
            lbCountry.setStyle("-fx-font-size: 18px;");
            HBox hbox = new HBox(lbCountry);
            hbox.setPadding(new Insets(20, 10, 10, 10));
            hbox.setAlignment(Pos.CENTER);
            container = new VBox(10,hbox, scrollPane);
        }else{
            HBox searchFilterBar = createSearchAndFilter(label, data);
            container = new VBox(10,searchFilterBar, scrollPane);
        }

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

    private TableView<Location> createLocationTableView(){
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

    private TableView<Clan> createClanTableView(){
        TableView<Clan> tableView = new TableView<>();

        // Column with badge image
        TableColumn<Clan, ImageView> badgeColumn = getClanBadgeImageViewColumn();

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

        TableColumn<Clan, HBox> labelsColumn = getClanLabelsTableColumn();

        //Set columns in table
        tableView.getColumns().add(badgeColumn);
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
        tableView.getColumns().add(labelsColumn);

        return tableView;
    }

    private TableView<ClashRoyaleClan> createRankingTableView(){
        TableView<ClashRoyaleClan> tableView = new TableView<>();

        TableColumn<ClashRoyaleClan, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

        TableColumn<ClashRoyaleClan, String> tagColumn = new TableColumn<>("Tag");
        tagColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTag()));

        TableColumn<ClashRoyaleClan, Integer> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRank()));

        TableColumn<ClashRoyaleClan, Integer> previousRankColumn = new TableColumn<>("Anterior Rank");
        previousRankColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPreviousRank()));

        TableColumn<ClashRoyaleClan, Integer> clanScoreColumn = new TableColumn<>("Puntuación");
        clanScoreColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getClanScore()));

        TableColumn<ClashRoyaleClan, Integer> membersColumn = new TableColumn<>("Nº miembros");
        membersColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMembers()));

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(tagColumn);
        tableView.getColumns().add(rankColumn);
        tableView.getColumns().add(previousRankColumn);
        tableView.getColumns().add(clanScoreColumn);
        tableView.getColumns().add(membersColumn);

        return tableView;
    }

    @NotNull
    private TableColumn<Clan, ImageView> getClanBadgeImageViewColumn() {
        TableColumn<Clan, ImageView> badgeColumn = new TableColumn<>("Placa");
        badgeColumn.setCellValueFactory(cellData -> {
            String url = cellData.getValue().getBadgeUrls().getSmall();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(30);
            imageView.setPreserveRatio(true);
            //Task to download and get image to set in imageView
            ShowImagesTask task = new ShowImagesTask(url);
            task.setOnSucceeded(event -> imageView.setImage(task.getValue()));
            task.setOnFailed(event -> {
                ErrorLogger.log(task.getException().getMessage());
                // Load default image
                InputStream defaultImage = getClass().getResourceAsStream("/assets/default_badge.png");
                if(defaultImage != null){
                    Image image = new Image(defaultImage);
                    imageView.setImage(image);
                }
            });
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            return new SimpleObjectProperty<>(imageView);
        });
        badgeColumn.setPrefWidth(40);
        return badgeColumn;
    }

    @NotNull
    private TableColumn<Clan, HBox> getClanLabelsTableColumn() {
        TableColumn<Clan, HBox> labelsColumn = new TableColumn<>("Etiquetas");
        labelsColumn.setCellValueFactory(cellData -> {
            List<Label> labels = cellData.getValue().getLabels();
            HBox hBox = new HBox();
            labels.forEach(label -> {
                String url = label.getIconUrls().getSmall();
                if(url.isBlank()) return;
                ImageView imageView = new ImageView();
                imageView.setFitWidth(30);
                imageView.setPreserveRatio(true);
                ShowImagesTask task = new ShowImagesTask(url);
                task.setOnSucceeded(event -> {
                    imageView.setImage(task.getValue());
                    hBox.getChildren().add(imageView);
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            });
            return new SimpleObjectProperty<>(hBox);
        });
        return labelsColumn;
    }

    public <T> HBox createSearchAndFilter(String label, ObservableList<T> data) {
        TextField input = new TextField();
        input.setDisable(true);
        input.setMinWidth(250);
        HBox hbox = new HBox();
        Button btnExport = new Button("Exportar");
        addOnActionBtnExport(btnExport, data, label);
        btnExport.setDisable(true);
        CheckBox cbZip = new CheckBox("Comprimirlo");
        cbZip.setDisable(true);
        if(label.equals(Constants.LABEL_CLANS)){
            input.setPromptText("Buscar en nombre o tag");
            ComboBox<WarFrequencyStructure> cmb = new ComboBox<>();
            for (WarFrequencyStructure wf : Constants.WAR_FRECUENCY) {
                cmb.getItems().add(wf);
            }
            cmb.getSelectionModel().selectFirst();
            cmb.setDisable(true);
            hbox.getChildren().addAll(input, cmb, btnExport, cbZip);
            HBox.setMargin(cmb, new Insets(0, 0, 0, 10));
        }else if(label.equals(Constants.LABEL_LOCATIONS)){
            input.setPromptText("Buscar en nombre o código de pais");
            ComboBox<String> cmb = new ComboBox<>();
            cmb.getItems().addAll("Filtrar por Es país?", "Si", "No");
            cmb.getSelectionModel().selectFirst();
            cmb.setDisable(true);
            hbox.getChildren().addAll(input, cmb, btnExport, cbZip);
            HBox.setMargin(cmb, new Insets(0, 0, 0, 10));
        }
        hbox.setPadding(new Insets(20, 10, 10, 10));
        hbox.setAlignment(Pos.CENTER);
        HBox.setMargin(input, new Insets(0, 10, 0, 0));
        HBox.setMargin(btnExport, new Insets(0, 10, 0, 60));
        return hbox;
    }
}