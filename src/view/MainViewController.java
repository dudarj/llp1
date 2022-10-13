package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.servicos.ContatoServico;
import model.servicos.GrupoServico;
import model.servicos.TelefoneServico;
import model.servicos.TiposServico;
import view.util.Alerts;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem mnuContato;

	@FXML
	private MenuItem mnuTelefone;

	@FXML
	private MenuItem mnuTipo;
	
	@FXML
	private MenuItem mnuGrupo;

	@FXML
	public void onMenuItemContatoAction() {
		
		loadView("/view/ListaContatos.fxml", x -> {
		});
		
		  loadView("/view/ListaContatos.fxml", (ListaContatosController controller) -> {
		  controller.setContatoServico(new ContatoServico());
		  controller.AlterarTabelaVisualizacao(); });
		
	}

	@FXML
	public void onMenuItemTipoAction() {
		
		loadView("/view/ListaTipos.fxml", x -> {
		});
		
		  loadView("/view/ListaTipos.fxml", (ListaTiposController controller) -> {
		  controller.setTiposServico(new TiposServico());
		  controller.AlterarTabelaVisualizacao(); }); 
		
		
	}

	@FXML
	public void onMenuItemTelefoneAction() {
		
		loadView("/view/ListaTelefone.fxml", x -> {
		});
		
		  loadView("/view/ListaTelefone.fxml", (ListaTelefoneController controller) -> {
		  controller.setTelefoneServico(new TelefoneServico());
		  controller.AlterarTabelaVisualizacao(); }); 
		  
		/*
		 * loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) ->
		 * { controller.setDepartmentService(new DepartmentService());
		 * controller.updateTableView(); });
		 */
	}
	
	@FXML 
	public void onMenuItemGruposAction() {
		loadView("/view/ListaGrupos.fxml", x -> {
		});
		
		  loadView("/view/ListaGrupos.fxml", (ListaGruposController controller) -> {
		  controller.setGrupoServico(new GrupoServico());
		  controller.AlterarTabelaVisualizacao(); }); 
	}

	@FXML
	public void onMenuItemSobreAction() {
		loadView("/view/Sobre.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());

			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			Alerts.showAlert("Exceção de entrada e saída", "Erro ao carregar a Tela", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
}