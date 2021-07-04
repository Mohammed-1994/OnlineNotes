package com.example.onlinenotes.activity.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinenotes.R;
import com.example.onlinenotes.api.ApiClient;
import com.example.onlinenotes.api.ApiInterface;
import com.example.onlinenotes.model.Note;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditorActivity extends AppCompatActivity implements EditorView {

    private static final String TAG = "EditorActivity myTag";
    EditText et_title, et_note;
    ProgressDialog progressDialog;
    SpectrumPalette palette;
    EditorPresenter presenter;
    int color, id;
    String title, note;
    Menu actionMenu;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        et_title = findViewById(R.id.title);
        et_note = findViewById(R.id.note);
        palette = findViewById(R.id.palette);
        palette.setOnColorSelectedListener(color ->
                this.color = color
        );


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");

        id = getIntent().getIntExtra("id", 0);
        color = getIntent().getIntExtra("color", 0);
        title = getIntent().getStringExtra("title");
        note = getIntent().getStringExtra("note");

        setDataFromIntentExtra();

        presenter = new EditorPresenter(this);
    }

    private void setDataFromIntentExtra() {
        if (id != 0) {
            et_title.setText(title);
            et_note.setText(note);
            palette.setSelectedColor(color);
            readMode();
        } else {
            //default color
            palette.setSelectedColor(getResources().getColor(R.color.white));
            color = getResources().getColor(R.color.white);
            editMode();
        }
    }

    private void editMode() {
        et_title.setFocusableInTouchMode(true);
        et_note.setFocusableInTouchMode(true);
        palette.setEnabled(true);
    }

    private void readMode() {
        et_title.setFocusableInTouchMode(false);
        et_note.setFocusableInTouchMode(false);
        et_note.setFocusable(false);
        et_note.setFocusable(false);
        palette.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        actionMenu = menu;
        if (id != 0) {
            actionMenu.findItem(R.id.save).setVisible(false);
            actionMenu.findItem(R.id.edit).setVisible(true);
            actionMenu.findItem(R.id.update).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(true);
        } else {

            actionMenu.findItem(R.id.save).setVisible(true);
            actionMenu.findItem(R.id.edit).setVisible(false);
            actionMenu.findItem(R.id.update).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String title = et_title.getText().toString();
        String note = et_note.getText().toString();
        int color = this.color;

        switch (item.getItemId()) {
            case R.id.save:
                if (!title.isEmpty() && !note.isEmpty())
                    presenter.saveNote(title, note, color);
                return true;

            case R.id.edit:
                editMode();

                actionMenu.findItem(R.id.save).setVisible(false);
                actionMenu.findItem(R.id.edit).setVisible(false);
                actionMenu.findItem(R.id.update).setVisible(true);
                actionMenu.findItem(R.id.delete).setVisible(false);
                return true;

            case R.id.update:
                if (!title.isEmpty() && !note.isEmpty())
                    presenter.updateNote(id, title, note, color);

                return true;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm!")
                        .setMessage("Are you sure?")
                        .setNegativeButton("Yes", (dialog, which) -> {
                            presenter.deleteNote(id);
                            dialog.dismiss();
                        })
                        .setPositiveButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void onRequestSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onRequestError: Error \n" + message);
    }
}