package model.DAO;

import java.util.List;

import model.Contato;
import model.Grupos;
import model.Telefone;

public interface ContatoDAO {

	void insert(Contato obj, List<Telefone> objLista);
	void update(Contato obj);
	void deleteById(Long id);
	Contato findById(Long id);
	List<Contato> findByNome(String nome);
	List<Contato> findAll();
	List<Contato> findByGrupo(Grupos grupo);
	List<Telefone> ListarFonesContatos(Long codigoContato);
}
