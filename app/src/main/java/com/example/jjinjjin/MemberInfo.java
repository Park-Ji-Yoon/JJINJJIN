package com.example.jjinjjin;

public class MemberInfo {
    // 변수 선언 (이름, 교육청, 학교, 학년, 반)
    private String name;
    private String ooe;
    private String school;
    private int grade;
    private int classNum;

    public MemberInfo(String name, String ooe, String school, int grade, int classNum){
        this.name = name;
        this.ooe = ooe;
        this.school = school;
        this.grade = grade;
        this.classNum = classNum;
    }

    // setter, getter
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getOoe(){
        return this.ooe;
    }
    public void setOoe(String ooe){
        this.ooe = ooe;
    }
    public String getSchool(){
        return this.school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
    public int getGrade(){
        return this.grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public int getClassNum(){
        return this.classNum;
    }
    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }
}
