 package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import conexao.ConexaoJdbc;
import conexao.DbException;
import model.Contato;
import model.Telefone;
import model.Tipos;
import model.DAO.TelefoneDAO;

public class TelefoneDAOJDBC implements TelefoneDAO {
	
	private Connection conn;
	
	public TelefoneDAOJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Telefone obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT into telefone(telefone) "
					+ "values(?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getTelefone());
	
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
	public void update(Telefone obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE telefone set "
					+ "telefone = ? "
					+ "where codigo = ?");
			
			st.setString(1, obj.getTelefone());
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
			st = conn.prepareStatement("DELETE from telefone "
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
	public Telefone findById(Long id) {
		PreparedStatement st = null;
		ResultSet r = null;
		
		try {
			st = conn.prepareStatement("SELECT * from telefone where codigo = ? ");
			st.setLong(1, id);
			
			r = st.executeQuery();
			
			if(r.next()) {
				Telefone t = new Telefone();
				t.setCodigo(r.getLong("codigo"));
				t.setTelefone(r.getString("telefone"));
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Telefone> ListarTelefones() {
		List<Telefone> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM telefone t "
				+ "inner join contato c on t.codigo_usuario = c.codigo "
				+ "inner join tipo ti on ti.codigo = t.codigo_tipo " 
				+ " ORDER BY telefone");
			rs = st.executeQuery();

			while (rs.next()) {
				Telefone obj = new Telefone();
				Contato c = new Contato();
				Tipos ti = new Tipos();
				obj.setCodigo(rs.getLong("codigo"));
				obj.setTelefone(rs.getString("telefone"));
				c.setCodigo(rs.getLong("codigo_usuario"));
				c.setNome(rs.getString("nome"));
				c.setEndereco(rs.getString("endereco"));
				c.setEmail(rs.getString("email"));
				c.setDataNascimento(rs.getDate("datadenascimento"));
				ti.setCodigo(rs.getLong("codigo_tipo"));
				ti.setCodigo(rs.getLong("codigo"));
				ti.setDescricao(rs.getString("descricao"));
				
				obj.setContato(c);
				obj.setTipo(ti);
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



	