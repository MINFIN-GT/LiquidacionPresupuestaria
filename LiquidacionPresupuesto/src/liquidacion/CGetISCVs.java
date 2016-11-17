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

		query.append(
				"select i.valor_pago, i.fecha_recaudo, ig.tipo, ig.monto, g.municipio_nombre, g.departamento_nombre, g.entidad_nombre, g.unidad_ejecutora_nombre, g.renglon_nombre ");
		query.append("from ISCV i,  ");
		query.append("ISCV_GASTO ig, ");
		query.append("GASTO g ");
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
		query.append("and (( ig.tipo='MU' and g.organismo = 101 and g.correlativo = 2 and g.fuente =29) or (ig.tipo = 'FC' and g.fuente = 11))");

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

	public void generarCorreo(String orden, String mailTo) {

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

		cuerpo = cuerpo.replace("#USUARIO#", mailTo);
		cuerpo = cuerpo.replace("#MONTO#", String.format("%.2f", monto.floatValue()));
		cuerpo = cuerpo.replace("#MUNI#", textMuni);
		cuerpo = cuerpo.replace("#FONDO#", textFondo);

		CLogger.writeConsole(cuerpo);

		//comentar todo lo anterior si se desea realizar prueba sin conexion a base de datos y descomentar la linea siguiente
		//String cuerpo = "Estimado <b>lurendel@gmail.com</b>,<br /><br />Como parte de nuestra tarea de transparencia en el manejo de los impuestos de los contribuyentes.  Atentamente le informamos que su pago de impuesto sobre circulación de vehículos de <b>Q 136.90</b>, fue destinado hacia los siguientes programas:<br /><br /><b>TRANS. A  LAS MUNICIPALIDADES</b><br />MUNICIPALIDAD DE SAN MIGUEL IXTAHUACAN, SAN MARCOS<br />Aporte: <b>Q 68.45</b><br /><br />PRESIDENCIA DE LA REPÚBLICA, SECRETARÍA DE ASUNTOS ADM.Y DE SEG.DE LA PRESIDENCIA<br />GUATEMALA, GUATEMALA<br /><b>PERSONAL PERMANENTE</b><br />Aporte: <b>Q 68.45</b><br /><br />Agradecemos su aporte responsable con el país y le invitamos a seguir con el cumplimiento de sus deberes ciudadano.  Por nuestra parte, le reiteramos por velar porque los recursos de los guatemaltecos sean asignados a las necesidades más sentidas de la población.<br /><br />También le invitamos a seguirnos en las redes sociales para conocer más,<br /><br /><a href=\"https://es-la.facebook.com/minfingt\"><img src=\"cid:imageFB\" alt=\"https://es-la.facebook.com/minfingt\"/></a><a href=\"https://twitter.com/minfingt?lang=es\"><img src=\"cid:imageTW\" alt=\"https://twitter.com/minfingt?lang=es\"/></a><br /><br />Atentamente,<br /><br /><div style=\"text-align:center;\">Ministerio de Finanzas Públicas y Superintendencia de Administración Tributaria<br /><br /><img src=\"cid:imageMF\" alt=\"MinFin\"/><img src=\"cid:imageSAT\" alt=\"SAT\"/></div>";
		
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
