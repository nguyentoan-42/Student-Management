package com.example.studentmanagement.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // API Đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username, password);
        } catch (Exception e) {
            return "FAIL";
        }
    }

    // API Kiểm tra tài khoản / mã SV đã tồn tại chưa
    @GetMapping("/check-id/{id}")
    public String checkId(@PathVariable String id) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return (count != null && count > 0) ? "EXISTS" : "OK";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    // API Xem toàn bộ sinh viên (Admin)
    // Định dạng mỗi dòng: id|password|name|dept|class|s1..s10|gpa
    @GetMapping("/students")
    public String getAllStudents() {
        String sql = "SELECT s.*, u.password FROM students s JOIN users u ON s.student_id = u.username";
        List<String> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(rs.getString("student_id")).append("|");
            sb.append(rs.getString("password")).append("|");
            sb.append(rs.getString("full_name")).append("|");
            sb.append(rs.getString("department")).append("|");
            sb.append(rs.getString("class_name")).append("|");
            for (int i = 1; i <= 10; i++) {
                Object score = rs.getObject("score_" + i);
                sb.append(score == null ? "null" : score.toString()).append("|");
            }
            sb.append(rs.getDouble("gpa"));
            return sb.toString();
        });
        return String.join("\n", list);
    }

    // API Xem thông tin sinh viên (Student)
    // Định dạng: id|password|name|dept|class|s1..s10|gpa
    @GetMapping("/students/{id}")
    public String getStudentById(@PathVariable String id) {
        String sql = "SELECT s.*, u.password FROM students s JOIN users u ON s.student_id = u.username WHERE s.student_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(rs.getString("student_id")).append("|");
                sb.append(rs.getString("password")).append("|");
                sb.append(rs.getString("full_name")).append("|");
                sb.append(rs.getString("department")).append("|");
                sb.append(rs.getString("class_name")).append("|");
                for (int i = 1; i <= 10; i++) {
                    Object score = rs.getObject("score_" + i);
                    sb.append(score == null ? "null" : score.toString()).append("|");
                }
                sb.append(rs.getDouble("gpa"));
                return sb.toString();
            }, id);
        } catch (Exception e) {
            return "ERROR";
        }
    }

    // API Thêm sinh viên (Admin)
    @PostMapping("/students")
    public String addStudent(
            @RequestParam String studentId,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String department,
            @RequestParam String className,
            @RequestParam String gpa,
            @RequestParam(required = false) String s1,
            @RequestParam(required = false) String s2,
            @RequestParam(required = false) String s3,
            @RequestParam(required = false) String s4,
            @RequestParam(required = false) String s5,
            @RequestParam(required = false) String s6,
            @RequestParam(required = false) String s7,
            @RequestParam(required = false) String s8,
            @RequestParam(required = false) String s9,
            @RequestParam(required = false) String s10) {

        try {
            // Kiểm tra tài khoản trùng lặp lần cuối tại tầng server
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, studentId);
            if (count != null && count > 0) {
                return "ERROR: Mã sinh viên / Tài khoản '" + studentId + "' đã tồn tại.";
            }

            String sqlUser = "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')";
            jdbcTemplate.update(sqlUser, studentId, password);

            String sqlStudent = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlStudent, studentId, fullName, department, className, parse(s1), parse(s2), parse(s3), parse(s4), parse(s5), parse(s6), parse(s7), parse(s8), parse(s9), parse(s10), Double.parseDouble(gpa));
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // API Sửa sinh viên (Admin)
    @PutMapping("/students/{id}")
    public String updateStudent(
            @PathVariable String id,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String department,
            @RequestParam String className,
            @RequestParam String gpa,
            @RequestParam(required = false) String s1,
            @RequestParam(required = false) String s2,
            @RequestParam(required = false) String s3,
            @RequestParam(required = false) String s4,
            @RequestParam(required = false) String s5,
            @RequestParam(required = false) String s6,
            @RequestParam(required = false) String s7,
            @RequestParam(required = false) String s8,
            @RequestParam(required = false) String s9,
            @RequestParam(required = false) String s10) {

        try {
            String sqlUser = "UPDATE users SET password = ? WHERE username = ?";
            jdbcTemplate.update(sqlUser, password, id);

            String sqlStudent = "UPDATE students SET full_name=?, department=?, class_name=?, " +
                    "score_1=?, score_2=?, score_3=?, score_4=?, score_5=?, " +
                    "score_6=?, score_7=?, score_8=?, score_9=?, score_10=?, gpa=? " +
                    "WHERE student_id=?";
            jdbcTemplate.update(sqlStudent, fullName, department, className, parse(s1), parse(s2), parse(s3), parse(s4), parse(s5), parse(s6), parse(s7), parse(s8), parse(s9), parse(s10), Double.parseDouble(gpa), id);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // API Xóa sinh viên (Admin)
    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable String id) {
        try {
            jdbcTemplate.update("DELETE FROM students WHERE student_id = ?", id);
            jdbcTemplate.update("DELETE FROM users WHERE username = ?", id);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // Helper
    private Double parse(String val) {
        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val)) return null;
        try { return Double.parseDouble(val.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}