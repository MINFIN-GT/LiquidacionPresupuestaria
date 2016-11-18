package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import liquidacion.CGetISCVs;
import utils.CUtils;

/**
 * Servlet implementation class SSendMail
 */
@WebServlet("/SSendMail")
public class SSendMail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SSendMail() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String accion = request.getParameter("accion");

		if (!CUtils.isEmpty(accion)) {
			if (accion.equalsIgnoreCase("preparar")) {
				String orden = request.getParameter("orden");

				if (!CUtils.isEmpty(orden)) {
					CGetISCVs iscv = new CGetISCVs();

					String cuerpo = iscv.generarCorreo(orden);

					List<String> objetos = new ArrayList<String>();
					objetos.add(cuerpo);

					CUtils.writeJSon(response, CUtils.getJSonString("cuerpo", objetos));
				}
			} else {
				String mailTo = request.getParameter("to");
				String cuerpo = request.getParameter("cuerpo");

				if (!CUtils.isEmpty(mailTo)) {
					CGetISCVs iscv = new CGetISCVs();

					iscv.enviarCorreo(mailTo, cuerpo);
				}
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
