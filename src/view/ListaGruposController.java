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
import model.Grupos;
import model.servicos.GrupoServico;
import view.util.Alerts;
import view.util.Utils;


public class ListaGruposController implements Initializable, DataChangeListener {

	@FXML
	private TableView<Grupos> tbvGrupo;

	@FXML
	private TableColumn<Grupos, Integer> tbcCodigo;

	@FXML
	private TableColumn<Grupos, String> tbcDescricao;
	
	@FXML
	private TableColumn<Grupos, Grupos> tbcEDIT;

	@FXML
	private TableColumn<Grupos, Grupos> tbcREMOVE;

	@FXML
	private Button btnNovo;
	
	private GrupoServico service;

	private ObservableList<Grupos> obLista;

	@FXML
	public void onBtnNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Grupos obj = new Grupos();
		createDialogForm(obj, "/view/TelaGrupos.fxml", parentStage);
	}


	
	public void setGrupoServico(GrupoServico service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tbcCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tbcDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tbvGrupo.prefHeightProperty().bind(stage.heightProperty());
	}
	
	
	public void AlterarTabelaVisualizacao() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Grupos> list = service.ListarGrupos();
		obLista = FXCollections.observableArrayList(list);
		tbvGrupo.setItems(obLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Grupos obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			GruposController controller = loader.getController();
			controller.setEntidade(obj);
			controller.setService(new GrupoServico());
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
		tbcEDIT.setCellFactory(param -> new TableCell<Grupos, Grupos>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Grupos obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj,"/view/TelaGrupos.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tbcREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcREMOVE.setCellFactory(param -> new TableCell<Grupos, Grupos>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Grupos obj, boolean empty) {
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

	private void removeEntity(Grupos obj) {
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
