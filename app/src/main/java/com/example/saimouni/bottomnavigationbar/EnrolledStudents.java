package com.example.saimouni.bottomnavigationbar;

public class EnrolledStudents {

    String branch,htNo,name,section;

    public EnrolledStudents(String branch, String htNo, String name, String section) {
        this.branch = branch;
        this.htNo = htNo;
        this.name = name;
        this.section = section;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getHtNo() {
        return htNo;
    }

    public void setHtNo(String htNo) {
        this.htNo = htNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public EnrolledStudents() {
    }
}
