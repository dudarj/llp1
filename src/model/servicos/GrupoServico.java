package model.servicos;

import java.util.List;

import model.Grupos;
import model.DAO.DAOFactory;
import model.DAO.GrupoDAO;

public class GrupoServico {
	
private GrupoDAO dao = DAOFactory.createGrupoDao();
	

	public List<Grupos> ListarGrupos() {
		return dao.ListarGrupos();
	}
	
	public void salvar(Grupos obj) {
		if (obj.getCodigo() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Grupos obj) {
		dao.deleteById(obj.getCodigo());
	}
}
