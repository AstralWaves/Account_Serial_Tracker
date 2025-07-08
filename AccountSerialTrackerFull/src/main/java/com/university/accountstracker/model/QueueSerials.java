package com.university.accountstracker.model;

import jakarta.persistence.*;

@Entity
public class QueueSerials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int tuitionFeeSerial;
    private int hallFeeSerial;
    private int clearanceSerial;

    public enum QueueType {
        TUITION_FEE,
        HALL_FEE,
        CLEARANCE
    }

    public QueueSerials() {}

    public QueueSerials(int tuitionFeeSerial, int hallFeeSerial, int clearanceSerial) {
        this.tuitionFeeSerial = tuitionFeeSerial;
        this.hallFeeSerial = hallFeeSerial;
        this.clearanceSerial = clearanceSerial;
    }

    public int getTuitionFeeSerial() { return tuitionFeeSerial; }
    public void setTuitionFeeSerial(int tuitionFeeSerial) { this.tuitionFeeSerial = tuitionFeeSerial; }
    public int getHallFeeSerial() { return hallFeeSerial; }
    public void setHallFeeSerial(int hallFeeSerial) { this.hallFeeSerial = hallFeeSerial; }
    public int getClearanceSerial() { return clearanceSerial; }
    public void setClearanceSerial(int clearanceSerial) { this.clearanceSerial = clearanceSerial; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String toString() {
        return "QueueSerials{" + "tuitionFeeSerial=" + tuitionFeeSerial +
               ", hallFeeSerial=" + hallFeeSerial + ", clearanceSerial=" + clearanceSerial + '}';
    }
}
