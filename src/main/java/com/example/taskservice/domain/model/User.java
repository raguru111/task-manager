package com.example.taskservice.domain.model;


public class User {
    private String transactionId;
    private String username;
    private String category;
    private String timeStamp;
    private Double amount;

    public User(String transactionId, String username, String category, String timeStamp, Double amount) {
        this.transactionId = transactionId;
        this.username = username;
        this.category = category;
        this.timeStamp = timeStamp;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "User{" +
                "transactionId='" + transactionId + '\'' +
                ", username='" + username + '\'' +
                ", category='" + category + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
