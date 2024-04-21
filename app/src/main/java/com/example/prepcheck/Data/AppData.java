package com.example.prepcheck.Data;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.prepcheck.Constants.MyConstants;
import com.example.prepcheck.DataBase.RoomDB;
import com.example.prepcheck.Models.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends Application {

    RoomDB database;
    String category;
    Context context;

    public static final String LAST_VERSION = "LAST_VERSION";
    public static final int NEW_VERSION = 1;

    public AppData(RoomDB database) {
        this.database = database;
    }

    public AppData(RoomDB database, Context context) {
        this.database = database;
        this.context = context;
    }

    public List<Items> getBasicData(){
        category = "Basic Needs";
        List<Items> basicItem = new ArrayList<>();
        basicItem.add(new Items("Visa", category,false));
        basicItem.add(new Items("Passport", category,false));
        basicItem.add(new Items("Tickets", category,false));
        basicItem.add(new Items("Wallet", category,false));
        basicItem.add(new Items("Driving License", category,false));
        basicItem.add(new Items("Umbrella", category,false));
        basicItem.add(new Items("Handkerchief", category,false));
        basicItem.add(new Items("House Keys", category,false));

        return basicItem;
    }

    public List<Items> getPersonalCareData(){
        String[] data = {"Tooth-Brush","Tooth-Paste","MouthWash","Floss","Brush","Perfume",
                "Moisturizer","Body Wash/Soap","Hand Sanitizer","Towel"};
        return prepareItemsList(MyConstants.PERSONAL_CARE_CAMEL_CASE, data);
    }

    public List<Items> getClothingData(){
        String[] data = {"Undergarments","Pajamas","T-Shirts","Shorts","Jackets","Sports Wear"
                ,"Swimwear","Sleepwear","Rain Gear","Slippers","Shoes","Belts"};
        return prepareItemsList(MyConstants.CLOTHING_CAMEL_CASE,data);
    }

    public List<Items> getBabyNeedsData(){
        String[] data = {"Outfit","Jumpsuit","Baby Socks","Baby Pyjamas","Bath Towel","Bottles"
                ,"Diaper","Water Bottle","Baby food spoon","Baby Shampoo","Baby Soap",
        "Wet Wipes","Moisturizer"};
        return prepareItemsList(MyConstants.BABY_NEEDS_CAMEL_CASE,data);
    }

    public List<Items> getHealthData(){
        String[] data ={"Aspirin","Drugs Used","Vitamins Used","Condoms","Lens Solutions","Hot Water Bag"
        ,"First Aid Kit","Replacement Lens","Pain Reliever","Pain Relieve Spray"};
        return prepareItemsList(MyConstants.HEALTH_CAMEL_CASE,data);
    }

    public List<Items> getTechnologyData(){
        String[] data = {"Mobile Phone","Phone Charger","Phone Cover","Camera","Camera Charger","Portable Speaker"
        ,"Headphones","Laptop","Laptop Charger","Extension Cable","Power Bank","Flash-Light","External Hard Disk"};
        return prepareItemsList(MyConstants.TECHNOLOGY_CAMEL_CASE,data);
    }

    public List<Items> getFoodData(){
        String[] data = {"Fruits","Snacks","Sandwich","Juice","Tea Bags","Water","Chips","Baby Food"
                ,"Energy Bars"};
        return prepareItemsList(MyConstants.FOOD_CAMEL_CASE,data);
    }

    public List<Items> getBeachSuppliesData(){
        String[] data = {"Sea Glasses","Sea Bed","Suntan cream","Beach Umbrella","Beach Towel"
                ,"Beach Slippers","Waterproof clock"};
        return prepareItemsList(MyConstants.BEACH_SUPPLIES_CAMEL_CASE,data);
    }

    public List<Items> getCarSuppliesData(){
        String[] data = {"Pump","Air Compressor","Car Jack","Spare Car Keys","Spare Tire","Car Cover","Car Charger"
                ,"Window Sun Shades","Travel Pillows","Vehicle Documents"};
        return prepareItemsList(MyConstants.CAR_SUPPLIES_CAMEL_CASE,data);
    }

    public List<Items> getNeedsData(){
        String[] data ={"BackPack","Daily Bags","Laundry Bags","Travel lock","Luggage Tag","Important Numbers"};
        return prepareItemsList(MyConstants.NEEDS_CAMEL_CASE,data);
    }

    public List<Items> prepareItemsList(String category, String[] data){
        List<String> list = Arrays.asList(data);
        List<Items> dataList = new ArrayList<>();
        dataList.clear();

        for(int i=0;i<list.size();i++){
            dataList.add(new Items(list.get(i),category,false));
        }
        return dataList;
    }


    public List<List<Items>> getAllData(){
        List<List<Items>> listOfAllItems = new ArrayList<>();
        listOfAllItems.clear();

        listOfAllItems.add(getBasicData());
        listOfAllItems.add(getClothingData());
        listOfAllItems.add(getPersonalCareData());
        listOfAllItems.add(getBabyNeedsData());
        listOfAllItems.add(getHealthData());
        listOfAllItems.add(getTechnologyData());
        listOfAllItems.add(getFoodData());
        listOfAllItems.add(getBeachSuppliesData());
        listOfAllItems.add(getCarSuppliesData());
        listOfAllItems.add(getNeedsData());

        return listOfAllItems;

    }

    public void persistAllData(){
        List<List<Items>> listOfAllItems = getAllData();
        for(List<Items> list: listOfAllItems){
            for(Items items:list){
                database.mainDao().saveItem(items);
            }
        }
        System.out.println("Data Added.");
    }


    public void persistDataByCategory(String category, Boolean onlyDelete){

        List<Items> list = deleteAllGetListByCategory(category, onlyDelete);
        try{
            if(!onlyDelete){
                for(Items item: list){
                    database.mainDao().saveItem(item);
                }
                Toast.makeText(context, category+" Reset Successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, category+" Reset Successfully", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }



    private List<Items> deleteAllGetListByCategory(String category, Boolean onlyDelete){
        if(onlyDelete){
            database.mainDao().deleteAllCategoryAndAddedBy(category,MyConstants.SYSTEM_SMALL);
        }else{
            database.mainDao().deleteAllByCategory(category);
        }

        switch (category){
            case MyConstants.BASIC_NEEDS_CAMEL_CASE:
                return getBasicData();

            case MyConstants.CLOTHING_CAMEL_CASE:
                return getClothingData();

            case MyConstants.PERSONAL_CARE_CAMEL_CASE:
                return getPersonalCareData();

            case MyConstants.BABY_NEEDS_CAMEL_CASE:
                return getBabyNeedsData();

            case MyConstants.HEALTH_CAMEL_CASE:
                return getHealthData();

            case MyConstants.TECHNOLOGY_CAMEL_CASE:
                return getTechnologyData();

            case MyConstants.FOOD_CAMEL_CASE:
                return getFoodData();

            case MyConstants.BEACH_SUPPLIES_CAMEL_CASE:
                return getBeachSuppliesData();

            case MyConstants.CAR_SUPPLIES_CAMEL_CASE:
                return getCarSuppliesData();

            case MyConstants.NEEDS_CAMEL_CASE:
                return getNeedsData();

            default:
                return new ArrayList<>();
        }
    }





}
