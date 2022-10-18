 package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.Statement;

import conexao.ConexaoJdbc;
import conexao.DbException;
import model.Tipos;
import model.DAO.TiposDAO;

public class TiposDAOJDBC implements TiposDAO {
	
	private Connection conn;
	
	public TiposDAOJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Tipos obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT into tipo(descricao) "
					+ "values(?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getDescricao());
			
			int linhasAfetadas = st.executeUpdate();
			if (linhasAfetadas > 0) {
				ResultSet r = st.getGeneratedKeys();
				if (r.next()) {
					Long codigo = r.getLong(1);
					obj.setCodigo(codigo);
				}
				
				ConexaoJdbc.closeResultSet(r);
			}
			else {
				throw new DbException("Erro inesperado, nenhuma linha foi afetada!");
			}
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
			
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public void update(Tipos obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE tipo set "
					+ "descricao=? "
					+ "where codigo=?");
			
			st.setString(1, obj.getDescricao());
			st.setLong(2, obj.getCodigo());
			st.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
			
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Long id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE from tipo "
					+ "where codigo = ?");
			
			st.setLong(1, id);
			st.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public Tipos findById(Long id) {
		PreparedStatement st = null;
		ResultSet r = null;
		
		try {
			st = conn.prepareStatement("SELECT * from tipo where codigo=?");
			st.setLong(1, id);
			
			r = st.executeQuery();
			
			if(r.next()) {
				Tipos ti = new Tipos();
				ti.setCodigo(r.getLong("codigo"));
				ti.setDescricao(r.getString("descricao"));
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeResultSet(r);
		}
		return null;
	}

	@Override
	public List<Tipos> ListarTipos() {
		List<Tipos> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM tipo ORDER BY descricao");
			rs = st.executeQuery();

			while (rs.next()) {
				Tipos obj = new Tipos();
				obj.setCodigo(rs.getLong("codigo"));
				obj.setDescricao(rs.getString("descricao"));
				lista.add(obj);
			}
			return lista;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeResultSet(rs);
		}
	}
}



	