package com.example.ezbook;

public class Contact {
    private int Item_Id;
    private String Item_PID;
    private String Item_Name;
    private String Item_Price_Each;
    private String Item_Price;
    private String Item_Quantity;

    public Contact(int item_Id, String item_PID, String item_Name, String item_Price_Each, String item_Price, String item_Quantity) {
        this.Item_Id = item_Id;
        this.Item_PID = item_PID;
        this.Item_Name = item_Name;
        this.Item_Price_Each = item_Price_Each;
        this.Item_Price = item_Price;
        this.Item_Quantity = item_Quantity;
    }

    public int getItem_Id() {
        return Item_Id;
    }

    public void setItem_Id(int item_Id) {
        Item_Id = item_Id;
    }

    public String getItem_PID() {
        return Item_PID;
    }

    public void setItem_PID(String item_PID) {
        Item_PID = item_PID;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getItem_Price_Each() {
        return Item_Price_Each;
    }

    public void setItem_Price_Each(String item_Price_Each) {
        Item_Price_Each = item_Price_Each;
    }

    public String getItem_Price() {
        return Item_Price;
    }

    public void setItem_Price(String item_Price) {
        Item_Price = item_Price;
    }

    public String getItem_Quantity() {
        return Item_Quantity;
    }

    public void setItem_Quantity(String item_Quantity) {
        Item_Quantity = item_Quantity;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "Item_Id='" + Item_Id + '\'' +
                ", Item_PID='" + Item_PID + '\'' +
                ", Item_Name='" + Item_Name + '\'' +
                ", Item_Price_Each='" + Item_Price_Each + '\'' +
                ", Item_Price='" + Item_Price + '\'' +
                ", Item_Quantity='" + Item_Quantity + '\'' +
                '}';
    }
}
