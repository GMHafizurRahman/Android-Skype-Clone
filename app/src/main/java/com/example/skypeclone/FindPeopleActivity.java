package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendList;
    private EditText searchET;
    private String str = "";
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        searchET = findViewById(R.id.search_user_text_Id);
        findFriendList = findViewById(R.id.find_friendsList_Id);

        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (searchET.getText().toString().equals("")) {
                    Toast.makeText(FindPeopleActivity.this, "Please input name to search..", Toast.LENGTH_SHORT).show();
                } else {
                    str = charSequence.toString();
                    onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable e) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = null;

        if (str.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(usersRef, Contacts.class)
                    .build();
        } else {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef
                                    .orderByChild("name")
                                    .startAt(str)
                                    .endAt(str + "\uf8ff")
                            , Contacts.class)
                    .build();
        }
        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final Contacts contacts) {
                        holder.userNameText.setText(contacts.getName());
                        Picasso.get().load(contacts.getImage()).into(holder.profileImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(position).getKey();

                                Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
                                intent.putExtra("visit_user_id", visit_user_id);
                                intent.putExtra("profile_image", contacts.getImage());
                                intent.putExtra("profile_name", contacts.getName());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design, parent, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);

                        return viewHolder;
                    }
                };

        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userNameText;
        Button videoCallBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameText = itemView.findViewById(R.id.name_contact_Id);
            videoCallBtn = itemView.findViewById(R.id.call_btn_Id);
            profileImageView = itemView.findViewById(R.id.image_contact_Id);
            cardView = itemView.findViewById(R.id.card_view_contact_id);

            videoCallBtn.setVisibility(View.GONE);
        }
    }

}