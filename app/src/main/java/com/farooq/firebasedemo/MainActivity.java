package com.farooq.firebasedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Firebase ref = new Firebase("<your-firebase-root-url>");
    //list that hold user objects
    private ArrayList<User> mUsers;
    private ListView listView;
    private Button add;
    private int dummyCount=1;//only for adding dummy data
    private CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        mUsers = new ArrayList<>();
        final Firebase users = ref.child("users");

        listView = (ListView) findViewById(R.id.listView);

        add = (Button) findViewById(R.id.add);

        adapter = new CustomAdapter(this,R.layout.list_view_item,mUsers);

        listView.setAdapter(adapter);

        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //to get push value, use dataSnapshot.getKey()
                User user = dataSnapshot.getValue(User.class);
                mUsers.add(user);

                //notify adapter so it can update views
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.setAbout("Dummy About # "+dummyCount);
                user.setFirstname("Dummy Firstname # " + dummyCount);
                user.setLastname("Dummy Lastname # " + dummyCount);
                user.setAbout("Dummy age # " + dummyCount);
                users.push().setValue(user, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        Toast.makeText(MainActivity.this,"User #"+dummyCount+" added",Toast.LENGTH_SHORT).show();
                        dummyCount+=1;
                    }
                });
            }
        });
    }






    class CustomAdapter extends ArrayAdapter<User>{
        ArrayList<User> users;
        Context context;
        public CustomAdapter(Context context, int resource, List<User> objects) {
            super(context, resource, objects);
            users = (ArrayList<User>) objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public User getItem(int position) {
            return users.get(position);
        }

        @Override
        public int getPosition(User item) {
            return users.indexOf(item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item,parent,false);

                holder.firstname = (TextView) convertView.findViewById(R.id.firstname);
                holder.lastname= (TextView) convertView.findViewById(R.id.lastname);
                holder.age= (TextView) convertView.findViewById(R.id.age);
                holder.about= (TextView) convertView.findViewById(R.id.about);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            User user = users.get(position);

            holder.firstname.setText(user.getFirstname());
            holder.lastname.setText(user.getLastname());
            holder.age.setText(user.getAge());
            holder.about.setText(user.getAbout());

            return convertView;
        }

        class ViewHolder{
            TextView firstname;
            TextView lastname;
            TextView about;
            TextView age;
        }
    }
}
