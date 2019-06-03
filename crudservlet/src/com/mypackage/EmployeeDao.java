package com.mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
	private static Connection connection = null;

	private static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return connection;
	}

	public static List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		try {
			connection = EmployeeDao.getConnection();
			PreparedStatement statement = connection.prepareStatement("Select * from user905");
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				Employee employee = new Employee();
				employee.setId(resultSet.getInt(1));
				employee.setUserName(resultSet.getString(2));
				employee.setPassword(resultSet.getString(3));
				employee.setEmail(resultSet.getString(4));
				employee.setCountry(resultSet.getString(5));
				employees.add(employee);
			}
			connection.close();
			
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return employees;
	}

}
