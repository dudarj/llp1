package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import conexao.DbException;
import gui.listeners.DataChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Telefone;
import model.exceptions.ValidationException;
import model.servicos.TelefoneServico;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;

public class TelefoneController implements Initializable {

	@FXML
	private TextField codigo;
	@FXML
	private TextField telefone;
	@FXML
	private ComboBox<?> contato;
	@FXML
	private Button btnAdicionar;
	@FXML
	private Button btnCancelar;
	@FXML
	private Label labelErrorName;
	
	private Telefone entidade;
	private TelefoneServico service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	
	public void setEntidade(Telefone entidade) {
		this.entidade = entidade;
	}

	public void setService(TelefoneServico service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtnAdicionaAction(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("A Entidade não pode estar nula");
		}
		if (service == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			entidade = getFormDados();
			service.salvar(entidade);
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
	
	private Telefone getFormDados() {
		Telefone obj = new Telefone();

		ValidationException exception = new ValidationException("Validação de erro");

		obj.setCodigo(Utils.tryParseToLong(codigo.getText()));

		if (telefone.getText() == null || telefone.getText().trim().equals("")) {
			exception.addError("nome", "O nome não pode estar vazio");
		}
		obj.setTelefone(telefone.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}
	
	
	@FXML
	public void onBtnCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(telefone, 20);
	}
	
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));

	}
	
	public void AlterarDadosForm() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade não pode ser nula");
		}
		codigo.setText(String.valueOf(entidade.getCodigo()));
		telefone.setText(entidade.getTelefone());
	}
	

}
