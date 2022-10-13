package view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import conexao.DbIntegrityException;
import gui.listeners.DataChangeListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Tipos;
import model.servicos.TiposServico;
import view.util.Alerts;
import view.util.Utils;


public class ListaTiposController implements Initializable, DataChangeListener {

	@FXML
	private TableView<Tipos> tbvTipos;
	@FXML
	private TableColumn<Tipos, Long> tbcCodigo;
	@FXML
	private TableColumn<Tipos, String> tbcDescricao;
	@FXML
	private TableColumn<Tipos, Tipos> tbcEDIT;
	@FXML
	private TableColumn<Tipos, Tipos> tbcREMOVE;
	@FXML
	private Button btnNovo;
	
	private TiposServico service;
	
	
	
	
	public void setTiposServico(TiposServico service) {
		this.service = service;
	}
	
	private ObservableList<Tipos> obLista;
	
	@FXML
	public void onBtnNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Tipos obj = new Tipos();
		createDialogForm(obj, "/view/TelaTipos.fxml", parentStage);
	}


	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tbcCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tbcDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tbvTipos.prefHeightProperty().bind(stage.heightProperty());
	}
	
	
	public void AlterarTabelaVisualizacao() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Tipos> list = service.listarTipos();
		obLista = FXCollections.observableArrayList(list);
		tbvTipos.setItems(obLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Tipos obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			TiposController controller = loader.getController();
			controller.setEntidade(obj);
			controller.setService(new TiposServico());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Inserir grupos aos contatos");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Excessão de entrada e Saída", "Erro ao carregamento da tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		AlterarTabelaVisualizacao();
	}

	private void initEditButtons() {
		tbcEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcEDIT.setCellFactory(param -> new TableCell<Tipos, Tipos>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Tipos obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj,"/view/TelaTipos.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tbcREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcREMOVE.setCellFactory(param -> new TableCell<Tipos, Tipos>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Tipos obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Tipos obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Você tem certeza que gostaria de deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				AlterarTabelaVisualizacao();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}