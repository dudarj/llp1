package model;

import java.io.Serializable;
import java.util.Objects;


public class Grupos implements Serializable {
	private static final long serialVersionUID = 1L;

	public Long codigo;
	public String descricao;
	
	public Grupos() {
	}

	public Grupos(Long codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupos other = (Grupos) obj;
		return Objects.equals(codigo, other.codigo);
	}

	@Override
	public String toString() {
		return "Grupo [codigo=" + codigo + ", descricao=" + descricao + "]";
	}
}
