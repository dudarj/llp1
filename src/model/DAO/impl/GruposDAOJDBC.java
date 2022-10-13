package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexao.ConexaoJdbc;
import conexao.DbException;
import model.Grupos;
import model.DAO.GrupoDAO;

public class GruposDAOJDBC implements GrupoDAO {
	
	private Connection conn;

	public GruposDAOJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Grupos obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO grupo " + "(descricao) " + "VALUES " + "(?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getDescricao());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					Long id = rs.getLong(1);
					obj.setCodigo(id);
				}
				ConexaoJdbc.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado, nehuma linha foi afetada!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public void update(Grupos obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE grupo " + "SET descricao = ? WHERE codigo = ? ");

			st.setString(1, obj.getDescricao());
			st.setLong(2, obj.getCodigo());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Long id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM grupo WHERE codigo = ?");

			st.setLong(1, id);
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
		}
		
	}

	@Override
	public Grupos findById(Long id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn
					.prepareStatement("SELECT * FROM grupo "
							+ "WHERE codigo = ?");

			st.setLong(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Grupos obj = new Grupos();
				obj.setCodigo(rs.getLong("codigo"));
				obj.setDescricao(rs.getString("descricao"));
				return obj;
			}
			
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeResultSet(rs);
		}
	}

	@Override
	public List<Grupos> ListarGrupos() {
		List<Grupos> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * " + "FROM grupo " + "ORDER BY descricao");
			rs = st.executeQuery();

			while (rs.next()) {
				Grupos obj = new Grupos();
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