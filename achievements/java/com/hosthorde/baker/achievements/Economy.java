package com.hosthorde.baker.achievements;

public class Economy {
	private int quantity;
    
    public Economy(int seedAmount) {
      quantity = seedAmount;
    }
    
    public int getQuantity() {
      return quantity;
    }
    
    public void setQuantity(int newQuantity) {
      quantity = newQuantity;
    }
    
    public void buyAtCost(int cost) {
      if (quantity >= cost) {
      	quantity = quantity - cost;
      } else {
//        System.out.println("You only have " + quantity + "! You can't afford this item!");
      }
    }
    
    public void sellAtPrice(int price) {
      quantity = quantity + price;
    }
    
    public void reward (int reward) {
    	quantity = quantity + reward;
    }
    
    public void printQuantity() {
//      System.out.println(quantity);
    }
    
    public void givePayment(int payment, Economy other) {
      if (quantity >= payment) {
      	quantity = quantity - payment;
     		other.setQuantity(other.getQuantity() + payment);
      } else {
//        System.out.println("You only have " + quantity + "! You can't afford to be this generous!");
      }
    }
}
