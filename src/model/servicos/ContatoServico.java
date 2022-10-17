package model.servicos;

import java.util.List;

import model.Contato;
import model.Telefone;
import model.DAO.ContatoDAO;
import model.DAO.DAOFactory;

public class ContatoServico {
	
	private ContatoDAO dao = DAOFactory.createContatoDao();
	
	public List<Contato> findAll() {
		return dao.findAll();
	}
	
	public List<Contato> findByNome(String nome) {
		return dao.findByNome(nome);
	}
	
	public List<Telefone> ListarFonesContatos(Long codigoContato){
		return dao.ListarFonesContatos(codigoContato);
		}
	
	public void salvar(Contato obj, List<Telefone> objLista) {
		if (obj.getCodigo() == null) {
			dao.insert(obj, objLista);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Contato obj) {
		dao.deleteById(obj.getCodigo());
	}
}

