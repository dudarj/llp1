package model.DAO.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import conexao.ConexaoJdbc;
import conexao.DbException;
import model.Contato;
import model.Grupos;
import model.DAO.ContatoDAO;

public class ContatoDAOJDBC implements ContatoDAO {

	private Connection conn;

	public  ContatoDAOJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Contato obj) {
		PreparedStatement st = null;
		PreparedStatement st2 = null;
		String sqlContato = "INSERT INTO contato "
				+ "(nome, endereco, email, datadenascimento) "
				+ "VALUES(?, ?, ?, ?)";
		try {
			st = conn.prepareStatement(sqlContato,Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEndereco());
			st.setString(3, obj.getEmail());
			st.setDate(4, new java.sql.Date(obj.getDataNascimento().getTime()));
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					Long id = rs.getLong(1);
					obj.setCodigo(id);
				}
				
				ConexaoJdbc.closeResultSet(rs);
			}
			else {
				throw new DbException("erro inesperado! Nenhuma linha afetada!");
			}
			
			String sqlgrupocontato = "INSERT INTO grupo_contato(codigo_usuario, codigo_grupo) "
					+ "VALUES(?, ?)";
			st2 = conn.prepareStatement(sqlgrupocontato);
			st2.setLong(1, obj.getCodigo());
			st2.setLong(2, obj.getGrupo().getCodigo());
			st2.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeStatement(st2);
		}
	}

	@Override
	public void update(Contato obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE contato "
					+ "SET nome = ?, endereco = ?, email = ?, datadenascimento = ? "
					+ "WHERE codigo = ?");
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEndereco());
			st.setString(3, obj.getEmail());
			st.setDate(4, new java.sql.Date(obj.getDataNascimento().getTime()));
			st.setLong(5, obj.getCodigo());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Long id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM contato WHERE codigo = ?");
			
			st.setLong(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
		}
	}

	@Override
	public Contato findById(Long id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "select contato.* , grupo.nome as gruNome "
					+ " from contato c "
					+ " inner join grupo_contato gc on gc.codigo_usuario = c.codigo "
					+ " inner join grupo g on gc.codigo_grupo = g.codigo "
					+ " where c.codigo = ?";
			st = conn.prepareStatement(sql);
			
			st.setLong(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Grupos g = instantiateGrupo(rs);
				Contato obj = instantiateContato(rs, g);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeResultSet(rs);
		}
	}

	private Contato instantiateContato(ResultSet rs, Grupos grupo) throws SQLException {
		Contato obj = new Contato();
		obj.setCodigo(rs.getLong("codigo"));
		obj.setNome(rs.getString("nome"));
		obj.setEndereco(rs.getString("endereco"));
		obj.setEmail(rs.getString("email"));
		obj.setDataNascimento(new java.util.Date(rs.getTimestamp("datadenascimento").getTime()));
		obj.setGrupo(grupo);
		return obj;
	}

	private Grupos instantiateGrupo(ResultSet rs) throws SQLException {
		Grupos grupo = new Grupos();
		grupo.setCodigo(rs.getLong("codigo"));
		grupo.setDescricao(rs.getString("descricao"));
		return grupo;
	}

	@Override
	public List<Contato> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					 "select * "
								+ " from contato c "
								+ " left join grupo_contato gc on gc.codigo_usuario = c.codigo "
								+ " left join grupo g on gc.codigo_grupo = g.codigo "
					+ "ORDER BY c.nome");
			
			rs = st.executeQuery();
			
			List<Contato> list = new ArrayList<>();
			Map<Long, Grupos> map = new HashMap<>();
			
			while (rs.next()) {
				
				Grupos grupo = map.get(rs.getLong("codigo"));
				
				if (grupo == null) {
					grupo = instantiateGrupo(rs);
					map.put(rs.getLong("codigo"), grupo);
				}
				
				Contato obj = instantiateContato(rs, grupo);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			ConexaoJdbc.closeStatement(st);
			ConexaoJdbc.closeResultSet(rs);
		}
	}

	@Override
	public List<Contato> findByGrupo(Grupos grupo) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT contato.*,department.Name as DepName "
					+ "FROM contato INNER JOIN department "
					+ "ON contato.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			st.setLong(1, grupo.getCodigo());
			
			rs = st.executeQuery();
			
			List<Contato> list = new ArrayList<>();
			Map<Integer, Grupos> map = new HashMap<>();
			
			while (rs.next()) {
				
				Grupos g = map.get(rs.getInt("codigo"));
				
				if (g == null) {
					g = instantiateGrupo(rs);
					map.put(rs.getInt("codigo"), g);
				}
				
				Contato obj = instantiateContato(rs, g);
				list.add(obj);
			}
			return list;
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