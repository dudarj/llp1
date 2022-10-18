package view;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Contato;
import model.servicos.ContatoServico;
import model.servicos.GrupoServico;
import model.servicos.TiposServico;
import view.util.Alerts;
import view.util.Utils;

public class ListaContatosController implements Initializable, DataChangeListener {

	private ContatoServico service;

	@FXML
	private TableView<Contato> tbvContato;
	@FXML
	private TableColumn<Contato, Long> tbcCodigo;
	@FXML
	private TableColumn<Contato, String> tbcNome;
	@FXML
	private TableColumn<Contato, String> tbcEndereco;
	@FXML
	private TableColumn<Contato, String> tbcEmail;
	@FXML
	private TableColumn<Contato, Date> tbcDataNascimento;
	@FXML
	private TableColumn<Contato, Contato> tbcEDIT;
	@FXML
	private TableColumn<Contato, Contato> tbcREMOVE;
	@FXML
	private Button btnNovo;
	@FXML
	private TextField tbcPesquisar;
	@FXML
	private Button btnPesquisar;
	
	
	
	private ObservableList<Contato> obsList;
	
	
	@FXML
	public void onbtnPesquisarAction(ActionEvent event) {
		List<Contato> objList;
		
		if(tbcPesquisar.getText() == " " ) {
			objList = service.findAll();
		}
		else {
			objList = service.findByNome(tbcPesquisar.getText());
			
		}
		
		ObservableList<Contato> list;
		list = FXCollections.observableArrayList(objList);
		tbvContato.setItems(list);
	}
	
	
	@FXML
	public void onbtnNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Contato obj = new Contato();
		createDialogForm(obj, "/view/TelaContatos.fxml", parentStage);
	}

	public void setContatoServico(ContatoServico service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tbcCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tbcNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tbcEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
		tbcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tbcDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tbcDataNascimento, "dd/MM/yyyy");

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tbvContato.prefHeightProperty().bind(stage.heightProperty());
	}

	public void AlterarTabelaVisualizacao() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Contato> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tbvContato.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	

	private void createDialogForm(Contato obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ContatoController controller = loader.getController();
			controller.setContato(obj);
			controller.setServices(new ContatoServico(), new GrupoServico(), new TiposServico());
			controller.loadAssociatedObjectsGrupo();
			controller.loadAssociatedObjectsTipo();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Contato data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		AlterarTabelaVisualizacao();
	}

	private void initEditButtons() {
		tbcEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcEDIT.setCellFactory(param -> new TableCell<Contato, Contato>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Contato obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/view/TelaContatos.fxml", Utils.currentStage(event)));
			}
		});
	}
	

	private void initRemoveButtons() {
		tbcREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbcREMOVE.setCellFactory(param -> new TableCell<Contato, Contato>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Contato obj, boolean empty) {
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

	private void removeEntity(Contato obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confimação", "Deseja realmente deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço Nulo");
			}
			try {
				service.remove(obj);
				AlterarTabelaVisualizacao();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}