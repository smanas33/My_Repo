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

	public static int save(Employee employee) {
		int status = 0;

		try {
			connection = EmployeeDao.getConnection();
			PreparedStatement statement = connection
					.prepareStatement("insert into user905(username, password, email, country) values(?,?,?,?)");
			statement.setString(1, employee.getUserName());
			statement.setString(2, employee.getPassword());
			statement.setString(3, employee.getEmail());
			statement.setString(4, employee.getCountry());

			status = statement.executeUpdate();
			connection.close();
		} catch (Exception exception) {
			System.out.println(exception);
		}

		return status;
	}

	public static int delete(int id) {
		int status = 0;
		try {
			connection = EmployeeDao.getConnection();
			PreparedStatement statement = connection.prepareStatement("delete from user905 where id = ?");
			statement.setInt(1, id);
			status = statement.executeUpdate();

			connection.close();
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return status;
	}

	public static Employee getEmployeeById(int id) {
		Employee employee = new Employee();
		try {
			connection = EmployeeDao.getConnection();
			PreparedStatement statement = connection.prepareStatement("select * from user905 where id = ?");
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				employee.setId(rs.getInt(1));
				employee.setUserName(rs.getString(2));
				employee.setPassword(rs.getString(3));
				employee.setEmail(rs.getString(4));
				employee.setCountry(rs.getString(5));
			}

			connection.close();
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return employee;
	}

	public static int update(Employee employee) {
		int status = 0;
		try {
			System.out.println("Pass: "+employee.getPassword());
			connection = EmployeeDao.getConnection();
			PreparedStatement statement = connection
					.prepareStatement("update user905 set username=?, password=?, email=?, country= ? where id = ?");

			statement.setString(1, employee.getUserName());
			statement.setString(2, employee.getPassword());
			statement.setString(3, employee.getEmail());
			statement.setString(4, employee.getCountry());
			statement.setInt(5, employee.getId());

			status = statement.executeUpdate();

			connection.close();
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return status;
	}
}
