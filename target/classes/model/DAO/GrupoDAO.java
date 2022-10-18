package model.DAO;

import java.util.List;

import model.Grupos;

public interface GrupoDAO {
	
	void insert(Grupos obj);
	void update(Grupos obj);
	void deleteById(Long id);
	Grupos findById(Long id);
	List<Grupos> ListarGrupos();
	
}
