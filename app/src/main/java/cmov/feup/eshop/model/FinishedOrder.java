package cmov.feup.eshop.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Duje on 2.11.2017..
 */

public class FinishedOrder {
    String id;
    Date date;

    ArrayList<Order> orders;

    public FinishedOrder(String id, Date date, ArrayList<Order> orders) {
        this.id = id;
        this.date = date;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for(Order o : orders){
            totalPrice+= (o.getQuantity()*o.getProduct().getPrice());
        }
        return totalPrice;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public String getDateFormatted(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(date.getTime()));
    }
}
