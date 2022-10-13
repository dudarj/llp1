package model.servicos;

import java.util.List;

import model.Contato;
import model.DAO.ContatoDAO;
import model.DAO.DAOFactory;

public class ContatoServico {
	
	private ContatoDAO dao = DAOFactory.createContatoDao();
	
	public List<Contato> findAll() {
		return dao.findAll();
	}
	
	public void salvar(Contato obj) {
		if (obj.getCodigo() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Contato obj) {
		dao.deleteById(obj.getCodigo());
	}
}

