package com.example.studentmanagement.frontend.view;

import com.example.studentmanagement.frontend.service.ApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StudentFrame extends JFrame {

    private static final Color C_DARK = LoginFrame.C_DARK;
    private static final Color C_BG = LoginFrame.C_BG;
    private static final Color C_WHITE = Color.WHITE;

    private final String apiData;
    private final String studentId;

    public StudentFrame(String studentId) {
        this.studentId = studentId;
        this.apiData = ApiService.sendGet("http://localhost:8080/api/students/" + studentId);

        setTitle("Thông tin Sinh viên – " + studentId);
        setSize(480, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);

        // PANEL TIÊU ĐỀ & THÔNG TIN
        JPanel pnlHeader = new JPanel(new GridLayout(5, 1, 0, 6));
        pnlHeader.setBackground(C_WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 224, 233)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        String[] parts = (apiData != null && !apiData.equals("ERROR")) ? apiData.split("\\|", -1) : new String[16];
        String fullName = parts.length > 2 ? parts[2] : "N/A";
        String dept = parts.length > 3 ? parts[3] : "N/A";
        String className = parts.length > 4 ? parts[4] : "N/A";

        double tempGpa = 0.0;
        if (parts.length > 15) {
            try {
                tempGpa = Double.parseDouble(parts[15]);
            } catch (Exception ignored) {}
        }
        final double gpa = tempGpa;

        JLabel lblTitle = new JLabel("BẢNG ĐIỂM CÁ NHÂN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(C_DARK);

        pnlHeader.add(lblTitle);
        pnlHeader.add(new JLabel("Mã sinh viên: " + studentId));
        pnlHeader.add(new JLabel("Họ và tên: " + fullName));
        pnlHeader.add(new JLabel("Khoa: " + dept + "   |   Lớp: " + className));
        pnlHeader.add(new JLabel("Điểm trung bình tích lũy (GPA): " + String.format("%.2f", gpa) + "  (" + xepLoai(gpa) + ")"));

        for (Component c : pnlHeader.getComponents()) {
            if (c != lblTitle) {
                c.setFont(new Font("Arial", Font.PLAIN, 13));
                c.setForeground(C_DARK);
            }
        }
        main.add(pnlHeader, BorderLayout.NORTH);

        // BẢNG ĐIỂM
        String[] cols = {"Môn học", "Điểm số"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (parts.length >= 16) {
            for (int i = 0; i < 10; i++) {
                String scoreStr = parts[5 + i];
                if (scoreStr != null && !scoreStr.trim().isEmpty() && !scoreStr.equalsIgnoreCase("null")) {
                    try {
                        double s = Double.parseDouble(scoreStr);
                        model.addRow(new Object[]{"Môn học số " + (i + 1), String.format("%.1f", s)});
                    } catch (Exception ignored) {}
                }
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(new Color(230, 235, 242));

        JTableHeader header = table.getTableHeader();
        header.setBackground(C_DARK);
        header.setForeground(C_WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 32));

        // Căn giữa dữ liệu cho cột Điểm số
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(10, 20, 10, 20));
        sp.getViewport().setBackground(C_BG);
        main.add(sp, BorderLayout.CENTER);

        // Đăng xuất
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        pnlBottom.setBackground(C_BG);
        JButton btnLogout = new JButton("Đăng xuất");
        LoginFrame.styleButton(btnLogout, new Color(100, 116, 139));
        btnLogout.setPreferredSize(new Dimension(110, 32));
        btnLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(StudentFrame.this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        pnlBottom.add(btnLogout);
        main.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(main);

        // SỰ KIỆN ĐÓNG CỬA SỔ ỨNG DỤNG
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                int ok = JOptionPane.showConfirmDialog(
                        StudentFrame.this,
                        "Bạn có chắc muốn thoát chương trình?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
    }

    // Xếp loại học lực
    private String xepLoai(double score) {
        if (score >= 9.0) return "Xuất sắc";
        if (score >= 8.0) return "Giỏi";
        if (score >= 6.5) return "Khá";
        if (score >= 4.0) return "Trung bình";
        return "Yếu";
    }
}