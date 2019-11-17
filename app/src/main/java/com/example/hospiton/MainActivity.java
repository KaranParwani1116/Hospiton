package com.example.hospiton;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;
    private DatabaseReference Rootref;
    private GoogleSignInClient mGoogleSignInClient;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Button Testbutton;
    private RequestQueue requestQueue;
    private String url_imp="https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        Rootref=FirebaseDatabase.getInstance().getReference();
        Testbutton=(Button)findViewById(R.id.test) ;

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        requestQueue= Volley.newRequestQueue(this);

        Testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendnotification();
            }
        });

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.item1:
                        menuItem.setChecked(true);
                        //displayMessage("item 1");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.item2:
                        menuItem.setChecked(true);
                        //displayMessage("Item 2");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.item3:
                        menuItem.setChecked(true);
                        //displayMessage("Item 2");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.item4:
                        menuItem.setChecked(true);
                        //displayMessage("Item 2");
                        drawerLayout.closeDrawers();
                        return true;

                }
                return false;
            }
        });

        toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);


        //Taking Root Reference of database
        Rootref= FirebaseDatabase.getInstance().getReference();



    }

    private void sendnotification() {
        JSONObject mainobj=new JSONObject();
        try {
            mainobj.put("to","/topics/"+"news");
            JSONObject notification=new JSONObject();
            notification.put("title","any title");
            notification.put("body","any body");
            mainobj.put("notification",notification);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url_imp,
                    mainobj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header=new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AIzaSyDMYebxipz5x6KH_iSOe25G6TFz_O54FVo");
                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.options_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.signout)
        {
          firebaseAuth.signOut();
          mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(MainActivity.this,"Signed out successfully",Toast.LENGTH_SHORT).show();
              }
          });

          Intent intent=new Intent(MainActivity.this,LoginActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);

        }
        else if(id==R.id.profile)
        {
            Intent intent=new Intent(MainActivity.this,User_Profile.class);
            startActivity(intent);
        }
        else if(id==R.id.destination)
        {
            Intent intent=new Intent(MainActivity.this,Destination.class);
            startActivity(intent);
        }
        else if(id==R.id.Find_Friends)
        {
            Intent intent=new Intent(MainActivity.this,FriendsActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.camera)
        {
            Intent intent=new Intent(MainActivity.this,textrecog.class);
            startActivity(intent);
        }
        else if(id==R.id.contacts)
        {
            Intent intent=new Intent(MainActivity.this,Contacts_Fragment.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

           if(firebaseAuth.getCurrentUser()!=null)
           {
              if(firebaseAuth.getCurrentUser().isEmailVerified())
              {
                 verifyexistence();
              }
              else
              {
                  if(firebaseAuth.getCurrentUser().getPhoneNumber().isEmpty()) {
                      Log.d("TAG", "Else invoked");
                      Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                      startActivity(intent);
                  }else {
                        verifyexistence();
                  }
              }
           }
           else
           {
               Intent intent=new Intent(MainActivity.this,LoginActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }


    }

    private void verifyexistence() {
       String userid=firebaseAuth.getCurrentUser().getUid();
       Rootref.child(getString(R.string.Users)).child(userid).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child(getString(R.string.name)).exists())
               {
                 Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Intent intent=new Intent(MainActivity.this,User_Profile.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
