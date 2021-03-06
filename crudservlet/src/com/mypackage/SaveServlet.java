package com.mypackage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SaveServlet")
public class SaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String country = request.getParameter("country");

		Employee employee = new Employee();
		employee.setUserName(name);
		employee.setPassword(password);
		employee.setEmail(email);
		employee.setCountry(country);

		if (!employee.getUserName().isEmpty() && !employee.getPassword().isEmpty() && !employee.getEmail().isEmpty()
				&& !employee.getCountry().isEmpty()) {
			int status = EmployeeDao.save(employee);

			if (status > 0) {
				out.print("<p>Record saved successfully!</p>");
				request.getRequestDispatcher("index.html").include(request, response);
			} else {
				out.println("Sorry! unable to save record");
			}
		} else {
			out.println("Sorry! Something you have not entered");
		}
		out.close();
	}

}
