/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.data;

/**
 *
 * @author Tom
 */
public class RowData {

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

   
    private double priceWithoutTaxes;
    private String itemCode;
    private double numberOfItems;
    private double taxRate;

    public double getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(double numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public double getPriceWithoutTaxes() {
        return priceWithoutTaxes;
    }

    public void setPriceWithoutTaxes(double priceWithoutTaxes) {
        this.priceWithoutTaxes = priceWithoutTaxes;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
    
    public RowData(){
        
    }

    
}
