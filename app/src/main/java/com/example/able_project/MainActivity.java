package com.example.able_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //
    ListView l1;
    ArrayList<String> arr = new ArrayList<String>();
    //    int[] arr;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnShowContacts = (Button) findViewById(R.id.button_Sync);
        l1 = (ListView) findViewById(R.id.list);
        fetchContacts(btnShowContacts);

        btnShowContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestContactPermission
                fetchContacts(v);
                getPermission(v);
                writeData();
                readData();

            }
        });
    }

    private void getContacts() {
        //TODO get contacts code here
        Toast.makeText(this, "Get contacts ....", Toast.LENGTH_LONG).show();
    }

    public void requestContactPermission(View v) {
        getPermission(v);
    }

    public void getPermission(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
                get(v);
            }
        } else {
            getContacts();
            get(v);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    public void get(View v) {

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);


        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};


        int[] to = {android.R.id.text1, android.R.id.text2};
//        arr = to;
//        Toast.makeText(this, Arrays.toString(arr), Toast.LENGTH_LONG).show();
//        TextView textView = findViewById(R.id.tv1);
//        textView.setText(arr.get(1));

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        l1.setAdapter(simpleCursorAdapter);
        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    private void writeData() {
        //firebase write call
        database = FirebaseDatabase.getInstance(
                "https://able-project-eba4e-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("users");

        for (int i = 0; i < arr.size(); i++) {
            try {

                String string = arr.get(i);
                String[] parts = string.split(",");
                String part1 = parts[0];
                String part2 = parts[1];
                String nu = Integer.toString(i);
                User userObj = new User(part1, part2);

                database.child(nu).setValue(userObj);
            } catch (Exception e) {

            }

        }
    }

    private void readData() {
        database = FirebaseDatabase.getInstance(
                "https://able-project-eba4e-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference();
        // Read from the database

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView tv = findViewById(R.id.test);
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Log.d(TAG, "" + ds.getKey());
                    Toast.makeText(MainActivity.this, "" + tv.getText().toString(), Toast.LENGTH_SHORT).show();

                }
                // String f = dataSnapshot.getValue(String.class);
                // String N = dataSnapshot.child("0").child("userName").getClass().toString();
                // DataSnapshot dataSnap = f.result;
                // NOTE: if you've multiple childs of same node then you can use Map to display all of them : EG:
                //Map<String, Map<String,String>> map = dataSnapshot.getValue(Map.class);
                //String val1 = map.get(0).toString();
                //String val2 = map.get(1).toString();
                //String val3 = map.get(2).toString();
                //Log.v("TAG", "Value 1 " + val1);
                //Log.v("TAG", "Value 2 " + val2);
                //Log.v("TAG", "Value 3 " + val3);
                // Log.d(TAG, "Value is: " + f);
                //Toast.makeText(MainActivity.this, val1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    @SuppressLint("Range")
    public void fetchContacts(View v) {
        arr.clear();
        getPermission(v);
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                output = new StringBuffer();  //re initialize your StringBuffer again

                @SuppressLint("Range") String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    output.append(name + ",");


                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append(phoneNumber);
                    }
                    phoneCursor.close();
                }
//                output.append("\n");
                arr.add(output.toString());  //add contact here in your array list.
            }

        }


    }

    // for uploading data
    private void getContact(String DisplayName , String MobileNumber){

        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();
        ops.add(ContentProviderOperation.newInsert(

                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}