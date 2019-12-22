package com.doan.timnhatro.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomAdapter;
import com.doan.timnhatro.model.MotelRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HouseFragment extends Fragment {

    private View container;
    private EditText edtSearch;
    private ImageView imgSearch;
    private MotelRoomAdapter motelRoomAdapter;
    private RecyclerView recRoomsFeatured;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();
    private ArrayList<MotelRoom> arrayMotelRoomSave = new ArrayList<>();

    public HouseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        container = inflater.inflate(R.layout.fragment_house, viewGroup, false);

        initView();

        return container;
    }

    private void initView() {
        edtSearch = container.findViewById(R.id.edtSearch);
        imgSearch = container.findViewById(R.id.imgSearch);
        recRoomsFeatured = container.findViewById(R.id.recRoomsFeatured);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createList();
        getArrayMotelRoom();
        addEvents();
    }

    private void addEvents() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)){
                    arrayMotelRoom.clear();
                    arrayMotelRoom.addAll(arrayMotelRoomSave);
                    motelRoomAdapter.notifyDataSetChanged();
                }else {
                    arrayMotelRoom.clear();
                    String search = edtSearch.getText().toString().toLowerCase();
                    for (MotelRoom motelRoom : arrayMotelRoomSave){
                        if (motelRoom.getStreet().toLowerCase().contains(search) | motelRoom.getDistrict().toLowerCase().contains(search) | motelRoom.getCity().toLowerCase().contains(search) ){
                            arrayMotelRoom.add(motelRoom);
                        }
                    }
                    motelRoomAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getArrayMotelRoom() {
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                arrayMotelRoom.add(snapshot.getValue(MotelRoom.class));
                                arrayMotelRoomSave.add(snapshot.getValue(MotelRoom.class));
                            }
                            motelRoomAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void createList() {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true));
        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
        recRoomsFeatured.setAdapter(motelRoomAdapter);
    }
}
