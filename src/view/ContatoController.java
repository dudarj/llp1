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
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.Contato;
import model.Grupos;
import model.exceptions.ValidationException;
import model.servicos.ContatoServico;
import model.servicos.GrupoServico;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;

public class ContatoController implements Initializable {

	private Contato entity;

	private ContatoServico service;

	private GrupoServico grupoService;

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
	private DatePicker dpDataNascimento;

	@FXML
	private ComboBox<Grupos> comboBoxGrupo;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btnSalva;

	@FXML
	private Button btnCancelar;

	private ObservableList<Grupos> obsList;

	public void setContato(Contato entity) {
		this.entity = entity;
	}

	public void setServices(ContatoServico service, GrupoServico grupoService) {
		this.service = service;
		this.grupoService = grupoService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
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
			service.salvar(entity);
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
			exception.addError("name", "Field can't be empty");
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
	}

	private void initializeNodes() {
		Constraints.setTextFieldLong(codigo);
		Constraints.setTextFieldMaxLength(nome, 70);
		Constraints.setTextFieldMaxLength(email, 60);	
		initializeComboBoxGrupo();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		codigo.setText(String.valueOf(entity.getCodigo()));
		nome.setText(entity.getNome());
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
	}

	public void loadAssociatedObjects() {
		if (grupoService == null) {
			throw new IllegalStateException("GrupoService was null");
		}
		List<Grupos> list = grupoService.ListarGrupos();
		obsList = FXCollections.observableArrayList(list);
		comboBoxGrupo.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
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
}