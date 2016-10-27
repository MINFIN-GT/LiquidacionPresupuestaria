package servlets;

import java.io.IOException;
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

		String mailTo = request.getParameter("to");
		String orden = request.getParameter("orden");

		if (!CUtils.isEmpty(mailTo) && !CUtils.isEmpty(orden)) {
			CGetISCVs iscv = new CGetISCVs();

			iscv.generarCorreo(orden, mailTo);

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
