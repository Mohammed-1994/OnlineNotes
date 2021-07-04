package com.example.onlinenotes.activity.editor;

import android.util.Log;
import android.view.View;

import com.example.onlinenotes.api.ApiClient;
import com.example.onlinenotes.api.ApiInterface;
import com.example.onlinenotes.model.Note;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditorPresenter {

    private static final String TAG = "EditorPresenter myTag";
    private final EditorView editorView;

    public EditorPresenter(EditorView editorView) {
        this.editorView = editorView;
    }

    public void saveNote(String title, String note, int color) {
        editorView.showProgress();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.saveNote(title, note, color);

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                editorView.hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = response.body().isSuccess();
                    if (success)
                        editorView.onRequestSuccess(response.body().getMessage());
                    else
                        editorView.onRequestError("not success" + response.body().getMessage());

                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                editorView.hideProgress();
                editorView.onRequestError("onFailure" + t.getMessage());
            }
        });
    }


    public void updateNote(int id, String title, String note, int color) {
        editorView.showProgress();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.updateNote(id, title, note, color);

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                editorView.hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.message());
                    editorView.onRequestSuccess(response.body().getMessage());
                } else {
                    editorView.onRequestError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                editorView.onRequestError(t.getMessage());
            }
        });

    }

    public void deleteNote(int id){
        editorView.showProgress();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.deleteNote(id);

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                editorView.hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.message());
                    editorView.onRequestSuccess(response.body().getMessage());
                } else {
                    editorView.onRequestError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                editorView.onRequestError(t.getMessage());

            }
        });

    }
}
