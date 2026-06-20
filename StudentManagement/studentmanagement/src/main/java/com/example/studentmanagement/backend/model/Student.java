package com.example.studentmanagement.backend.model;

public class Student {
    private String studentId;
    private String fullName;
    private String department;
    private String className;

    private Double score1;
    private Double score2;
    private Double score3;
    private Double score4;
    private Double score5;
    private Double score6;
    private Double score7;
    private Double score8;
    private Double score9;
    private Double score10;

    private double gpa;

    public Student() {}

    public Student(String studentId, String fullName, String department, String className, Double score1, Double score2, Double score3, Double score4, Double score5, Double score6, Double score7, Double score8, Double score9, Double score10, double gpa) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.className = className;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.score5 = score5;
        this.score6 = score6;
        this.score7 = score7;
        this.score8 = score8;
        this.score9 = score9;
        this.score10 = score10;
        this.gpa = gpa;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Double getScore1() { return score1; }
    public void setScore1(Double score1) { this.score1 = score1; }

    public Double getScore2() { return score2; }
    public void setScore2(Double score2) { this.score2 = score2; }

    public Double getScore3() { return score3; }
    public void setScore3(Double score3) { this.score3 = score3; }

    public Double getScore4() { return score4; }
    public void setScore4(Double score4) { this.score4 = score4; }

    public Double getScore5() { return score5; }
    public void setScore5(Double score5) { this.score5 = score5; }

    public Double getScore6() { return score6; }
    public void setScore6(Double score6) { this.score6 = score6; }

    public Double getScore7() { return score7; }
    public void setScore7(Double score7) { this.score7 = score7; }

    public Double getScore8() { return score8; }
    public void setScore8(Double score8) { this.score8 = score8; }

    public Double getScore9() { return score9; }
    public void setScore9(Double score9) { this.score9 = score9; }

    public Double getScore10() { return score10; }
    public void setScore10(Double score10) { this.score10 = score10; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
}