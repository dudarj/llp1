package view;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import application.Main;
import conexao.DbException;
import gui.listeners.DataChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Contato;
import model.Grupos;
import model.Telefone;
import model.Tipos;
import model.exceptions.ValidationException;
import model.servicos.ContatoServico;
import model.servicos.GrupoServico;
import model.servicos.TelefoneServico;
import model.servicos.TiposServico;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;

public class ContatoController implements Initializable, DataChangeListener {

	private Contato entity;

	private ContatoServico service;

	private GrupoServico grupoService;
	
	private TiposServico tiposServico;
	
	private TelefoneServico telefoneServico = new TelefoneServico();
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField codigo;

	@FXML
	private TextField nome;

	@FXML
	private TextField endereco;
	
	@FXML
	private TextField email;
	
	@FXML
	private TextField telefone;
	
	@FXML
	private DatePicker dpDataNascimento;

	@FXML
	private ComboBox<Grupos> comboBoxGrupo;
	
	@FXML
	private ComboBox<Tipos> comboboxTipo;
	
	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Label labelErrorEndereco;
	
	@FXML
	private Button btnSalva;

	@FXML
	private Button btnCancelar;
	
	@FXML
	private Button btnAdcionarTelefone;
	
	@FXML 
	private Button btnDeletarTelefone;
	
	@FXML
	private TableView<Telefone> tbvTelefone;
	@FXML
	private TableColumn<Telefone, Telefone> tbcTelefone;
	@FXML
	private TableColumn<Telefone, Tipos> tbcTipo;
	@FXML
	private TableColumn<Telefone, Telefone> tbcEDIT;
	@FXML
	private TableColumn<Telefone, Telefone> tbcREMOVE;
	
	@FXML
	private TableColumn<Telefone, Long> tbcCodigo;
	
	private Telefone tel = new Telefone();
	

	private ObservableList<Grupos> obsList;
	private ObservableList<Tipos> obsListTipo;
	ObservableList<Telefone> lista;
	List<Telefone> list = new ArrayList<>();

	public void setContato(Contato entity) {
		this.entity = entity;
	}

	public void setServices(ContatoServico service, GrupoServico grupoService, TiposServico tiposServico) {
		this.service = service;
		this.grupoService = grupoService;
		this.tiposServico = tiposServico;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	  
	@FXML
	public void onbtnbtnAdcionarTelefoneAction(ActionEvent event) {
		Telefone fone = new Telefone();
		Tipos tipos = new Tipos();
		
		Tipos tipocomponenteCombobox = comboboxTipo.getSelectionModel().getSelectedItem();
		tipos.setCodigo(tipocomponenteCombobox.getCodigo());
		tipos.setDescricao(tipocomponenteCombobox.getDescricao());

		fone.setTelefone(telefone.getText());
		fone.setTipo(tipocomponenteCombobox);
		if(codigo.getText() != "") {
			telefoneServico.inserir(fone, entity.getCodigo());
		}	
		list.add(fone);
		lista = FXCollections.observableArrayList(list);
		tbvTelefone.setItems(lista);
		telefone.setText("");
		telefone.requestFocus();
	}
	
	@FXML
	public void onbtnbtnDeletarTelefoneAction(ActionEvent event) {
		
		Long codigo = tel.getCodigo();
		System.out.println(codigo);
		telefoneServico.remove(codigo);
		notifyDataChangeListeners();
		
		List<Telefone> listaContatoTelefone = service.ListarFonesContatos(entity.getCodigo());
		lista = FXCollections.observableArrayList(listaContatoTelefone);
		tbvTelefone.setItems(lista);	
		populaTableViewTelefone();
	}
	
	
	@FXML
	public void SelecionarTelefone(MouseEvent event) {
		Integer codigoFone = (Integer) tbvTelefone.getSelectionModel().getSelectedIndex();
		Telefone fone = (Telefone)tbvTelefone.getItems().get(codigoFone);
		System.out.println(fone);
		tel = fone;
		
		
	}
	
	public void populaTableViewTelefone() {
		
		tbcTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tbcTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
		tbcCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tbvTelefone.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	
	@FXML
	public void onbtnSalvaAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.salvar(entity, list);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Contato getFormData() {
		Contato obj = new Contato();

		ValidationException exception = new ValidationException("Validation error");

		obj.setCodigo(Utils.tryParseToLong(codigo.getText()));

		if (nome.getText() == null || nome.getText().trim().equals("")) {
			exception.addError("nome", "Field can't be empty");
		}
		obj.setNome(nome.getText());
		
		if (endereco.getText() == null || endereco.getText().trim().equals("")) {
			exception.addError("endereco", "Field can't be empty");
		}
		obj.setEndereco(endereco.getText());
		
		if (email.getText() == null || email.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(email.getText());
		
		if (dpDataNascimento.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataNascimento(Date.from(instant));
		}
		
		obj.setGrupo(comboBoxGrupo.getValue());
		obj.setTipo(comboboxTipo.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onbtnCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		populaTableViewTelefone();
	}

	private void initializeNodes() {
		Constraints.setTextFieldLong(codigo);
		Constraints.setTextFieldMaxLength(nome, 70);
		Constraints.setTextFieldMaxLength(email, 60);
		initializeComboBoxGrupo();
		initializeComboBoxTipo();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		codigo.setText(String.valueOf(entity.getCodigo()));
		nome.setText(entity.getNome());
		endereco.setText(entity.getEndereco());
		email.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		if (entity.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(entity.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getGrupo() == null) {
			comboBoxGrupo.getSelectionModel().selectFirst();
		} else {
			comboBoxGrupo.setValue(entity.getGrupo());
		}
		if(entity.getTipo()== null) {
			comboboxTipo.getSelectionModel().selectFirst();
		}else {
			comboboxTipo.setValue(entity.getTipo());
		}
		if(entity.getCodigo() != null) {
			Long cod  = entity.getCodigo();
			list = service.ListarFonesContatos(cod);
			
			lista = FXCollections.observableArrayList(list);
			tbvTelefone.setItems(lista);
		}
		
	}

	public void loadAssociatedObjectsGrupo() {
		if (grupoService == null) {
			throw new IllegalStateException("grupo servico nulo");
		}
		List<Grupos> list = grupoService.ListarGrupos();
		obsList = FXCollections.observableArrayList(list);
		comboBoxGrupo.setItems(obsList);
	}
	
	public void loadAssociatedObjectsTipo() {
		if (tiposServico == null) {
			throw new IllegalStateException("tipo servico nulo");
		}
		List<Tipos> list = tiposServico.listarTipos();
		obsListTipo = FXCollections.observableArrayList(list);
		comboboxTipo.setItems(obsListTipo);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("nome") ? errors.get("nome") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorEndereco.setText((fields.contains("endereco") ? errors.get("endereco") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
	}

	private void initializeComboBoxGrupo() {
		Callback<ListView<Grupos>, ListCell<Grupos>> factory = lv -> new ListCell<Grupos>() {
			@Override
			protected void updateItem(Grupos item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDescricao());
			}
		};
		comboBoxGrupo.setCellFactory(factory);
		comboBoxGrupo.setButtonCell(factory.call(null));
	}
	
	private void initializeComboBoxTipo() {
		Callback<ListView<Tipos>, ListCell<Tipos>> factory = lv -> new ListCell<Tipos>() {
			@Override
			protected void updateItem(Tipos item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDescricao());
			}
		};
		comboboxTipo.setCellFactory(factory);
		comboboxTipo.setButtonCell(factory.call(null));
	}

	@Override
	public void onDataChanged() {
		getFormData();	
	}
	
}