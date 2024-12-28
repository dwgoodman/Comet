package com.example.comet;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PlayerBottomSheetFragment extends BottomSheetDialogFragment {

    public PlayerBottomSheetFragment() {
        // Required empty public constructor
    }

    public static PlayerBottomSheetFragment newInstance() {
        PlayerBottomSheetFragment fragment = new PlayerBottomSheetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);



        return view;
    }

    private void setupBottomSheetBehavior(BottomSheetDialog dialog) {
        // Assume your bottom sheet ID is bottom_sheet
        //this does not work as I don't know what view to make the bottom_sheet
        FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Show full player, hide mini player
                    getView().findViewById(R.id.full_player_layout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.mini_player_layout).setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Show mini player, hide full player
                    getView().findViewById(R.id.full_player_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.mini_player_layout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Handle slide
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            setupBottomSheetBehavior(bottomSheetDialog);
        });

        return dialog;
    }




}