package pojo;

public class CISCV {
	private Double orden;
	private Double documento;
	private Double formulario;
	private Double pago;

	public CISCV(Double orden, Double documento, Double formulario, Double pago) {
		this.orden = orden;
		this.documento = documento;
		this.formulario = formulario;
		this.pago = pago;
	}

	public Double getOrden() {
		return orden;
	}

	public void setOrden(Double orden) {
		this.orden = orden;
	}

	public Double getDocumento() {
		return documento;
	}

	public void setDocumento(Double documento) {
		this.documento = documento;
	}

	public Double getFormulario() {
		return formulario;
	}

	public void setFormulario(Double formulario) {
		this.formulario = formulario;
	}

	public Double getPago() {
		return pago;
	}

	public void setPago(Double pago) {
		this.pago = pago;
	}

}
