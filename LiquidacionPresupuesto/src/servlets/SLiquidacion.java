package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	    response.setHeader("Content-Type", "text/plain");
	    response.setHeader("success", "yes");

		String mes = request.getParameter("mes");

		if (!CUtils.isEmpty(mes)) {
			CLiquidacionPresupuestaria.liquidarPresupuesto(response.getWriter(), false, mes);
		} else {
			CLiquidacionPresupuestaria.getImpuestoGasto(response.getWriter());
		}

		response.getWriter().close();
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
