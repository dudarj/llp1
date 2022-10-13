package model.servicos;

import java.util.List;

import model.Telefone;
import model.DAO.DAOFactory;
import model.DAO.TelefoneDAO;

public class TelefoneServico {
	
	private TelefoneDAO dao = DAOFactory.createTelefoneDao();
	
	public List<Telefone> listarTelefones() {
		return dao.ListarTelefones();
	}
	
	public void salvar(Telefone obj) {
		if (obj.getCodigo() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Telefone obj) {
		dao.deleteById(obj.getCodigo());
	}
}

