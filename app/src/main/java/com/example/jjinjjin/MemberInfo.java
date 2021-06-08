package com.example.jjinjjin;

import java.util.HashMap;
import java.util.Map;

public class MemberInfo {
    private String name;
    private String school;
//    private String ooe_code;
    private String schoolCode;
    private String eduCode;

    public MemberInfo(String name, String school, String schoolCode, String eduCode){
        this.name = name;
        this.school = school;
//        this.ooe_code = ooe_code;
        this.schoolCode = schoolCode;
        this.eduCode = eduCode;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
//    public String getOoe_code() {
//        return ooe_code;
//    }
//    public void setOoe_code(String ooe_code) {
//        this.ooe_code = ooe_code;
//    }
    public String getSchoolCode() {
        return schoolCode;
    }
    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }
    public String getEduCode() {
        return eduCode;
    }
    public void setEduCode(String eduCode) {
        this.eduCode = eduCode;
    }

    public Map<String, Object> getHash(){
        Map<String, Object> member = new HashMap<>();
        member.put("name", getName());
        member.put("school", getSchool());
        member.put("schoolcode", getSchoolCode());
        member.put("educode", getEduCode());
        return member;
    }
}
