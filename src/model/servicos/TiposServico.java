package model.servicos;

import java.util.List;

import model.Tipos;
import model.DAO.DAOFactory;
import model.DAO.TiposDAO;

public class TiposServico {
	
	private TiposDAO dao = DAOFactory.createTiposDao();
	
	public List<Tipos> listarTipos(){
		return dao.ListarTipos();
	}
	
	public void salvar(Tipos obj) {
		if (obj.getCodigo() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Tipos obj) {
		dao.deleteById(obj.getCodigo());
	}


}

