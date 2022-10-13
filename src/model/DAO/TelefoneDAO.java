package model.DAO;

import java.util.List;

import model.Telefone;

public interface TelefoneDAO {

	void insert(Telefone obj);
	void update(Telefone obj);
	void deleteById(Long id);
	Telefone findById(Long id);
	List<Telefone> ListarTelefones();
}
