package com.mypackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewServlet")
public class ViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<a href='index.html'>Add New Employee</a>");
		out.println("<h1>Employees List</h1>");

		List<Employee> employees = EmployeeDao.getAllEmployees();

		out.print("<table border='1' width='100%'>");
		out.print(
				"<tr><th>Id</th><th>Name</th><th>Password</th><th>Email</th><th>Country</th><th>Edit</th><th>Delete</th></tr>");
		for (Employee employee : employees) {
			out.print("<tr><td>"+employee.getId()+"</td><td>"+employee.getUserName()+"</td><td>"+employee.getPassword()
					+"</td><td>"+employee.getEmail()+"</td><td>"+employee.getCountry()
					+"</td><td><a href='EditServlet?id="+employee.getId()+"'>edit</a></td>"
					+"<td><a href='DeleteServlet?id="+employee.getId()+"'>delete</a></td></tr>");
		}
		out.print("</table>");
		out.close();
	}
}
