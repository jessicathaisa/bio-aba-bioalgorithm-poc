package com.bioaba.bioalgorithmpoc.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="parameter")
public class LocalTaskParameter {

	@Id
	@GeneratedValue()
	private Long id;
	private String chave;
	private String valor;
	
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = chave;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
