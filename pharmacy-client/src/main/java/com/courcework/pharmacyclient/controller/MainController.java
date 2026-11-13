package com.courcework.pharmacyclient.controller;

import com.courcework.pharmacyclient.api.*;
import com.courcework.pharmacyclient.api.error.*;
import com.courcework.pharmacyclient.dto.MedicineDTO;
import com.courcework.pharmacyclient.dto.PriceDTO;

import javafx.application.HostServices;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MainController {

    public TextField searchField;
    public ListView<MedicineDTO> medicineListView;
    public Label medicineNameLabel;
    public Label medicineDescriptionLabel;
    public TableView<PriceDTO> priceTableView;
    public TableColumn<PriceDTO, String> pharmacyColum;
    public TableColumn<PriceDTO, BigDecimal> priceColum;
    public Label minPriceLabel;
    public ListView<MedicineDTO> analogsListView;
    public Button openWebsiteButton;

    private boolean syncingSelection = false;
    private HostServices hostServices;

    private final PharmacyApiClient apiClient = new PharmacyApiClient();
    private static final PseudoClass MIN_PRICE = PseudoClass.getPseudoClass("min-price");


    private final Label medicinePlaceholder = new Label("Список препаратов пуст");
    private final Label pricePlaceholder = new Label("Выберете препарат слева");
    private final Label analogsPlaceholder = new Label("Выберете препарат слева");

    public void initialize() {
        setupPlaceholder();
        medicineNameLabel.setText("Выберете препарат");
        medicineDescriptionLabel.setText("");
        minPriceLabel.setText("-");

        setupPriceTable();
        setupLists();

        try {
            medicineListView.setItems(FXCollections.observableArrayList(apiClient.getAllMeds()));
        }catch (ApiException e){
            medicinePlaceholder.setText("Не удалось загрузить список препаратов");
            showError(e.getMessage());
        }

    }

    private void setupPlaceholder(){
        for(Label label : List.of(medicinePlaceholder, pricePlaceholder, analogsPlaceholder)){
            label.getStyleClass().add("placeholder-label");
            label.setWrapText(true);
        }
        medicineListView.setPlaceholder(medicinePlaceholder);
        analogsListView.setPlaceholder(analogsPlaceholder);
        priceTableView.setPlaceholder(pricePlaceholder);
    }

    private void setupPriceTable() {
        pharmacyColum.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().pharmacyName()));
        priceColum.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().price()));

        priceTableView.setEditable(false);
        priceTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        priceTableView.setRowFactory(tableView -> new TableRow<>(){
            @Override
            protected void updateItem(PriceDTO item, boolean empty) {
                super.updateItem(item, empty);
                boolean isMin = false;
                if(!empty && item != null){
                    BigDecimal min = findMinPrice(priceTableView.getItems()).orElse(null);
                    isMin = min != null && item.price().compareTo(min) == 0;
                }
                pseudoClassStateChanged(MIN_PRICE, isMin);
            }
        });

        openWebsiteButton.disableProperty().bind(priceTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private Optional<BigDecimal> findMinPrice(List<PriceDTO> items) {
        return items.stream().map(PriceDTO::price).min(Comparator.naturalOrder());
    }

    private void setupLists() {
        medicineListView.setCellFactory(lv -> medicineCell());
        analogsListView.setCellFactory(lv -> medicineCell());

        medicineListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, selected) ->{
            if(syncingSelection || selected == null) return;
            syncingSelection = true;
            try {
                showMedicineDetails(selected);
                syncSelection(analogsListView, selected.id());
            }finally {
                syncingSelection = false;
            }
        }));
        analogsListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, selected) ->{
            if(syncingSelection || selected == null) return;
            syncingSelection = true;
            try {
                showMedicineDetails(selected);
                syncSelection(medicineListView, selected.id());
            }finally {
                syncingSelection = false;
            }
        } ));
    }

    private void syncSelection(ListView<MedicineDTO> targetList, Long id) {
        MedicineDTO match = targetList.getItems().stream()
                .filter(m -> m.id().equals(id)).findFirst().orElse(null);

        if (match != null){
            targetList.getSelectionModel().select(match);
        }else {
            targetList.getSelectionModel().clearSelection();
        }
    }

    private void showMedicineDetails(MedicineDTO meds) {
        medicineNameLabel.setText(meds.name() + " " + meds.dosage());
        medicineDescriptionLabel.setText(meds.description());

        try {
            pricePlaceholder.setText("Для данного препарата нет цен");
            analogsPlaceholder.setText("Аналогов нет");

            ObservableList<PriceDTO> price = FXCollections.observableArrayList(apiClient.getPriceByMeds(meds.id()));
            priceTableView.setItems(price);
            findMinPrice(price).ifPresentOrElse(
                    min -> minPriceLabel.setText(min + " \u20BD"),
                    () -> minPriceLabel.setText("нет данных"));
            analogsListView.setItems(FXCollections.observableArrayList(apiClient.getAnalogs(meds.id())));
        }catch (ApiException e){
            pricePlaceholder.setText("Не удалось загрузить цены");
            analogsPlaceholder.setText("Не удалось загрузить аналоги");

            priceTableView.setItems(FXCollections.observableArrayList());
            analogsListView.setItems(FXCollections.observableArrayList());
            minPriceLabel.setText("нет данных");
            showError(e.getMessage());
        }
    }

    private ListCell<MedicineDTO> medicineCell() {
        return new ListCell<>(){
            @Override
            protected void updateItem(MedicineDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name() + " " + item.dosage());
            }
        };
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.show();
    }


    @FXML
    private void onOpenSite(){
        PriceDTO selected = priceTableView.getSelectionModel().getSelectedItem();
        if(selected == null || selected.websiteUrl() == null){
            return;
        }
        hostServices.showDocument(selected.websiteUrl());
    }

    @FXML
    private void onSearch(){
        String query = searchField.getText();
        try {
            List<MedicineDTO> result = query.isBlank()
                    ? apiClient.getAllMeds()
                    : apiClient.searchMeds(query);
            medicinePlaceholder.setText(query.isEmpty()
                    ? "Список препаратов пуст"
                    : "Ничего не найдено по запросу '" + query + "'");
            medicineListView.setItems(FXCollections.observableArrayList(result));
        }catch (ApiException e){
            medicinePlaceholder.setText("Не удалось выполнить поиск");
            showError(e.getMessage());
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
