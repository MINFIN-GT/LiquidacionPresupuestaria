package liquidacion;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import data.CDatabase;
import utils.CLogger;

public class CLiquidacionPresupuestaria {

	private static final String QUERY_GASTOS_MUNI = "select * from GASTO WHERE monto_renglon>0 AND fuente=29 and mes = %d ORDER BY mes";
	private static final String QUERY_GASTOS_FONDO = "select * from GASTO WHERE monto_renglon>0 AND fuente=11 and mes = %d ORDER BY mes";
	private static final String QUERY_PAGO = "select * from ISCV where fecha_recaudo >= str_to_date(%s) and fecha_recaudo < str_to_date(%s) order by fecha_recaudo";

	private static final String INSERT_PAGO_GASTOS = "insert into ISCV_GASTO (formulario,documento,geografico_ingreso,no_cur,entidad,unidad_ejecutora,programa,subprograma,proyecto,actividad,obra,renglon,geografico_gasto,monto,tipo) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static boolean liquidarPresupuesto(PrintWriter writer, boolean show_pagos, String mes) {
		CDatabase dbGastosMuni = new CDatabase();
		CDatabase dbGastosFondo = new CDatabase();

		CDatabase dbpago_munis = new CDatabase();

		CDatabase dbpago_gastos = new CDatabase();

		try {
			if (dbGastosMuni.isOpen() && dbpago_munis.isOpen() && dbGastosFondo.isOpen() && dbpago_munis.isOpen()
					&& dbpago_gastos.isOpen()) {

				int numMes = Integer.valueOf(mes);

				CLogger.writeConsole(String.format(QUERY_GASTOS_MUNI, numMes));
				ResultSet resultGastosMuni = dbGastosMuni.runQuery(String.format(QUERY_GASTOS_MUNI, numMes));

				CLogger.writeConsole(String.format(QUERY_GASTOS_FONDO, numMes));
				ResultSet resultGastosFondo = dbGastosFondo.runQuery(String.format(QUERY_GASTOS_FONDO, numMes));

				String fechaRecaudoFrom = "'01/" + String.format("%02d", numMes) + "/2016','%d/%m/%Y'";
				String fechaRecaudoTo = "'01/" + String.format("%02d", (numMes + 1)) + "/2016','%d/%m/%Y'";
				CLogger.writeConsole(String.format(QUERY_PAGO, fechaRecaudoFrom, fechaRecaudoTo));
				ResultSet resultpago = dbpago_munis
						.runQuery(String.format(QUERY_PAGO, fechaRecaudoFrom, fechaRecaudoTo));

				dbpago_gastos.setSqlInsert(INSERT_PAGO_GASTOS);

				Double saldo_muni = 0.0;
				Double saldo_fondo = 0.0;
				Double pago_muni = 0.0;
				Double pago_fondo = 0.0;
				Double gasto_muni = 0.0;
				Double gasto_fondo = 0.0;
				long cont = 1;

				writer.println("INCIA...");

				while (resultpago.next()) {
					pago_muni = resultpago.getDouble("valor_pago") * 0.5;
					pago_fondo = resultpago.getDouble("valor_pago") * 0.5;

					if (saldo_muni < 0 && !resultGastosMuni.isAfterLast()) {
						Double temp_saldo = saldo_muni;

						saldo_muni = saldo_muni + pago_muni;

						if (saldo_muni < 0) {
							temp_saldo = pago_muni;
						}

						agregarIscvGasto(dbpago_gastos, resultpago, resultGastosMuni, temp_saldo, "MU");
					} else
						saldo_muni = pago_muni;

					while (saldo_muni > 0 && resultGastosMuni.next()) {
						Double temp_saldo = saldo_muni;

						gasto_muni = resultGastosMuni.getDouble("monto_renglon");

						saldo_muni = saldo_muni - gasto_muni;

						if (saldo_muni > 0) {
							temp_saldo = gasto_muni;
						}

						agregarIscvGasto(dbpago_gastos, resultpago, resultGastosMuni, temp_saldo, "MU");

					}

					if (saldo_fondo < 0 && !resultGastosFondo.isAfterLast()) {
						Double temp_saldo = saldo_fondo;

						saldo_fondo = saldo_fondo + pago_fondo;

						if (saldo_fondo < 0) {
							temp_saldo = pago_fondo;
						}

						agregarIscvGasto(dbpago_gastos, resultpago, resultGastosFondo, temp_saldo, "FC");
					} else
						saldo_fondo = pago_fondo;

					while (saldo_fondo > 0 && resultGastosFondo.next()) {
						gasto_fondo = resultGastosFondo.getDouble("monto_renglon");

						Double temp_saldo = saldo_fondo;

						saldo_fondo = saldo_fondo - gasto_fondo;

						if (saldo_fondo > 0) {
							temp_saldo = gasto_fondo;
						}

						agregarIscvGasto(dbpago_gastos, resultpago, resultGastosFondo, temp_saldo, "FC");

					}

					cont++;
					if (cont % 1000 == 0) {
						writer.println("SE HAN INSERTADO: " + (cont / 1000) + " MIL");
						writer.flush();
					}
				}

				writer.println("...FINALIZADO");

				writer.flush();
			}
		} catch (Exception e) {
			CLogger.writeFullConsole("ERROR-EXECUTION: liquidarPresupuesto(): ", e);
		} finally {
			try {
				dbGastosMuni.close();
				dbGastosFondo.close();
				dbpago_munis.close();
				dbpago_gastos.close();

				return true;
			} catch (SQLException e) {
				CLogger.writeFullConsole("ERROR-CLOSE: liquidarPresupuesto(): ", e);
			}

		}

		return false;
	}

	private static void agregarIscvGasto(CDatabase dbpago_gastos, ResultSet rsISCV, ResultSet rsGastos, Double pago,
			String tipo) throws SQLException {

		dbpago_gastos.addBatch(rsISCV.getDouble("formulario"), rsISCV.getDouble("documento"),
				rsISCV.getDouble("cod_municipio"), rsGastos.getDouble("no_cur"), rsGastos.getDouble("entidad"),
				rsGastos.getDouble("unidad_ejecutora"), rsGastos.getDouble("programa"),
				rsGastos.getDouble("subprograma"), rsGastos.getDouble("proyecto"), rsGastos.getDouble("actividad"),
				rsGastos.getDouble("obra"), rsGastos.getDouble("renglon"), rsGastos.getDouble("geografico"),
				pago < 0 ? pago * -1 : pago, tipo);

		// writer.println(String.join(""," Entidad:
		// ",rsGastos.getString("entidad_nombre"),",
		// ",rsGastos.getString("unidad_ejecutora_nombre")));
		// writer.println(String.join("","
		// ",rsGastos.getString("municipio_nombre"),",
		// ",rsGastos.getString("departamento_nombre")));
		// writer.println(String.join(""," ",
		// rsGastos.getString("renglon_nombre")+", Q
		// ",gasto_muni.toString()));

		// if(show_pagos)
		// writer.println("saldo_muni: "+saldo_muni);
	}

}
