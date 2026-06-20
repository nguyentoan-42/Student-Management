package com.example.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.studentmanagement.frontend.view.LoginFrame;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class StudentmanagementApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");

		SpringApplication.run(StudentmanagementApplication.class, args);

		SwingUtilities.invokeLater(() -> {
			new LoginFrame().setVisible(true);
		});
	}

}
