package model;

import java.io.Serializable;
import java.util.Objects;

public class Tipos implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Long codigo;
	private String descricao;
	
	
	public Tipos() {
		
	}

	public Tipos(Long codigo, String descricao) {
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
		return Objects.hash(codigo, descricao);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tipos other = (Tipos) obj;
		return codigo == other.codigo && Objects.equals(descricao, other.descricao);
	}

	@Override
	public String toString() {
		return "Tipos [codigo=" + codigo + ", descricao=" + descricao + "]";
	}
}
