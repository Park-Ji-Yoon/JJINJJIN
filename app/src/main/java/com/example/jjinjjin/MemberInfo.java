package com.example.jjinjjin;

import java.util.HashMap;
import java.util.Map;

public class MemberInfo {
    private String name;
    private String school;
    private String schoolCode;
    private String eduCode;
    private String city;
    private String sigungu;

    public MemberInfo(String name, String school, String schoolCode, String eduCode, String city, String sigungu){
        this.name = name;
        this.school = school;
        this.schoolCode = schoolCode;
        this.eduCode = eduCode;
        this.city = city;
        this.sigungu = sigungu;
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
    public String getCity() { return city;}
    public void setCity(String city) { this.city = city; }
    public String getSigungu() { return sigungu; }
    public void setSigungu(String sigungu) { this.sigungu = sigungu; }


    public Map<String, Object> getHash(){
        Map<String, Object> member = new HashMap<>();
        member.put("name", getName());
        member.put("school", getSchool());
        member.put("schoolcode", getSchoolCode());
        member.put("educode", getEduCode());
        member.put("city",getCity());
        member.put("sigungu", getSigungu());
        return member;
    }
}
