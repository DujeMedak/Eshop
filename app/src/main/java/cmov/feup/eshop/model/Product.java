package cmov.feup.eshop.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Duje on 25.10.2017..
 */

//TODO if needed you can add more member variables

public class Product implements Parcelable {

    String description;
    String name;
    double price;

    public String getName() {
        return name;
    }

    public double getPrice(){
        return price;
    }

    public String getProductDescription(){
        return this.description;
    }

    public Product(String name,String description, double price){
        this.name = name;
        this.description = description;
        this.price = price;
    }


    protected Product(Parcel in) {
        description = in.readString();
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}