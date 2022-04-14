package com.iam2kabhishek.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import java.util.ArrayList;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname", c.getContactName());
            initialValues.put("streetaddress", c.getStreetAddress());
            initialValues.put("city", c.getCity());
            initialValues.put("state", c.getState());
            initialValues.put("zipcode", c.getZipcode());
            initialValues.put("phonenumber", c.getPhoneNumber());
            initialValues.put("cellnumber", c.getCellNumber());
            initialValues.put("email", c.getMail());
            initialValues.put("birthday", String.valueOf(c.getBirthday().toMillis(false)));

            didSucceed = database.insert("contact", null, initialValues) > 0;
        } catch (Exception e) {

        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            Long rowId = Long.valueOf(c.getContactID());
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname", c.getContactName());
            updateValues.put("streetaddress", c.getStreetAddress());
            updateValues.put("city", c.getCity());
            updateValues.put("state", c.getState());
            updateValues.put("zipcode", c.getZipcode());
            updateValues.put("phonenumber", c.getPhoneNumber());
            updateValues.put("cellnumber", c.getCellNumber());
            updateValues.put("email", c.getMail());
            updateValues.put("birthday", String.valueOf(c.getBirthday().toMillis(false)));

            didSucceed = database.update("contact", updateValues, "_id=" + rowId, null) > 0;
        } catch (Exception e) {

        }
        return didSucceed;
    }

    public int getLastContactId() {
        int lastId = -1;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    public ArrayList<Contact> getContacts(String sortField, String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            String query = "SELECT * FROM contact ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);
            Contact curContact;
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                curContact = new Contact();
                curContact.setContactID(cursor.getInt(0));
                curContact.setContactName(cursor.getString(1));
                curContact.setStreetAddress(cursor.getString(2));
                curContact.setCity(cursor.getString(3));
                curContact.setState(cursor.getString(4));
                curContact.setZipcode(cursor.getString(5));
                curContact.setPhoneNumber(cursor.getString(6));
                curContact.setCellNumber(cursor.getString(7));
                curContact.setMail(cursor.getString(8));
                Time t = new Time();
                t.set(Long.valueOf(cursor.getString(9)));
                curContact.setBirthday(t);

                contacts.add(curContact);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            contacts = new ArrayList<>();
        }
        return contacts;
    }

    public boolean deleteContact(int contactId) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("contact", "_id=" + contactId, null) > 0;
        } catch (Exception e) {

        }
        return didDelete;
    }

    public Contact getSpecificContact(int contactId) {
        Contact contact = new Contact();
        String query = "SELECT * FROM contact WHERE _id =" + contactId;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipcode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.setMail(cursor.getString(8));
            Time t = new Time();
            t.set(Long.valueOf(cursor.getString(9)));
            contact.setBirthday(t);
            cursor.close();
        }
        return contact;
    }
}