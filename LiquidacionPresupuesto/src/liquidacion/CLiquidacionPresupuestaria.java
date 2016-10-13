package liquidacion;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import data.CDatabase;
import utils.CLogger;

public class CLiquidacionPresupuestaria {

	private static final String QUERY_GASTOS_MUNI = "select * from GASTO WHERE monto_renglon>0 AND fuente=29 ORDER BY fec_aprobado";
	private static final String QUERY_GASTOS_FONDO = "select * from GASTO WHERE monto_renglon>0 AND fuente=11 ORDER BY fec_aprobado";
	private static final String QUERY_pago = "select * from ISCV order by fecha_recaudo";
	
	private static CDatabase dbGastosMuni;
	private static CDatabase dbGastosFondo;
	private static CDatabase dbpago_munis;
	
	public static boolean liquidarPresupuesto(PrintWriter writer, boolean show_pagos) {

		dbGastosMuni = new CDatabase();
		dbGastosFondo = new CDatabase();
		dbpago_munis = new CDatabase();
		//dbLiquidacion = new Database();

		try {

			if (dbGastosMuni.isOpen() && dbpago_munis.isOpen() && dbGastosFondo.isOpen()) {

				ResultSet resultGastosMuni = dbGastosMuni.runQuery(QUERY_GASTOS_MUNI);
				ResultSet resultGastosFondo = dbGastosFondo.runQuery(QUERY_GASTOS_FONDO);
				ResultSet resultpago = dbpago_munis.runQuery(QUERY_pago);

				//Double total = 0.0;
				Double saldo_muni = 0.0;
				Double saldo_fondo = 0.0;
				Double pago_muni = 0.0;
				Double pago_fondo = 0.0;
				Double gasto_muni = 0.0;
				Double gasto_fondo = 0.0;
				long cont=1;
				while(resultpago.next()){
					pago_muni = resultpago.getDouble("valor_pago")*0.5;
					pago_fondo = resultpago.getDouble("valor_pago")*0.5;
					writer.println("Contribuyente #"+cont+" con su aporte"+(show_pagos ? " de Q "+pago_muni : "")+" usted contribuyo a: ");
					if(saldo_muni<0 && !resultGastosMuni.isAfterLast()){
						writer.println(String.join("","     Entidad: ",resultGastosMuni.getString("entidad_nombre"),", ",resultGastosMuni.getString("unidad_ejecutora_nombre")));
						writer.println(String.join("","     ",resultGastosMuni.getString("municipio_nombre"),", ",resultGastosMuni.getString("departamento_nombre")));
						writer.println(String.join("","     ", resultGastosMuni.getString("renglon_nombre")+", Q ",gasto_muni.toString()));
						saldo_muni = saldo_muni + pago_muni;
						if(show_pagos)
							writer.println("saldo_muni: "+saldo_muni);
						writer.println();
					}
					else
						saldo_muni = pago_muni;
					while(saldo_muni>0 && resultGastosMuni.next()){
						gasto_muni = resultGastosMuni.getDouble("monto_renglon");
							writer.println(String.join("","     Entidad: ",resultGastosMuni.getString("entidad_nombre"),", ",resultGastosMuni.getString("unidad_ejecutora_nombre")));
							writer.println(String.join("","     ",resultGastosMuni.getString("municipio_nombre"),", ",resultGastosMuni.getString("departamento_nombre")));
							writer.println(String.join("","     ", resultGastosMuni.getString("renglon_nombre")+", Q ",gasto_muni.toString()));
							saldo_muni = saldo_muni - gasto_muni;
							if(show_pagos)
								writer.println("saldo_muni: "+saldo_muni);
							writer.println();
							while(saldo_muni>0 && resultGastosMuni.next()){
								gasto_muni = resultGastosMuni.getDouble("monto_renglon");
								writer.println(String.join("","     Entidad: ",resultGastosMuni.getString("entidad_nombre"),", ",resultGastosMuni.getString("unidad_ejecutora_nombre")));
								writer.println(String.join("","     ",resultGastosMuni.getString("municipio_nombre"),", ",resultGastosMuni.getString("departamento_nombre")));
								writer.println(String.join("","     ", resultGastosMuni.getString("renglon_nombre")+", Q ",gasto_muni.toString()));
								saldo_muni = saldo_muni - gasto_muni;
								if(show_pagos)
									writer.println("saldo_muni: "+saldo_muni);
								writer.println();
							}
					}
					if(saldo_fondo<0 && !resultGastosFondo.isAfterLast()){
						writer.println(String.join("","     Entidad: ",resultGastosFondo.getString("entidad_nombre"),", ",resultGastosFondo.getString("unidad_ejecutora_nombre")));
						writer.println(String.join("","     ",resultGastosFondo.getString("municipio_nombre"),", ",resultGastosFondo.getString("departamento_nombre")));
						writer.println(String.join("","     ", resultGastosFondo.getString("renglon_nombre")+", Q ",gasto_fondo.toString()));
						saldo_fondo = saldo_fondo + pago_fondo;
						if(show_pagos)
							writer.println("saldo_fondo: "+saldo_fondo);
						writer.println();
					}
					else
						saldo_fondo = pago_fondo;
					while(saldo_fondo>0 && resultGastosFondo.next()){
						gasto_fondo = resultGastosFondo.getDouble("monto_renglon");
							writer.println(String.join("","     Entidad: ",resultGastosFondo.getString("entidad_nombre"),", ",resultGastosFondo.getString("unidad_ejecutora_nombre")));
							writer.println(String.join("","     ",resultGastosFondo.getString("municipio_nombre"),", ",resultGastosFondo.getString("departamento_nombre")));
							writer.println(String.join("","     ", resultGastosFondo.getString("renglon_nombre")+", Q ",gasto_fondo.toString()));
							saldo_fondo = saldo_fondo - gasto_fondo;
							if(show_pagos)
								writer.println("saldo_fondo: "+saldo_fondo);
							writer.println();
							while(saldo_fondo>0 && resultGastosFondo.next()){
								gasto_fondo = resultGastosFondo.getDouble("monto_renglon");
								writer.println(String.join("","     Entidad: ",resultGastosFondo.getString("entidad_nombre"),", ",resultGastosFondo.getString("unidad_ejecutora_nombre")));
								writer.println(String.join("","     ",resultGastosFondo.getString("municipio_nombre"),", ",resultGastosFondo.getString("departamento_nombre")));
								writer.println(String.join("","     ", resultGastosFondo.getString("renglon_nombre")+", Q ",gasto_fondo.toString()));
								saldo_fondo = saldo_fondo - gasto_fondo;
								if(show_pagos)
									writer.println("saldo_fondo: "+saldo_fondo);
								writer.println();
							}
						
					}
					cont++;
					if(cont%1000==0)
						writer.flush();
				}
				
				return true;
			}
		} catch (Exception e) {
			CLogger.writeFullConsole("ERROR-EXECUTION: liquidarPresupuesto(): ", e);
		} finally {
			try {
				dbGastosMuni.close();
				dbGastosFondo.close();
				dbpago_munis.close();
			} catch (SQLException e) {
				CLogger.writeFullConsole("ERROR-CLOSE: liquidarPresupuesto(): ", e);
			}

		}

		return false;
	}

}
