
package model.DAO;

import conexao.ConexaoJdbc;
import model.DAO.impl.ContatoDAOJDBC;
import model.DAO.impl.GruposDAOJDBC;
import model.DAO.impl.TelefoneDAOJDBC;
import model.DAO.impl.TiposDAOJDBC;


public class DAOFactory {
	
	public static ContatoDAOJDBC createContatoDao()  {
		return new ContatoDAOJDBC(ConexaoJdbc.getConnection());
	}
	
	public static TiposDAOJDBC createTiposDao()  {
		return new TiposDAOJDBC(ConexaoJdbc.getConnection());
	}
	
	public static TelefoneDAOJDBC createTelefoneDao()  {
		return new TelefoneDAOJDBC(ConexaoJdbc.getConnection());
	}
	
	public static GruposDAOJDBC createGrupoDao()  {
		return new GruposDAOJDBC(ConexaoJdbc.getConnection());
	}
	

}