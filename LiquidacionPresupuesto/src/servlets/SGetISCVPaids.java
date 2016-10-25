package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import liquidacion.CGetISCVs;
import pojo.CISCV;

@WebServlet("/SGetISCVPaids")
public class SGetISCVPaids extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SGetISCVPaids() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String rangeFrom = request.getParameter("from");
		String rangeTo = request.getParameter("to");

		response.setContentType("application/json");
		response.getWriter().println("{ \"data\":");

		List<CISCV> info = new ArrayList<CISCV>();
		CGetISCVs isvc = new CGetISCVs();

		info = isvc.getISCVs(rangeFrom, rangeTo);

		Gson g = new Gson();
		String res = g.toJson(info);
		response.getWriter().append(res).println("}");

		response.getWriter().flush();
		response.getWriter().close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

class Data {
	String descripcion;
	int valor;
}
