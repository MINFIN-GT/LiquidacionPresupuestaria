package pojo;

public class CDetalleGasto {
	private String tipo;

	private Double pago;
	private Double monto;

	private String municipio;
	private String departamento;

	private String entidad;
	private String unidad;
	private String renglon;

	public CDetalleGasto(String tipo, Double pago, Double monto, String municipio, String departamento, String entidad,
			String unidad, String renglon) {
		this.tipo = tipo;
		this.pago = pago;
		this.monto = monto;
		this.municipio = municipio;
		this.departamento = departamento;
		this.entidad = entidad;
		this.unidad = unidad;
		this.renglon = renglon;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Double getPago() {
		return pago;
	}

	public void setPago(Double pago) {
		this.pago = pago;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public String getRenglon() {
		return renglon;
	}

	public void setRenglon(String renglon) {
		this.renglon = renglon;
	}

}
