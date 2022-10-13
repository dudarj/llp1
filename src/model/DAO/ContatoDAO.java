package model.DAO;

import java.util.List;

import model.Contato;
import model.Grupos;

public interface ContatoDAO {

	void insert(Contato obj);
	void update(Contato obj);
	void deleteById(Long id);
	Contato findById(Long id);
	List<Contato> findAll();
	List<Contato> findByGrupo(Grupos grupo);
}
