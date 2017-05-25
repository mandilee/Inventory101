package uk.co.mandilee.inventory101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    // Database version.
    public static final int DATABASE_VERSION = 1;
    // Database name.
    public static final String DATABASE_NAME = "inventory",
            TABLE_ITEMS = "items",
            TAG = DbHelper.class.getSimpleName(),
            COLUMN_ITEM_ID = "_id",
            COLUMN_NAME = "name",
            COLUMN_STOCK = "stock",
            COLUMN_PRICE = "price",
            COLUMN_DESCRIPTION = "description",
            COLUMN_IMAGE_PATH = "image_path";
    // Database lock to prevent conflicts.
    public static final Object[] databaseLock = new Object[0];
    // Command to create a table of clients.
    private static final String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEMS + " ("
            + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_IMAGE_PATH + " TEXT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_STOCK + " TEXT, "
            + COLUMN_PRICE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT)";
    // Context in which this database exists.
    private static Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    private static Item getItem(Cursor cursor) {
        int indexId = cursor.getColumnIndex(COLUMN_ITEM_ID),
                indexName = cursor.getColumnIndex(COLUMN_NAME),
                indexStock = cursor.getColumnIndex(COLUMN_STOCK),
                indexPrice = cursor.getColumnIndex(COLUMN_PRICE),
                indexDescription = cursor.getColumnIndex(COLUMN_DESCRIPTION),
                indexImagePath = cursor.getColumnIndex(COLUMN_IMAGE_PATH);

        Item item = new Item();
        item.setId(cursor.getInt(indexId));
        item.setName(cursor.getString(indexName));
        item.setStock(cursor.getInt(indexStock));
        item.setPrice(cursor.getFloat(indexPrice));
        item.setDescription(cursor.getString(indexDescription));
        item.setImagePath(cursor.getString(indexImagePath));
        return item;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

    }

    public List<Item> getAllItems() {
        //Initialize an empty list of Items
        List<Item> itemList = new ArrayList<Item>();

        //Command to select all Items
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        //lock database for reading
        synchronized (databaseLock) {
            //Get a readable database
            SQLiteDatabase database = getReadableDatabase();

            //Make sure database is not empty
            if (database != null) {

                //Get a cursor for all Items in the database
                Cursor cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        Item item = getItem(cursor);
                        itemList.add(item);
                        cursor.moveToNext();
                    }
                }
                //Close the database connection
                database.close();
            }
            //Return the list of items
            return itemList;
        }

    }

    public Long addItem(Item item) {
        Long ret = null;

        //Lock database for writing
        synchronized (databaseLock) {
            //Get a writable database
            SQLiteDatabase database = getWritableDatabase();

            //Ensure the database exists
            if (database != null) {
                //Prepare the item information that will be saved to the database
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, item.getName());
                values.put(COLUMN_STOCK, item.getStock());
                values.put(COLUMN_PRICE, item.getPrice());
                values.put(COLUMN_DESCRIPTION, item.getDescription());
                values.put(COLUMN_IMAGE_PATH, item.getImagePath());

                //Attempt to insert the client information into the transaction table
                try {
                    ret = database.insert(TABLE_ITEMS, null, values);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to add Item to database " + e.getMessage());
                }
                //Close database connection
                database.close();
            }
        }
        return ret;
    }

    public Item getItemById(long id) {
        List<Item> tempItemList = getAllItems();
        for (Item item : tempItemList) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public boolean itemExists(long id) {
        //Check if there is an existing item
        List<Item> tempItemList = getAllItems();
        for (Item item : tempItemList) {
            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }


}
