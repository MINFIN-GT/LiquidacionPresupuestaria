package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import liquidacion.CGetISCVs;
import liquidacion.CLiquidacionPresupuestaria;
import utils.CUtils;

/**
 * Servlet implementation class SLiquidacion
 */
@WebServlet("/SLiquidacion")
public class SLiquidacion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SLiquidacion() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		String accion = request.getParameter("accion");
		String mailTo = request.getParameter("to");
		String orden = request.getParameter("orden");

		String mes = request.getParameter("mes");

		if (!CUtils.isEmpty(accion) && !CUtils.isEmpty(mailTo) && !CUtils.isEmpty(orden) && accion.contains("mail")) {
			CGetISCVs iscv = new CGetISCVs();

			iscv.generarCorreo(orden, mailTo);

		} else if (!CUtils.isEmpty(mes)) {
			CLiquidacionPresupuestaria.liquidarPresupuesto(response.getWriter(), false, mes);

			response.getWriter().flush();
			response.getWriter().close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
	}

}
