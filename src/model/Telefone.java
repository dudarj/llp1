package model;

import java.io.Serializable;
import java.util.Objects;

public class Telefone implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private Long codigo;
	private String telefone;
	private Contato contato;
	private Tipos tipo;
	
	public Telefone(){
		
	}

	public Telefone(Long codigo, String telefone, Contato contato, Tipos tipo) {
		this.codigo = codigo;
		this.telefone = telefone;
		this.contato = contato;
		this.tipo = tipo;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Contato getContato() {
		return contato;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}
	
	public Tipos getTipo() {
		return tipo;
	}

	public void setTipo(Tipos tipo) {
		this.tipo = tipo;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(codigo, contato, telefone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Telefone other = (Telefone) obj;
		return codigo == other.codigo && Objects.equals(contato, other.contato)
				&& Objects.equals(telefone, other.telefone);
	}

	@Override
	public String toString() {
		return "Telefone [codigo=" + codigo + ", telefone=" + telefone + ", contato=" + contato + ", tipo=" + tipo + "]";
	}
	
	
	
	
	
}
