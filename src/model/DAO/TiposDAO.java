package model.DAO;

import java.util.List;

import model.Tipos;

public interface TiposDAO {

	void insert(Tipos obj);
	void update(Tipos obj);
	void deleteById(Long id);
	Tipos findById(Long id);
	List<Tipos> ListarTipos();
}
