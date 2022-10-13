package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Contato implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private Long codigo;
	private String nome;
	private String endereco;
	private String email;
	private Date dataNascimento = new Date();
	
	private List<Telefone> telefone = new ArrayList<>();
	private Grupos grupo;

	public Contato() {
		
	}

	public Contato(Long codigo, String nome, String endereco, String email, Date dataNascimento, List<Telefone> telefone) {
		this.codigo = codigo;
		this.nome = nome;
		this.endereco = endereco;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.telefone = telefone;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public List<Telefone> getTelefone() {
		return telefone;
	}

	public void setTelefone(List<Telefone> telefone) {
		this.telefone = telefone;
	}
	public Grupos getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupos grupo) {
		this.grupo = grupo;
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
		Contato other = (Contato) obj;
		return codigo == other.codigo;
	}

	@Override
	public String toString() {
		return "Contato [codigo=" + codigo + ", nome=" + nome + ", endereco=" + endereco + ", email=" + email
				+ ", dataNascimento=" + dataNascimento +", telefone=" + telefone + "]";
	}
	
	

}
