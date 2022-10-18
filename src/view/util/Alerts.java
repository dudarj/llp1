package view.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {
	
	public static void showAlert(String titulo, String cabecalho, String conteudo, AlertType tipo) {
		
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecalho);
		alert.setContentText(conteudo);
		alert.show();
	}
	
	public static Optional<ButtonType> showConfirmation(String title, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		return alert.showAndWait();
	}
}