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
	
	public void inserir(Telefone obj, Long contato) {
		dao.insert(obj, contato);
	}

	public void salvar(Telefone obj, Long contato) {
		if (obj.getCodigo() == null) {
			dao.insert(obj, contato);
		} else {
			dao.update(obj);
		}
	}
	
	public void remove(Long obj) {
		dao.deleteById(obj);
	}
}

