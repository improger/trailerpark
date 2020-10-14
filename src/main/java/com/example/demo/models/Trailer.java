package com.example.demo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Trailer {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String trl;
    private String inTrc;
    private String inName;
    private String inLname;
    private String outTrc;
    private String outName;
    private String outLname;
    private String department;
    private Date inDate, outDate;
    private String operatorIn;
    private String operatorOut;

    private String inDepartment;
    private String outDepartment;



    private String status;
    private String repair;



//Getters and Setters

    public String getOperatorIn() {
        return operatorIn;
    }

    public void setOperatorIn(String operatorIn) {
        this.operatorIn = operatorIn;
    }

    public String getOperatorOut() {
        return operatorOut;
    }

    public void setOperatorOut(String operatorOut) {
        this.operatorOut = operatorOut;
    }

    public String getInDepartment() {
        return inDepartment;
    }

    public void setInDepartment(String inDepartment) {
        this.inDepartment = inDepartment;
    }

    public String getOutDepartment() {
        return outDepartment;
    }

    public void setOutDepartment(String outDepartment) {
        this.outDepartment = outDepartment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }


   public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrl() {
        return trl;
    }

    public void setTrl(String trl) {
        this.trl = trl;
    }

    public String getInTrc() {
        return inTrc;
    }

    public void setInTrc(String inTrc) {
        this.inTrc = inTrc;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getInLname() {
        return inLname;
    }

    public void setInLname(String inLname) {
        this.inLname = inLname;
    }

    public String getOutTrc() {
        return outTrc;
    }

    public void setOutTrc(String outTrc) {

        this.outTrc = outTrc;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public String getOutLname() {
        return outLname;
    }

    public void setOutLname(String outLname) {
        this.outLname = outLname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }


    // Constructors

    //trailer Arriva
    public Trailer(String trl, String inTrc, String inName, String inLname, String inDepartment, Date inDate, String operatorIn, String status) {
        this.trl = trl;
        this.inTrc = inTrc;
        this.inName = inName;
        this.inLname = inLname;
        this.department = department;
        this.inDate = inDate;
        this.operatorIn = operatorIn;
        this.inDepartment = inDepartment;
        this.status = status;
        this.repair = repair;

    }

    //Trailer departure
    public Trailer(Long id, java.sql.Date outDate, String outTruck, String outName, String outLname, String outDepartment, String operatorOut) {
        this.id = id;
        this.outDate = outDate;
        this.outTrc = outTruck;
        this.outName = outName;
        this.outLname = outLname;
        this.department = department;
        this.outDepartment = outDepartment;
        this.operatorOut = operatorOut;
    }

    public Trailer() {
    }
}
