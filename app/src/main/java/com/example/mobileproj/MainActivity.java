package com.example.mobileproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,NotesRecyclerAdapter.NoteListener{

    RecyclerView recyclerView;

    NotesRecyclerAdapter notesadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog(){
        final EditText noteEdit = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(noteEdit)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addNote(noteEdit.getText().toString());
                        Log.d("TAG","onClick: "+noteEdit.getText());
                    }
                }).setNegativeButton("Cancel",null)
                  .show();
    }

    private void addNote(String t){

        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        note n = new note(t,false,new Timestamp(new Date()),Uid);

        FirebaseFirestore.getInstance()
                .collection("notes")
                .add(n)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this,"Note Added.",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startHome(){
        Intent intent = new Intent(this,homepage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item2:
                Toast.makeText(this,"1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                AuthUI.getInstance().signOut(this);

                return true;
            default:
                return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            startHome();
            return;
        }

        initRecyclerView(firebaseAuth.getCurrentUser());
    }

    private void initRecyclerView(FirebaseUser user){

        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userId",user.getUid());

        FirestoreRecyclerOptions<note> options = new FirestoreRecyclerOptions.Builder<note>()
                .setQuery(query,note.class)
                .build();

        notesadapter = new NotesRecyclerAdapter(options,this);
        recyclerView.setAdapter(notesadapter);

        notesadapter.startListening();

    }


    @Override
    public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot) {
        snapshot.getReference().update("completed",isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("s","onSuccess:");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("s","onFailure:"+e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void handleEditNote(final DocumentSnapshot snapshot) {

        final note note = snapshot.toObject(note.class);
        final EditText editText = new EditText(this);
        editText.setText(note.getText());
        editText.setSelection((note.getText().length()));
        new AlertDialog.Builder(this)
                .setTitle("Edit Note")
                .setView(editText)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String  newText = editText.getText().toString();
                        note.setText(newText);
                        snapshot.getReference().set(note);
                    }
                }).setNegativeButton("Cancel",null)
                .show();
    }
}
