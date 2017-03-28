package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by ashishc on 06-03-2017.
 */

public class POSM_MASTERGetterSetter {

    String POSM_MASTER_table;
    ArrayList<String> POSM_CD = new ArrayList<String>();
    ArrayList<String> POSM = new ArrayList<String>();

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String quantity = "";
    String image = "";

    public String getPOSM_MASTER_table() {
        return POSM_MASTER_table;
    }

    public void setPOSM_MASTER_table(String POSM_MASTER_table) {
        this.POSM_MASTER_table = POSM_MASTER_table;
    }


    public ArrayList<String> getPOSM() {
        return POSM;
    }

    public void setPOSM(String POSM) {
        this.POSM.add(POSM);
    }

    public ArrayList<String> getPOSM_CD() {
        return POSM_CD;
    }

    public void setPOSM_CD(String POSM_CD) {
        this.POSM_CD.add(POSM_CD);
    }


}
