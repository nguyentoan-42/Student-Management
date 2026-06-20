package com.example.studentmanagement.frontend.view;

import com.example.studentmanagement.frontend.service.ApiService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    // MÀU SẮC CHỦ ĐẠO
    static final Color C_DARK = new Color(30, 41, 59);   // nền tiêu đề
    static final Color C_PRIMARY = new Color(37, 99, 235);  // nút chính
    static final Color C_BG = new Color(241, 245, 249); // nền tổng thể
    static final Color C_WHITE = Color.WHITE;
    static final Font F_TITLE = new Font("Arial", Font.BOLD, 20);
    static final Font F_LABEL = new Font("Arial", Font.BOLD, 13);
    static final Font F_INPUT = new Font("Arial", Font.PLAIN, 13);


    private JTextField txtUser = new JTextField(18);
    private JPasswordField txtPass = new JPasswordField(18);
    private JButton btnLogin = new JButton("Đăng nhập");

    public LoginFrame() {
        setTitle("Hệ thống Quản Lý Sinh Viên");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // PANEL TỔNG THỂ
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);

        // TIÊU ĐỀ
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel lblTitle = new JLabel("QUẢN LÝ SINH VIÊN");
        lblTitle.setFont(F_TITLE);
        lblTitle.setForeground(C_WHITE);

        JLabel lblSub = new JLabel("Vui lòng đăng nhập để tiếp tục");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 4));
        headerText.setOpaque(false);
        headerText.add(lblTitle);
        headerText.add(lblSub);
        header.add(headerText, BorderLayout.CENTER);

        // FORM
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(7, 0, 7, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Style ô nhập liệu
        styleField(txtUser);
        styleField(txtPass);

        // Tài khoản
        JLabel lUser = new JLabel("Tài khoản");
        lUser.setFont(F_LABEL);
        lUser.setForeground(C_DARK);
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lUser, gbc);

        gbc.gridy = 1;
        form.add(txtUser, gbc);

        // Mật khẩu
        JLabel lPass = new JLabel("Mật khẩu");
        lPass.setFont(F_LABEL);
        lPass.setForeground(C_DARK);
        gbc.gridy = 2;
        form.add(lPass, gbc);

        gbc.gridy = 3;
        form.add(txtPass, gbc);

        // Nút đăng nhập
        styleButton(btnLogin, C_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 0, 0);
        form.add(btnLogin, gbc);

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        setContentPane(main);

        // SỰ KIỆN
        txtPass.addActionListener(e -> btnLogin.doClick());

        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
                return;
            }

            String params = "username=" + username + "&password=" + password;
            String role = ApiService.sendPostOrPut("http://localhost:8080/api/login", "POST", params);

            if ("ADMIN".equals(role)) {
                new AdminFrame().setVisible(true);
                dispose();
            } else if ("STUDENT".equals(role)) {
                new StudentFrame(username).setVisible(true);
                dispose();
            } else {
                showError("Sai tài khoản hoặc mật khẩu!");
                txtPass.setText("");
            }
        });
    }

    // Style cho ô nhập liệu
    private void styleField(JTextField field) {
        field.setFont(F_INPUT);
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Style cho nút bấm
    static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(C_WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            Color original = bg;
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(original); }
        });
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}