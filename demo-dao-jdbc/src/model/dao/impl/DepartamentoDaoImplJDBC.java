package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoDaoImplJDBC implements DepartamentoDao {

	/*
	 * vou ter esse objeto conn a disposi��o em qualquer lugar da classe
	 * DepartamentoDaoImplJDBC
	 */
	private Connection conn;

	/* exemplo de outra forma de inje��o de depend�ncia */
	public DepartamentoDaoImplJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Departamento obj) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);// essa linha retorna o id do objeto inserido no banco de dados
			ps.setString(1, obj.getNome());
			
			int rowsAffetc = ps.executeUpdate();
			
			if (rowsAffetc > 0) {
				rs = ps.getGeneratedKeys();
				
				if (rs.next()) {
					Long id = rs.getLong(1);
					obj.setId(id);
				}
			} else {
				throw new DbException("Erro inesperado, nenhuma linha foi afetada!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void update(Departamento obj) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?",
					Statement.RETURN_GENERATED_KEYS);// essa linha retorna o id do objeto inserido no banco de dados
			ps.setString(1, obj.getNome());
			ps.setLong(2, obj.getId());
			
			int rowsAffetc = ps.executeUpdate();
			
			if (rowsAffetc > 0) {
				rs = ps.getGeneratedKeys();
				
				if (rs.next()) {
					Long id = rs.getLong(1);
					obj.setId(id);
				}
			} else {
				throw new DbException("Erro inesperado, nenhuma linha foi afetada!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public void deleteById(Long id) {
		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement("DELETE FROM department WHERE Id = ?",
					Statement.RETURN_GENERATED_KEYS);// essa linha retorna o id do objeto inserido no banco de dados
			ps.setLong(1, id);
			
			int rowsAffetc = ps.executeUpdate();
			
			if (rowsAffetc == 0) {
				throw new DbException("Id informado n�o existe!");
			} 

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	private Departamento instantiateDepartment(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getLong("Id"));
		dep.setNome(rs.getString("Name"));
		return dep;
	}

	@Override
	public Departamento findById(Long id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Departamento dep = instantiateDepartment(rs);
				return dep;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Departamento> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement("SELECT * FROM department ORDER BY Name");
			rs = ps.executeQuery();

			List<Departamento> list = new ArrayList<Departamento>();

			while (rs.next()) {
				Departamento depart = instantiateDepartment(rs);
				list.add(depart);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

}
