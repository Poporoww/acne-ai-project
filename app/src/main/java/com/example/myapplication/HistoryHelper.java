package com.example.myapplication;

public class HistoryHelper {

    String uriPic, labels, advice;

    public String getUriPic() {
        return uriPic;
    }

    public void setUriPic(String uriPic) {
        this.uriPic = uriPic;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.uriPic = advice;
    }

    public HistoryHelper(String uriPic, String labels, String advice) {
        this.uriPic = uriPic;
        this.labels = labels;
        this.advice = advice;
    }

}
