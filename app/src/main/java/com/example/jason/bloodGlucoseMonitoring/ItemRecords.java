package com.example.jason.bloodGlucoseMonitoring;


class ItemRecords {
    private int id;
    private float measurementSugar;
    private long timeInSeconds;
    private String comment;

    ItemRecords(int id, float measurementSugar, long timeInSeconds, String comment) {
        this.id = id;
        this.measurementSugar = measurementSugar;
        this.timeInSeconds = timeInSeconds;
        this.comment = comment;
    }

    // Getters
    public int getId() {
        return id;
    }

    float getMeasurementSugar() {
        return measurementSugar;
    }

    /*float getMeasurementSugarMg() {
        return measurementSugar * 18;
    }*/

    /*public long getTimeInSeconds() {
        return timeInSeconds;
    }*/

    long getTimeInMillis() {
        return timeInSeconds * 1000;
    }

    /*public String getComment() {
        return comment;
    }*/

    /*
    // Setters
    public void setRecordNo() {
        this.measurementSugar = measurementSugar;
    }

    public void setPlayerName() {
        this.timeInSeconds = timeInSeconds;
    }

    public void setStepsCount() {
        this.comment = comment;
    }
    */
}