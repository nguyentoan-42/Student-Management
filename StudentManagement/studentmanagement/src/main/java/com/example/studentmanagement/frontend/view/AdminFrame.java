package com.example.studentmanagement.frontend.view;

import com.example.studentmanagement.frontend.service.ApiService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {

    private static final Color C_DARK = LoginFrame.C_DARK;
    private static final Color C_PRIMARY = LoginFrame.C_PRIMARY;
    private static final Color C_BG = LoginFrame.C_BG;
    private static final Color C_SUCCESS = new Color(22, 163, 74);
    private static final Color C_DANGER = new Color(220, 38, 38);

    // TRƯỜNG NHẬP LIỆU
    private JTextField txtId = new JTextField(7);
    private JTextField txtName = new JTextField(11);
    private JTextField txtDept = new JTextField(9);
    private JTextField txtClass = new JTextField(6);
    private JTextField txtUsername = new JTextField(9);
    private JTextField txtPassword = new JTextField(9);
    private JTextField[] txtScores = new JTextField[10];

    // TÌM KIẾM
    private JComboBox<String> cmbSearch = new JComboBox<>(new String[]{
            "Theo Mã SV", "Theo Tên", "Theo Lớp", "Theo Xếp loại",
            "GPA < 4 (Yếu)", "GPA ≥ 4 (Đạt)"
    });
    private JTextField txtSearchVal = new JTextField(14);
    private JButton btnSearch  = makeBtn("Tìm", C_PRIMARY);
    private JButton btnShowAll = makeBtn("Tất cả", new Color(100, 116, 139));

    // CÁC NÚT HÀNH ĐỘNG
    private JButton btnAdd = makeBtn("Thêm", C_SUCCESS);
    private JButton btnEdit = makeBtn("Sửa", C_PRIMARY);
    private JButton btnDelete = makeBtn("Xóa", C_DANGER);
    private JButton btnClear = makeBtn("Làm trống", new Color(100, 116, 139));
    private JButton btnExport = makeBtn("Xuất Excel", new Color(21, 128, 61));

    // BẢNG
    private JTable table;
    private DefaultTableModel model;

    private List<Object[]> allStudents = new ArrayList<>();


    // KHỞI TẠO
    public AdminFrame() {
        setTitle("Hệ thống Quản Lý Sinh Viên – Trang Admin");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        for (int i = 0; i < 10; i++) {
            txtScores[i] = new JTextField(4);
            txtScores[i].setHorizontalAlignment(JTextField.CENTER);
        }

        // txtUsername chỉ đọc – tự đồng bộ theo txtId
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(241, 245, 249));
        txtUsername.setForeground(new Color(100, 116, 139));
        txtUsername.setToolTipText("Tài khoản đăng nhập = Mã SV");

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildBottomArea(), BorderLayout.SOUTH);

        registerEvents();
        loadStudentList();
    }


    // GIAO DIỆN
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_DARK);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("QUẢN LÝ SINH VIÊN");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        // Đăng xuất
        JButton btnLogout = makeBtn("Đăng xuất", new Color(100, 116, 139));
        btnLogout.setPreferredSize(new Dimension(110, 32));
        btnLogout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(AdminFrame.this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        bar.add(title, BorderLayout.WEST);
        bar.add(btnLogout, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTable() {
        // Chỉnh theo yêu cầu: bỏ "Lớp"
        String[] cols = {"Mã SV", "Họ và Tên", "Khoa", "Tài khoản", "Mật khẩu", "GPA", "Xếp loại"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };


        // Chỉnh theo yêu cầu: màu chẵn lẻ cho dễ nhìn
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isCellSelected(row, column)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
                }
                return c;
            }
        };

        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionForeground(C_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(C_DARK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 32));

        // Chỉnh theo yêu cầu: Censor password
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (val != null) {
                    setText("********");
                }
                return comp;
            }
        });

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setFont(new Font("Arial", Font.BOLD, 12));
                if (!sel) {
                    String v = val == null ? "" : val.toString();
                    switch (v) {
                        case "Xuất sắc": lbl.setForeground(new Color(109, 40, 217)); break;
                        case "Giỏi": lbl.setForeground(new Color( 29, 78, 216)); break;
                        case "Khá": lbl.setForeground(new Color( 21,128,  61)); break;
                        case "Trung bình":lbl.setForeground(new Color(180, 83,   9)); break;
                        default: lbl.setForeground(C_DANGER);
                    }
                } else { lbl.setForeground(C_DARK); }
                return lbl;
            }
        });

        // Chỉnh theo yêu cầu: bỏ độ rộng của chỗ trước đây là cho lớp
        int[] widths = {75, 165, 150, 95, 95, 65, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(203, 213, 225)));
        return sp;
    }

    private JPanel buildBottomArea() {
        JPanel wrap = new JPanel(new BorderLayout(0, 0));
        wrap.setBackground(C_BG);
        wrap.add(buildSearchBar(), BorderLayout.NORTH);
        wrap.add(buildInputForm(), BorderLayout.CENTER);
        wrap.add(buildActionBar(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createMatteBorder(0, 4, 0, 0, C_PRIMARY)
        ));

        JLabel lbl = new JLabel("Tìm kiếm:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(C_DARK);

        cmbSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        txtSearchVal.setFont(new Font("Arial", Font.PLAIN, 12));
        txtSearchVal.setPreferredSize(new Dimension(160, 28));
        txtSearchVal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        LoginFrame.styleButton(btnSearch, C_PRIMARY);
        LoginFrame.styleButton(btnShowAll, new Color(100, 116, 139));
        btnSearch.setPreferredSize(new Dimension(90, 28));
        btnShowAll.setPreferredSize(new Dimension(90, 28));

        p.add(lbl); p.add(cmbSearch); p.add(txtSearchVal);
        p.add(btnSearch); p.add(btnShowAll);
        return p;
    }

    private JPanel buildInputForm() {
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        hdr.setBackground(new Color(248, 250, 252));
        hdr.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createMatteBorder(0, 4, 0, 0, C_PRIMARY)
        ));
        JLabel lhdr = new JLabel("  NHẬP / SỬA THÔNG TIN SINH VIÊN");
        lhdr.setFont(new Font("Arial", Font.BOLD, 11));
        lhdr.setForeground(new Color(71, 85, 105));
        hdr.add(lhdr);

        // Hàng 1: Thông tin cơ bản
        JPanel r1 = row();
        r1.add(lbl("Mã SV:")); r1.add(sf(txtId));
        r1.add(lbl("Họ tên:")); r1.add(sf(txtName));
        r1.add(lbl("Khoa:")); r1.add(sf(txtDept));
        r1.add(lbl("Lớp:")); r1.add(sf(txtClass));

        // Hàng 2: Tài khoản (chỉ đọc = Mã SV) và Mật khẩu
        JPanel r2 = row(); r2.setBackground(new Color(248, 250, 252));
        JLabel acctNote = new JLabel("Tài khoản đăng nhập cho sinh viên:");
        acctNote.setFont(new Font("Arial", Font.ITALIC, 11));
        acctNote.setForeground(new Color(100, 116, 139));
        r2.add(acctNote);
        r2.add(lbl("Tài khoản:")); r2.add(sf(txtUsername));
        JLabel lockNote = new JLabel("(= Mã SV)");
        lockNote.setFont(new Font("Arial", Font.ITALIC, 11));
        lockNote.setForeground(new Color(148, 163, 184));
        r2.add(lockNote);
        r2.add(lbl("Mật khẩu:")); r2.add(sf(txtPassword));


        // Hàng 3: Điểm Môn 1-Môn 5
        JPanel r3 = row();
        JLabel hint = new JLabel("Điểm (bỏ trống = không đăng ký môn đó):");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(100, 116, 139));
        r3.add(hint);
        for (int i = 0; i < 5; i++) { r3.add(lbl("Môn "+(i+1)+":")); r3.add(sf(txtScores[i])); }

        // Hàng 4: Điểm Môn 6-Môn 10
        JPanel r4 = row(); r4.setBackground(new Color(248, 250, 252));
        r4.add(Box.createHorizontalStrut(198));
        for (int i = 5; i < 10; i++) { r4.add(lbl("Môn "+(i+1)+":")); r4.add(sf(txtScores[i])); }
        JLabel note2 = new JLabel("  ← tối thiểu 6 môn / kỳ");
        note2.setFont(new Font("Arial", Font.ITALIC, 11));
        note2.setForeground(new Color(100, 116, 139));
        r4.add(note2);

        JPanel rows = new JPanel(new GridLayout(4, 1, 0, 0));
        rows.setBorder(BorderFactory.createEmptyBorder(2, 14, 4, 14));
        rows.add(r1); rows.add(r2); rows.add(r3); rows.add(r4);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        wrap.add(hdr, BorderLayout.NORTH);
        wrap.add(rows, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildActionBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, C_PRIMARY));
        LoginFrame.styleButton(btnAdd, C_SUCCESS);
        LoginFrame.styleButton(btnEdit, C_PRIMARY);
        LoginFrame.styleButton(btnDelete, C_DANGER);
        LoginFrame.styleButton(btnClear, new Color(100, 116, 139));
        LoginFrame.styleButton(btnExport, new Color(21, 128, 61));
        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete, btnClear, btnExport})
            b.setPreferredSize(new Dimension(110, 34));
        p.add(btnAdd); p.add(btnEdit); p.add(btnDelete); p.add(btnClear); p.add(btnExport);
        return p;
    }


    // SỰ KIỆN
    private void registerEvents() {

        // Click hàng bảng => đổ dữ liệu xuống form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                // Vẫn lấy Mã SV ở cột 0 để tìm toàn bộ thông tin gốc
                String id = model.getValueAt(row, 0).toString();
                Object[] d = findById(id);
                if (d == null) return;
                txtId.setText(d[0].toString());
                txtName.setText(d[1].toString());
                txtDept.setText(d[2].toString());
                txtClass.setText(d[3].toString()); // Dữ liệu Lớp vẫn được nạp lên Textfield
                txtUsername.setText(d[4].toString());
                txtPassword.setText(d[5].toString()); // Dữ liệu Password vẫn được nạp thật lên Textfield
                for (int i = 0; i < 10; i++)
                    txtScores[i].setText(d[6+i] != null ? d[6+i].toString() : "");
            }
        });

        // txtId thay đổi => tự cập nhật txtUsername
        txtId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { syncUsername(); }
            public void removeUpdate(DocumentEvent e)  { syncUsername(); }
            public void changedUpdate(DocumentEvent e) { syncUsername(); }
            private void syncUsername() {
                txtUsername.setText(txtId.getText().trim());
            }
        });

        // Ẩn ô text khi lọc GPA (không cần gõ từ khóa)
        cmbSearch.addActionListener(e -> {
            int idx = cmbSearch.getSelectedIndex();
            txtSearchVal.setEnabled(idx < 4);
            if (idx >= 4) txtSearchVal.setText("");
        });

        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e -> clearForm());
        btnExport.addActionListener(e -> doExportExcel());

        btnSearch.addActionListener(e  -> doSearch());
        btnShowAll.addActionListener(e -> reloadTable(allStudents));
        txtSearchVal.addActionListener(e -> doSearch());   // Nhấn Enter trong ô tìm

        // Đóng cửa sổ cần xác nhận hệ thống
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                int ok = JOptionPane.showConfirmDialog(
                        AdminFrame.this,
                        "Bạn có chắc muốn thoát chương trình?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
    }


    // CÁC THAO TÁC
    private void doAdd() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String dept = txtDept.getText().trim();
        String cls = txtClass.getText().trim();
        String upass = txtPassword.getText().trim();

        // Validate thông tin
        if (id.isEmpty()) { err("Mã SV không được để trống!"); return; }
        if (name.isEmpty()) { err("Họ tên không được để trống!"); return; }
        if (dept.isEmpty()) { err("Khoa không được để trống!"); return; }
        if (cls.isEmpty()) { err("Lớp không được để trống!"); return; }
        if (upass.isEmpty()) { err("Mật khẩu sinh viên không được để trống!"); return; }

        // Kiểm tra trùng Mã SV trong danh sách
        if (findById(id) != null) {
            err("Mã sinh viên \"" + id + "\" đã tồn tại trong danh sách!\n"
                    + "Vui lòng dùng mã khác.");
            return;
        }

        // Kiểm tra trùng tài khoản qua API
        String checkResult = ApiService.sendGet("http://localhost:8080/api/check-id/" + id);
        if ("EXISTS".equals(checkResult)) {
            err("Mã sinh viên / Tài khoản \"" + id + "\" đã tồn tại trong CSDL!\n"
                    + "Đang tải lại danh sách...");
            loadStudentList();
            return;
        }
        if ("ERROR".equals(checkResult)) {
            err("Không thể kết nối server để kiểm tra mã SV!\nVui lòng thử lại sau.");
            return;
        }

        // Validate và tính GPA
        Double[] scores = validateScores();
        if (scores == null) return;
        double gpa = computeGpa(scores);
        if (gpa < 0) return;
        String xl = xepLoai(gpa);

        // Gọi API thêm sinh viên
        String params = buildAddParams(id, name, dept, cls, upass, scores, gpa);
        String result = ApiService.sendPostOrPut("http://localhost:8080/api/students", "POST", params);
        if (result.startsWith("ERROR")) {
            err("Lỗi khi thêm sinh viên:\n" + result);
            return;
        }

        // Cập nhật dữ liệu cục bộ
        allStudents.add(row18(id, name, dept, cls, id, upass, scores, gpa, xl));

        // chỉnh theo yêu câu: cls
        model.addRow(new Object[]{id, name, dept, id, upass, String.format("%.2f", gpa), xl});

        info("Đã thêm sinh viên thành công!\n\n"
                + "  Họ tên:         " + name + "\n"
                + "  Mã SV:          " + id + "\n");
        clearForm();
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui lòng chọn một sinh viên trong bảng để sửa."); return; }

        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String dept = txtDept.getText().trim();
        String cls = txtClass.getText().trim();
        String upass = txtPassword.getText().trim();

        if (name.isEmpty()) { err("Họ tên không được để trống!"); return; }
        if (dept.isEmpty()) { err("Khoa không được để trống!"); return; }
        if (cls.isEmpty()) { err("Lớp không được để trống!"); return; }
        if (upass.isEmpty()) { err("Mật khẩu không được để trống!"); return; }

        Double[] scores = validateScores();
        if (scores == null) return;
        double gpa = computeGpa(scores);
        if (gpa < 0) return;
        String xl = xepLoai(gpa);

        String params = buildEditParams(name, dept, cls, upass, scores, gpa);
        String result = ApiService.sendPostOrPut(
                "http://localhost:8080/api/students/" + id, "PUT", params);
        if (result.startsWith("ERROR")) {
            err("Lỗi khi cập nhật thông tin:\n" + result);
            return;
        }

        // Cập nhật allStudents
        Object[] d = findById(id);
        if (d != null) {
            d[1]=name; d[2]=dept; d[3]=cls; d[5]=upass;
            for (int i = 0; i < 10; i++) d[6+i] = scores[i];
            d[16] = gpa; d[17] = xl;
        }

        model.setValueAt(name, row, 1);
        model.setValueAt(dept, row, 2);
        // Chỉnh theo yêu cầu: bỏ setValueAt ... 3
        model.setValueAt(upass, row, 4);
        model.setValueAt(String.format("%.2f", gpa), row, 5);
        model.setValueAt(xl, row, 6);

        info("Cập nhật thông tin sinh viên '" + name + "' thành công!");
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui lòng chọn một sinh viên trong bảng để xóa."); return; }

        String id = model.getValueAt(row, 0).toString();
        String name = model.getValueAt(row, 1).toString();

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa sinh viên:\n"
                        + "  Họ tên: " + name + "\n"
                        + "  Mã SV:  " + id + "\n\n"
                        + "Hành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        String result = ApiService.sendPostOrPut(
                "http://localhost:8080/api/students/" + id, "DELETE", "");
        if (result.startsWith("ERROR")) {
            err("Lỗi khi xóa sinh viên:\n" + result);
            return;
        }

        allStudents.removeIf(d -> d[0].toString().equals(id));
        model.removeRow(row);
        info("Đã xóa sinh viên '" + name + "' thành công!");
        clearForm();
    }

    private void doSearch() {
        int type = cmbSearch.getSelectedIndex();
        String kw = txtSearchVal.getText().trim().toLowerCase();
        if (type < 4 && kw.isEmpty()) { warn("Vui lòng nhập từ khóa tìm kiếm."); return; }

        List<Object[]> result = new ArrayList<>();
        for (Object[] d : allStudents) {
            boolean match = false;
            switch (type) {
                case 0: match = d[0].toString().toLowerCase().contains(kw); break; // Mã SV
                case 1: match = d[1].toString().toLowerCase().contains(kw); break; // Tên
                case 2: match = d[3].toString().toLowerCase().contains(kw); break; // Lớp
                case 3: match = d[17].toString().toLowerCase().contains(kw); break;// Xếp loại
                case 4: match = (double) d[16] < 4.0;  break;                      // GPA < 4
                case 5: match = (double) d[16] >= 4.0; break;                      // GPA >= 4
            }
            if (match) result.add(d);
        }
        reloadTable(result);
        if (result.isEmpty()) info("Không tìm thấy kết quả phù hợp.");
    }

    private void doExportExcel() {
        if (allStudents.isEmpty()) { warn("Danh sách sinh viên đang trống, không thể xuất!"); return; }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn nơi lưu file dữ liệu Excel");
        fc.setSelectedFile(new java.io.File("DanhSachSinhVien.csv"));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            f = new java.io.File(f.getAbsolutePath() + ".csv");
        }

        try (java.io.BufferedWriter w = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(f), "UTF-8"))) {

            w.write("\uFEFF"); // Ghi byte ký hiệu UTF-8 BOM để Excel hiển thị đúng dấu Tiếng Việt

            // File Excel VẪN xuất đầy đủ thông tin kể cả Lớp và Mật khẩu gốc
            w.write("Mã SV,Họ và Tên,Khoa,Lớp,Tài khoản,Mật khẩu,Môn 1,Môn 2,Môn 3,Môn 4,Môn 5,Môn 6,Môn 7,Môn 8,Môn 9,Môn 10,GPA,Xếp loại\n");

            for (Object[] d : allStudents) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i <= 5; i++) {
                    sb.append("\"").append(d[i].toString().replace("\"", "\"\"")).append("\",");
                }

                for (int i = 0; i < 10; i++) {
                    sb.append(d[6 + i] != null ? d[6 + i].toString() : "").append(",");
                }

                sb.append(String.format(java.util.Locale.US, "%.2f", (double) d[16])).append(",");
                sb.append("\"").append(d[17].toString().replace("\"", "\"\"")).append("\"\n");

                w.write(sb.toString());
            }

            info("Xuất file dữ liệu Excel thành công!\nĐường dẫn: " + f.getAbsolutePath());
        } catch (Exception ex) {
            err("Đã xảy ra lỗi khi xuất file Excel:\n" + ex.getMessage());
        }
    }


    // TẢI VÀ PARSE DANH SÁCH SINH VIÊN TỪ API
    private void loadStudentList() {
        String result = ApiService.sendGet("http://localhost:8080/api/students");
        allStudents.clear();

        if ("ERROR".equals(result) || result == null || result.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không kết nối được tới server.\nDanh sách sinh viên đang trống.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            reloadTable(allStudents);
            return;
        }

        for (String line : result.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] p = line.split("\\|", -1);
            if (p.length < 16) continue;

            Double[] scores = new Double[10];
            for (int i = 0; i < 10; i++) {
                String sv = (5 + i < p.length) ? p[5 + i].trim() : "";
                scores[i] = (!sv.isEmpty() && !"null".equalsIgnoreCase(sv))
                        ? Double.parseDouble(sv) : null;
            }

            double gpa;
            try { gpa = Double.parseDouble(p[15].trim()); }
            catch (NumberFormatException e) { gpa = 0.0; }

            String xl = xepLoai(gpa);

            allStudents.add(row18(p[0], p[2], p[3], p[4], p[0], p[1], scores, gpa, xl));
        }

        reloadTable(allStudents);
    }


    // XÂY DỰNG THAM SỐ GỬI API
    private String buildAddParams(String id, String name, String dept, String cls, String upass, Double[] scores, double gpa) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("studentId=").append(URLEncoder.encode(id, "UTF-8"))
                    .append("&fullName=").append(URLEncoder.encode(name, "UTF-8"))
                    .append("&department=").append(URLEncoder.encode(dept,"UTF-8"))
                    .append("&className=").append(URLEncoder.encode(cls, "UTF-8"))
                    .append("&password=").append(URLEncoder.encode(upass, "UTF-8"))
                    .append("&gpa=").append(gpa);
            for (int i = 0; i < 10; i++)
                sb.append("&s").append(i + 1).append("=")
                        .append(scores[i] != null ? scores[i] : "");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            err("Lỗi mã hóa tham số:\n" + e.getMessage());
            return "";
        }
    }

    private String buildEditParams(String name, String dept, String cls,
                                   String upass, Double[] scores, double gpa) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("fullName=").append(URLEncoder.encode(name, "UTF-8"))
                    .append("&department=").append(URLEncoder.encode(dept,"UTF-8"))
                    .append("&className=").append(URLEncoder.encode(cls, "UTF-8"))
                    .append("&password=").append(URLEncoder.encode(upass, "UTF-8"))
                    .append("&gpa=").append(gpa);
            for (int i = 0; i < 10; i++)
                sb.append("&s").append(i + 1).append("=")
                        .append(scores[i] != null ? scores[i] : "");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            err("Lỗi mã hóa tham số:\n" + e.getMessage());
            return "";
        }
    }


    // VALIDATION
    private Double[] validateScores() {
        Double[] scores = new Double[10];
        for (int i = 0; i < 10; i++) {
            String text = txtScores[i].getText().trim();
            if (text.isEmpty()) { scores[i] = null; continue; }
            try {
                double val = Double.parseDouble(text);
                if (val < 0 || val > 10) {
                    err("Điểm Môn " + (i+1) + " không hợp lệ!\n"
                            + "Điểm phải từ 0.0 đến 10.0. (Bạn nhập: " + text + ")");
                    return null;
                }
                scores[i] = val;
            } catch (NumberFormatException ex) {
                err("Điểm Môn " + (i+1) + " sai định dạng!\n"
                        + "Vui lòng nhập số thực, ví dụ: 8.5. (Bạn nhập: '" + text + "')");
                return null;
            }
        }
        return scores;
    }

    private double computeGpa(Double[] scores) {
        int count = 0;
        double sum = 0;
        for (Double s : scores) { if (s != null) { sum += s; count++; } }
        if (count < 6) {
            err("Không đủ số môn đăng ký!\n"
                    + "Một kỳ học phải có ít nhất 6 môn.\n"
                    + "Hiện tại bạn đã nhập: " + count + " môn.");
            return -1;
        }
        return sum / count;
    }


    // HELPERS
    private String xepLoai(double gpa) {
        if (gpa >= 9.0) return "Xuất sắc";
        if (gpa >= 8.0) return "Giỏi";
        if (gpa >= 6.5) return "Khá";
        if (gpa >= 4.0) return "Trung bình";
        return "Yếu";
    }

    private Object[] row18(String id, String name, String dept, String cls,
                           String uname, String upass, Double[] scores,
                           double gpa, String xl) {
        Object[] d = new Object[18];
        d[0]=id; d[1]=name; d[2]=dept; d[3]=cls;
        d[4]=uname; d[5]=upass;
        for (int i = 0; i < 10; i++) d[6+i] = scores[i];
        d[16] = gpa; d[17] = xl;
        return d;
    }

    private Object[] findById(String id) {
        for (Object[] d : allStudents)
            if (d[0].toString().equals(id)) return d;
        return null;
    }

    private void reloadTable(List<Object[]> data) {
        model.setRowCount(0);
        for (Object[] d : data)
            model.addRow(new Object[]{
                    d[0], d[1], d[2], d[4], d[5],
                    String.format("%.2f", (double) d[16]), d[17]
            });
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText(""); txtDept.setText("");
        txtClass.setText(""); txtPassword.setText("");
        for (JTextField f : txtScores) f.setText("");
        table.clearSelection();
    }

    // Tiện ích
    private static JButton makeBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(C_DARK);
        return l;
    }
    private static JTextField sf(JTextField f) {
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 26));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        return f;
    }
    private static JPanel row() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 3));
        p.setBackground(Color.WHITE);
        return p;
    }
    private void err(String m) { JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Cảnh báo",JOptionPane.WARNING_MESSAGE); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m, "Thông báo",JOptionPane.INFORMATION_MESSAGE); }
}