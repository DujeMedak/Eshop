package cmov.feup.eshop.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Duje on 25.10.2017..
 */
public class Order implements Parcelable{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    Product p;
    int quantity;

    public Order(Product p,int q){
        this.p = p;
        quantity = q;
    }

    public Order(Parcel in) {/*
        String[] data = new String[5];

        in.readStringArray(data);
        //name,description
        Product p = new Product(data[0],data[1], data[2],Double.parseDouble(data[3]));
        this.p = p;

        this.quantity = tryParseInt(data[4]);*/
        String[] data = new String[4];

        in.readStringArray(data);
        //name,description
        Product p = new Product("", data[0], data[1], Double.parseDouble(data[2]));
        this.p = p;

        this.quantity = tryParseInt(data[3]);

    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int q){
        quantity = q;
    }

    public Product getProduct(){
        return p;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.getProduct().getName(),
                this.getProduct().getProductDescription(),
                String.valueOf(this.getProduct().getPrice()),
                String.valueOf(this.getQuantity())
        });
    }

    int tryParseInt(String value) {
        int intValue = 1;
        try {
            intValue = Integer.parseInt(value);
            return intValue;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

}
