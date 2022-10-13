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
import model.Telefone;
import model.servicos.TelefoneServico;
import view.util.Alerts;
import view.util.Utils;


public class ListaTelefoneController implements Initializable, DataChangeListener {

	@FXML
	private TableView<Telefone> tbvTelefone;
	@FXML
	private TableColumn<Telefone, Long> tbcCodigo;
	@FXML
	private TableColumn<Telefone, String> tbcTelefone;
	
	@FXML
	private TableColumn<Telefone, ?> tbcTipoDescricao;

	
	@FXML
	private TableColumn<Telefone, Telefone> tbcEDIT;
	@FXML
	private TableColumn<Telefone, Telefone> tbcREMOVE;
	@FXML
	private Button btnNovo;
	
	private TelefoneServico service;
	
	public void setTelefoneServico(TelefoneServico service) {
		this.service = service;
	}
	
	private ObservableList<Telefone> obLista;
	@FXML
	public void onBtnNovoAction(ActionEvent event) {
		Telefone obj = new Telefone();
		Stage parentStage = view.util.Utils.currentStage(event);
		createDialogForm(obj, "/view/TelaTelefone.fxml", parentStage);
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tbcCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tbcTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tbvTelefone.prefHeightProperty().bind(stage.heightProperty());
	}
	
	
	public void AlterarTabelaVisualizacao() {
		if(service == null) {
			throw new IllegalStateException("O serviço não pode ser nulo");
		}
		
		List<Telefone> lista = service.listarTelefones();
		obLista = FXCollections.observableArrayList(lista);
		tbvTelefone.setItems(obLista);
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Telefone obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Inserir dados do Telefone");
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
		tbcEDIT.setCellFactory(param -> new TableCell<Telefone, Telefone>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Telefone obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj,"/view/TelaTelefone.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tbcREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcREMOVE.setCellFactory(param -> new TableCell<Telefone, Telefone>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Telefone obj, boolean empty) {
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

	private void removeEntity(Telefone obj) {
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