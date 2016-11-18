package liquidacion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.CDatabase;
import pojo.CDetalleGasto;
import pojo.CISCV;
import utils.CLogger;
import utils.CUtils;

public class CGetISCVs {

	private String getQuery(int from, int to) {
		StringBuilder query = new StringBuilder();

		query.append("select orden, documento, formulario, valor_pago from ISCV ");
		query.append("where orden >= ").append(from).append(" ");
		query.append("and orden <= ").append(to).append(" ");
		query.append("order by orden");

		return query.toString();
	}

	private String getQueryGasto(String orden) {
		StringBuilder query = new StringBuilder();

		query.append("select ");
		query.append(" i.valor_pago, i.fecha_recaudo, ");
		query.append("ig.tipo, ig.monto, ");
		query.append("g.municipio_nombre, g.departamento_nombre, g.entidad_nombre, g.unidad_ejecutora_nombre, ");
		query.append("ifnull(gr.renglon_nombre,g.renglon_nombre) renglon_nombre ");
		query.append("from ISCV i,  ");
		query.append("ISCV_GASTO ig, ");
		query.append("GASTO g ");
		query.append("left join GASTO_RENGLON gr ");
		query.append("on ( ");
		query.append("     gr.entidad = g.entidad ");
		query.append("     and gr.renglon = g.renglon ");
		query.append("     and gr.ejercicio = 2016 ");
		query.append(") ");
		query.append("where orden = ").append(orden).append(" ");
		query.append("and ig.documento = i.documento ");
		query.append("and ig.formulario = i.formulario  ");
		query.append("and g.no_cur = ig.no_cur ");
		query.append("and g.entidad = ig.entidad ");
		query.append("and g.unidad_ejecutora = ig.unidad_ejecutora ");
		query.append("and g.programa = ig.programa ");
		query.append("and g.subprograma = ig.subprograma ");
		query.append("and g.proyecto = ig.proyecto ");
		query.append("and g.actividad = ig.actividad ");
		query.append("and g.obra = ig.obra ");
		query.append("and g.renglon = ig.renglon ");
		query.append("and g.mes = ig.mes ");
		query.append("and g.geografico = ig.geografico_gasto ");
		query.append("and ( ");
		query.append("     (ig.tipo = 'MU' and g.organismo = 101 and g.correlativo = 2 and g.fuente = 29) ");
		query.append("     or (ig.tipo = 'FC' and g.fuente = 11) ");
		query.append(")");

		return query.toString();
	}

	public List<CISCV> getISCVs(int from, int to) {
		CDatabase db = new CDatabase();

		List<CISCV> pagos = new ArrayList<CISCV>();
		try {
			if (db.isOpen()) {
				ResultSet rs = db.runQuery(getQuery(from, to));

				while (rs.next()) {
					CISCV pago = new CISCV(rs.getDouble("orden"), rs.getDouble("documento"), rs.getDouble("formulario"),
							rs.getDouble("valor_pago"));

					pagos.add(pago);
				}
			}
		} catch (Exception e) {
			CLogger.writeFullConsole("ERROR-EXECUTION: getISCVs(): ", e);
		} finally {
			try {
				db.close();
			} catch (SQLException e) {
				CLogger.writeFullConsole("ERROR-CLOSE: getISCVs(): ", e);
			}

		}

		return pagos;
	}

	public List<CDetalleGasto> getDetalleGasto(String orden) {
		CDatabase db = new CDatabase();

		List<CDetalleGasto> gastos = new ArrayList<CDetalleGasto>();
		try {
			if (db.isOpen()) {
				ResultSet rs = db.runQuery(getQueryGasto(orden));

				while (rs.next()) {
					CDetalleGasto gasto = new CDetalleGasto(rs.getString("tipo"), rs.getDouble("valor_pago"),
							rs.getDouble("monto"), rs.getString("municipio_nombre"),
							rs.getString("departamento_nombre"), rs.getString("entidad_nombre"),
							rs.getString("unidad_ejecutora_nombre"), rs.getString("renglon_nombre"));

					gastos.add(gasto);
				}
			}
		} catch (Exception e) {
			CLogger.writeFullConsole("ERROR-EXECUTION: getDetalleGasto(): ", e);
		} finally {
			try {
				db.close();
			} catch (SQLException e) {
				CLogger.writeFullConsole("ERROR-CLOSE: getDetalleGasto(): ", e);
			}

		}

		return gastos;
	}

	// bar reformador Karen Guzman 3596 55k 2%

	public String generarCorreo(String orden) {

		String cuerpo = CUtils.MAIL_BODY;
		String textMuni = "";
		String textFondo = "";

		List<CDetalleGasto> detalle = getDetalleGasto(orden);

		Double monto = null;
		for (CDetalleGasto gasto : detalle) {
			String text = "";

			if (monto == null) {
				monto = gasto.getPago();
			}

			if (gasto.getTipo().equals("MU")) {
				text = CUtils.TEXT_MUNI;
				text = text.replace("#RENGLON#", gasto.getRenglon());
				text = text.replace("#MUNICIPIO#", gasto.getMunicipio());
				text = text.replace("#DEPARTAMENTO#", gasto.getDepartamento());
				text = text.replace("#MONTO#", String.format("%.2f", gasto.getMonto().floatValue()));

				if (!CUtils.isEmpty(textMuni)) {
					textMuni += "<br />";
				}

				textMuni += text;
			} else {
				text = CUtils.TEXT_FONDO;
				text = text.replace("#ENTIDAD#", gasto.getEntidad());
				text = text.replace("#UNIDAD#", gasto.getUnidad());
				text = text.replace("#MUNICIPIO#", gasto.getMunicipio());
				text = text.replace("#DEPARTAMENTO#", gasto.getDepartamento());
				text = text.replace("#RENGLON#", gasto.getRenglon());
				text = text.replace("#MONTO#", String.format("%.2f", gasto.getMonto().floatValue()));

				if (!CUtils.isEmpty(textFondo)) {
					textFondo += "<br />";
				}

				textFondo += text;
			}

		}

		cuerpo = cuerpo.replace("#MONTO#", String.format("%.2f", monto.floatValue()));
		cuerpo = cuerpo.replace("#MUNI#", textMuni);
		cuerpo = cuerpo.replace("#FONDO#", textFondo);

		return cuerpo;
	}
	
	public void enviarCorreo(String mailTo, String cuerpo){
		CLogger.writeConsole(cuerpo);

		StringBuffer buffer = new StringBuffer();
		
		buffer.append(cuerpo);

		Map<String, String> images = new HashMap<String, String>();
		images.put("imageFB", "/logs/mail_pics/fb_image.png");
		images.put("imageTW", "/logs/mail_pics/twitter_image.png");
		images.put("imageMF", "/logs/mail_pics/minfin_image.png"); 
		images.put("imageSAT", "/logs/mail_pics/sat_image.png");
		
		CUtils.send("rdminfin@gmail.com", mailTo, "", "", CUtils.MAIL_SUBJECT, true, buffer, images);
	}

}
