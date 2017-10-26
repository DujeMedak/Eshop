package cmov.feup.eshop.model;

/**
 * Created by Duje on 25.10.2017..
 */

public class Order {
    Product p;
    int quantity;

    public Order(Product p,int q){
        this.p = p;
        quantity = q;
    }

    public int getQuantity(){
        return quantity;
    }

    public Product getProduct(){
        return p;
    }
}
